/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;
import java.util.Map;


/**
 * @deprecated Use {@link LocalMetastoreSchema} instead. This class will be removed in a future release.
 */
@Deprecated
public class LocalMetastoreHiveSchema extends LocalMetastoreSchema {

  public LocalMetastoreHiveSchema(Map<String, Map<String, List<String>>> localMetastore) {
    super(localMetastore);
  }
}
