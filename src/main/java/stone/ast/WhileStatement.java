package stone.ast;

import java.util.List;

/**
 * Created by xi on 16-2-21.
 */
public class WhileStatement extends ASTList {
    public WhileStatement(List<ASTree> c) {
        super(c);
    }

    public ASTree getCondition() {
        return child(0);
    }

    public ASTree getBody() {
        return child(1);
    }

    @Override
    public String toString() {
        return "(while " + getCondition() + " " + getBody() + ")";
    }
}
