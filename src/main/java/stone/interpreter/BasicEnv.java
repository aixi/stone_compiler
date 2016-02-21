package stone.interpreter;


import java.util.HashMap;

/**
 * Created by xi on 16-2-21.
 */
public class BasicEnv implements Environment {
    protected HashMap<String, Object> values;

    public BasicEnv() {
        values = new HashMap<String, Object>();
    }

    public void put(String name, Object value) {
        values.put(name, value);
    }

    public Object get(String name) {
        return values.get(name);
    }
}
