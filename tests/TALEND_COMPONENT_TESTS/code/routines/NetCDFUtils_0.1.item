package routines;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import routines.system.FastDateParser;

/*
 * user specification: the function's comment should contain keys as follows: 1. write about the function's comment.but
 * it must be before the "{talendTypes}" key.
 *
 * 2. {talendTypes} 's value must be talend Type, it is required . its value should be one of: String, char | Character,
 * long | Long, int | Integer, boolean | Boolean, byte | Byte, Date, double | Double, float | Float, Object, short |
 * Short
 *
 * 3. {Category} define a category for the Function. it is required. its value is user-defined .
 *
 * 4. {param} 's format is: {param} <type>[(<default value or closed list values>)] <name>[ : <comment>]
 *
 * <type> 's value should be one of: string, int, list, double, object, boolean, long, char, date. <name>'s value is the
 * Function's parameter name. the {param} is optional. so if you the Function without the parameters. the {param} don't
 * added. you can have many parameters for the Function.
 *
 * 5. {example} gives a example for the Function. it is optional.
 */
public class NetCDFUtils {
    /**
     * addDays: add days including fractional component to reference date.
     *
     *
     * {talendTypes} Date
     *
     * {Category} NetCDF
     *
     * {param} date(referenceDate) input: the reference date.
     *
     * {param} double(addValue) days : days since the reference date including fractional component
     *
     * {param} boolean(nearestSecond) nearestSecond : round to nearest second
     *
     * {example}
     *
     * ->> addDays(referenceDate, new Double("19326.1830555648", false) returns date 30/11/2002 04:23:36 with reference
     * date 1/1/1950 00:00:00 #
     */
    public static Date addDays(Date referenceDate, Double days, boolean nearestSecond) {
        if (days == null || days.isNaN()) return null;

        long referenceMillis = referenceDate.getTime();
        long resultMillis = referenceMillis + Math.round(days * 24.0 * 60.0 * 60.0 * 1000);

        if (nearestSecond) {
            resultMillis = Math.round(resultMillis/1000.0)*1000;
        }

        return new Date(resultMillis);
    }

    /**
     * addDays: add days including fractional component to reference date.
     *
     *
     * {talendTypes} Date
     *
     * {Category} NetCDF
     *
     * {param} date(referenceDate) input: the reference date.
     *
     * {param} double(addValue) days : days since the reference date including fractional component
     *
     * {example}
     *
     * ->> addDays(referenceDate, new Double("19326.1830555648") returns date 30/11/2002 04:23:36 with reference
     * date 1/1/1950 00:00:00 #
     */
    public static Date addDays(Date referenceDate, Double days) {
        return addDays(referenceDate, days, false);
    }


    /**
     * addDays: add days including fractional component to reference date (UTC).
     *
     *
     * {talendTypes} Date
     *
     * {Category} NetCDF
     *
     * {param} string(referenceDate) input: the reference date.
     *
     * {param} string(format) input: format of the reference date (as per SimpleDateFormat).
     *
     * {param} double(addValue) days : days since the reference date including fractional component
     *
     * {param} boolean(nearestSecond) nearestSecond : round to nearest second
     *
     * {example}
     *
     * ->> addDays("19500101000000", "yyyyMMddHHmmss", new Double("19326.1830555648", false) returns date 30/11/2002 04:23:36 #
     */
    public static Date addDays(String stringDate, String format, Double days, boolean nearestSecond) {
        try {
            if (stringDate == null || stringDate.equals("")) return null;
            DateFormat df = FastDateParser.getInstance(format);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(stringDate);
            return addDays(date, days, nearestSecond);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * addDays: add days including fractional component to reference date.
     *
     *
     * {talendTypes} Date
     *
     * {Category} NetCDF
     *
     * {param} string(referenceDate) input: the reference date.
     *
     * {param} string(format) input: format of the reference date (as per SimpleDateFormat).
     *
     * {param} double(addValue) days : days since the reference date including fractional component
     *
     * {example}
     *
     * ->> addDays("19500101000000", "yyyyMMddHHmmss", new Double("19326.1830555648") returns date 30/11/2002 04:23:36 #
     */
    public static Date addDays(String stringDate, String format, Double days) {
        return addDays(stringDate, format, days, false);
    }

    /**
     * testAddDays: test addDays stringDate method
     */

    public static void testAddDays() {

        // Create utc date parser/formatter for use in tests

        SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // 'Manually' test some Australian daylight time affected/unaffected dates

        Object[][] testCases1 = {
            {"19500101000000","yyyyMMddHHmmss", 19326.1830555648, "2002-11-30 04:23:36"},
            {"01/01/1950 00:00:00","MM/dd/yyyy HH:mm:ss", 24926.625011999975, "2018-03-31 15:00:01"},
            {"01/01/1950 00:00:00","MM/dd/yyyy HH:mm:ss", 24985.53751199995, "2018-05-29 12:54:01"}
        };

        for (Object[] testCase: testCases1) {
            String testDate = (String) testCase[0];
            String testFormat = (String) testCase[1];
            double daysToAdd = (Double) testCase[2];
            String expected = (String) testCase[3];

            Date result = addDays(testDate, testFormat, daysToAdd, true);
            Date expectedDate;

            try {
                expectedDate = utcDateFormat.parse(expected);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (!expectedDate.equals(result)) {
                String errorString = String.format("Expected '%s' but got '%s' for '%s', '%s', '%f'",
                    expected, utcDateFormat.format(result), testDate, testFormat, daysToAdd);
                System.err.println(errorString);
            }
        }

        // More exhaustive testing using non time zone affected calculations for comparison

        long referenceTime = -631152000000l; // time in milliseconds after January 1, 1970 00:00:00 GMT for reference date
                                            // 1/1/1950 00:00:00 UTC

        String[][] testCases = {
            {"01/01/1950 00:00:00","MM/dd/yyyy HH:mm:ss"},
            {"1950-01-01 00:00:00","yyyy-MM-dd HH:mm:ss"},
            {"19500101000000", "yyyyMMddHHmmss"}
        };

        // loop through each test case (reference date/formats used by harvesters)
        for (String[] testCase: testCases) {
            String testDate = testCase[0];
            String testFormat = testCase[1];

            // Loop through every 1/2 hour time increment (from 1/1/1950 to 29/4/2019)
            for (double daysToAdd=0; daysToAdd < 25202;  daysToAdd+=1.0/24.0/2.0) {

                // loop through possible rounding values
                for (boolean roundToNearestSecond: new boolean[] {false, true}) {
                    // test date returned from addDays for test daysToAdd and format rounding to nearest second
                    // against 1950-01-01 00:00:00 GMT plus daysToAdd rounded to nearest second

                    Date result = NetCDFUtils.addDays(testDate, testFormat, daysToAdd, roundToNearestSecond);

                    // calculate expected result without any date parsing/formatting/time zone processing
                    long referenceResult = referenceTime + Math.round(daysToAdd * 24.0 * 60 * 60 * 1000);

                    if (roundToNearestSecond) {
                        referenceResult = Math.round(referenceResult/1000.0) * 1000;
                    }

                    Date expected = new Date(referenceResult);

                    if (!result.equals(expected)) {
                        String errorString = String.format("Expected '%s' but got '%s' for '%s', '%s', '%f', '%b'",
                            utcDateFormat.format(expected), utcDateFormat.format(result), testDate, testFormat,
                            daysToAdd, roundToNearestSecond);
                        System.err.println(errorString);
                    }
                }

            }
        }

    }
}
