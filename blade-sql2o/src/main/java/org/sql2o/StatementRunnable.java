package org.sql2o;

/**
 * Represents a method with a {@link Connection} and an optional argument. Implementations of this interface be used as
 * a parameter to one of the {@link Sql2o#runInTransaction(StatementRunnable) Sql2o.runInTransaction} overloads, to run
 * code safely in a transaction.
 */
public interface StatementRunnable {

    void run(Connection connection, Object argument) throws Throwable;
}
