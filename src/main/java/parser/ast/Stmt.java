package parser.ast;

import parser.util.ParseException;
import parser.util.PeekTokenIterator;

public abstract class Stmt extends ASTNode{
    public Stmt(ASTNodeTypes _type, String _label) {
        super( _type, _label);
    }

    public static ASTNode parseStmt(PeekTokenIterator it) throws ParseException {
        if(!it.hasNext()) {
            return null;
        }
        var token = it.next();
        var lookahead = it.peek();
        //这里putback就跟当时1+1 和+1，要根据前一个操作数来判断赋值还是函数，还是返回
        it.putBack();
        //赋值
        if(token.isVariable() && lookahead != null && lookahead.getValue().equals("=")) {
            return AssignStmt.parse(it);
        } else if(token.getValue().equals("var")) {
            return DeclareStmt.parse(it);
        } else if(token.getValue().equals("func")) {
            return FunctionDeclareStmt.parse(it);
        } else if(token.getValue().equals("return")) {
            return ReturnStmt.parse(it);
        } else if(token.getValue().equals("if")) {
            return IfStmt.parse(it);
        } else if(token.getValue().equals("{")) {
            return Block.parse(it);
        }else {
            return Expr.parse(it);
        }
    }
}
