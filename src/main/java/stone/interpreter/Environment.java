package stone.interpreter;

/**
 * Created by xi on 16-2-21.
 */
public interface Environment {
    void put(String name, Object value);
    Object get(String name);
}
