package stone.ast;

import stone.Token;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by xi on 16-2-20.
 */
public class ASTLeaf extends ASTree {
    private static ArrayList<ASTree> empty = new ArrayList<ASTree>();
    protected Token token;

    public ASTLeaf(Token t) {
        token = t;
    }

    @Override
    public ASTree child(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int ChildrenAmount() {
        return 0;
    }

    @Override
    public Iterator<ASTree> getChildren() {
        return empty.iterator();
    }

    @Override
    public String toString() {
        return token.getText();
    }

    public String location() {
        return "at line " + token.getLineNumber();
    }

    public Token getToken() {
        return token;
    }
}













