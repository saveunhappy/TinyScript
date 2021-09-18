package parser.ast;

import lexer.Token;
import lexer.TokenType;
import parser.util.ParseException;
import parser.util.PeekTokenIterator;

/**
 * 就是计算，不管是布尔或者是变量，或者是数字
 */
public abstract class Factor extends ASTNode{
    public Factor(Token token) {
        super();
        this.lexeme = token;
        this.label = token.getValue();
    }

    public static ASTNode parse(PeekTokenIterator it) {
        var token = it.peek();
        var type = token.getType();

        if(type == TokenType.VARIABLE) {
            it.next();
            return new Variable(token);
        } else if(token.isScalar()){
            it.next();
            return new Scalar(token);
        }
        return null;
    }
}
