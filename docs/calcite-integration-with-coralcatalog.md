# Calcite Integration Using CoralCatalog - Improved Design

## Overview
This document describes the improved design for Calcite integration using `CoralCatalog` instead of `HiveMetastoreClient`. This approach provides cleaner abstraction and easier support for multiple table formats (Hive, Iceberg, etc.).

## Key Insight

**Current Design**:
```
HiveSchema → HiveDbSchema → HiveMetastoreClient.getTable() 
    → Hive Table object → wrap in HiveTable (Calcite)
```

**Improved Design**:
```
HiveSchema → HiveDbSchema → CoralCatalog.getDataset() 
    → Dataset → create HiveTable or IcebergTable (Calcite) based on Dataset type
```

## Benefits

1. **Cleaner Abstraction**: Use our unified `CoralCatalog` API throughout
2. **Format Agnostic**: Easy to add support for Delta Lake, Hudi, etc.
3. **Type Safety**: `Dataset` provides type information without exposing internals
4. **Consistent API**: Same catalog abstraction used everywhere
5. **Backward Compatible**: `HiveMetastoreClient extends CoralCatalog` - existing code works

## Modified Class Design

### 1. HiveSchema - Use CoralCatalog

**Current**:
```java
public class HiveSchema implements Schema {
    private final HiveMetastoreClient msc;
    
    public HiveSchema(HiveMetastoreClient msc) {
        this.msc = msc;
    }
    
    @Override
    public Schema getSubSchema(String name) {
        Database database = msc.getDatabase(name);
        return (database == null) ? null : new HiveDbSchema(msc, database.getName());
    }
    
    @Override
    public Set<String> getSubSchemaNames() {
        return ImmutableSet.copyOf(msc.getAllDatabases());
    }
}
```

**Improved**:
```java
public class HiveSchema implements Schema {
    private final CoralCatalog catalog;  // Use CoralCatalog!
    
    public HiveSchema(CoralCatalog catalog) {
        this.catalog = checkNotNull(catalog);
    }
    
    @Override
    public Schema getSubSchema(String name) {
        // Use CoralCatalog API
        List<String> datasets = catalog.getAllDatasets(name);
        return datasets.isEmpty() ? null : new HiveDbSchema(catalog, name);
    }
    
    @Override
    public Set<String> getSubSchemaNames() {
        return ImmutableSet.copyOf(catalog.getAllDatabases());
    }
}
```

**Changes**:
- Use `CoralCatalog` instead of `HiveMetastoreClient`
- Call `catalog.getAllDatabases()` and `catalog.getAllDatasets()`
- Pass `CoralCatalog` to `HiveDbSchema`

---

### 2. HiveDbSchema - Create Appropriate Calcite Table

**Current**:
```java
public class HiveDbSchema implements Schema {
    private final HiveMetastoreClient msc;
    private final String dbName;
    
    @Override
    public Table getTable(String name) {
        org.apache.hadoop.hive.metastore.api.Table table = msc.getTable(dbName, name);
        if (table == null) {
            return null;
        }
        switch (tableType) {
            case VIRTUAL_VIEW:
                return new HiveViewTable(table, ...);
            default:
                return new HiveTable(table);  // Always HiveTable!
        }
    }
}
```

**Improved**:
```java
public class HiveDbSchema implements Schema {
    private final CoralCatalog catalog;  // Use CoralCatalog!
    private final String dbName;
    
    HiveDbSchema(CoralCatalog catalog, String dbName) {
        this.catalog = checkNotNull(catalog);
        this.dbName = checkNotNull(dbName);
    }
    
    @Override
    public Table getTable(String name) {
        // Get unified Dataset from CoralCatalog
        Dataset dataset = catalog.getDataset(dbName, name);
        if (dataset == null) {
            return null;
        }
        
        // Check if it's a view
        if (dataset.tableType() == TableType.VIEW) {
            // For views, we still need the Hive Table object for view expansion
            org.apache.hadoop.hive.metastore.api.Table hiveTable = 
                getHiveTableFromCatalog(dbName, name);
            return new HiveViewTable(hiveTable, ImmutableList.of(HiveSchema.ROOT_SCHEMA, dbName));
        }
        
        // Dispatch based on Dataset implementation type
        if (dataset instanceof IcebergDataset) {
            return new IcebergTable((IcebergDataset) dataset);
        } else if (dataset instanceof HiveDataset) {
            return new HiveTable((HiveDataset) dataset);
        } else {
            throw new UnsupportedOperationException("Unknown dataset type: " + dataset.getClass());
        }
    }
    
    @Override
    public Set<String> getTableNames() {
        return ImmutableSet.copyOf(catalog.getAllDatasets(dbName));
    }
    
    // Helper to get Hive table when needed (e.g., for views)
    private org.apache.hadoop.hive.metastore.api.Table getHiveTableFromCatalog(
        String dbName, String tableName) {
        // If catalog is HiveMetastoreClient, we can get the Hive table
        if (catalog instanceof HiveMetastoreClient) {
            return ((HiveMetastoreClient) catalog).getTable(dbName, tableName);
        }
        throw new RuntimeException("Cannot get Hive table from non-Hive catalog");
    }
}
```

**Key Changes**:
- Use `CoralCatalog.getDataset()` instead of `getTable()`
- Check `dataset instanceof IcebergDataset` to decide which Calcite Table to create
- Return `IcebergTable` for Iceberg datasets, `HiveTable` for Hive datasets
- For views, still need access to Hive Table for view expansion logic

---

### 3. HiveTable - Accept HiveDataset

**Current**:
```java
public class HiveTable implements ScannableTable {
    protected final org.apache.hadoop.hive.metastore.api.Table hiveTable;
    
    public HiveTable(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
        this.hiveTable = checkNotNull(hiveTable);
    }
    
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        final List<FieldSchema> cols = getColumns();
        // ... convert using TypeConverter
    }
}
```

**Option A - Minimal Change (Keep Hive Table)**:
```java
public class HiveTable implements ScannableTable {
    protected final org.apache.hadoop.hive.metastore.api.Table hiveTable;
    
    // Existing constructor for backward compatibility
    public HiveTable(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
        this.hiveTable = checkNotNull(hiveTable);
    }
    
    // New constructor accepting HiveDataset
    public HiveTable(HiveDataset dataset) {
        this.hiveTable = dataset.getHiveTable();
    }
    
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        // Same as before - uses hiveTable
        final List<FieldSchema> cols = getColumns();
        // ... convert using TypeConverter
    }
}
```

**Option B - Use Dataset (Cleaner)**:
```java
public class HiveTable implements ScannableTable {
    protected final HiveDataset dataset;
    protected final org.apache.hadoop.hive.metastore.api.Table hiveTable;
    
    // Constructor accepting HiveDataset
    public HiveTable(HiveDataset dataset) {
        this.dataset = checkNotNull(dataset);
        this.hiveTable = dataset.getHiveTable();
    }
    
    // Keep old constructor for backward compatibility
    public HiveTable(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
        this.hiveTable = checkNotNull(hiveTable);
        this.dataset = null;
    }
    
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        // Can potentially use dataset.avroSchema() here in future
        // For now, use existing logic
        final List<FieldSchema> cols = getColumns();
        // ... convert using TypeConverter
    }
}
```

---

### 4. IcebergTable - NEW CLASS

```java
/**
 * Calcite Table implementation for Iceberg tables.
 * Uses IcebergDataset to provide native Iceberg schema to Calcite.
 */
public class IcebergTable implements ScannableTable {
    
    private final IcebergDataset dataset;
    
    /**
     * Creates IcebergTable from IcebergDataset.
     */
    public IcebergTable(IcebergDataset dataset) {
        this.dataset = checkNotNull(dataset);
    }
    
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        // Get Iceberg table from dataset
        org.apache.iceberg.Table icebergTable = 
            (org.apache.iceberg.Table) dataset.underlyingTable();
        
        // Use IcebergTypeConverter for native schema conversion
        return IcebergTypeConverter.convert(
            icebergTable.schema(), 
            dataset.name(), 
            typeFactory
        );
    }
    
    @Override
    public Schema.TableType getJdbcTableType() {
        return dataset.tableType() == TableType.VIEW 
            ? Schema.TableType.VIEW 
            : Schema.TableType.TABLE;
    }
    
    @Override
    public Statistic getStatistic() {
        // Future: Could use Iceberg statistics here
        return Statistics.UNKNOWN;
    }
    
    @Override
    public boolean isRolledUp(String s) {
        return false;
    }
    
    @Override
    public boolean rolledUpColumnValidInsideAgg(
        String s, SqlCall sqlCall, SqlNode sqlNode,
        CalciteConnectionConfig config) {
        return true;
    }
    
    @Override
    public Enumerable<Object[]> scan(DataContext dataContext) {
        throw new RuntimeException("Calcite runtime is not supported");
    }
}
```

---

### 5. IcebergTypeConverter - NEW CLASS

```java
/**
 * Converts Iceberg Schema to Calcite RelDataType.
 * Preserves Iceberg type semantics and features.
 */
public class IcebergTypeConverter {
    
    private IcebergTypeConverter() {
        // Utility class
    }
    
    /**
     * Converts Iceberg Schema to Calcite RelDataType.
     * 
     * @param icebergSchema Iceberg table schema
     * @param tableName Table name for naming nested types
     * @param typeFactory Calcite type factory
     * @return RelDataType representing the schema
     */
    public static RelDataType convert(
        org.apache.iceberg.Schema icebergSchema,
        String tableName,
        RelDataTypeFactory typeFactory) {
        
        List<org.apache.iceberg.types.Types.NestedField> columns = 
            icebergSchema.columns();
        
        List<RelDataType> fieldTypes = new ArrayList<>(columns.size());
        List<String> fieldNames = new ArrayList<>(columns.size());
        
        for (org.apache.iceberg.types.Types.NestedField field : columns) {
            fieldNames.add(field.name());
            
            // Convert field type
            RelDataType fieldType = convertIcebergType(
                field.type(), 
                field.name(),
                typeFactory
            );
            
            // Handle nullability - Iceberg has explicit required/optional
            if (field.isOptional()) {
                fieldType = typeFactory.createTypeWithNullability(fieldType, true);
            } else {
                fieldType = typeFactory.createTypeWithNullability(fieldType, false);
            }
            
            fieldTypes.add(fieldType);
        }
        
        return typeFactory.createStructType(fieldTypes, fieldNames);
    }
    
    /**
     * Converts Iceberg Type to Calcite RelDataType.
     */
    private static RelDataType convertIcebergType(
        org.apache.iceberg.types.Type icebergType,
        String fieldName,
        RelDataTypeFactory typeFactory) {
        
        org.apache.iceberg.types.Type.TypeID typeId = icebergType.typeId();
        
        switch (typeId) {
            case BOOLEAN:
                return typeFactory.createSqlType(SqlTypeName.BOOLEAN);
                
            case INTEGER:
                return typeFactory.createSqlType(SqlTypeName.INTEGER);
                
            case LONG:
                return typeFactory.createSqlType(SqlTypeName.BIGINT);
                
            case FLOAT:
                return typeFactory.createSqlType(SqlTypeName.FLOAT);
                
            case DOUBLE:
                return typeFactory.createSqlType(SqlTypeName.DOUBLE);
                
            case DATE:
                return typeFactory.createSqlType(SqlTypeName.DATE);
                
            case TIME:
                return typeFactory.createSqlType(SqlTypeName.TIME);
                
            case TIMESTAMP:
                // Iceberg has TIMESTAMP_WITH_TIMEZONE and TIMESTAMP_WITHOUT_TIMEZONE
                org.apache.iceberg.types.Types.TimestampType tsType = 
                    (org.apache.iceberg.types.Types.TimestampType) icebergType;
                return tsType.shouldAdjustToUTC() 
                    ? typeFactory.createSqlType(SqlTypeName.TIMESTAMP_WITH_LOCAL_TIME_ZONE)
                    : typeFactory.createSqlType(SqlTypeName.TIMESTAMP);
                
            case STRING:
                return typeFactory.createSqlType(SqlTypeName.VARCHAR, Integer.MAX_VALUE);
                
            case UUID:
                // Represent UUID as BINARY(16) or CHAR(36)
                return typeFactory.createSqlType(SqlTypeName.CHAR, 36);
                
            case FIXED:
                org.apache.iceberg.types.Types.FixedType fixedType = 
                    (org.apache.iceberg.types.Types.FixedType) icebergType;
                return typeFactory.createSqlType(SqlTypeName.BINARY, fixedType.length());
                
            case BINARY:
                return typeFactory.createSqlType(SqlTypeName.VARBINARY, Integer.MAX_VALUE);
                
            case DECIMAL:
                org.apache.iceberg.types.Types.DecimalType decimalType = 
                    (org.apache.iceberg.types.Types.DecimalType) icebergType;
                return typeFactory.createSqlType(
                    SqlTypeName.DECIMAL, 
                    decimalType.precision(), 
                    decimalType.scale()
                );
                
            case STRUCT:
                return convertIcebergStruct(
                    (org.apache.iceberg.types.Types.StructType) icebergType,
                    fieldName,
                    typeFactory
                );
                
            case LIST:
                org.apache.iceberg.types.Types.ListType listType = 
                    (org.apache.iceberg.types.Types.ListType) icebergType;
                RelDataType elementType = convertIcebergType(
                    listType.elementType(),
                    fieldName + "_element",
                    typeFactory
                );
                // Iceberg list elements can be required or optional
                if (listType.isElementOptional()) {
                    elementType = typeFactory.createTypeWithNullability(elementType, true);
                }
                return typeFactory.createArrayType(elementType, -1);
                
            case MAP:
                org.apache.iceberg.types.Types.MapType mapType = 
                    (org.apache.iceberg.types.Types.MapType) icebergType;
                RelDataType keyType = convertIcebergType(
                    mapType.keyType(),
                    fieldName + "_key",
                    typeFactory
                );
                RelDataType valueType = convertIcebergType(
                    mapType.valueType(),
                    fieldName + "_value",
                    typeFactory
                );
                // Iceberg map values can be required or optional
                if (mapType.isValueOptional()) {
                    valueType = typeFactory.createTypeWithNullability(valueType, true);
                }
                return typeFactory.createMapType(keyType, valueType);
                
            default:
                throw new UnsupportedOperationException(
                    "Unsupported Iceberg type: " + icebergType
                );
        }
    }
    
    /**
     * Converts Iceberg StructType to Calcite RelDataType.
     */
    private static RelDataType convertIcebergStruct(
        org.apache.iceberg.types.Types.StructType structType,
        String structName,
        RelDataTypeFactory typeFactory) {
        
        List<org.apache.iceberg.types.Types.NestedField> fields = structType.fields();
        List<RelDataType> fieldTypes = new ArrayList<>(fields.size());
        List<String> fieldNames = new ArrayList<>(fields.size());
        
        for (org.apache.iceberg.types.Types.NestedField field : fields) {
            fieldNames.add(field.name());
            
            RelDataType fieldType = convertIcebergType(
                field.type(),
                structName + "_" + field.name(),
                typeFactory
            );
            
            // Handle field nullability
            if (field.isOptional()) {
                fieldType = typeFactory.createTypeWithNullability(fieldType, true);
            }
            
            fieldTypes.add(fieldType);
        }
        
        return typeFactory.createStructType(fieldTypes, fieldNames);
    }
}
```

---

### 6. ToRelConverter - Use CoralCatalog

**Current**:
```java
public abstract class ToRelConverter {
    private final HiveMetastoreClient hiveMetastoreClient;
    
    protected ToRelConverter(HiveMetastoreClient hiveMetastoreClient) {
        this.hiveMetastoreClient = checkNotNull(hiveMetastoreClient);
        SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
        schemaPlus.add(HiveSchema.ROOT_SCHEMA, new HiveSchema(hiveMetastoreClient));
        // ...
    }
}
```

**Improved**:
```java
public abstract class ToRelConverter {
    private final CoralCatalog catalog;  // Use CoralCatalog!
    
    protected ToRelConverter(CoralCatalog catalog) {
        this.catalog = checkNotNull(catalog);
        SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
        schemaPlus.add(HiveSchema.ROOT_SCHEMA, new HiveSchema(catalog));
        // ...
    }
    
    // Backward compatibility constructor
    protected ToRelConverter(HiveMetastoreClient hiveMetastoreClient) {
        this((CoralCatalog) hiveMetastoreClient);  // Safe cast since HiveMetastoreClient extends CoralCatalog
    }
    
    public SqlNode processView(String dbName, String tableName) {
        // If we need Hive table for view expansion, get it from catalog
        if (catalog instanceof HiveMetastoreClient) {
            org.apache.hadoop.hive.metastore.api.Table table = 
                ((HiveMetastoreClient) catalog).getTable(dbName, tableName);
            // ... process view
        } else {
            throw new UnsupportedOperationException(
                "View processing requires HiveMetastoreClient");
        }
    }
}
```

---

## Complete Flow Diagram

```
SQL Query
    ↓
ToRelConverter (accepts CoralCatalog)
    ↓
Frameworks.createRootSchema()
    ↓
HiveSchema (uses CoralCatalog)
    ├── getSubSchemaNames() → catalog.getAllDatabases()
    └── getSubSchema(dbName) → new HiveDbSchema(catalog, dbName)
            ↓
        HiveDbSchema (uses CoralCatalog)
            ├── getTableNames() → catalog.getAllDatasets(dbName)
            └── getTable(tableName) → 
                    ↓
                Dataset dataset = catalog.getDataset(dbName, tableName)
                    ↓
                if (dataset instanceof IcebergDataset)
                    return new IcebergTable(dataset)
                        ↓
                    IcebergTable.getRowType()
                        ↓
                    IcebergTypeConverter.convert()
                        ↓
                    RelDataType (Iceberg schema)
                    
                else if (dataset instanceof HiveDataset)
                    return new HiveTable(dataset)
                        ↓
                    HiveTable.getRowType()
                        ↓
                    TypeConverter.convert()
                        ↓
                    RelDataType (Hive schema)
```

---

## Implementation Plan

### Phase 1: Update Core Classes

1. **Modify HiveSchema.java**
   - Change constructor to accept `CoralCatalog` instead of `HiveMetastoreClient`
   - Update method calls to use `CoralCatalog` API
   - Keep backward compatibility

2. **Modify HiveDbSchema.java**
   - Change constructor to accept `CoralCatalog`
   - Update `getTable()` to use `catalog.getDataset()`
   - Add type-based dispatch to create `HiveTable` or `IcebergTable`

3. **Modify HiveTable.java**
   - Add constructor accepting `HiveDataset`
   - Keep existing constructor for backward compatibility

### Phase 2: Add Iceberg Support

4. **Create IcebergTable.java**
   - Implement `ScannableTable`
   - Use `IcebergDataset` internally
   - Call `IcebergTypeConverter` for schema

5. **Create IcebergTypeConverter.java**
   - Convert Iceberg Schema → Calcite RelDataType
   - Handle all Iceberg types (including UUID, FIXED)
   - Preserve nullability semantics

### Phase 3: Update Entry Points

6. **Modify ToRelConverter.java**
   - Change constructor to accept `CoralCatalog`
   - Keep backward compatibility constructor
   - Update to pass `CoralCatalog` to `HiveSchema`

### Phase 4: Testing

7. **Test with Hive Tables**
   - Verify existing Hive tables still work
   - No regression in Hive query processing

8. **Test with Iceberg Tables**
   - Verify Iceberg schema is read correctly
   - Test all Iceberg types
   - Validate nullability handling

---

## Benefits of This Design

### 1. Unified API
```java
// Single interface used throughout
CoralCatalog catalog = new HiveMscAdapter(metastoreClient);
ToRelConverter converter = new HiveToRelConverter(catalog);
```

### 2. Format Agnostic
```java
// Easy to add new formats
if (dataset instanceof DeltaLakeDataset) {
    return new DeltaLakeTable(dataset);
}
```

### 3. Clean Separation
- **Catalog Layer**: `CoralCatalog`, `Dataset`
- **Calcite Layer**: `HiveSchema`, `HiveDbSchema`
- **Table Layer**: `HiveTable`, `IcebergTable`
- **Type Conversion**: `TypeConverter`, `IcebergTypeConverter`

### 4. Backward Compatible
```java
// Old code still works
HiveMetastoreClient client = new HiveMscAdapter(msc);
ToRelConverter converter = new HiveToRelConverter(client);  // Works!
// Because HiveMetastoreClient extends CoralCatalog
```

### 5. Type Safety
```java
// Dataset type tells us what to create
Dataset dataset = catalog.getDataset("db", "table");
if (dataset instanceof IcebergDataset) {
    // We know it's Iceberg, create IcebergTable
    return new IcebergTable((IcebergDataset) dataset);
}
```

---

## Key Differences from Previous Design

| Aspect | Previous Design | New Design |
|--------|----------------|------------|
| **Schema Layer** | Uses `HiveMetastoreClient` | Uses `CoralCatalog` |
| **Table Access** | `getTable()` returns Hive Table | `getDataset()` returns Dataset |
| **Type Dispatch** | Check table properties | Check `instanceof Dataset` |
| **Iceberg Detection** | In `HiveTable.getRowType()` | In `HiveDbSchema.getTable()` |
| **Table Creation** | Always `new HiveTable()` | `new HiveTable()` or `new IcebergTable()` |
| **Abstraction** | Hive-centric | Format-agnostic |

---

## Code Changes Summary

### Modified Files (4 files)
1. `HiveSchema.java` - Use `CoralCatalog` instead of `HiveMetastoreClient`
2. `HiveDbSchema.java` - Use `CoralCatalog`, dispatch based on `Dataset` type
3. `HiveTable.java` - Add constructor accepting `HiveDataset`
4. `ToRelConverter.java` - Accept `CoralCatalog` in constructor

### New Files (2 files)
1. `IcebergTable.java` - Calcite Table for Iceberg datasets
2. `IcebergTypeConverter.java` - Convert Iceberg types to Calcite types

### Total Changes
- **~200 lines** of modifications
- **~300 lines** of new code
- **No breaking changes** - backward compatible

---

## Example Usage

### Creating Converter
```java
// Using CoralCatalog (new way)
CoralCatalog catalog = new HiveMscAdapter(metastoreClient);
HiveToRelConverter converter = new HiveToRelConverter(catalog);

// Using HiveMetastoreClient (old way - still works)
HiveMetastoreClient client = new HiveMscAdapter(metastoreClient);
HiveToRelConverter converter = new HiveToRelConverter(client);
```

### Processing Hive Table
```java
RelNode relNode = converter.convertView("mydb", "hive_table");
// HiveDbSchema.getTable() →
//   catalog.getDataset() → HiveDataset →
//   new HiveTable(hiveDataset) →
//   TypeConverter.convert() →
//   Hive RelDataType
```

### Processing Iceberg Table
```java
RelNode relNode = converter.convertView("mydb", "iceberg_table");
// HiveDbSchema.getTable() →
//   catalog.getDataset() → IcebergDataset →
//   new IcebergTable(icebergDataset) →
//   IcebergTypeConverter.convert() →
//   Iceberg RelDataType (native schema!)
```

---

## Summary

This improved design:
- ✅ Uses `CoralCatalog` throughout Calcite integration
- ✅ `Dataset` tells us which Calcite `Table` implementation to create
- ✅ Type dispatch in `HiveDbSchema.getTable()` based on `instanceof`
- ✅ Clean separation: format detection → Dataset type → Calcite Table type
- ✅ Easy to extend for new formats (Delta Lake, Hudi, etc.)
- ✅ Backward compatible with existing code
- ✅ Minimal code changes (~500 lines total)

**The key insight**: Use `CoralCatalog.getDataset()` to get format information, then create the appropriate Calcite `Table` implementation (`HiveTable` or `IcebergTable`) that knows how to convert its format's schema to Calcite's `RelDataType`.

