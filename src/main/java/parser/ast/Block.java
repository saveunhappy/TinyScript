package parser.ast;

import parser.util.ParseException;
import parser.util.PeekTokenIterator;

public class Block extends Stmt{
    public Block() {
        super(ASTNodeTypes.BLOCK, "block");
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        it.nextMatch("{");
        var block = new Block();
        ASTNode stmt = null;
        //一个语句块中能包含好多东西，如果有，就一直添加比如，public class Test{}这个里面也是语句块，所有方法可以，
        // 如果是方法里面的，可以声明变量，所以这个算是一个最大的，能包含好多个东西
        while( (stmt = Stmt.parseStmt(it)) != null) {
            block.addChild(stmt);
        }
        it.nextMatch("}");
        return block;

    }
}
