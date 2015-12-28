package com.blade.plugin.tx;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

import javax.sql.DataSource;

import blade.kit.log.Logger;

public class TransactionManagerImpl implements TransactionManager {
	
	private static final Logger LOGGER = Logger.getLogger(TransactionManagerImpl.class); 
	
	private Stack<Connection>connections = new Stack<Connection>();
	
	private DataSource dataSource;
	
	public TransactionManagerImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
    public Connection getConnection() {
 
        if (connections.isEmpty()) {
            this.addConn();
        }
 
        return connections.peek();
    }
 
    @Override
    public void beginTransaction() {
        this.addConn();
    }
 
    @Override
    public void commit() {
        try {
            if (connections.peek() != null&& !connections.peek().isClosed()) {
            	LOGGER.info(connections.peek() + "--> commit");
                connections.peek().commit();
                connections.pop().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
    }
 
    @Override
    public void rollback() {
        try {
            if (connections.peek() != null&& !connections.peek().isClosed()) {
            	LOGGER.info(connections.peek() + "--> rollback");
                connections.peek().rollback();
                connections.pop().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	private void addConn() {
		try {
			Connection con = dataSource.getConnection();
			con.setAutoCommit(false);
			connections.push(con);
			LOGGER.info(con + "--> connection");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
}
