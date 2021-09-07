package parser.ast;

import lexer.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    /*树*/
    protected ArrayList<ASTNode> children = new ArrayList<>();
    protected ASTNode parent;

    /*关键信息*/
    protected Token lexeme;//词法单元  Integer 123，String "hello"
    protected String label;//备注(标签)  整形         字符串
    protected ASTNodeTypes type;//类型，if，赋值，for


    public ASTNode(ASTNode _parent){
        this.parent = _parent;
    }
    public ASTNode(ASTNode _parent,ASTNodeTypes _type,String _label){
        this.parent = _parent;
        this.type = _type;
        this.label = _label;
    }

    public ASTNode getChild(int index){
        return this.children.get(index);
    }
    public void addChild(ASTNode node){
        children.add(node);
    }

    public Token getLexeme() {
        return lexeme;
    }

    public void setChildren(ArrayList<ASTNode> children) {
        this.children = children;
    }

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public void setLexeme(Token lexeme) {
        this.lexeme = lexeme;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ASTNodeTypes getType() {
        return type;
    }

    public void setType(ASTNodeTypes type) {
        this.type = type;
    }

    public List<ASTNode> getChildren(){
        return children;
    }

    public void print(int indent) {
        if(indent == 0) {
            System.out.println("print:" + this);
        }

        System.out.println(StringUtils.leftPad(" ", indent *2) + label);
        for(var child : children) {
            child.print(indent + 1);
        }
    }
}
