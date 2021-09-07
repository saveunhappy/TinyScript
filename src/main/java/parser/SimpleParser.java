package parser;

import parser.ast.ASTNode;
import parser.ast.ASTNodeTypes;
import parser.ast.Expr;
import parser.ast.Scalar;
import parser.util.ParseException;
import parser.util.PeekTokenIterator;

public class SimpleParser {
    //Expr -> digit + Expr | digit
    //digit -> 0|1|2|3|4|5|6|7|...|9|

    /**
     * 这个递归个人理解为就是直到找到不再是数字加表达式了，条件上说就是没有下一个元素了，直到找到两个数字相加的
     * 然后没有下一个了，作为一个expr返回，然后接二连三的，就都能返回了，递归结束，拼接成了抽象语法树
     * @param it
     * @return
     * @throws ParseException
     */
    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        //每个表达式都是独立的，都是默认没有父节点
        var expr = new Expr(null);
        //这里就看成1+2，expr就是+,然后一直往下流，scalar就是左右节点，
        // 当然，直到最后，返回回去的时候，才能拿到两边都是数字，就是!it.hasNext()这行代码。
        var scalar = new Scalar(expr, it);
        //base condition
        if (!it.hasNext()) {
            return scalar;
        }
        // 就是那个加号+左边表达式，右边表达式，加号连接，
        expr.setLexeme(it.peek());
        //这里必须是加号，如果不是，就说明抽象语法树不对了。
        //必须1后面必须是+不能是数组，而且，上面peek了，这里必须要消费掉它。
        it.nextMatch("+");

        expr.setLabel("+");
        expr.addChild(scalar);
        //有了加号了，那就是二元的表达式，就是BINARY的，i++就是一元的，就是UNARY，上面设置了，这里没有用链表，
        // 就是用数组模拟了一下，左边的是数字，右边的是表达式，那么就是0是数字，1是表达式
        expr.setType(ASTNodeTypes.BINARY_EXPR);
        var rightNode = parse(it);
        expr.addChild(rightNode);
        return expr;
    }

}
