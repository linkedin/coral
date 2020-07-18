/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import java.io.Serializable;
import java.util.ArrayList;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;


//CHECKSTYLE:OFF
public class ASTNode extends CommonTree implements Node, Serializable {
  private static final long serialVersionUID = 1L;

  private transient ASTNodeOrigin origin;

  public ASTNode() {
  }

  /**
   * Constructor.
   *
   * @param t
   *          Token for the CommonTree Node
   */
  public ASTNode(Token t) {
    super(t);
  }

  public ASTNode(ASTNode node) {
    super(node);
    this.origin = node.origin;
  }

  @Override
  public Tree dupNode() {
    return new ASTNode(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.hadoop.hive.ql.lib.Node#getChildren()
   */
  @Override
  public ArrayList<Node> getChildren() {
    if (super.getChildCount() == 0) {
      return null;
    }

    ArrayList<Node> ret_vec = new ArrayList<Node>();
    for (int i = 0; i < super.getChildCount(); ++i) {
      ret_vec.add((Node) super.getChild(i));
    }

    return ret_vec;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.hadoop.hive.ql.lib.Node#getName()
   */
  public String getName() {
    return (Integer.valueOf(super.getToken().getType())).toString();
  }

  /**
   * @return information about the object from which this ASTNode originated, or
   *         null if this ASTNode was not expanded from an object reference
   */
  public ASTNodeOrigin getOrigin() {
    return origin;
  }

  /**
   * Tag this ASTNode with information about the object from which this node originated.
   *
   * @param origin {@link ASTNodeOrigin} object
   */
  public void setOrigin(ASTNodeOrigin origin) {
    this.origin = origin;
  }

  public String dump() {
    StringBuilder sb = new StringBuilder("\n");
    dump(sb, "");
    return sb.toString();
  }

  private StringBuilder dump(StringBuilder sb, String ws) {
    sb.append(ws);
    sb.append(toString());
    sb.append("\n");

    ArrayList<Node> children = getChildren();
    if (children != null) {
      for (Node node : getChildren()) {
        if (node instanceof ASTNode) {
          ((ASTNode) node).dump(sb, ws + "   ");
        } else {
          sb.append(ws);
          sb.append("   NON-ASTNODE!!");
          sb.append("\n");
        }
      }
    }
    return sb;
  }
}
