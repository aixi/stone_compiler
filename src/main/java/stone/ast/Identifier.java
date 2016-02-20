package stone.ast;

import stone.tokenizer.Token;

/**
 * Created by xi on 16-2-20.
 */
public class Identifier extends ASTLeaf {
    public Identifier(Token t) {
        super(t);
    }

    public String getName() {
        return getToken().getText();
    }
}
