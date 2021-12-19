package parser.ast;

import lexer.Token;
import parser.util.ExprHOF;
import parser.util.ParseException;
import parser.util.PeekTokenIterator;
import parser.util.PriorityTable;

import java.util.Objects;

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
        // 刚开始还是一直进来，一直++操作，看符号优先级表，如果发现超过了表，
        //那么就应该是最后的了，是一个数字，Factor，或者是一个一元表达式括号，
        // 直接就找到最后，找最高优先级的。

        if (k < table.size() - 1) {
            //这里首先进去的是combine,里面的参数一个是E(),E_(),算是策略模式吧，就是如果A是空就返回B
            //如果B是空就返回A，可能会有疑惑啊，A是空不就直接返回了吗？还能轮到B？这个是递归的，里面
            //会一直往下走，这里是E(),下面还有一个E_()呢，一直往下走呢，如果A和B都不是空，就能组成一个
            //表达式了，这个可能就是最终combine的结果，把A和B组合在一起。
            //E(k) -> E(k+1) E_(k)
            return combine(it,
                    () -> E(k + 1, it),
                    () -> E_(k, it));
        } else {
            //一直递归的k+1,超过了索引就不适用了，就得到这个里面，这个就是到最深处了，是数字。1-9
            //或者说是一元表达式
            // E(t) -> F E_(k) | U E_(k)

            return race(
                    it,
                    () -> combine(it, () -> U(it), () -> E_(k, it)),

                    () -> combine(it, () -> F(it), () -> E_(k, it))
            );
        }
    }

    private static ASTNode E_(int k, PeekTokenIterator it) throws ParseException {
        var token = it.peek();
        var value = token.getValue();
        // E_(k) = op(k) E(k+1) E_(k) | ε,因为op有了，所以执行下面的啊，E(k+1),_E(k) != <= >= 这个是从那里面取到的，nextMatch得符合
        //就是看你这里面有没有操作符了，没有就返回null。
        if (table.get(k).contains(value)) {
            Expr expr = new Expr(ASTNodeTypes.BINARY_EXPR, it.nextMatch(value));
            //到这里，又开始递归了，因为E_(k) = op(k) E(k+1) E_(k)
            //如果有child的话，再去递归的查看，E(k+1) E_(k)就是这俩的合并
            expr.addChild(Objects.requireNonNull(combine(it,
                    () -> E(k + 1, it),
                    () -> E_(k, it)
            )));
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
        Token token = it.peek();
        var value = token.getValue();

        if (value.equals("(")) {
            it.nextMatch("(");
            //如果是左括号，就再重新走一遍逻辑，重新解析一遍，然后到最后的右括号，
            // 就会返回一个BRACKET，也是ASTNode类型的然后调用next的时候去stackPutBack中
            //找到这个)，那么就匹配成功了
            //
            var expr = E(0, it);
            //这个上面那个expr又去递归了，然后经过一系列的，返回一个expr,因为解析到1 * 2 是a
            // 还有 !=7 是b，已经是一个表达式了，那这里刚开始是一个(，这里就得要求下一个是右括号。
            it.nextMatch(")");
            return expr;
        } else if (value.equals("++") || value.equals("--") || value.equals("!")) {
            //这里知道你是一元操作符了，先peek一下，然后，得消费掉啊，那就调用nextMatch
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
        //Factor,就是数字，到这的，不是变量就是数字
        var factor = Factor.parse(it);
        if (factor == null) {
            return null;
        }
        if (it.hasNext() && it.peek().getValue().equals("(")) {
            return CallExpr.parse(factor, it);
        }
        return factor;
    }

    private static ASTNode combine(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        //目前先理解为： 1 + (2 + 3 * 4)
        //E(k) -> E(k+1)就是后面的(2+3*4),如果这个是空了，就不用了继续递归了，如果有，那就会去弄(3*4)
        //E_(k) A` -> aA`|ε,这个也可能是空
        //如果A是空，就返回B，如果B是空，就返回A，如果都不是空，那么就是创建一个表达式了
        //上面可能有误解
        //这个其实也是个递推式，就是看看两个返回谁，a是空返回b，b是空返回a，因为是递归的，说不定
        //哪个方法就出栈了，一直递归
        var a = aFunc.hoc();
        if (a == null) {
            return it.hasNext() ? bFunc.hoc() : null;
        }
        var b = it.hasNext() ? bFunc.hoc() : null;
        if (b == null) {
            return a;
        }

        //创建表达式
        Expr expr = new Expr(ASTNodeTypes.BINARY_EXPR, b.lexeme);
        //E(k)
        expr.addChild(a);
        //为什么这里是b的child？因为b是E_就是能一直递归的，这里就在E_()添加过一次，获取当然也是从0获取
        /*
         expr.addChild(Objects.requireNonNull(combine(it,
                    () -> E(k + 1, it),
                    () -> E_(k, it)
            )));
         */
        expr.addChild(b.getChild(0));
        return expr;
    }

    private static ASTNode race(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        //竞争关系，谁再前面，谁如果不是空了就返回谁，就是你只能是个数字或者是个一元运算符
        if (!it.hasNext()) {
            return null;
        }
        var a = aFunc.hoc();
        if (a != null) {
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
