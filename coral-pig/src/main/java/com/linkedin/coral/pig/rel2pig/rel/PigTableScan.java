package com.linkedin.coral.pig.rel2pig.rel;

import com.linkedin.coral.pig.rel2pig.PigLoadFunction;
import com.linkedin.coral.pig.rel2pig.TableToPigPathFunction;
import java.util.List;
import org.apache.calcite.rel.core.TableScan;


//TODO(ralam): Add comments and clean up code
public class PigTableScan {

  private static final String TABLE_SCAN_TEMPLATE = "%s = LOAD '%s' USING %s ;";

  private PigTableScan() {

  }

  public static String getScript(TableScan tableScan, String outputRelation, PigLoadFunction pigLoadFunction,
      TableToPigPathFunction tableToPigPathFunction) {
    // TODO(ralam): Make the loadFunction determined by a UDF that takes the table and database and returns a string
    List<String> qualifiedNames = tableScan.getTable().getQualifiedName();
    String database = qualifiedNames.get(1);
    String table = qualifiedNames.get(2);

    String loadFunction = pigLoadFunction.getLoadFunction(database, table);
    String pigPath = tableToPigPathFunction.getPigPath(database, table);

    return String.format(TABLE_SCAN_TEMPLATE, outputRelation, pigPath, loadFunction);
  }
}
