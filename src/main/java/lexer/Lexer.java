package lexer;

import common.AlphabetHelper;
import common.PeekIterator;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Lexer {
    /**
     * 这个就是一个综合的了，提取字符串的，操作数的，数字的，每个方法都能获取到完整的流，一直解析到不属于自己的
     * 然后return出去，添加到list中去，然后字符串再从上一次解析完的个地方继续往下流，因为你不知道下一个是谁解析的，
     * 所以你一直要putBack，如果不是自己能解析了的，也不至于流失了字符，然后，+ - .那里，你得看一看，+5,3*-3.5
     * 如果当前流的前面是一个操作符，后面是一个数字，那就不能简单的解析成字符了，否则*是操作数，-也是一个操作数，
     * 所以这里要额外处理一下，那你怎么能判断呢，还需要lookahead，往后多看一个字符，如果你当前是 + - .可以往
     * 后面看看是不是数字，具体的，再不懂，debug看
     * @param it
     * @return
     * @throws LexicalException
     */
    public ArrayList<Token> analyse(PeekIterator<Character> it) throws LexicalException {
        var tokens = new ArrayList<Token>();

        while(it.hasNext()) {
            char c = it.next();

            if(c == 0) {
                break;
            }
            char lookahead = it.peek();

            if(c == ' ' || c == '\n') {
                continue;
            }

            // 删除注释
            if(c == '/') {
                if(lookahead == '/') {
                    while(it.hasNext() && (c = it.next()) != '\n') {};
                    continue;
                }
                else if(lookahead == '*') {
                    it.next();//多读一个* 避免/*/通过
                    boolean valid = false;
                    while(it.hasNext()) {
                        char p = it.next();
                        if(p == '*' && it.peek() == '/') {
                            it.next();
                            valid = true;
                            break;
                        }
                    }
                    if(!valid) {
                        throw new LexicalException("comments not match");
                    }
                    continue;
                }
            }

            if(c == '{' || c == '}' || c == '(' || c == ')') {
                tokens.add(new Token(TokenType.BRACKET, c+""));
                continue;
            }

            if(c == '"' || c == '\'') {
                //这里putBack就是要交给子程序去使用，这个putBack是缓存中和栈中一直在动，不影响这个正常的流
                it.putBack();
                tokens.add(Token.makeString(it));
                continue;
            }

            if(AlphabetHelper.isLetter(c)) {
                it.putBack();;
                tokens.add(Token.makeVarOrKeyword(it));
                continue;
            }


            if(AlphabetHelper.isNumber(c)) {
                it.putBack();
                tokens.add(Token.makeNumber(it));
                continue;
            }

            // + - .
            // 3+5,+5,3*-5,3*3.5
            /*
             *这里需要额外判断一下，就是这三个和数字组合开，因为你如果不额外判断，+5就分开了，程序就会认为
             * +是操作数，5是数字，3*-5的话，*是操作数没毛病，因为没有*-这个操作数，不像++ += 但是-5还是要
             * 认为是一个数字，然后3 * 3.5，而且，你从tokens,就是这个ArrayList中获取到的最后一个必须得是操作符
             * ，这样才能有-5啊3.5之类的，要不5就被分割了，小数就被number给解析走了，
             * 这里就是小数前面是操作符才能解析，3*-3.5是怎么解析的呢，还是那句话，注意，每个方法都能拿到完整的流，
             * -3.5如果符合，他可以一直往下流，各个方法都是各司其职，不属于自己的范围了，才放手。
             */
            if((c == '+' || c == '-' || c == '.') && AlphabetHelper.isNumber(lookahead)) {
                //因为你得看你这个操作数
                var lastToken = tokens.size() == 0 ? null : tokens.get(tokens.size() - 1);

                if(lastToken == null || !lastToken.isNumber() || lastToken.isOperator()) {
                    it.putBack();
                    tokens.add(Token.makeNumber(it));
                    continue;
                }
            }

            if(AlphabetHelper.isOperator(c)) {
                it.putBack();
                tokens.add(Token.makeOp(it));
                continue;
            }

            throw new LexicalException(c);
        } // end while
        return tokens;
    }
    public ArrayList<Token> analyse(Stream source) throws LexicalException {
        var it = new PeekIterator<Character>(source, (char)0);
        return this.analyse(it);
    }

}
