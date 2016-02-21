package stone.ast;

import java.util.List;

/**
 * Created by xi on 16-2-21.
 */
public class NegativeExpr extends ASTList {
    public NegativeExpr(List<ASTree> c) {
        super(c);
    }

    public ASTree getOperand() {
        return child(0);
    }

    @Override
    public String toString() {
        return "-" + getOperand();
    }
}
