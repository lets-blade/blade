package org.sql2o.quirks;

/**
 * User: dimzon
 * Date: 4/24/14
 * Time: 9:31 AM
 */
public interface QuirksProvider {
    Quirks forURL(String jdbcUrl);
    Quirks forObject(Object jdbcObject);
}
