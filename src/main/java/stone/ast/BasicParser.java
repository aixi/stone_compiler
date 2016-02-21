package stone.ast;

import java.util.HashSet;
import stone.ast.Parser.Operators;
import stone.tokenizer.Lexer;
import stone.tokenizer.Token;

import static stone.ast.Parser.rule;
/**
 * Created by xi on 16-2-20.
 */
public class BasicParser {
    HashSet<String> reserved = new HashSet<String>();
    Operators operators = new Operators();
    Parser expr0 = rule();
    Parser primary = rule(PrimaryExpr.class)
            .or(rule().sep("(").ast(expr0).sep(")"),
                rule().number(NumberLiteral.class),
                rule().identifier(Identifier.class, reserved),
                rule().string(StringLiteral.class));
    Parser factor = rule().or(rule(NegativeExpr.class).sep("-").ast(primary), primary);
    Parser expr = expr0.expression(BinaryExpression.class, factor, operators);
    Parser statement0 = rule();
    Parser block = rule(BlockStatement.class).sep("{").optional(statement0)
            .repeat(rule().sep(";", Token.EOL).optional(statement0)).sep("}");
    Parser simple = rule(PrimaryExpr.class).ast(expr);
    Parser statement = statement0.or(
            rule(IfStatement.class).sep("if").ast(expr).
                    ast(block).optional(rule().sep("else").ast(block)),
            rule(WhileStatement.class).sep("while").ast(expr).ast(block), simple);
    Parser program = rule().or(statement, rule(NullStatement.class)).sep(";", Token.EOL);

    public BasicParser() {
        reserved.add(";");
        reserved.add("}");
        reserved.add(Token.EOL);

        operators.add("=", 1, Operators.RIGHT);
        operators.add("==", 2, Operators.LEFT);
        operators.add(">", 2, Operators.LEFT);
        operators.add("<", 2, Operators.LEFT);
        operators.add("+", 3, Operators.LEFT);
        operators.add("-", 3, Operators.LEFT);
        operators.add("*", 4, Operators.LEFT);
        operators.add("/", 4, Operators.LEFT);
    }

    public ASTree parse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }
}
