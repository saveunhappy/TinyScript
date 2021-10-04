package translator.symbol;

import lexer.Token;
import lexer.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTest {

    @Test
    public void symbolTable(){
        var symbolTable = new SymbolTable();
        symbolTable.createLabel("L0", new Token(TokenType.VARIABLE, "foo"));
        symbolTable.createVariable();
        //前面声明过了，foo，这里找，就是一直往父节点找的那个，当然能找到了，所以localSize就是创建时候的1，没有增加。
        symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "foo"));
        assertEquals(1, symbolTable.localSize());
    }

    @Test
    public void symbolTableChain() {
        var symbolTable = new SymbolTable();
        symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "a"));

        var childTable = new SymbolTable();
        symbolTable.addChild(childTable);

        var childChildTable = new SymbolTable();
        childTable.addChild(childChildTable);
        //这个就是声明了一个变量，然后childTable和childChildTable就当成两个block，就是下面那两个花括号
        //本来是没有a的，但是新建的这两个变成最外面函数的子节点了，那么子节点递归的向上找，当然能找到了。
      /*
        var a;
        {
            {
                var b = a;
            }
        }
     */
        assertEquals(true, childChildTable.exists(new Token(TokenType.VARIABLE, "a")));
        assertEquals(true, childTable.exists(new Token(TokenType.VARIABLE, "a")));

    }

    @Test
    public void symbolOffset() {

        var symbolTable = new SymbolTable();


        symbolTable.createSymbolByLexeme(new Token(TokenType.INTEGER, "100"));
        var symbolA = symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "a"));
        var symbolB = symbolTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "b"));


        var childTable = new SymbolTable();
        symbolTable.addChild(childTable);
        var anotherSymbolB = childTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "b"));
        var symbolC = childTable.createSymbolByLexeme(new Token(TokenType.VARIABLE, "c"));
        //首先常数100不在这个变量里面，不占据占空间
        //然后a先进来，索引从0开始，就是0
        assertEquals(0, symbolA.getOffset());
        //接下来就是b，就是1
        assertEquals(1, symbolB.getOffset());
        //因为b前面有了，a是0，b是1，这里去父节点直接就找到了，进行了一个深拷贝，所以offset是1
        assertEquals(1, anotherSymbolB.getOffset());
        //这个是在当前child里面没有找到，往上面找了一层就找到了，所以是1。
        assertEquals(1, anotherSymbolB.getLayerOffset());
        //c没有，直接就创建了，就是从0开始的。
        assertEquals(0, symbolC.getOffset());
        //往上肯定也找不到啊，那当然是0
        assertEquals(0, symbolC.getLayerOffset());



    }

}