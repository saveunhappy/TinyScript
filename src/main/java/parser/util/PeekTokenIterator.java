package parser.util;

import common.PeekIterator;
import lexer.Token;
import lexer.TokenType;

import java.util.stream.Stream;

public class PeekTokenIterator extends PeekIterator<Token> {
    public PeekTokenIterator(Stream<Token> stream) {
        super(stream);
    }
    //第一个是数字，第二个必须是个操作数，就是加减乘除
    public Token nextMatch(String value) throws ParseException {
        var token =  this.next();
        if(!token.getValue().equals(value)){
            throw new ParseException(token);
        }
        return token;
    }
    public Token nextMatch(TokenType type) throws ParseException {
        var token = this.next();
        if(!token.getType().equals(type)){
            throw new ParseException(token);
        }
        return token;
    }
}
