package stone.ast;

import stone.tokenizer.Token;

/**
 * Created by xi on 16-2-20.
 */
public class NumberLiteral extends ASTLeaf {
    public NumberLiteral(Token t) {
        super(t);
    }

    public int value() {
        return getToken().getNumber();
    }
}
