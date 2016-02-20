package stone.ast;

import java.util.Iterator;
import java.util.List;

/**
 * Created by xi on 16-2-20.
 */
public class ASTList extends ASTree {
    protected List<ASTree> children;

    public ASTList(List<ASTree> list) {
        children = list;
    }

    @Override
    public ASTree child(int i) {
        return children.get(i);
    }

    @Override
    public int ChildrenAmount() {
        return children.size();
    }

    @Override
    public Iterator<ASTree> getChildren() {
        return children.iterator();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        String sep = "";
        for (ASTree t : children) {
            stringBuilder.append(sep);
            sep = " ";
            stringBuilder.append(t.toString());
        }
        return stringBuilder.append(')').toString();
    }

    public String location() {
        for (ASTree t : children) {
            String s = t.location();
            if (s != null)
                return s;
        }
        return null;
    }
}
