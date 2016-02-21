package stone.interpreter;

import javassist.gluonj.util.Loader;

/**
 * Created by xi on 16-2-21.
 */
public class Runner {
    public static void main(String[] args) throws Throwable {
        Loader.run(BasicInterpreter.class, args, BasicEnv.class);
    }
}
