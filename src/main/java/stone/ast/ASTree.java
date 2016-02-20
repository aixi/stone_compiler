package stone.ast;

import java.util.Iterator;

/**
 * Created by xi on 16-2-20.
 */
public abstract class ASTree implements Iterable<ASTree> {
    public abstract ASTree child(int i);
    public abstract int ChildrenAmount();
    public abstract Iterator<ASTree> getChildren();
    public abstract String location();


    public Iterator<ASTree> iterator() {
        return getChildren();
    }
}
