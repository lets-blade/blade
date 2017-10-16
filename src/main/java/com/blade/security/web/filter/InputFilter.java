package com.blade.security.web.filter;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Input filter
 *
 * @author biezhi
 * @date 2017/10/13
 */
public class InputFilter {


    private static final Pattern SCRIPT_PATTERN       = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_PATTERN          = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern SRC2_PATTERN         = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern SCRIPT_END_PATTERN   = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCRIPT_START_PATTERN = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern EVAL_PATTERN         = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern EXPRESSION_PATTERN   = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern JAVASCRIPT_PATTERN   = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern VBSCRIPT_PATTERN     = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
    private static final  Pattern ONLOAD_PATTERN       = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private String value;

    public InputFilter(String value) {
        this.value = value;
    }

    public InputFilter htmlToText() {
        this.value = this.value.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        return this;
    }

    public InputFilter cleanXss() {
        if (value != null) {
            String cleanValue;
            cleanValue = Normalizer.normalize(value, Normalizer.Form.NFD);
            // Avoid null characters
            cleanValue = cleanValue.replaceAll("\0", "");

            // Avoid anything between script tags
            Pattern scriptPattern = SCRIPT_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid anything in a src='...' type of expression
            scriptPattern = SRC_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            scriptPattern = SRC2_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = SCRIPT_END_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = SCRIPT_START_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = EVAL_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = EXPRESSION_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = JAVASCRIPT_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = VBSCRIPT_PATTERN;
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = ONLOAD_PATTERN;
            this.value = scriptPattern.matcher(cleanValue).replaceAll("");
        }
        return this;
    }

    public InputFilter htmlFilter() {
        this.value = new HTMLFilter().filter(this.value);
        return this;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
