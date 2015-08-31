package org.sql2o.quirks.parameterparsing.impl;

/**
 * Created by lars on 22.09.2014.
 */
public class QuoteParser implements CharParser {

    @Override
    public boolean canParse(char c, String sql, int idx) {
        return c == '\'' || c == '"';
    }

    @Override
    public int parse(char c, int idx, StringBuilder parsedSql, String sql, int length) {
        char quoteChar = c;

        do {
            parsedSql.append(c);
            if (++idx == length) return idx;
            c = sql.charAt(idx);

        } while(c != quoteChar);
        parsedSql.append(c);
        return idx;
    }
}
