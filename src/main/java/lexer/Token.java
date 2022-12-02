package lexer;

import common.AlphabetHelper;
import common.PeekIterator;

public class Token {
    TokenType _type;
    String _value;

    public TokenType getType() {
        return _type;
    }

    public Token(TokenType _type, String _value) {
        this._type = _type;
        this._value = _value;
    }

    @Override
    public String toString() {
        return String.format("type %s, value %s", _type, _value);
    }

    public boolean isVariable() {
        return _type == TokenType.VARIABLE;
    }

    public boolean isScalar() {
        return _type == TokenType.INTEGER || _type == TokenType.BOOLEAN ||
                _type == TokenType.STRING || _type == TokenType.FLOAT;
    }

    public void set_type(TokenType _type) {
        this._type = _type;
    }

    public String getValue() {
        return _value;
    }

    public void set_value(String _value) {
        this._value = _value;
    }

    /**
     * 提取变量或者关键字
     *
     * @param it
     * @return
     */
    public static Token makeVarOrKeyword(PeekIterator<Character> it) {
        //这个就是在一个长串中去(有空格)提取一个关键字或者变量，比如 "if abc"
        //他是一直在循环的，每次提取一个，看看是不是literal,就是字母数字，下划线，letter是只有字母，
        //注意，提取，如果解析到空格，空格肯定不是数字，字母下划线，就跳出循环了啊，然后就去那个set里面去
        //进行比对，看看有没有，有就是关键字了，如果接着想往下呢？这里已经进去了，那就再调用一下next,
        //然后重新调用这个方法，就是从abc那里开始了，不符合，就是变量
        /*
        *  2022-12-2复习
        *  这其实是一个状态机，因为变量可以是aaa  paso, 之类的，所以你要去一个字符一个字符的去累加，
        *  String s = "";就是定义一个开始的，进行拼接的，所有的数字啊之类的和String进行拼接就会变成一个String
        * while (it.hasNext()) 这个没啥好说的，就是一直往下走
        * var lookahead = it.peek();就是一直往下走，碰到不属于自己的，就退出了。
        */
        String s = "";
        while (it.hasNext()) {

            var lookahead = it.peek();
            if (AlphabetHelper.isLiteral(lookahead)) {
                s += lookahead;
            } else {
                break;
            }
            it.next();
        }
        //判断关键词OR变量
        if (Keywords.isKeyword(s)) {
            return new Token(TokenType.KEYWORD, s);
        }
        if (s.equals("true") || s.equals("false")) {
            return new Token(TokenType.BOOLEAN, s);
        }
        return new Token(TokenType.VARIABLE, s);
    }

    public static Token makeString(PeekIterator<Character> it) throws LexicalException {
        String s = "";
        int state = 0;
        while (it.hasNext()) {
            char c = it.next();
            switch (state) {
                //刚开始进来肯定得是单引号或者是双引号，然后进入case 1和case 2，
                // 如果再匹配到单引号或者双引号那就结束了,没有的话，就把其他的字符一直用StringBuilder一直拼接
                case 0:
                    if (c == '\"') {
                        state = 1;
                    } else {
                        state = 2;
                    }
                    s += c;
                    break;
                case 1:
                    if (c == '"') {
                        return new Token(TokenType.STRING, s.toString() + c);
                    } else {
                        s += c;
                    }
                    break;
                case 2:
                    if (c == '\'') {
                        return new Token(TokenType.STRING, s.toString() + c);
                    } else {
                        s += c;
                    }
                    break;
            }
        }
        throw new LexicalException("unexpected error");
    }

    public static Token makeOp(PeekIterator<Character> it) throws LexicalException {
        /**
         * 这里和那个makeString差不多，刚开始也是看那些符号，+*-/之类的，然后state就变了
         * 再次循环，++，+=都是操作符，但是如果是一个数字呢？那就不能动了，就得放回去，这里就是已经
         * 提取到操作符了，下次提取数字的时候调用next就可以找到了。
         */
        int state = 0;

        while (it.hasNext()) {
            var lookahead = it.next();

            switch (state) {
                case 0:
                    switch (lookahead) {
                        case '+':
                            state = 1;
                            break;
                        case '-':
                            state = 2;
                            break;
                        case '*':
                            state = 3;
                            break;
                        case '/':
                            state = 4;
                            break;
                        case '>':
                            state = 5;
                            break;
                        case '<':
                            state = 6;
                            break;
                        case '=':
                            state = 7;
                            break;
                        case '!':
                            state = 8;
                            break;
                        case '&':
                            state = 9;
                            break;
                        case '|':
                            state = 10;
                            break;
                        case '^':
                            state = 11;
                            break;
                        case '%':
                            state = 12;
                            break;
                        case ',':
                            return new Token(TokenType.OPERATOR, ",");
                        case ';':
                            return new Token(TokenType.OPERATOR, ";");
                    }
                    break;
                case 1:
                    if (lookahead == '+') {
                        return new Token(TokenType.OPERATOR, "++");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "+=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "+");
                    }
                case 2:
                    if (lookahead == '-') {
                        return new Token(TokenType.OPERATOR, "--");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "-=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "-");
                    }
                case 3:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "*=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "*");
                    }
                case 4:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "/=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "/");
                    }
                case 5:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, ">=");
                    } else if (lookahead == '>') {
                        return new Token(TokenType.OPERATOR, ">>");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, ">");

                    }
                case 6:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "<=");
                    } else if (lookahead == '<') {
                        return new Token(TokenType.OPERATOR, "<<");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "<");
                    }
                case 7:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "==");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "=");
                    }
                case 8:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "!=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "!");
                    }
                case 9:
                    if (lookahead == '&') {
                        return new Token(TokenType.OPERATOR, "&&");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "&=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "&");
                    }
                case 10:
                    if (lookahead == '|') {
                        return new Token(TokenType.OPERATOR, "||");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "|=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "|");
                    }
                case 11:
                    if (lookahead == '^') {
                        return new Token(TokenType.OPERATOR, "^^");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "^=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "^");
                    }
                case 12:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "%=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "%");
                    }
            }

        }
        throw new LexicalException("Unexpected error");

    }

    public static Token makeNumber(PeekIterator<Character> it) throws LexicalException {
        /**为什么这里还是要peek?你用完了之后，还是要it.next()，那这里直接char lookahead = it.next();不行吗?
         * 不行，为啥，因为如果能走到it.next那，说明前面是符合规则的，后面可能也是符合规则的，还没有匹配到
         * 结束的东西，就是边界，因为如果你要是结束了，那就直接return new Token(TokenType.INTEGER, s);或者FLOAT了，
         * 就不会走到最后了，直接break了，所以，和单个测试不一样，你测是不是数字啊，是不是操作数啊，直接后面的不管了
         * 这里不行，你要是直接消费了，后面就拿不到了，就会少东西，所以一直是peek，保证你是能往下走，才去调用next消费
         * 掉这个东西
         */
        String s = "";

        // 具体state的定义请查看状态机
        int state = 0;

        while (it.hasNext()) {
            char lookahead = it.peek();
//            char lookahead = it.next();

            switch (state) {
                case 0:
                    if (lookahead == '0') {
                        state = 1;
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '+' || lookahead == '-') {
                        state = 3;
                    } else if (lookahead == '.') {
                        state = 5;
                    }
                    break;
                case 1:
                    if (lookahead == '0') {
                        state = 1;
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        return new Token(TokenType.INTEGER, s);
                    }
                    break;
                case 2:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        return new Token(TokenType.INTEGER, s);
                    }
                    break;
                case 3:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 5;
                    } else {
                        throw new LexicalException(lookahead);
                    }
                    break;
                case 4:
                    if (lookahead == '.') {
                        throw new LexicalException(lookahead);
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 20;
                    } else {
                        return new Token(TokenType.FLOAT, s);
                    }
                    break;
                case 5:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 20;
                    } else {
                        throw new LexicalException(lookahead);
                    }
                    break;
                case 20:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 20;
                    } else if (lookahead == '.') {
                        throw new LexicalException(lookahead);
                    } else {
                        return new Token(TokenType.FLOAT, s);
                    }

            }
            it.next();
            s += lookahead;
        }
        throw new LexicalException("Unexpected error");
    }

    public boolean isNumber() {
        return this._type == TokenType.INTEGER || this._type == TokenType.FLOAT;
    }

    public boolean isOperator() {
        return this._type == TokenType.OPERATOR;
    }

    public boolean isType() {
        return this._value.equals("bool")
                || this._value.equals("int")
                || this._value.equals("float")
                || this._value.equals("void")
                || this._value.equals("string");

    }

    public boolean isValue() {
        return isVariable() || isScalar();
    }
}
