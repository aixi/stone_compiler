package stone;

import stone.ast.ASTree;

/**
 * Created by xi on 16-2-20.
 */
public class StoneException extends RuntimeException {
    public StoneException(String message) {
        super(message);
    }

    public StoneException(String m, ASTree t) {
        super(m + " " + t.location());
    }
}