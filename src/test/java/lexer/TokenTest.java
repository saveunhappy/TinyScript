package lexer;

import common.PeekIterator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    void assertToken(Token token,String value,TokenType type){
        assertEquals(type,token.get_type());
        assertEquals(value,token.get_value());
    }
    @Test
    public void test_varOrKeyword() throws Exception{
        var it1 = new PeekIterator<Character>("if abc".chars().mapToObj(x->(char)x));
        var it2 = new PeekIterator<Character>("true abc".chars().mapToObj(x->(char)x));
        var token1 = Token.makeVarOrKeyword(it1);
        var token2 = Token.makeVarOrKeyword(it2);
        assertToken(token1,"if",TokenType.KEYWORD);
        assertToken(token2,"true",TokenType.BOOLEAN);
        it1.next();
        var token3 = Token.makeVarOrKeyword(it1);
        assertToken(token3,"abc",TokenType.VARIABLE);
    }

}