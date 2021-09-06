package lexer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    void assertToken(Token token, String value, TokenType type){
        assertEquals(value, token.getValue());
        assertEquals(type, token.getType());
    }

    @Test
    public void test_expression() throws LexicalException {
        var lexer = new Lexer();
        var source = "(a+b)^100.12==+100-20";
        var tokens = lexer.analyse(source.chars().mapToObj(x -> (char)x));
        assertEquals(11, tokens.size());
        assertToken(tokens.get(0), "(", TokenType.BRACKET);
        assertToken(tokens.get(1), "a", TokenType.VARIABLE);
        assertToken(tokens.get(2), "+", TokenType.OPERATOR);
        assertToken(tokens.get(3), "b", TokenType.VARIABLE);
        assertToken(tokens.get(4), ")", TokenType.BRACKET);
        assertToken(tokens.get(5), "^", TokenType.OPERATOR);
        assertToken(tokens.get(6), "100.12", TokenType.FLOAT);
        assertToken(tokens.get(7), "==", TokenType.OPERATOR);
        assertToken(tokens.get(8), "+100", TokenType.INTEGER);
        assertToken(tokens.get(9), "-", TokenType.OPERATOR);
        assertToken(tokens.get(10), "20", TokenType.INTEGER);
    }
    @Test
    public void test_expression2() throws LexicalException {
        var lexer = new Lexer();
        var source = "3*-3.5";
        var tokens = lexer.analyse(source.chars().mapToObj(x -> (char)x));
        assertToken(tokens.get(0), "3", TokenType.INTEGER);
        assertToken(tokens.get(1), "*", TokenType.OPERATOR);
        assertToken(tokens.get(2), "-3.5", TokenType.FLOAT);
    }
}