/**
 * Copyright 2020-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.sparkplan.containers;

import java.util.Objects;


/**
 * This class aggregates all information required for representing a single Spark plan node, such as
 *
 * +- Project [area_code#71, code#72]
 *    +- Filter (isnotnull(country#70))
 *       +- HiveTableRelation `default`.`airports`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [name#69, country#70, area_code#71, code#72]
 */
public class SparkPlanNode {

  // need to distinguish different plan types because we use different mechanisms
  // to parse different plan types.
  public enum PLANTYPE {
    HIVE_SCAN,
    FILE_SCAN,
    PROJECT,
    FILTER,
    JOIN,
    AGGREGATE,
    UNDEFINED
  }

  private final int position;
  private final String description;
  private PLANTYPE planType;

  /**
   *
   * @param position Position of the Spark plan node, from top to bottom is 0, 1, 2, 3...
   * @param description Description string of the Spark plan node
   * @param planType Type of the Spark plan node, like SCAN, PROJECT, FILTER...
   *
   * Example: In previous example, for 'Project [area_code#71, code#72]',
   * position is 0, description is "Project [area_code#71, code#72]", and planType is PROJECT;
   * for 'Filter (isnotnull(country#70))',
   * position is 1, description is "Filter (isnotnull(country#70))", and planType is FILTER
   */
  public SparkPlanNode(int position, String description, PLANTYPE planType) {
    this.position = position;
    this.description = description;
    this.planType = planType;
  }

  public int getPosition() {
    return position;
  }

  public String getDescription() {
    return description;
  }

  public PLANTYPE getPlanType() {
    return planType;
  }

  public void setPlanType(PLANTYPE planType) {
    this.planType = planType;
  }

  @Override
  public String toString() {
    return "SparkPlanNode{" + "position=" + position + ", description='" + description + '\'' + ", planType=" + planType
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SparkPlanNode that = (SparkPlanNode) o;
    return position == that.position && description.equals(that.description) && planType == that.planType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, description, planType);
  }
}
