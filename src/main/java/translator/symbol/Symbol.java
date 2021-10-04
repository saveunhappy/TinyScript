package translator.symbol;

import lexer.Token;
/**
 * 一个值或者变量的集合体
 */
public class Symbol {

    SymbolTable parent;
    Token lexeme;
    String label;
    //这个是相对位置，每一个括号里面，或者是叫block里面，offset都是从0开始的。
    int offset;
    int layerOffset = 0;
    /*
        var a;
        {
            {
                var b = a;这个时候a的layerOffset就是2
            }
        }
     */
    SymbolType type;
    public Symbol(SymbolType type){
        this.type = type;
    }

    public static Symbol createAddressSymbol(Token lexeme, int offset){
        var symbol = new Symbol(SymbolType.ADDRESS_SYMBOL);
        symbol.lexeme = lexeme;
        symbol.offset = offset;
        return symbol;
    }

    public static Symbol createImmediateSymbol(Token lexeme){
        //常量，任何地方都能直接用，存到静态区了，所以不需要偏移量
        var symbol = new Symbol(SymbolType.IMMEDIATE_SYMBOL);
        symbol.lexeme = lexeme;
        return symbol;
    }

    public static Symbol createLabelSymbol(String label, Token lexeme) {
        //就是变成汇编指令了，有goto的那个东西，跳转到哪个位置
        var symbol = new Symbol(SymbolType.LABEL_SYMBOL);
        symbol.label = label;//这个标签就是表示要跳转到哪个位置
        symbol.lexeme = lexeme;
        return symbol;
    }


    public Symbol copy() {
        var symbol = new Symbol(this.type);
        symbol.lexeme = this.lexeme;
        symbol.label = this.label;
        symbol.offset = this.offset;
        symbol.layerOffset = this.layerOffset;
        symbol.type = this.type;
        return symbol;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public SymbolType getType(){
        return this.type;
    }

    @Override
    public String toString() {
        if(this.type == SymbolType.LABEL_SYMBOL){
            return this.label;
        }
        return lexeme.getValue();
    }

    public void setLexeme(Token lexeme) {
        this.lexeme = lexeme;
    }

    public int getOffset() {
        return this.offset;
    }

    public Token getLexeme() {
        return this.lexeme;
    }

    public void setLayerOffset(int offset) {
        this.layerOffset = offset;
    }

    public int getLayerOffset(){
        return this.layerOffset;
    }

    public String getLabel() {
        return this.label;
    }
}
