package parser.ast;

import parser.util.PeekTokenIterator;

public class Scalar extends Factor{
    public Scalar(ASTNode _parent, PeekTokenIterator it) {
        super(_parent, it);//目前理解为数值，就是1 2 3 这样的
    }
}
