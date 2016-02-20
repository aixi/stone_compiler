package stone.ast;

import java.util.HashSet;
import stone.ast.Parser.Operators;
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
                rule().number(NumberLiteral.class)
                rule().identifier(Identifier.class, reserved)
                rule().string(StringLiteral.class));
    Parser expr = expr0.expression(BinaryExpression.class, factor, operators);
    // TODO: 16-2-20
}
