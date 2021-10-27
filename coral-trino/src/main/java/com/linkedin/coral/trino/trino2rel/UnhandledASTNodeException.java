package com.linkedin.coral.trino.trino2rel;

import io.trino.sql.tree.Node;

public class UnhandledASTNodeException extends RuntimeException {
    public UnhandledASTNodeException(Node node, String message) {
        super(String.format(message, node.toString(), getLine(node), getColumn(node)));
    }

    private static int getLine(Node node) {
        return node.getLocation().isPresent() ? node.getLocation().get().getLineNumber() : -1;
    }

    private static int getColumn(Node node) {
        return node.getLocation().isPresent() ? node.getLocation().get().getColumnNumber() : -1;
    }
}
