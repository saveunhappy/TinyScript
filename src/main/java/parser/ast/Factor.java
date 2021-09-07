package parser.ast;

import lexer.TokenType;
import parser.util.PeekTokenIterator;

/**
 * 就是计算，不管是布尔或者是变量，或者是数字
 */
public abstract class Factor extends ASTNode{
    public Factor(ASTNode _parent, PeekTokenIterator it) {
        super(_parent);
        var token = it.next();
        var type = token.getType();
        if(type == TokenType.VARIABLE){
            this.type = ASTNodeTypes.VARIABLE;
        }else{
            this.type = ASTNodeTypes.SCALAR;
        }
        this.label = token.getValue();
        this.lexeme = token;
    }
}
