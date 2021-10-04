package translator.symbol;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;

public class SymbolTable {
    private SymbolTable parent = null;
    private ArrayList<SymbolTable> children;
    private ArrayList<Symbol> symbols;
    //var a = 1;
    //var a = 1 + 2 * 3,那么放到寄存器里面，p0 = 2 * 3,所以tempIndex就是干这个活儿的
    private int tempIndex = 0;
    //var a = 1;
    //var a = 2;
    //var a = 3;
    //就是相对位置
    private int offsetIndex = 0;
    private int level = 0;

    public SymbolTable(){
        this.children = new ArrayList<>();
        this.symbols = new ArrayList<>();
    }

    public void addSymbol(Symbol symbol) {
        this.symbols.add(symbol);
        symbol.setParent(this);
    }
    /*
    var a;
    {
        {
           {
             var b = a;
             在当前括号内，b的offset就是0，如果a没有在最外面定义的话，也是0
                但是a是当前括号 0，加两个括号，跳出所有的括号的那个作用域，就是0+3 = 3

            }
        }
    }
 */
    public Symbol cloneFromSymbolTree(Token lexeme, int layerOffset) {

        var _symbol = this.symbols.stream()
                .filter(x -> x.lexeme.getValue().equals(lexeme.getValue()))
                .findFirst();
        if(!_symbol.isEmpty()) {
            var symbol = _symbol.get().copy();
            symbol.setLayerOffset(layerOffset);
            return symbol;
        }
        if(this.parent != null) {
            //也是递归的，每次往上面找一次，offset+1
            return this.parent.cloneFromSymbolTree(lexeme, layerOffset + 1);
        }
        return null;
    }

    public boolean exists(Token lexeme) {
        //判断这个token是不是在symbol中，如果当前没有找到，就去父节点中找，递归的找，就跟找前驱后继一样
        //如果最终还没有找到，那就是没有，return false
        var _symbol = this.symbols.stream().filter(x -> x.lexeme.getValue().equals(lexeme.getValue())).findFirst();
        if(!_symbol.isEmpty()) {
            return true;
        }
        if(this.parent != null) {
            return this.parent.exists(lexeme);
        }
        return false;
    }

    public Symbol createSymbolByLexeme(Token lexeme) {
        Symbol symbol = null;
        if(lexeme.isScalar()) {
            //看一个这个Symbol是啥类型的，如果是数字，那么就是立即数
            symbol = Symbol.createImmediateSymbol(lexeme);
            this.addSymbol(symbol);
        } else {
            var _symbol = this.symbols.stream().filter(x -> x.getLexeme().getValue().equals(lexeme.getValue())).findFirst();
            if (_symbol.isEmpty()) {
                //这里判断空不空之后，为啥下面还要判断呢？因为可能在之前声明过了啊，当前没有，不代表
                //父节点也没有，如果父节点真的遍历完了还是没有，我们就创建一个，是地址类型的。
                symbol = cloneFromSymbolTree(lexeme, 0);
                if(symbol == null) {
                    //这个就是你创建完一个变量，offsetIndex++
                    symbol = Symbol.createAddressSymbol(lexeme, this.offsetIndex++);
                }
                this.addSymbol(symbol);
            } else {
                //到这里就说明这个变量之前已经声明过了，直接就返回了。
                symbol = _symbol.get();
            }

        }
        return symbol;
    }

    public Symbol createVariable() {
        /*
        var a = 1 +2 * 3;
        p0 = 2 * 3;
        p1 = 1 +p0;
        a = p0 + p1;
         */
        //这里就是创建了一个变量，就是从寄存器的角度来看了，1占据一个寄存器2占据一个寄存器3占据一个寄存器
        var lexeme = new Token(TokenType.VARIABLE, "p" + this.tempIndex ++);
        var symbol = Symbol.createAddressSymbol(lexeme, this.offsetIndex++);
        this.addSymbol(symbol);
        return symbol;
    }

    public void addChild(SymbolTable child) {
        child.parent = this;
        child.level = this.level + 1;
        this.children.add(child);
    }
    /*
    var a;
    {
        {
           {
             var b = a;
            a其实是不占据栈空间的，是占用的静态空间
            }
        }
    }
 */
    public int localSize() {
        return this.offsetIndex;
    }

    public ArrayList<Symbol> getSymbols(){
        return this.symbols;
    }

    public ArrayList<SymbolTable> getChildren(){
        return this.children;
    }


    public void createLabel(String label, Token lexeme) {
        var labelSymbol = Symbol.createLabelSymbol(label, lexeme);
        this.addSymbol(labelSymbol);

    }
}
