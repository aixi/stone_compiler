package stone;

/**
 * Created by xi on 16-2-20.
 */
public class LexerRunner {
    public static void main(String[] args) throws ParseException {
        Lexer lexer = new Lexer(new InputDialog());
        for (Token token; (token = lexer.read()) != Token.EOF; )
            System.out.println("=>" + token.getText());
    }
}
