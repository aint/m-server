package ua.softgroup.matrix.server.supervisor.producer;

import java.util.function.Predicate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class Utils {

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    public static double calculateIdlePercent(int workSeconds, int idleSeconds) {
        return workSeconds != 0
                ? (double) idleSeconds / workSeconds * 100
                : 0.0;
    }

}
