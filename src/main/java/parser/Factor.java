package parser;

/**
 * 就是计算，不管是布尔或者是变量，或者是数字
 */
public abstract class Factor extends ASTNode{
    public Factor(ASTNode _parent, ASTNodeTypes _type, String _label) {
        super(_parent, _type, _label);
    }
}
