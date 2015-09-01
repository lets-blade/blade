package org.sql2o;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import blade.kit.log.Logger;

/**
 * Created by lars on 16.09.2014.
 */
public class JndiDataSource {

    private final static Logger logger = Logger.getLogger(JndiDataSource.class);

    static DataSource getJndiDatasource(String jndiLookup) {
        Context ctx = null;
        DataSource datasource = null;

        try {
            ctx = new InitialContext();
            datasource = (DataSource) ctx.lookup(jndiLookup);
        }
        catch (NamingException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (Throwable e) {
                    logger.warn("error closing context", e);
                }
            }
        }

        return datasource;
    }
}
