package org.sql2o.quirks.parameterparsing.impl;

/**
 * Created by lars on 22.09.2014.
 */
public abstract class AbstractCommentParser implements CharParser {

    protected void init(){};

    @Override
    public int parse(char c, int idx, StringBuilder parsedSql, String sql, int length) {
        init();
        do {
            parsedSql.append(c);
            if (++idx == length) return idx;
            c = sql.charAt(idx);
        } while(!isEndComment(c));
        parsedSql.append(c);
        return idx;
    }

    public abstract boolean isEndComment(char c);
}
