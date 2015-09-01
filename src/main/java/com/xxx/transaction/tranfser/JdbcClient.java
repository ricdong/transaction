package com.xxx.transaction.tranfser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ricdong on 15-8-31.
 */
public interface JdbcClient {

    Connection getConnection();

    String getCatalog();

    ResultSet execute(String query) throws SQLException;

    int executeUpdate(String updateQuery) throws SQLException;
}
