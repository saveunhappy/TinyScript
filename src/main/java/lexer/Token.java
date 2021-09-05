package lexer;

import common.AlphabetHelper;
import common.PeekIterator;

public class Token {
    TokenType _type;
    String _value;

    public TokenType getType() {
        return _type;
    }

    public Token(TokenType _type, String _value) {
        this._type = _type;
        this._value = _value;
    }

    @Override
    public String toString() {
        return String.format("type %s, value %s", _type, _value);
    }

    public boolean isVariable() {
        return _type == TokenType.VARIABLE;
    }

    public boolean isScalar() {
        return _type == TokenType.INTEGER || _type == TokenType.BOOLEAN ||
                _type == TokenType.STRING || _type == TokenType.FLOAT;
    }

    public TokenType get_type() {
        return _type;
    }

    public void set_type(TokenType _type) {
        this._type = _type;
    }

    public String get_value() {
        return _value;
    }

    public void set_value(String _value) {
        this._value = _value;
    }

    /**
     * 提取变量或者关键字
     * @param it
     * @return
     */
    public static Token makeVarOrKeyword(PeekIterator<Character> it){
        String s = "";
        while (it.hasNext()){
            var lookahead = it.peek();
            if(AlphabetHelper.isLiteral(lookahead)){
                s += lookahead;
            }else {
                break;
            }
            it.next();
        }
        //判断关键词OR变量
        if(Keywords.isKeyword(s)){
            return new Token(TokenType.KEYWORD,s);
        }
        if(s.equals("true") || s.equals("false")){
            return new Token(TokenType.BOOLEAN,s);
        }
        return new Token(TokenType.VARIABLE,s);
    }
}
