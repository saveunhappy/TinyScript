package parser.ast;

import lexer.Token;
import parser.util.ExprHOF;
import parser.util.ParseException;
import parser.util.PeekTokenIterator;
import parser.util.PriorityTable;

public class Expr extends ASTNode {

    private static PriorityTable table = new PriorityTable();


    public Expr() {
        super();
    }

    public Expr(ASTNodeTypes type, Token lexeme) {
        super();
        this.type = type;
        this.label = lexeme.getValue();
        this.lexeme = lexeme;
    }
    //Expr -> Expr + 1 | 1 求解左递归
    // a = +1   B = 1
    // A -> BA`   A`->aA`|ε
    //Expr -> 1Expr`      Expr` = +1Expr`|ε=

    //left: E(k) -> E(k) op(k) E(k+1) | E(k+1)
    //right:
    //     E(k) -> E(k+1) E_(k)  这个E_(k)就是 E(k) op(k)可能还有，这些指代不清楚的。
    //     var e  = new Expr();e.left = E(k+1);e.op = op(k) e.right = E_(k)
    //     然后E_(k) = op(k) E(k+1) E_(k) | ε   ,所以      e.right = E(k+1) E_(k) 这里不用出现op到最后一级，就是A`或者说E_(k)的时候才用

    //因为  E(k) -> E(k) op(k) E(k+1)| E(k+1)
    //所以     E_(k) = op(k) E(k+1) E_(k) | ε

    //如果有k和k+1,那么到最里面那一层，肯定是要跳出来的，所以，这个F U 就是定义了怎么跳出来，就是碰到
    //了一个一元表达式或者一个Factor
    //最高优先级
    //就跟刚开始左递归一样肯定要有一个数字先被吃掉，这里对应的就是F，
    // 如果有括号就应该先算括号里面的，那么就是U
    // E(t) -> F E_(k) | U E_(k)
    // E_(t)-> op(t) E(t) E_(t) | ε   这个E(t)就是F或者是E
    //                (F | E)
    /**
     * E(k) = E(k) op(k) E(k+1) | E(k+1)
     * E(t) = E(t) op(t) Factor | Factor
     * E_(t) = Factor E_(t)
     * E_(t) = op(t) Factor E_(t)
     */


    /**
     * 每次先进来，如果有小括号，肯定要先算小括号里面的，所以一进来k一直+1,一直到最后，就是看有没有乘除
     * 啊，还有自增自减的，每次都是到F或者是U，F就是得到数字，U里面就处理了括号和++ --这些，因为这些
     * 东西都没有在优先级表中出现，算是硬编码了吧
     */

    private static ASTNode E(int k, PeekTokenIterator it) throws ParseException {
        if(k < table.size() - 1) {
            return combine( it, () -> E( k+1, it), () -> E_(k, it));
        } else {
            return race(
                    it,
                    () -> combine( it, () -> F(it), () -> E_( k, it)),
                    () -> combine( it, () -> U(it), () -> E_( k, it))
            );
        }
    }

    private static ASTNode E_(int k, PeekTokenIterator it) throws ParseException {
        var token = it.peek();
        var value = token.getValue();

        if(table.get(k).indexOf(value) != -1) {
            var expr = new Expr(ASTNodeTypes.BINARY_EXPR, it.nextMatch(value));
            expr.addChild(combine(it,
                    () -> E(k+1, it),
                    () -> E_(k, it)
            ));
            return expr;

        }
        return null;
    }
    /**
     * E(k) = E(k) op(k) E(k+1) | E(k+1)
     * E(t) = E(t) op(t) Factor | Factor    这个Factor就再带入一下，E(t)
     * E_(t) = Factor E_(t)
     * E_(t) = E(t) E_(t)
     * E_(t) = op(t) E(t) E_(t)
     */
    private static ASTNode U(PeekTokenIterator it) throws ParseException {
        var token = it.peek();
        var value = token.getValue();

        if(value.equals("(")) {
            it.nextMatch("(");
            var expr = E(0, it);
            it.nextMatch(")");
            return expr;
        }
        else if (value.equals("++") || value.equals("--") || value.equals("!")) {
            var t = it.peek();
            it.nextMatch(value);
            Expr unaryExpr = new Expr(ASTNodeTypes.UNARY_EXPR, t);
            unaryExpr.addChild(E(0, it));
            return unaryExpr;
        }
        return null;
    }
    /**
     * E(k) = E(k) op(k) E(k+1) | E(k+1)
     * E(t) = E(t) op(t) Factor | Factor    这个Factor就再带入一下，E(t)
     * E_(t) = Factor E_(t)
     * E_(t) = op(t) Factor E_(t)
     * E_(t) = E(t) E_(t)
     * E_(t) = op(t) E(t) E_(t)
     * 这里就是数字了，直接return了
     */
    private static ASTNode F(PeekTokenIterator it) throws ParseException {
        var factor = Factor.parse(it);
        if(factor == null) {
            return null;
        }
        if(it.hasNext() && it.peek().getValue().equals("(")) {
            return CallExpr.parse(factor,it);
        }
        return factor;
    }
    private static ASTNode combine(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        var a = aFunc.hoc();
        if(a == null) {
            return it.hasNext() ? bFunc.hoc() : null;
        }
        var b = it.hasNext() ? bFunc.hoc() : null;
        if(b == null) {
            return a;
        }

        Expr expr = new Expr(ASTNodeTypes.BINARY_EXPR, b.lexeme);
        expr.addChild(a);
        expr.addChild(b.getChild(0));
        return expr;

    }

    private static ASTNode race(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        if(!it.hasNext()) {
            return null;
        }
        var a = aFunc.hoc();
        if(a != null) {
            return a;
        }
        return bFunc.hoc();
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        return E(0, it);

    }
    /**
     * E(k) = E(k) op(k) E(k+1) | E(k+1)
     * E(t) = E(t) op(t) Factor | Factor
     * E_(t) = Factor E_(t)
     * E_(t) = op(t) Factor E_(t)
     */
}
