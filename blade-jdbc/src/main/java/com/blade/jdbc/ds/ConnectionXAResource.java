package com.blade.jdbc.ds;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.SQLException;

class ConnectionXAResource implements XAResource {
	
    private final ConnectionWrapper connection;
    private Xid xid;

    ConnectionXAResource(ConnectionWrapper connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "ConnectionXAResource{" + connection.dataSource.getName() + ':' + xid + '}';
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        if (this.xid != null) throw new XAException(XAException.XAER_DUPID);
        this.xid = xid;
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        if (this.xid != xid) throw new XAException(XAException.XAER_INVAL);
        this.xid = null;
    }

    @Override
    public void forget(Xid xid) throws XAException {
        if (this.xid != xid) throw new XAException(XAException.XAER_INVAL);
        this.xid = null;
    }

    @Override
    public int prepare(Xid xid) {
        return XA_OK;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        if (this.xid != xid) throw new XAException(XAException.XAER_INVAL);

        try {
            connection.commit();
        } catch (SQLException e) {
            throw XAException(e);
        }
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        if (this.xid != xid) throw new XAException(XAException.XAER_INVAL);

        try {
            connection.rollback();
        } catch (SQLException e) {
            throw XAException(e);
        }
    }

    @Override
    public boolean isSameRM(XAResource xaResource) {
        return xaResource == this;
    }

    @Override
    public Xid[] recover(int flag) {
        return xid == null ? new Xid[0] : new Xid[]{xid};
    }

    @Override
    public int getTransactionTimeout() {
        return 0;
    }

    @Override
    public boolean setTransactionTimeout(int seconds) {
        return false;
    }

    private static XAException XAException(SQLException e) {
        XAException wrapper = new XAException(XAException.XA_RBCOMMFAIL);
        wrapper.initCause(e);
        return wrapper;
    }
}
