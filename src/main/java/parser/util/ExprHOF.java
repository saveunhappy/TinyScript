package parser.util;

import parser.ast.ASTNode;

@FunctionalInterface
public interface ExprHOF {

    ASTNode hoc() throws ParseException;
}
