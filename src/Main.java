import apply.DaleDB;
import apply.StaticDaleDB;
import refactor.StaticTreeMap;
import refactor.TreeMap;

/**
 * Entry point for accessing your project 2 files.
 *
 * @author Shrey Patel
 * @version 1.0
 * @userid spatel3207
 * @GTID 903969933
 * <br>
 * <p>
 * Collaborators: None
 * <p>
 * Resources: CSvistool
 * <p>
 * <br>
 * By typing 'I agree' below, you are agreeing that this is your
 * own work and that you are responsible for the contents of all
 * submitted files. If this is left blank, this project will lose
 * points.
 *<p>
 *<br>
 * Agree Here: I agree
 */
public class Main {


    /**
     * Creates and returns a new instance of your class implementing
     * {@link StaticTreeMap}.
     *
     * @param <K> the type of keys; must implement {@link Comparable}
     * @param <V> the type of values
     * @return a new {@link StaticTreeMap} instance
     * @apiNote This method must be implemented for unit tests to run.
     */
    public static <K extends Comparable<? super K>, V> StaticTreeMap<K, V> getTreeMapInstance() {
        return new TreeMap<>();
    }

    /**
     * Creates and returns a new instance of your class implementing
     * {@link StaticDaleDB}.
     *
     * @return a new {@link StaticDaleDB} instance
     * @apiNote This method must be implemented for unit tests to run.
     */
    public static StaticDaleDB getDaleDBInstance() {
        return new DaleDB();
    }
}