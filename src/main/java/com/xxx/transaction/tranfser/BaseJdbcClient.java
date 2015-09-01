package com.xxx.transaction.tranfser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Properties;

/**
 * Created by ricdong on 15-8-31.
 */
public class BaseJdbcClient implements JdbcClient {
    private static final Log LOG = LogFactory.getLog(BaseJdbcClient.class);

    protected final String connectorId;
    protected final Driver driver;
    protected final Properties connectionProperties;

    private Connection connection;

    public BaseJdbcClient(JdbcConnectorId connectorId,
                          Properties config, Driver driver)throws SQLException {
        this.connectorId = connectorId.toString(); // TODO check not null
        this.driver = driver;
        this.connectionProperties = config;

        try {
            initialize();
        } catch(SQLException se) {
            LOG.error("could not initialize the connection to " + connectorId);

            throw se;
        }

    }

    /**
     * Create the connection to connectorId with config.
     * @throws SQLException
     */
    private void initialize()throws SQLException {
        this.connection = driver.connect(this.connectorId, connectionProperties);
    }

    /**
     * Close the connection to connectorId
     * @throws SQLException
     */
    public void close()throws SQLException {
        if(this.connection != null) {
            this.connection.close();
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public String getCatalog() {
        try {
            return connection.getCatalog();
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public ResultSet execute(String query) throws SQLException {
        Statement stat = connection.createStatement();

        return stat.executeQuery(query);
    }

    @Override
    public int executeUpdate(String updateQuery) throws SQLException {
        return connection.createStatement().executeUpdate(updateQuery);
    }
}
