package org.sql2o.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Takes a string formatted like: 'my_string_variable' and returns it as: 'myStringVariable'
 * 
 * @author ryancarlson
 * @author dimzon - complete rewrite
 */
public class UnderscoreToCamelCase {
    public static String convert(String  underscore){
        if(underscore==null || underscore.isEmpty()) return underscore;
        return convert00(underscore);
    }

    private static String convert00(String  underscore){
        char[] chars = underscore.toCharArray();
        int write=-1,len=chars.length;
        boolean upper=false;
        for (int read = 0; read < len; ++read) {
            char c = chars[read];
            if('_'==c){
                upper = true;
                continue;
            }
            if(upper){
                upper = false;
                chars[++write]=Character.toUpperCase(c);
            } else {
                chars[++write]=Character.toLowerCase(c);
            }
        }
        return new String(chars, 0, ++write);
    }
}
