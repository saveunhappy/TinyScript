package parser;

public class Scalar extends Factor{
    public Scalar(ASTNode _parent, ASTNodeTypes _type, String _label) {
        super(_parent, ASTNodeTypes.SCALAR, null);//目前还不知道到底是变量啊，还是常量啊，还是bool
    }
}
