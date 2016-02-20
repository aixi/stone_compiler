package stone;

/**
 * Created by xi on 16-2-20.
 */
public class Token {
    public static final Token EOF = new Token(-1) {}; //end of file
    public static final String EOL = System.getProperty("line.separator");
    private int lineNumber;

    protected Token(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean isIdentifier() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public int getNumber() {
        throw new StoneException("not a number token");
    }

    public String getText() {
        return "";
    }
}
