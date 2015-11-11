package com.github.p4535992.util.regex.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Commonly used regular expression patterns.
 * I don't own any right on this class of code is belong to android.util.Patterns and android.telephony.PhoneNumberUtils.
 * @version 2015-09-28.
 */
@SuppressWarnings("unused")
public class Patterns {
    /**
     *  Regular expression to match all IANA top-level domains.
     *  List accurate as of 2011/07/18.  List taken from:
     *  http://data.iana.org/TLD/tlds-alpha-by-domain.txt
     *  This pattern is auto-generated by frameworks/ex/common/tools/make-iana-tld-pattern.py
     *
     *  @deprecated Due to the recent profileration of gTLDs, this API is
     *  expected to become out-of-date very quickly. Therefore it is now
     *  deprecated.
     */
    @Deprecated
    public static final String TOP_LEVEL_DOMAIN_STR =
        "((aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
        + "|(biz|b[abdefghijmnorstvwyz])"
        + "|(cat|com|coop|c[acdfghiklmnoruvxyz])"
        + "|d[ejkmoz]"
        + "|(edu|e[cegrstu])"
        + "|f[ijkmor]"
        + "|(gov|g[abdefghilmnpqrstuwy])"
        + "|h[kmnrtu]"
        + "|(info|int|i[delmnoqrst])"
        + "|(jobs|j[emop])"
        + "|k[eghimnprwyz]"
        + "|l[abcikrstuvy]"
        + "|(mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
        + "|(name|net|n[acefgilopruz])"
        + "|(org|om)"
        + "|(pro|p[aefghklmnrstwy])"
        + "|qa"
        + "|r[eosuw]"
        + "|s[abcdeghijklmnortuvyz]"
        + "|(tel|travel|t[cdfghjklmnoprtvwz])"
        + "|u[agksyz]"
        + "|v[aceginu]"
        + "|w[fs]"
        + "|(\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae|\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435|\u0440\u0444|\u0441\u0440\u0431|\u05d8\u05e2\u05e1\u05d8|\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc|\u0625\u062e\u062a\u0628\u0627\u0631|\u0627\u0644\u0627\u0631\u062f\u0646|\u0627\u0644\u062c\u0632\u0627\u0626\u0631|\u0627\u0644\u0633\u0639\u0648\u062f\u064a\u0629|\u0627\u0644\u0645\u063a\u0631\u0628|\u0627\u0645\u0627\u0631\u0627\u062a|\u0628\u06be\u0627\u0631\u062a|\u062a\u0648\u0646\u0633|\u0633\u0648\u0631\u064a\u0629|\u0641\u0644\u0633\u0637\u064a\u0646|\u0642\u0637\u0631|\u0645\u0635\u0631|\u092a\u0930\u0940\u0915\u094d\u0937\u093e|\u092d\u093e\u0930\u0924|\u09ad\u09be\u09b0\u09a4|\u0a2d\u0a3e\u0a30\u0a24|\u0aad\u0abe\u0ab0\u0aa4|\u0b87\u0ba8\u0bcd\u0ba4\u0bbf\u0baf\u0bbe|\u0b87\u0bb2\u0b99\u0bcd\u0b95\u0bc8|\u0b9a\u0bbf\u0b99\u0bcd\u0b95\u0baa\u0bcd\u0baa\u0bc2\u0bb0\u0bcd|\u0baa\u0bb0\u0bbf\u0b9f\u0bcd\u0b9a\u0bc8|\u0c2d\u0c3e\u0c30\u0c24\u0c4d|\u0dbd\u0d82\u0d9a\u0dcf|\u0e44\u0e17\u0e22|\u30c6\u30b9\u30c8|\u4e2d\u56fd|\u4e2d\u570b|\u53f0\u6e7e|\u53f0\u7063|\u65b0\u52a0\u5761|\u6d4b\u8bd5|\u6e2c\u8a66|\u9999\u6e2f|\ud14c\uc2a4\ud2b8|\ud55c\uad6d|xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-3e0b707e|xn\\-\\-45brj9c|xn\\-\\-80akhbyknj4f|xn\\-\\-90a3ac|xn\\-\\-9t4b11yi5a|xn\\-\\-clchc0ea0b2g2a9gcd|xn\\-\\-deba0ad|xn\\-\\-fiqs8s|xn\\-\\-fiqz9s|xn\\-\\-fpcrj9c3d|xn\\-\\-fzc2c9e2c|xn\\-\\-g6w251d|xn\\-\\-gecrj9c|xn\\-\\-h2brj9c|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-j6w193g|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-kprw13d|xn\\-\\-kpry57d|xn\\-\\-lgbbat1ad8j|xn\\-\\-mgbaam7a8h|xn\\-\\-mgbayh7gpa|xn\\-\\-mgbbh1a71e|xn\\-\\-mgbc0a9azcg|xn\\-\\-mgberp4a5d4ar|xn\\-\\-o3cw4h|xn\\-\\-ogbpf8fl|xn\\-\\-p1ai|xn\\-\\-pgbs0dh|xn\\-\\-s9brj9c|xn\\-\\-wgbh1c|xn\\-\\-wgbl6a|xn\\-\\-xkc2al3hye2a|xn\\-\\-xkc2dl3a5ee0h|xn\\-\\-yfro4i67o|xn\\-\\-ygbi2ammx|xn\\-\\-zckzah|xxx)"
        + "|y[et]"
        + "|z[amw])";

    /**
     *  Regular expression pattern to match all IANA top-level domains.
     *  @deprecated This API is deprecated. See {@link #TOP_LEVEL_DOMAIN_STR}.
     */
    @Deprecated
    public static final Pattern TOP_LEVEL_DOMAIN =
        Pattern.compile(TOP_LEVEL_DOMAIN_STR);

    /**
     *  Regular expression to match all IANA top-level domains for WEB_URL.
     *  List accurate as of 2011/07/18.  List taken from:
     *  http://data.iana.org/TLD/tlds-alpha-by-domain.txt
     *  This pattern is auto-generated by frameworks/ex/common/tools/make-iana-tld-pattern.py
     *
     *  @deprecated This API is deprecated. See {@link #TOP_LEVEL_DOMAIN_STR}.
     */
    @Deprecated
    public static final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL =
        "(?:"
        + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
        + "|(?:biz|b[abdefghijmnorstvwyz])"
        + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
        + "|d[ejkmoz]"
        + "|(?:edu|e[cegrstu])"
        + "|f[ijkmor]"
        + "|(?:gov|g[abdefghilmnpqrstuwy])"
        + "|h[kmnrtu]"
        + "|(?:info|int|i[delmnoqrst])"
        + "|(?:jobs|j[emop])"
        + "|k[eghimnprwyz]"
        + "|l[abcikrstuvy]"
        + "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
        + "|(?:name|net|n[acefgilopruz])"
        + "|(?:org|om)"
        + "|(?:pro|p[aefghklmnrstwy])"
        + "|qa"
        + "|r[eosuw]"
        + "|s[abcdeghijklmnortuvyz]"
        + "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
        + "|u[agksyz]"
        + "|v[aceginu]"
        + "|w[fs]"
        + "|(?:\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae|\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435|\u0440\u0444|\u0441\u0440\u0431|\u05d8\u05e2\u05e1\u05d8|\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc|\u0625\u062e\u062a\u0628\u0627\u0631|\u0627\u0644\u0627\u0631\u062f\u0646|\u0627\u0644\u062c\u0632\u0627\u0626\u0631|\u0627\u0644\u0633\u0639\u0648\u062f\u064a\u0629|\u0627\u0644\u0645\u063a\u0631\u0628|\u0627\u0645\u0627\u0631\u0627\u062a|\u0628\u06be\u0627\u0631\u062a|\u062a\u0648\u0646\u0633|\u0633\u0648\u0631\u064a\u0629|\u0641\u0644\u0633\u0637\u064a\u0646|\u0642\u0637\u0631|\u0645\u0635\u0631|\u092a\u0930\u0940\u0915\u094d\u0937\u093e|\u092d\u093e\u0930\u0924|\u09ad\u09be\u09b0\u09a4|\u0a2d\u0a3e\u0a30\u0a24|\u0aad\u0abe\u0ab0\u0aa4|\u0b87\u0ba8\u0bcd\u0ba4\u0bbf\u0baf\u0bbe|\u0b87\u0bb2\u0b99\u0bcd\u0b95\u0bc8|\u0b9a\u0bbf\u0b99\u0bcd\u0b95\u0baa\u0bcd\u0baa\u0bc2\u0bb0\u0bcd|\u0baa\u0bb0\u0bbf\u0b9f\u0bcd\u0b9a\u0bc8|\u0c2d\u0c3e\u0c30\u0c24\u0c4d|\u0dbd\u0d82\u0d9a\u0dcf|\u0e44\u0e17\u0e22|\u30c6\u30b9\u30c8|\u4e2d\u56fd|\u4e2d\u570b|\u53f0\u6e7e|\u53f0\u7063|\u65b0\u52a0\u5761|\u6d4b\u8bd5|\u6e2c\u8a66|\u9999\u6e2f|\ud14c\uc2a4\ud2b8|\ud55c\uad6d|xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-3e0b707e|xn\\-\\-45brj9c|xn\\-\\-80akhbyknj4f|xn\\-\\-90a3ac|xn\\-\\-9t4b11yi5a|xn\\-\\-clchc0ea0b2g2a9gcd|xn\\-\\-deba0ad|xn\\-\\-fiqs8s|xn\\-\\-fiqz9s|xn\\-\\-fpcrj9c3d|xn\\-\\-fzc2c9e2c|xn\\-\\-g6w251d|xn\\-\\-gecrj9c|xn\\-\\-h2brj9c|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-j6w193g|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-kprw13d|xn\\-\\-kpry57d|xn\\-\\-lgbbat1ad8j|xn\\-\\-mgbaam7a8h|xn\\-\\-mgbayh7gpa|xn\\-\\-mgbbh1a71e|xn\\-\\-mgbc0a9azcg|xn\\-\\-mgberp4a5d4ar|xn\\-\\-o3cw4h|xn\\-\\-ogbpf8fl|xn\\-\\-p1ai|xn\\-\\-pgbs0dh|xn\\-\\-s9brj9c|xn\\-\\-wgbh1c|xn\\-\\-wgbl6a|xn\\-\\-xkc2al3hye2a|xn\\-\\-xkc2dl3a5ee0h|xn\\-\\-yfro4i67o|xn\\-\\-ygbi2ammx|xn\\-\\-zckzah|xxx)"
        + "|y[et]"
        + "|z[amw]))";

    /**
     * Good characters for Internationalized Resource Identifiers (IRI).
     * This comprises most common used Unicode characters allowed in IRI
     * as detailed in RFC 3987.
     * Specifically, those two byte Unicode characters are not included.
     */
    public static final String GOOD_IRI_CHAR =
        "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

    public static final Pattern IP_ADDRESS
        = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9]))");

    /**
     * RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets.
     */
    private static final String IRI
        = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR + "]){0,1}";

    private static final String GOOD_GTLD_CHAR =
        "a-zA-Z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";
    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";
    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;

    public static final Pattern DOMAIN_NAME
        = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");

    /**
     *  Regular expression pattern to match most part of RFC 3987
     *  Internationalized URLs, aka IRIs.  Commonly used Unicode characters are
     *  added.
     */
    public static final Pattern WEB_URL = Pattern.compile(
        "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
        + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
        + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
        + "(?:" + DOMAIN_NAME + ")"
        + "(?:\\:\\d{1,5})?)" // plus option port number
        + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
        + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
        + "(?:\\b|$)"); // and finally, a word boundary or end of
                        // input.  This is to stop foo.sure from
                        // matching as foo.su

    public static final Pattern WEB_URL_NO_PROTOCOL = Pattern.compile(
            "(((?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)"); // and finally, a word boundary or end of
    // input.  This is to stop foo.sure from
    // matching as foo.su

    public static final Pattern Protocol_URL = Pattern.compile("((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/))");

    public static final Pattern EMAIL_ADDRESS
        = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        );

    /**
     * This pattern is intended for searching for things that look like they
     * might be phone numbers in arbitrary text, not for validating whether
     * something is in fact a phone number.  It will miss many things that
     * are legitimate phone numbers.
     *
     * <p> The pattern matches the following:
     * <ul>
     * <li>Optionally, a + sign followed immediately by one or more digits. Spaces, dots, or dashes
     * may follow.
     * <li>Optionally, sets of digits in parentheses, separated by spaces, dots, or dashes.
     * <li>A string starting and ending with a digit, containing digits, spaces, dots, and/or dashes.
     * </ul>
     */
    public static final Pattern PHONE
        = Pattern.compile(                      // sdd = space, dot, or dash
                "(\\+[0-9]+[\\- \\.]*)?"        // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.]+[0-9])"); // <digit><digit|sdd>+<digit>

    /**
     *  Convenience method to take all of the non-null matching groups in a
     *  regex Matcher and return them as a concatenated string.
     *
     *  @param matcher      The Matcher object from which grouped text will
     *                      be extracted
     *
     *  @return             A String comprising all of the non-null matched
     *                      groups concatenated together
     */
    public static String concatGroups(Matcher matcher) {
        StringBuilder b = new StringBuilder();
        final int numGroups = matcher.groupCount();

        for (int i = 1; i <= numGroups; i++) {
            String s = matcher.group(i);

            if (s != null) {
                b.append(s);
            }
        }

        return b.toString();
    }

    /**
     * Convenience method to return only the digits and plus signs
     * in the matching string.
     *
     * @param matcher      The Matcher object from which digits and plus will
     *                     be extracted
     *
     * @return             A String comprising all of the digits and plus in
     *                     the match
     */
    public static String digitsAndPlusOnly(Matcher matcher) {
        StringBuilder buffer = new StringBuilder();
        String matchingRegion = matcher.group();

        for (int i = 0, size = matchingRegion.length(); i < size; i++) {
            char character = matchingRegion.charAt(i);

            if (character == '+' || Character.isDigit(character)) {
                buffer.append(character);
            }
        }
        return buffer.toString();
    }

    //-------------------------------------------------------------------------------
    //Utility for JOOQSupport
    //-------------------------------------------------------------------------------
    public static final Pattern MANAGE_SQL_QUERY_GET_VALUES_PARAM_1
            = Pattern.compile("(values)\\s*(\\(|\\{)\\s*(.*?)\\s*(\\)|\\})+",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_PREQUERY_INSERT
            = Pattern.compile("(insert into)(\\s*(.*?)\\s*)\\s*(\\(|\\{)\\s*(.*?)\\s*(\\)|\\})+",Pattern.CASE_INSENSITIVE);
    /*public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_VALUES_PARAM_2
            = Pattern.compile("(values)\\s*\\(",Pattern.CASE_INSENSITIVE);*/
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_VALUES_PARAM_2v2
            = Pattern.compile("(insert into)\\s*(.*?)\\(\\s*",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_1
            = Pattern.compile("(where)\\s*(\\(|\\{)\\s*(.*?)\\s*(\\)|\\})+",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_2
            = Pattern.compile("(where)\\s*\\(",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_CHECK_WHERE
            = Pattern.compile("(values)(\\s*[(])((.*?)|\\s*)(\\s*[)])(\\s*)(where)",Pattern.CASE_INSENSITIVE);
    public static final Pattern MANAGE_SQL_QUERY_INSERT_GET_WHERE_PARAM_3 =
            Pattern.compile("(\\s*[)])(\\s*)(where)",Pattern.CASE_INSENSITIVE);



    //---------------------------------------
    // Utility for StringUtil
    //---------------------------------------
    public static final Pattern IS_INT = Pattern.compile("(\\d)+");
    public static final Pattern IS_NUMERIC = Pattern.compile("(\\-|\\+)?\\d+(\\.\\d+)?");

    /**
     * Method copied from the default Scanner code og java.
     * http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/misc/LRUCache.java#LRUCache
     */

    // Size of internal character buffer
    private static final int BUFFER_SIZE = 1024; // change to 1024;
    // Boolean is true if source is done
    private static boolean sourceClosed = false;
    // Boolean indicating more input is required
    private static boolean needInput = false;
    // Boolean indicating if a delim has been skipped this operation
    private static boolean skipped = false;
    // A store of a position that the scanner may fall back to
    private static int savedScannerPosition = -1;
    // A cache of the last primitive type scanned
    private static Object typeCache = null;
    // Boolean indicating if a match result is available
    private static boolean matchValid = false;
    // Boolean indicating if this scanner has been closed
    private static boolean closed = false;
    // The current radix used by this scanner
    private static int radix = 10;
    // The default radix for this scanner
    private static int defaultRadix = 10;

    // A pattern for java whitespace
    private static Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");
    // A pattern for any token
    private static Pattern FIND_ANY_PATTERN = Pattern.compile("(?s).*");
    // A pattern for non-ASCII digits
    private static Pattern NON_ASCII_DIGIT = Pattern.compile("[\\p{javaDigit}&&[^0-9]]");

    private static String Digits     = "(\\p{Digit}+)";
    private static String HexDigits  = "(\\p{XDigit}+)";

    private static String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static String non0Digit = "[\\p{javaDigit}&&[^0]]";
    private static int SIMPLE_GROUP_INDEX = 5;

    private static String groupSeparator = "\\,";
    private static String decimalSeparator = "\\.";
    private static String nanString = "NaN";
    private static String infinityString = "Infinity";
    private static String positivePrefix = "";
    private static String negativePrefix = "\\-";
    private static String positiveSuffix = "";
    private static String negativeSuffix = "";



    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    private static String Exp        = "[eE][+-]?"+Digits;
    private static String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                    "[+-]?(" + // Optional sign character
                    "NaN|" +           // "NaN" string
                    "Infinity|" +      // "Infinity" string

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from the Java Language Specification, 2nd
                    // edition, section 3.10.2.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.("+Digits+")("+Exp+")?)|"+

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing "whitespace"

    public static final Pattern IS_DOUBLE = Pattern.compile(fpRegex);


    private static Pattern floatPattern;
    private static Pattern decimalPattern;

    private static void  buildFloatAndDecimalPattern() {
        // \\p{javaDigit} may not be perfect, see above
        String digit = "([0-9]|(\\p{javaDigit}))";
        String exponent = "([eE][+-]?"+digit+"+)?";
        String groupedNumeral = "("+non0Digit+digit+"?"+digit+"?("+groupSeparator+digit+digit+digit+")+)";
        // Once again digit++ is used for performance, as above
        String numeral = "(("+digit+"++)|"+groupedNumeral+")";
        String decimalNumeral = "("+numeral+"|"+numeral +decimalSeparator + digit + "*+|"+ decimalSeparator +digit + "++)";
        String nonNumber = "(NaN|"+nanString+"|Infinity|"+infinityString+")";
        String positiveFloat = "(" + positivePrefix + decimalNumeral +positiveSuffix + exponent + ")";
        String negativeFloat = "(" + negativePrefix + decimalNumeral +negativeSuffix + exponent + ")";
        String decimal = "(([-+]?" + decimalNumeral + exponent + ")|"+positiveFloat + "|" + negativeFloat + ")";
        String hexFloat = "[-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?";
        String positiveNonNumber = "(" + positivePrefix + nonNumber +positiveSuffix + ")";
        String negativeNonNumber = "(" + negativePrefix + nonNumber + negativeSuffix + ")";
        String signedNonNumber = "(([-+]?"+nonNumber+")|" + positiveNonNumber + "|" + negativeNonNumber + ")";
        floatPattern = Pattern.compile(decimal + "|" + hexFloat + "|" + signedNonNumber);
        decimalPattern = Pattern.compile(decimal);
    }

    private static Pattern integerPattern;

    private static String  buildIntegerPatternString() {
        String radixDigits = digits.substring(0, radix);
        // \\p{javaDigit} is not guaranteed to be appropriate
        // here but what can we do? The final authority will be
        // whatever parse method is invoked, so ultimately the
        // Scanner will do the right thing
        String digit = "((?i)["+radixDigits+"]|\\p{javaDigit})";
        String groupedNumeral = "("+non0Digit+digit+"?"+digit+"?("+groupSeparator+digit+digit+digit+")+)";
        // digit++ is the possessive form which is necessary for reducing
        // backtracking that would otherwise cause unacceptable performance
        String numeral = "(("+ digit+"++)|"+groupedNumeral+")";
        String javaStyleInteger = "([-+]?(" + numeral + "))";
        String negativeInteger = negativePrefix + numeral + negativeSuffix;
        String positiveInteger = positivePrefix + numeral + positiveSuffix;
        return "("+ javaStyleInteger + ")|(" + positiveInteger + ")|(" + negativeInteger + ")";
    }

    // A cache of the last few recently used Patterns
    /*
    private static final sun.misc.LRUCache<String,Pattern> patternCache =
            new sun.misc.LRUCache<String,Pattern>(7) {
        @Override
        protected Pattern create(String s) {
            return Pattern.compile(s);
        }
        @Override
        protected boolean  hasName(Pattern p, String s) {
            return p.pattern().equals(s);
        }
    };
    */

    public static final Pattern IS_INTEGER = isInteger();

    public static Pattern isInteger() {
        //if (integerPattern == null) {integerPattern = patternCache.forName(buildIntegerPatternString());}
        if (integerPattern == null) {integerPattern = Pattern.compile(buildIntegerPatternString(),Pattern.CASE_INSENSITIVE);}
        return integerPattern;
    }

    public static final Pattern IS_FLOAT = isFloat();

    public static Pattern isFloat(){
        if (floatPattern == null) {buildFloatAndDecimalPattern();}
        return floatPattern;
    }

    public static final Pattern IS_DECIMAL = isDecimal();

    public static Pattern isDecimal(){
        if (decimalPattern == null) {buildFloatAndDecimalPattern();}
        return decimalPattern;
    }

    private static volatile Pattern separatorPattern;
    private static volatile Pattern linePattern;
    private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
    private static final String LINE_PATTERN = ".*("+LINE_SEPARATOR_PATTERN+")|.+$";

    public static Pattern IS_SEPARATOR() {
        Pattern sp = separatorPattern;
        if (sp == null) separatorPattern = sp = Pattern.compile(LINE_SEPARATOR_PATTERN);
        return sp;
    }
    public static Pattern  IS_LINE() {
        Pattern lp = linePattern;
        if (lp == null) linePattern = lp = Pattern.compile(LINE_PATTERN);
        return lp;
    }



    private static volatile Pattern boolPattern;
    private static final String BOOLEAN_PATTERN = "true|false";
    public static Pattern IS_BOOLEAN() {
        Pattern bp = boolPattern;
        if (bp == null) boolPattern = bp = Pattern.compile(BOOLEAN_PATTERN,Pattern.CASE_INSENSITIVE);
        return bp;
    }


    public static final Pattern  GET_LAST_PART_OF_URI = Pattern.compile("([^/\\#])+(?=/$|$)",Pattern.CASE_INSENSITIVE);


    /**
     * Do not create this static utility class.
     */
    private Patterns() {}




}
