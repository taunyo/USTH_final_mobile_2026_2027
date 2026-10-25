package vn.edu.usth.final_mobile_2026_2027.utils;

import android.content.Context;

import androidx.annotation.StringRes;

import vn.edu.usth.final_mobile_2026_2027.R;

/**
 * Turns raw values into short texts for the screens: dates ("5h ago"), big numbers ("1.2k")
 * and names ("technology" instead of "technology@lemmy.ml").
 */
public class FormatUtils {

    /**
     * Units used to show the age of a post or of an account, from the smallest to the biggest.
     * Each unit knows its length in seconds and the text of strings.xml that displays it.
     */
    enum AgeUnit {
        NOW(1, R.string.time_now),
        MINUTE(60, R.string.time_minutes),
        HOUR(60 * 60, R.string.time_hours),
        DAY(24 * 60 * 60, R.string.time_days),
        MONTH(30 * 24 * 60 * 60L, R.string.time_months),
        YEAR(365 * 24 * 60 * 60L, R.string.time_years);

        final long seconds;
        @StringRes final int text;

        AgeUnit(long seconds, @StringRes int text) {
            this.seconds = seconds;
            this.text = text;
        }
    }

    private FormatUtils() {
        // Only static methods
    }

    /**
     * Returns how long ago a date was, e.g. "just now", "5h ago", "3y ago".
     *
     * @param createdUtc the date in seconds since 01/01/1970, like Post.getCreatedUtc()
     */
    public static String formatTimeAgo(Context context, long createdUtc) {
        long now = System.currentTimeMillis() / 1000;
        long age = now - createdUtc;

        AgeUnit unit = getAgeUnit(age);
        if (unit == AgeUnit.NOW) {
            return context.getString(unit.text);
        }
        // e.g. 18 000 seconds / 3 600 = 5, displayed as "5h ago"
        return context.getString(unit.text, age / unit.seconds);
    }

    /**
     * Chooses the unit to show an age: the biggest unit that fits in it.
     * Less than a minute, or a negative age (a date in the future), gives NOW.
     */
    static AgeUnit getAgeUnit(long ageSeconds) {
        if (ageSeconds < AgeUnit.MINUTE.seconds) {
            return AgeUnit.NOW;
        }
        // values() returns the units in the order of the enum, from the smallest to the biggest
        AgeUnit result = AgeUnit.MINUTE;
        for (AgeUnit unit : AgeUnit.values()) {
            if (unit.seconds <= ageSeconds) {
                result = unit;
            }
        }
        return result;
    }

    /**
     * Shortens a big number: 950 -> "950", 1234 -> "1.2k", 3456789 -> "3.4M".
     * The decimal is cut, not rounded, so 999 999 gives "999.9k" and not "1000k".
     * Only integer operations are used: String.format() would write "1,2k" on a French phone.
     */
    public static String formatCount(long count) {
        if (count < 0) {
            // A score can be negative: format the positive number and put the sign back
            return "-" + formatCount(-count);
        }
        if (count < 1000) {
            return String.valueOf(count);
        }
        if (count < 1_000_000) {
            return withOneDecimal(count, 1000) + "k";
        }
        return withOneDecimal(count, 1_000_000) + "M";
    }

    /** Divides count by unit and keeps one decimal: (1234, 1000) -> "1.2", (1000, 1000) -> "1". */
    private static String withOneDecimal(long count, long unit) {
        long whole = count / unit;                   // 1234 / 1000 = 1
        long decimal = (count % unit) * 10 / unit;   // 234 * 10 / 1000 = 2
        if (decimal == 0) {
            return String.valueOf(whole);
        }
        return whole + "." + decimal;
    }

    /**
     * Returns the name to display for a user or a community: "akunohana@piefed.blahaj.zone"
     * gives "akunohana", and a name of our instance like "technology" doesn't change.
     * Only display the short name: the API and the pages always need the full one.
     */
    public static String shortName(String fullName) {
        int at = fullName.indexOf('@');
        if (at == -1) {
            return fullName;
        }
        return fullName.substring(0, at);
    }
}
