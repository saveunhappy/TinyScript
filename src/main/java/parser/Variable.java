package parser;

public class Variable extends Factor{
    public Variable(ASTNode _parent, ASTNodeTypes _type, String _label) {
        super(_parent, ASTNodeTypes.VARIABLE, null);
    }
}
