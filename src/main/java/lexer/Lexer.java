package lexer;

import common.AlphabetHelper;
import common.PeekIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class Lexer {

    public ArrayList<Token> analyse(PeekIterator<Character> it) throws LexicalException {
        var tokens = new ArrayList<Token>();

        while (it.hasNext()) {
            char c = it.next();

            if (c == 0) {
                break;
            }
            char lookahead = it.peek();

            if (c == ' ' || c == '\n') {
                continue;
            }

            // 删除注释
            if (c == '/') {
                if (lookahead == '/') {
                    //到这里就是注释一行里面的东西都不解析，如果没有下一个了，
                    // 或者读到了换行符，读到换行符，那就不是注释了，可以跳出去了
                    while (true) {
                        if (!it.hasNext() || (c = it.next()) == '\n') break;
                    }
                    continue;
                } else if (lookahead == '*') {
                    /*多行注释
                     *
                     */
                    it.next();//多读一个* 避免/*/通过，
                    // 看下面，当前p是*，但是peek一下，看看后面的，这里必须是同时成立才可以，
                    //这个方法，估计是把源代码全部都得扫一遍
                    boolean valid = false;
                    while (it.hasNext()) {
                        char p = it.next();
                        if (p == '*' && it.peek() == '/') {
                            it.next();
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        throw new LexicalException("comments not match");
                    }
                    continue;
                }
            }

            if (c == '{' || c == '}' || c == '(' || c == ')') {
                tokens.add(new Token(TokenType.BRACKET, c + ""));
                continue;
            }

            if (c == '"' || c == '\'') {
                //这里putBack就是要交给子程序去使用，这个putBack是缓存中和栈中一直在动，不影响这个正常的流
                it.putBack();
                tokens.add(Token.makeString(it));
                continue;
            }

            if (AlphabetHelper.isLetter(c)) {
                it.putBack();
                tokens.add(Token.makeVarOrKeyword(it));
                continue;
            }


            if (AlphabetHelper.isNumber(c)) {
                it.putBack();
                tokens.add(Token.makeNumber(it));
                continue;
            }

            // + - .
            // 3+5,+5,3*-5,3*3.5 ,
            /*
             *这里需要额外判断一下，就是这三个和数字组合开，因为你如果不额外判断，+5就分开了，程序就会认为
             * +是操作数，5是数字，3*-5的话，*是操作数没毛病，因为没有*-这个操作数，不像++ += 但是-5还是要
             * 认为是一个数字，然后3 * 3.5，而且，你从tokens,就是这个ArrayList中获取到的最后一个必须得是操作符
             * ，这样才能有-5啊3.5之类的，要不5就被分割了，小数就被number给解析走了，
             * 这里就是小数前面是操作符才能解析，3*-3.5是怎么解析的呢，还是那句话，注意，每个方法都能拿到完整的流，
             * -3.5如果符合，他可以一直往下流，各个方法都是各司其职，不属于自己的范围了，才放手。
             */
            if ((c == '+' || c == '-' || c == '.') && AlphabetHelper.isNumber(lookahead)) {
                //因为你得看你这个操作数
                var lastToken = tokens.size() == 0 ? null : tokens.get(tokens.size() - 1);
                //如果是n-1呢？那就不能用isNumber了，得用isValue，踩坑了。
                if (lastToken == null || !lastToken.isValue() || lastToken.isOperator()) {
                    it.putBack();
                    tokens.add(Token.makeNumber(it));
                    continue;
                }
            }

            if (AlphabetHelper.isOperator(c)) {
                it.putBack();
                tokens.add(Token.makeOp(it));
                continue;
            }

            throw new LexicalException(c);
        } // end while
        return tokens;
    }

    public ArrayList<Token> analyse(Stream source) throws LexicalException {
        var it = new PeekIterator<Character>(source, (char) 0);
        return this.analyse(it);
    }
    /**
     * 从源代码文件加载并解析
     * @param src
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws LexicalException
     */
    public static ArrayList<Token> fromFile(String src) throws FileNotFoundException, UnsupportedEncodingException, LexicalException {
        var file = new File(src);
        var fileStream = new FileInputStream(file);
        var inputStreamReader = new InputStreamReader(fileStream, "UTF-8");

        var br = new BufferedReader(inputStreamReader);


        /**
         * 利用BufferedReader每次读取一行
         */
        var it = new Iterator<Character>() {
            private String line = null;
            private int cursor = 0;

            private void readLine() throws IOException {
                if(line == null || cursor == line.length()) {
                    line = br.readLine();
                    cursor = 0;
                }
            }
            @Override
            public boolean hasNext() {
                try {
                    readLine();
                    return line != null;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public Character next() {
                try {
                    readLine();
                    return line != null ? line.charAt(cursor++) :null;
                } catch (IOException e) {
                    return null;
                }
            }
        };

        var peekIt = new PeekIterator<Character>(it, '\0');

        var lexer = new Lexer();
        return lexer.analyse(peekIt);

    }

}
