package stone.interpreter;

import javassist.gluonj.Reviser;
import stone.ast.*;
import stone.tokenizer.StoneException;
import stone.tokenizer.Token;
import java.util.List;

/**
 * Created by xi on 16-2-21.
 */
public class BasicEvaluator {
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    @Reviser
    public static abstract class ASTreeEx extends ASTree {
        public abstract Object eval(Environment env);
    }

    @Reviser
    public static class ASTListEx extends ASTList {
        public ASTListEx(List<ASTree> c) {
            super(c);
        }

        public Object eval(Environment env) {
            throw new StoneException("cannot eval: " + toString(), this);
        }
    }

    @Reviser
    public static class ASTLeafEx extends ASTLeaf {
        public ASTLeafEx(Token t) {
            super(t);
        }

        public Object eval(Environment env) {
            throw new StoneException("cannot eval: " + toString(), this);
        }
    }

    @Reviser
    public static class NumberEx extends NumberLiteral {
        public NumberEx(Token t) {
            super(t);
        }

        public Object eval(Environment env) {
            return value();
        }
    }

    @Reviser
    public static class StringEx extends StringLiteral {
        public StringEx(Token t) {
            super(t);
        }

        public Object eval(Environment env) {
            return getValue();
        }
    }

    @Reviser
    public static class IdentifierEx extends Identifier {
        public IdentifierEx(Token t) {
            super(t);
        }

        public Object eval(Environment env) {
            Object value = env.get(getName());
            if (value == null)
                throw new StoneException("undefined name: " + getName(), this);
            else
                return value;
        }
    }

    @Reviser
    public static class NegativeEx extends NegativeExpr {
        public NegativeEx(List<ASTree> c) {
            super(c);
        }

        public Object eval(Environment env) {
            Object value = ((ASTreeEx) getOperand()).eval(env);
            if (value instanceof Integer)
                return new Integer(-((Integer)value).intValue());
            else
                throw new StoneException("bad type for -", this);
        }
    }

    @Reviser
    public static class BinaryExpressionEx extends BinaryExpression {
        public BinaryExpressionEx(List<ASTree> children) {
            super(children);
        }

        protected Object computeAssign(Environment env, Object rValue) {
            ASTree tree = left();
            if (tree instanceof Identifier) {
                env.put(((Identifier)tree).getName(), rValue);
                return rValue;
            }
            else
                throw new StoneException("bad assignment", this);
        }

        protected Object computeNumber(Integer left, String op, Integer right) {
            int a = left.intValue();
            int b = left.intValue();

            if ("+".equals(op))
                return a + b;
            else if ("-".equals(op))
                return a - b;
            else if ("*".equals(op))
                return a * b;
            else if ("/".equals(op))
                return a / b;
            else if ("%".equals(op))
                return a % b;
            else if ("==".equals(op))
                return a == b ? TRUE : FALSE;
            else if (">".equals(op))
                return a > b ? TRUE : FALSE;
            else if ("<".equals(op))
                return a < b ? TRUE : FALSE;
            else
                throw new StoneException("bad operator", this);
        }
        protected Object computeOp(Object left, String op, Object right) {
            if (left instanceof Integer && right instanceof Integer)
                return computeNumber((Integer) left, op, (Integer)right);
            else if ("+".equals(op))
                return String.valueOf(left) + String.valueOf(right);
            else if ("==".equals(op)) {
                if (left == null)
                    return right == null ? TRUE : FALSE;
                else
                    return left.equals(right) ? TRUE : FALSE;
            }
            else
                throw new StoneException("bad type", this);
        }
    }

    @Reviser
    public static class BlockEx extends BlockStatement {
        public BlockEx(List<ASTree> c) {
            super(c);
        }

        public Object eval(Environment env) {
            Object result = 0;
            for (ASTree tree : this) {
                if (!(tree instanceof NullStatement))
                    result = ((ASTreeEx) tree).eval(env);
            }
            return result;
        }
    }

    @Reviser
    public static class IfEx extends IfStatement {
        public IfEx(List<ASTree> c) {
            super(c);
        }

        public Object eval(Environment env) {
            Object c = ((ASTreeEx) getChildren()).eval(env);
            if (c instanceof Integer && ((Integer) c).intValue() != FALSE)
                return ((ASTreeEx) getThenBlock()).eval(env);
            else {
                ASTree b = getElseBlock();
                if (b == null)
                    return 0;
                else
                    return ((ASTreeEx) b).eval(env);
            }
        }
    }

    @Reviser
    public static class WhileEx extends WhileStatement {
        public WhileEx(List<ASTree> c) {
            super(c);
        }

        public Object eval(Environment env) {
            Object result = 0;
            while (true) {
                Object c = ((ASTreeEx) getCondition()).eval(env);
                if (c instanceof Integer && ((Integer) c).intValue() == FALSE)
                    return result;
                else
                    result = ((ASTreeEx) getBody()).eval(env);
            }
        }
    }
}




















