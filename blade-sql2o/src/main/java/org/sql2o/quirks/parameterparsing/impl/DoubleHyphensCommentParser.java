package org.sql2o.quirks.parameterparsing.impl;

/**
 * Created by lars on 22.09.2014.
 */
public class DoubleHyphensCommentParser extends AbstractCommentParser {

    @Override
    public boolean canParse(char c, String sql, int idx) {
        return sql.length() > idx + 1 && c == '-' && sql.charAt(idx + 1) == '-';
    }

    @Override
    public boolean isEndComment(char c) {
        return c == '\n';
    }
}
