package org.sql2o.quirks;

import org.sql2o.GenericDatasource;

import java.util.ServiceLoader;

/**
 * User: dimzon
 * Date: 4/24/14
 * Time: 9:39 AM
 */
public class QuirksDetector{
    static final ServiceLoader<QuirksProvider> providers = ServiceLoader.load(QuirksProvider.class);

    public static Quirks forURL(String jdbcUrl) {
        Quirks quirks;
        for (QuirksProvider provider : providers) {
            quirks=provider.forURL(jdbcUrl);
            if(quirks!=null) return quirks;
        }
        return new NoQuirks();
    }

    public static Quirks forObject(Object jdbcObject) {
        if(jdbcObject instanceof GenericDatasource)
            return forURL(((GenericDatasource) jdbcObject).getUrl());
        Quirks quirks;
        for (QuirksProvider provider : providers) {
            quirks=provider.forObject(jdbcObject);
            if(quirks!=null) return quirks;
        }
        return new NoQuirks();
    }
}
