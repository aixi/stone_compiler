package stone.interpreter;


import stone.ast.ASTree;
import stone.ast.BasicParser;
import stone.ast.NullStatement;
import stone.ast.ParseException;
import stone.tokenizer.InputDialog;
import stone.tokenizer.Lexer;
import stone.tokenizer.Token;

/**
 * Created by xi on 16-2-21.
 */
public class BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        run(new BasicParser(), new BasicEnv());
    }

    public static void run(BasicParser bp, Environment env) throws ParseException {
        Lexer lexer = new Lexer(new InputDialog());
        while (lexer.peek(0) != Token.EOF) {
            ASTree tree = bp.parse(lexer);
            if (!(tree instanceof NullStatement)) {
                Object r = ((BasicEvaluator.ASTreeEx) tree).eval(env);
                System.out.println("=> " + r);
            }
        }
    }
}
