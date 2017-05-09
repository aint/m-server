package ua.softgroup.matrix.server.supervisor.producer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class Utils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    public static double calculateIdlePercent(int workSeconds, int idleSeconds) {
        return workSeconds != 0
                ? (double) idleSeconds / workSeconds * 100
                : 0.0;
    }

    public static LocalDate parseData(String data) {
        return LocalDate.parse(data, formatter);
    }

    public static LocalDate validateEndRangeDate(LocalDate endDate) {
        return endDate.isAfter(LocalDate.now()) ? LocalDate.now().plusDays(1) : endDate.plusDays(1);
    }

}
