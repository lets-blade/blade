package com.blade.kit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Regular utility class
 * <p>
 * Provide verification email, phone number, phone number, id number, number, etc.
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PatternKit {

    private static final String EMAIL_REGEX  = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String IP_REGEX     = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final String URL_REGEX    = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final String MOBILE_REGEX = "^(13[0-9]|14[57]|15[012356789]|17[0678]|18[0-9])[0-9]{8}$";

    /**
     * Validation Email
     *
     * @param email email format：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx representative email service provider.
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isEmail(String email) {
        return isMatch(EMAIL_REGEX, email);
    }

    /**
     * Verify the id card number
     *
     * @param idCard the resident identity card number is 18, and the last one may be Numbers or letters.
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isIdCard18(String idCard) {
        String regex = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
        return isMatch(regex, idCard);
    }

    /**
     * Verify the id card number
     *
     * @param idCard the resident identity card number is 15, and the last one may be Numbers or letters.
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isIdCard15(String idCard) {
        String regex = "^[1-9]\\\\d{7}((0\\\\d)|(1[0-2]))(([0|1|2]\\\\d)|3[0-1])\\\\d{3}$";
        return isMatch(regex, idCard);
    }

    /**
     * Verify that the suffix is a picture format.
     *
     * @param suffix filename suffix
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isImage(String suffix) {
        if (null != suffix && !"".equals(suffix) && suffix.contains(".")) {
            String regex = "(.*?)(?i)(jpg|jpeg|png|gif|bmp|webp)";
            return isMatch(regex, suffix);
        }
        return false;
    }

    /**
     * Verify phone number (support international format, +86135 XXXX... (mainland China), +00852137 XXXX... (Hong Kong, China)
     *
     * @param mobile Regular: mobile phone number (exact)
     *               <p>Mobile：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188</p>
     *               <p>Unicom：130、131、132、145、155、156、171、175、176、185、186</p>
     *               <p>Chinanet：133、153、173、177、180、181、189</p>
     *               <p>Globalstar：1349</p>
     *               <p>VNO：170</p>
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isMobile(String mobile) {
        return isMatch(MOBILE_REGEX, mobile);
    }

    /**
     * Verify the fixed phone number.
     *
     * @param phone Telephone number, format: country (area) telephone code + area code (city code) + phone number, for example：+8602085588447
     *              <p><b>Country (region) code: </b>Country (region) code that identifies the country (region) of telephone number. It contains one or more digits from 0 to 9,
     *              the number is followed by a space - separated country (region) code.</p>
     *              <p><b>Area code (city code): </b>this may include one or more digits from 0 to 9, area or city code in parentheses.——
     *              for countries that do not use regional or urban code, this component is omitted.</p>
     *              <p><b>phone number:</b>this contains one or more digits from 0 to 9. </p>
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isPhone(String phone) {
        return isMatch("(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$", phone);
    }

    /**
     * Verification of integers (positive and negative integers)
     *
     * @param digit One or more integers between 0 and 9.
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isDigit(String digit) {
        String regex = "\\-?[1-9]\\d+";
        return isMatch(regex, digit);
    }

    /**
     * Verify integer and floating point Numbers (positive and negative integers and positive and negative floating point Numbers)
     *
     * @param decimals one or more float point Numbers between 0-9, such as: 1.23，233.30
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isDecimals(String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        return isMatch(regex, decimals);
    }

    /**
     * verify blank character
     *
     * @param blankSpace Blank characters, including: space、\t、\n、\r、\f、\x0B
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isBlankSpace(String blankSpace) {
        String regex = "\\s+";
        return isMatch(regex, blankSpace);
    }

    /**
     * verify the Chinese
     *
     * @param chinese chinese character
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isChinese(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return isMatch(regex, chinese);
    }

    /**
     * Verify the Chinese alphanumeric space.
     *
     * @param chinese chinese character
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isRealName(String chinese) {
        String regex = "^[A-Za-z0-9\\s\u4E00-\u9FA5]+$";
        return isMatch(regex, chinese);
    }

    /**
     * The test is a number.
     *
     * @param str the string to match.
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isNumber(String str) {
        String regex = "^[1-9]\\d*$";
        return isMatch(regex, str);
    }

    /**
     * Verification date (date)
     *
     * @param birthday date format: "1992-09-03" or "1992.09.03"
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isBirthday(String birthday) {
        String regex = "^(\\d{4})-(\\d{2})-(\\d{2})$";
        return isMatch(regex, birthday);
    }

    /**
     * Verify URL address
     *
     * @param url format：http://biezhi.me:80 ftp://192.168.2.12
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isURL(String url) {
        return isMatch(URL_REGEX, url);
    }

    /**
     * Match Chinese postal code.
     *
     * @param postcode postal code
     * @return verify that success returns true, and the failure returns false.
     */
    public static boolean isPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return isMatch(regex, postcode);
    }

    /**
     * Match the IP address (simple match, format, such as: 192.168.1.1, 127.0.0.1, no matching IP segment size)
     *
     * @param ipAddress IPv4 standard address
     * @return {@code true}: matching<br>{@code false}: mismatching
     */
    public static boolean isIpAddress(String ipAddress) {
        return isMatch(IP_REGEX, ipAddress);
    }

    /**
     * Determines whether the regular is matched.
     *
     * @param regex regular expression
     * @param input the string to match.
     * @return {@code true}: matching<br>{@code false}: mismatching
     */
    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

}