package parser;

public class AssignStmt extends Stmt{
    public AssignStmt(ASTNode _parent, ASTNodeTypes _type, String _label) {
        super(_parent, ASTNodeTypes.ASSIGN_STMT, "assign");
    }
}
