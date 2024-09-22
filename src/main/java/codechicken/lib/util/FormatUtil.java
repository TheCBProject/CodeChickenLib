package codechicken.lib.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by brandon3055 on 31/10/2023
 */
public class FormatUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###,###,###", DecimalFormatSymbols.getInstance(Locale.ROOT));

    public static String formatNumber(double value) {
        if (Math.abs(value) < 1000D) return String.valueOf(value);
        if (Math.abs(value) < 1000000D) return addCommas((int) value); //I mean whats the point of displaying 1.235K instead of 1,235?
        if (Math.abs(value) < 1000000000D) return Math.round(value / 1000D) / 1000D + "M";
        if (Math.abs(value) < 1000000000000D) return Math.round(value / 1000000D) / 1000D + "G";
        return Math.round(value / 1000000000D) / 1000D + "T";
    }

    public static String formatNumber(long value) {
        if (value == Long.MIN_VALUE) value = Long.MAX_VALUE;
        if (Math.abs(value) < 1000L) return String.valueOf(value);
        if (Math.abs(value) < 1000000L) return addCommas(value);
        if (Math.abs(value) < 1000000000L) return Math.round((double) (value / 100000L)) / 10D + "M";
        if (Math.abs(value) < 1000000000000L) return Math.round((double) (value / 100000000L)) / 10D + "G";
        if (Math.abs(value) < 1000000000000000L) return Math.round((double) (value / 1000000000L)) / 1000D + "T";
        if (Math.abs(value) < 1000000000000000000L) return Math.round((double) (value / 1000000000000L)) / 1000D + "P";
        return Math.round((double) (value / 1000000000000000L)) / 1000D + "E";
    }

    /**
     * Add commas to a number e.g. 161253126 > 161,253,126
     */
    public static String addCommas(int value) {
        return DECIMAL_FORMAT.format(value);
    }

    /**
     * Add commas to a number e.g. 161253126 > 161,253,126
     */
    public static String addCommas(long value) {
        return DECIMAL_FORMAT.format(value);
    }
}
