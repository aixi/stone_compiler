package stone.ast;

import stone.tokenizer.Token;

/**
 * Created by xi on 16-2-21.
 */
public class StringLiteral extends ASTLeaf {
    public StringLiteral(Token t) {
        super(t);
    }

    public String getValue() {
        return getToken().getText();
    }
}
