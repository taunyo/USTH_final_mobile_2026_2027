package vn.edu.usth.final_mobile_2026_2027.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests of FormatUtils. They run on the computer, without a phone:
 * ./gradlew testDebugUnitTest --tests "*FormatUtilsTest"
 */
public class FormatUtilsTest {

    // ---------------------------------------------------------------------------------------------
    // formatCount()
    // ---------------------------------------------------------------------------------------------

    @Test
    public void formatCount_small() {
        assertEquals("0", FormatUtils.formatCount(0));
        assertEquals("950", FormatUtils.formatCount(950));
        assertEquals("999", FormatUtils.formatCount(999));
    }

    @Test
    public void formatCount_thousands() {
        assertEquals("1k", FormatUtils.formatCount(1000));
        assertEquals("1.2k", FormatUtils.formatCount(1234));
        // Truncated, not rounded: 999 999 must not become "1000k"
        assertEquals("999.9k", FormatUtils.formatCount(999_999));
    }

    @Test
    public void formatCount_millions() {
        assertEquals("1M", FormatUtils.formatCount(1_000_000));
        assertEquals("3.4M", FormatUtils.formatCount(3_456_789));
    }

    @Test
    public void formatCount_negative() {
        // Lemmy has downvotes, so a score can be negative
        assertEquals("-1", FormatUtils.formatCount(-1));
        assertEquals("-1.2k", FormatUtils.formatCount(-1234));
    }

    // ---------------------------------------------------------------------------------------------
    // shortName()
    // ---------------------------------------------------------------------------------------------

    @Test
    public void shortName_otherInstance() {
        assertEquals("akunohana", FormatUtils.shortName("akunohana@piefed.blahaj.zone"));
    }

    @Test
    public void shortName_ourInstance() {
        assertEquals("technology", FormatUtils.shortName("technology"));
    }

    // ---------------------------------------------------------------------------------------------
    // getAgeUnit(), used by formatTimeAgo()
    // ---------------------------------------------------------------------------------------------

    @Test
    public void getAgeUnit_units() {
        assertEquals(FormatUtils.AgeUnit.NOW, FormatUtils.getAgeUnit(59));
        assertEquals(FormatUtils.AgeUnit.MINUTE, FormatUtils.getAgeUnit(60));
        assertEquals(FormatUtils.AgeUnit.HOUR, FormatUtils.getAgeUnit(5 * 3600));
        assertEquals(FormatUtils.AgeUnit.DAY, FormatUtils.getAgeUnit(2 * 86400));
        assertEquals(FormatUtils.AgeUnit.MONTH, FormatUtils.getAgeUnit(45 * 86400));
        assertEquals(FormatUtils.AgeUnit.YEAR, FormatUtils.getAgeUnit(3 * 365 * 86400L));
    }

    @Test
    public void getAgeUnit_futureDateIsNow() {
        // The clock of the phone can be a little behind the clock of the server
        assertEquals(FormatUtils.AgeUnit.NOW, FormatUtils.getAgeUnit(-30));
    }
}
