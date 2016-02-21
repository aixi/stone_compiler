package stone.ast;

import stone.tokenizer.InputDialog;
import stone.tokenizer.Lexer;
import stone.tokenizer.Token;

/**
 * Created by xi on 16-2-21.
 */
public class ParserRunner {
    public static void main(String[] args) throws ParseException {
        Lexer lexer = new Lexer(new InputDialog());
        BasicParser basicParser = new BasicParser();
        while (lexer.peek(0) != Token.EOF) {
            ASTree ast = basicParser.parse(lexer);
            System.out.println("=>" + ast.toString());
        }
    }
}
