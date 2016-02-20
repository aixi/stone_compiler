package stone.ast;

import java.util.List;

/**
 * Created by xi on 16-2-20.
 */
public class BinaryExpression extends ASTList {
    public BinaryExpression(List<ASTree> children) {
        super(children);
    }

    public ASTree left() {
        return child(0);
    }

    public ASTree right() {
        return child(2);
    }

    public String operator() {
        return ((ASTLeaf)child(1)).getToken().getText();
    }
}
