package parser;

import lexer.Lexer;
import lexer.LexicalException;
import org.junit.jupiter.api.Test;
import parser.ast.Expr;
import parser.ast.Scalar;
import parser.util.ParseException;
import parser.util.PeekTokenIterator;

import static org.junit.jupiter.api.Assertions.*;

class SimpleParserTest {

    @Test
    void parse() throws LexicalException, ParseException {

        /*
        *  2022-12-03 解析过程就是这样的。
        * 1 + (2 + 3 + 4)
        * 2 + (3 + 4)
        * 3 + 4
        */
        var source = "1+2+3+4".chars().mapToObj(x->(char)x);
        var lexer = new Lexer();
        //lexer.analyse(source).stream()这里返回的是一个ArrayList<Token>，
        // 就是已经解析好了1+2+3+4了，是操作数还是数字，
        var it = new PeekTokenIterator(lexer.analyse(source).stream());
        var expr = SimpleParser.parse(it);
        //这个expr就是根节点，左边是1，右边是2+3+4
        assertEquals(2, expr.getChildren().size());
        var v1 = (Scalar)expr.getChild(0);
        assertEquals("1", v1.getLexeme().getValue());
        assertEquals("+", expr.getLexeme().getValue());
        //这个就是右节点了，右边就是2+3+4，那再用第一个加号分割开，左边及时2，右边就是3+4,那么它本身就是一个+
        var e2 = (Expr)expr.getChild(1);
        var v2 = (Scalar)e2.getChild(0);
        assertEquals("2", v2.getLexeme().getValue());
        assertEquals("+", e2.getLexeme().getValue());
        //现在知道e2是加号了，那child(1)就是右边了。右边就是3+4,那么e3还是表达式，就还是加号，左边是三，
        var e3 = (Expr)e2.getChild(1);
        var v3 = (Scalar)e3.getChild(0);
        assertEquals("3", v3.getLexeme().getValue());
        assertEquals("+", e3.getLexeme().getValue());
        //那么这里右边就是4,
        var v4 = (Scalar)e3.getChild(1);
        assertEquals("4", v4.getLexeme().getValue());

        expr.print(0);

    }
}