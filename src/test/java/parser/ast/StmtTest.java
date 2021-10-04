package parser.ast;

import lexer.Lexer;
import lexer.LexicalException;
import org.junit.jupiter.api.Test;
import parser.util.ParseException;
import parser.util.ParserUtils;
import parser.util.PeekTokenIterator;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

class StmtTest {
    @Test
    public void declare() throws LexicalException, ParseException {
        var it = createTokenIt("var i = 100 * 2");
        var stmt = DeclareStmt.parse(it);
        assertEquals(ParserUtils.toPostfixExpression(stmt), "i 100 2 * =");
    }

    @Test
    public void assign() throws LexicalException, ParseException {
        var it = createTokenIt("i = 100 * 2");
        var stmt = AssignStmt.parse( it);
        assertEquals(ParserUtils.toPostfixExpression(stmt), "i 100 2 * =");
    }

    @Test
    public void ifstmt() throws LexicalException, ParseException {
        var it = createTokenIt("if(a){\n" +
                "a = 1\n" +
                "}"
        );

        var stmt = (IfStmt)IfStmt.parse(it);
        var expr = (Variable)stmt.getChild(0);
        var block = (Block)stmt.getChild(1);
        var assignStmt = (AssignStmt)block.getChild(0);

        assertEquals("a", expr.getLexeme().getValue());
        assertEquals("=", assignStmt.getLexeme().getValue());
    }

    @Test
    public void ifElseStmt() throws LexicalException, ParseException {
        var it = createTokenIt("if(a) {\n" +
                "a = 1\n" +
                "} else {\n" +
                "a = 2\n" +
                "a = a * 3" +
                "}"
        );
        var stmt = (IfStmt)IfStmt.parse( it);
        //if(a)里面的a，第一个
        var expr = (Variable)stmt.getChild(0);
        //{a = 1}这个block，第二个
        var block = (Block)stmt.getChild(1);
        //assign就是赋值的意思，这里 就是 =
        var assignStmt = (AssignStmt)block.getChild(0);
        //else里面的语句块，就是第三个else{a = 2 \n a = a * 3}
        var elseBlock = (Block)stmt.getChild(2);
        //else里面的赋值，就是 a = 2,a = a * 3
        var assignStmt2 = (AssignStmt)elseBlock.getChild(0);
        var assignStmt3 = (AssignStmt)elseBlock.getChild(1);

        assertEquals("a", expr.getLexeme().getValue());
        assertEquals("=", assignStmt.getLexeme().getValue());
        assertEquals("=", assignStmt2.getLexeme().getValue());
        assertEquals(2, elseBlock.getChildren().size());
        assertEquals("=", assignStmt3.getLexeme().getValue());
    }

    @Test
    public void function() throws FileNotFoundException, UnsupportedEncodingException, LexicalException, ParseException {
        var tokens = Lexer.fromFile("./example/function.ts");
        /**
         * func add(int a, int b) int {
         *   return a + b
         * }
         */
        var functionStmt = (FunctionDeclareStmt)Stmt.parseStmt( new PeekTokenIterator(tokens.stream()));

        var args = functionStmt.getArgs();
        assertEquals("a", args.getChild(0).getLexeme().getValue());
        assertEquals("b", args.getChild(1).getLexeme().getValue());

        var type = functionStmt.getFuncType();
        assertEquals("int", type);
        //方法名就是这个方法变量，就是这么设计的
        var functionVariable = functionStmt.getFunctionVariable();
        assertEquals("add", functionVariable.getLexeme().getValue());

        var block = functionStmt.getBlock();
        assertEquals(true, block.getChild(0) instanceof ReturnStmt);

    }

    @Test
    public void function1() throws FileNotFoundException, UnsupportedEncodingException, LexicalException, ParseException {
        var tokens = Lexer.fromFile("./example/recursion.ts");
        /**
         * func fact(int n)  int {
         *   if(n == 0) {
         *     return 1
         *   }
         *   return fact(n-1) * n
         * }
         */
        var functionStmt = (FunctionDeclareStmt)Stmt.parseStmt( new PeekTokenIterator(tokens.stream()));
        assertEquals("func fact args block", ParserUtils.toBFSString(functionStmt, 4));
        assertEquals("args n", ParserUtils.toBFSString(functionStmt.getArgs(), 2));
        assertEquals("block if return", ParserUtils.toBFSString(functionStmt.getBlock(), 3));
    }



    private PeekTokenIterator createTokenIt(String src) throws LexicalException, ParseException {
        var lexer = new Lexer();
        var tokens = lexer.analyse(src.chars().mapToObj(x ->(char)x));
        var tokenIt = new PeekTokenIterator(tokens.stream());
        return tokenIt;
    }
}