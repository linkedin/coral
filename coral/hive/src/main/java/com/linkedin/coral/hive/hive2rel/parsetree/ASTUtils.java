package com.linkedin.coral.hive.hive2rel.parsetree;

import java.util.ArrayList;
import java.util.Optional;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import static com.google.common.base.Preconditions.*;


public class ASTUtils {

  public static ASTNode getChildAtPath(ASTNode root, int[] pathNodes) {
    checkNotNull(root);
    checkNotNull(pathNodes);
    checkArgument(pathNodes.length > 0);

    int depth = 0;
    ASTNode current = root;

    while (depth < pathNodes.length) {
      ArrayList<Node> children = current.getChildren();
      final int d = depth;
      Optional<Node> node = children.stream()
          .filter(c -> ((ASTNode) c).getType() == pathNodes[d])
          .findFirst();
      if (! node.isPresent()) {
        throw new IllegalStateException(
            String.format("Illegal AST state. Expected: %s, found: %s", pathNodes[depth], current.dump()));
      }
      current = (ASTNode) node.get();
      ++depth;
    }

    if (current.getChildren() != null && current.getChildren().size() > 0) {
      return (ASTNode) current.getChildren().get(0);
    }
    return null;
  }

}
