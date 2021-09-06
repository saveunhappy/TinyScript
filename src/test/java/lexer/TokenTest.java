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
    @Test
    public void test_makeString() throws Exception{
        String[] tests = {
                "\"123\"",
                "'123'"
        };
        for (String test : tests) {
            var it = new PeekIterator<>(test.chars().mapToObj(x -> (char) x));
            var token = Token.makeString(it);
            assertToken(token,test,TokenType.STRING);
        }
    }
    @Test
    public void test_makeOperator() throws Exception{
        String[] tests = {
                "+ xxx",
                "++mmm",
                "/=g",
                "==1",
                "&=3982",
                "&777",
                "||xxx",
                "^=111",
                "%7"
        };
        int i = 0;
        String[] results = {"+","++","/=","==","&=","&","||","^=","%"};
        for (String test : tests) {
            var it = new PeekIterator<>(test.chars().mapToObj(x ->(char)x));
            var token = Token.makeOp(it);
            assertToken(token,results[i++],TokenType.OPERATOR);
        }
    }

    @Test
    public void test_makeNumber() throws Exception{
        String[] tests = {
                "+0 aa",
                "-0 aa",
                ".3 ccc",
                ".5555 ddd",
                "7789.8888 ooo",
                "-1000.123123*123123"
        };
        for (String test : tests) {
            var it = new PeekIterator<Character>(test.chars().mapToObj(x->(char)x));
            var token = Token.makeNumber(it);
            var splitValue = test.split("[* ]+");
            assertToken(token,splitValue[0], test.contains(".") ?TokenType.FLOAT:TokenType.INTEGER);
        }
    }

}