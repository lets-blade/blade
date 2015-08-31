package org.sql2o.quirks.parameterparsing.impl;

import org.sql2o.quirks.parameterparsing.SqlParameterParsingStrategy;
import org.sql2o.tools.AbstractCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lars on 11.04.14.
 */
public class DefaultSqlParameterParsingStrategy implements SqlParameterParsingStrategy {

    public CharParser[] getCharParsers(Map<String, List<Integer>> paramMap) {
        return new CharParser[]{
                new QuoteParser(),
                new DoubleHyphensCommentParser(),
                new ForwardSlashCommentParser(),
                new ParameterParser(paramMap),
                new DefaultParser()
        };
    }

    @SuppressWarnings("ConstantConditions")
    public String parseSql(String statement, Map<String, List<Integer>> paramMap) {
        final int length = statement.length();
        final StringBuilder parsedQuery = new StringBuilder(length);

        final CharParser[] charParsers = getCharParsers(paramMap);

        for (int idx = 0; idx < length; idx++) {
            for (CharParser parser : charParsers) {
                char c = statement.charAt(idx);
                if (parser.canParse(c, statement, idx)){
                    idx = parser.parse(c, idx, parsedQuery, statement, length);
                    break;
                }
            }
        }

        return parsedQuery.toString();
    }
}
