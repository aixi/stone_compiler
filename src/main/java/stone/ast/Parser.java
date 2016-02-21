package stone.ast;

import stone.tokenizer.Lexer;
import stone.tokenizer.Token;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by xi on 16-2-20.
 */
public class Parser {
    public static final String factoryName = "create";

    protected abstract static class Factory {
        protected abstract ASTree make0(Object arg) throws Exception;

        protected ASTree make(Object arg) {
            try {
                return make0(arg);
            }
            catch (IllegalArgumentException e1) {
                throw e1;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2); //this compiler is broken
            }
        }

        protected static Factory get(Class<? extends ASTree> type, Class<?> argType) {
            if (type == null)
                return null;
            try {
                final Method m = type.getMethod(factoryName, new Class<?>[] {argType});
                return new Factory() {
                    @Override
                    protected ASTree make0(Object arg) throws Exception {
                        return (ASTree) m.invoke(null, arg);
                    }
                };
            }
            catch (NoSuchMethodException e) {}

            try {
                final Constructor<? extends ASTree> c = type.getConstructor(argType);
                return new Factory() {
                    @Override
                    protected ASTree make0(Object arg) throws Exception {
                        return c.newInstance(arg);
                    }
                };
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        protected static Factory getForASTList(Class<? extends ASTree> type) {
            Factory f = get(type, List.class);
            if (f == null) {
                f = new Factory() {
                    @Override
                    protected ASTree make0(Object arg) throws Exception {
                        List<ASTree> results = (List<ASTree>) arg;
                        if (results.size() == 1)
                            return results.get(0);
                        else
                            return new ASTList(results);
                    }
                };
            }
            return f;
        }



    }
    protected abstract static class Element {
        protected abstract void parse(Lexer lexer, List<ASTree> res) throws ParseException;
        protected abstract boolean match(Lexer lexer) throws ParseException;
    }

    protected static class Tree extends Element {
        protected Parser parser;

        protected Tree(Parser parser) {
            this.parser = parser;
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> res) {
            try {
                res.add(parser.parse(lexer));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    protected static class OrTree extends Element {
        protected Parser[] parsers;

        protected OrTree(Parser[] parsers) {
            this.parsers = parsers;
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> res) throws ParseException {
            Parser p = choose(lexer);
            if (p == null)
                throw new ParseException(lexer.peek(0));
            else
                res.add(p.parse(lexer));
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return choose(lexer) != null;
        }

        protected Parser choose(Lexer lexer) throws ParseException {
            for (Parser p : parsers)
                if (p.match(lexer))
                    return p;
            return null;
        }

        protected void insert(Parser p) {
            Parser[] newParsers = new Parser[parsers.length + 1];
            newParsers[0] = p;
            System.arraycopy(parsers, 0, newParsers, 1, parsers.length);
            parsers = newParsers;
        }
    }

    protected static class Repeat extends Element {
        protected Parser parser;
        protected boolean onlyOnce;

        protected Repeat(Parser p, boolean once) {
            parser = p;
            onlyOnce = once;
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> res) throws ParseException {
            while (parser.match(lexer)) {
                ASTree t = parser.parse(lexer);
                if (t.getClass() != ASTList.class || t.childrenAmount() > 0)
                    res.add(t);
                if (onlyOnce)
                    break;
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    protected static abstract class AToken extends Element {
        protected Factory factory;

        protected AToken(Class<? extends ASTLeaf> type) {
            if (type == null)
                type = ASTLeaf.class;
            factory = Factory.get(type, Token.class);
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> res) throws ParseException {
            Token t = lexer.read();
            if (test(t)) {
                ASTree leaf = factory.make(t);
                res.add(leaf);
            }
            else
                throw new ParseException(t);
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return test(lexer.peek(0));
        }

        protected abstract boolean test(Token t);
    }

    protected static class IdToken extends AToken {
        HashSet<String> reversed;

        protected IdToken(Class<? extends ASTLeaf> type, HashSet<String> r) {
            super(type);
            reversed = r != null ? r : new HashSet<String>();
        }

        protected boolean test(Token t) {
            return t.isIdentifier() && !reversed.contains(t.getText());
        }
    }

    protected static class NumToken extends AToken {
        protected NumToken(Class<? extends ASTLeaf> type) {
            super(type);
        }

        protected boolean test(Token t) {
            return t.isNumber();
        }
    }

    protected static class StrToken extends AToken {
        protected StrToken(Class<? extends ASTLeaf> type) {
            super(type);
        }

        protected boolean test(Token t) {
            return t.isString();
        }
    }

    protected static class Leaf extends Element {
        protected String[] tokens;

        protected Leaf(String[] pat) {
            tokens = pat;
        }

        protected void find(List<ASTree> res, Token t) {
            res.add(new ASTLeaf(t));
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> res) throws ParseException {
            Token t = lexer.read();
            if (t.isIdentifier()) {
                for (String token : tokens) {
                    if (token.equals(t.getText())) {
                        find(res, t);
                        return;
                    }
                }
            }
            if (tokens.length > 0)
                throw new ParseException(tokens[0] + " expected ", t);
            else
                throw new ParseException(t);
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            Token t = lexer.peek(0);
            if (t.isIdentifier()) {
                for (String token : tokens) {
                    if (token.equals(t.getText())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    protected static class Skip extends Leaf {
        protected Skip(String[] t) {
            super(t);
        }

        @Override
        protected void find(List<ASTree> res, Token t) {}
    }

    protected static class Precedence {
        int value;
        boolean leftAssoc; //left associative

        public Precedence(int value, boolean leftAssoc) {
            this.leftAssoc = leftAssoc;
            this.value = value;
        }
    }

    public static class Operators extends HashMap<String, Precedence> {
        public static boolean LEFT = true;
        public static boolean RIGHT = false;

        public void add(String name, int prec, boolean leftAssoc) {
            put(name, new Precedence(prec, leftAssoc));
        }
    }

    protected static class Expr extends Element {
        protected Factory factory;
        protected Operators ops;
        protected Parser factor;

        protected Expr(Class<? extends ASTree> type, Parser exp, Operators map) {
            factory = Factory.getForASTList(type);
            ops = map;
            factor = exp;
        }

        private ASTree doShift(Lexer lexer, ASTree left, int prec) throws ParseException {
            ArrayList<ASTree> list = new ArrayList<ASTree>();
            list.add(left);
            list.add(new ASTLeaf(lexer.read()));
            ASTree right = factor.parse(lexer);
            Precedence next;
            while ((next = nextOperator(lexer)) != null && rightIsExpr(prec, next)) {
                right = doShift(lexer, right, next.value);
            }
            list.add(right);
            return factory.make(list);
        }

        private Precedence nextOperator(Lexer lexer) throws ParseException {
            Token t = lexer.peek(0);
            if (t.isIdentifier())
                return ops.get(t.getText());
            else
                return null;
        }

        private static boolean rightIsExpr(int prec, Precedence nextPrec) {
            if (nextPrec.leftAssoc)
                return prec < nextPrec.value;
            else
                return prec <= nextPrec.value;
        }

        @Override
        public void parse(Lexer lexer, List<ASTree> res) throws ParseException {
            ASTree right = factor.parse(lexer);
            Precedence precedence;
            while ((precedence = nextOperator(lexer)) != null) {
                right = doShift(lexer, right, precedence.value);
            }
            res.add(right);
        }

        @Override
        public boolean match(Lexer lexer) throws ParseException {
            return factor.match(lexer);
        }
    }

    protected List<Element> elements;
    protected Factory factory;

    public Parser(Class<? extends ASTree> type) {
        reset(type);
    }

    protected Parser(Parser p) {
        elements = p.elements;
        factory = p.factory;
    }

    public ASTree parse(Lexer lexer) throws ParseException {
        ArrayList<ASTree> results = new ArrayList<ASTree>();
        for (Element e : elements) {
            e.parse(lexer, results);
        }
        return factory.make(results);
    }

    protected boolean match(Lexer lexer) throws ParseException {
        if (elements.size() == 0)
            return true;
        else {
            Element e = elements.get(0);
            return e.match(lexer);
        }
    }

    public Parser reset() {
        elements = new ArrayList<Element>();
        return this;
    }

    public Parser reset(Class<? extends ASTree> type) {
        elements = new ArrayList<Element>();
        factory = Factory.getForASTList(type);
        return this;
    }

    public static Parser rule() {
        return rule(null);
    }

    public static Parser rule(Class<? extends ASTree> type) {
        return new Parser(type);
    }

    public Parser number() {
        return number(null);
    }

    public Parser number(Class<? extends ASTLeaf> type) {
        elements.add(new NumToken(type));
        return this;
    }

    public Parser identifier(HashSet<String> reserved) {
        return identifier(null, reserved);
    }

    public Parser identifier(Class<? extends ASTLeaf> type, HashSet<String> reserved) {
        elements.add(new IdToken(type, reserved));
        return this;
    }

    public Parser string() {
        return string(null);
    }

    public Parser string(Class<? extends ASTLeaf> type) {
        elements.add(new StrToken(type));
        return this;
    }

    public Parser token(String... pat) {
        elements.add(new Skip(pat));
        return this;
    }

    public Parser sep(String... pat) {
        elements.add(new Skip(pat));
        return this;
    }

    public Parser ast(Parser p) {
        elements.add(new Tree(p));
        return this;
    }

    public Parser or(Parser... p) {
        elements.add(new OrTree(p));
        return this;
    }

    public Parser maybe(Parser p) {
        Parser p2 = new Parser(p);
        p2.reset();
        elements.add(new OrTree(new Parser[] {p, p2}));
        return this;
    }

    public Parser optional(Parser p) {
        elements.add(new Repeat(p, true));
        return this;
    }

    public Parser repeat(Parser p) {
        elements.add(new Repeat(p, true));
        return this;
    }

    public Parser expression(Class<? extends ASTree> type, Parser subexp, Operators operators) {
        elements.add(new Expr(type, subexp, operators));
        return this;
    }

    public Parser insertChoice(Parser p) {
        Element e = elements.get(0);
        if (e instanceof OrTree)
            ((OrTree) e).insert(p);
        else {
            Parser otherwise = new Parser(this);
            reset(null);
            or(p, otherwise);
        }
        return this;
    }
}