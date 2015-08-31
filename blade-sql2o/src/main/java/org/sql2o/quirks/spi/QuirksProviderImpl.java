package org.sql2o.quirks.spi;

import org.sql2o.quirks.*;

/**
 * User: dimzon
 * Date: 4/24/14
 * Time: 9:35 AM
 */
public class QuirksProviderImpl implements QuirksProvider {
    public Quirks forURL(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:oracle:")) return oracleQuirks.q;
        if (jdbcUrl.startsWith("jdbc:db2:")) return db2Quirks.q;
        if (jdbcUrl.startsWith("jdbc:db2j:net:")) return db2Quirks.q;
        if (jdbcUrl.startsWith("jdbc:db2os390")) return db2Quirks.q;
        if (jdbcUrl.startsWith("jdbc:postgresql:")) return postgresQuirks.q;
        return null;
    }

    public Quirks forObject(Object jdbcObject) {
        String className = jdbcObject.getClass().getCanonicalName();
        if (className.startsWith("oracle.jdbc.")) return oracleQuirks.q;
        if (className.startsWith("oracle.sql.")) return oracleQuirks.q;
        if (className.startsWith("com.ibm.db2.jcc.DB2")) return db2Quirks.q;
        if (className.startsWith("org.postgresql.")) return postgresQuirks.q;
        return null;
    }

    private static class oracleQuirks {
        static final Quirks q = new OracleQuirks();
    }

    private static class db2Quirks {
        static final Quirks q = new Db2Quirks();
    }

    private static class postgresQuirks {
        static final Quirks q = new PostgresQuirks();
    }
}
