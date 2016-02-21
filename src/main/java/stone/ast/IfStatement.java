package stone.ast;

import java.util.List;

/**
 * Created by xi on 16-2-21.
 */
public class IfStatement extends ASTList {
    public IfStatement(List<ASTree> c) {
        super(c);
    }

    public ASTree getCondition() {
        return child(0);
    }

    public ASTree getThenBlock() {
        return child(1);
    }

    public ASTree getElseBlock() {
        return childrenAmount() > 2 ? child(2) : null;
    }

    @Override
    public String toString() {
        return "(if " + getCondition() + " " + getThenBlock()
                + " else " + getElseBlock() + ")";
    }
}
