package com.xxx.transaction.tranfser;

import com.xxx.transaction.tranfser.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.*;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default implementation of interface {@link ITransfer}
 * Created by ricdong on 15-8-30.
 */
public class TradingSystem implements ITransfer {
    public static final Log LOG = LogFactory.getLog(TradingSystem.class);
    public static final Log LOG_TRACE = LogFactory.getLog(TradingSystem.class.getName() + " TRACE ");

    private String recordsQueryString = "SELECT COUNT(1) FROM %s WHERE %s";
    private String updateQueryString = "UPDATE %s SET %s WHERE %s";
    private String insertQueryString = "INSERT INTO %s(%s,%s) VALUE(%s,%d)";

    private String tableName = "";
    private String databaseName = "";

    private MysqlClient storeHandler = null;


    class MysqlClient extends BaseJdbcClient {

        public MysqlClient(JdbcConnectorId connectorId,
                           Properties config, Driver driver) throws SQLException {
            super(connectorId, config, new com.mysql.jdbc.Driver());
        }

        public int records(String query) throws SQLException {
            ResultSet resultSet = execute(query);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    public TradingSystem() throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().
                getResourceAsStream("mysql.properties");

        if (in == null) {
            throw new IllegalArgumentException("Could not find mysql.properties in classpath");
        }

        Properties pro = new Properties();
        pro.load(in);

        String id = checkNotNull(pro.getProperty("connection-url"), "connectorId is null");
        tableName = checkNotNull(pro.getProperty("connection-table"));
        String user = checkNotNull(pro.getProperty("connection-user"));
        String password = checkNotNull(pro.getProperty("connection-password"));

        Properties config = new Properties();
        config.setProperty("user", user);
        config.setProperty("password", password);

        storeHandler = new MysqlClient(new JdbcConnectorId(id), config, null);
        databaseName = checkNotNull(storeHandler.getCatalog());

        LOG.info("Successful to connect to " + id + ", start up " + this.getClass().getName());
    }

    /**
     * Just an example with single thread one by one, does not support multi-threads invoking.
     *
     * @param userName Not null, the name of user.
     * @param coin     Number of coin, should be > 0
     * @throws TransferRuntimeException
     */
    @Override
    public synchronized void addUserCoin(String userName,
                                         int coin) throws TransferRuntimeException {
        LOG.info("Try to add user coin " + userName + ", coin " + coin);

        String buildQuery = String.format(recordsQueryString, this.tableName,
                "user_name = '" + userName + "'");

        try {
            // first, we check the user whether he already in our database.
            int count = storeHandler.records(buildQuery);

            if (count > 0) {
                String updateSql = String.format(updateQueryString, this.tableName,
                        "coins = coins + " + coin, "user_name = '" + userName + "'");
                LOG_TRACE.info("updateSql " + updateSql);
                count = storeHandler.executeUpdate(updateSql);

                if (count > 0) {
                    LOG.info("Update the coin for user " + userName + " with " + coin);
                }

                // TODO  un-expected exception
            } else {
                // this is a new user
                String insertSql = String.format(insertQueryString, this.tableName,
                        "user_name", "coins", "'" + userName + "'", coin);
                LOG_TRACE.info("insertSql " + insertSql);
                count = storeHandler.executeUpdate(insertSql);
                if (count > 0) {
                    LOG.info("Insert a new User " + userName + " with the coin number " + coin);
                }
            }
        } catch (SQLException se) {
            LOG.info("Could not communicate with database " + StringUtils.stringifyException(se));
            throw new TransferRuntimeException(TransferRuntimeException.ERROR_RUNTIME);
        }
    }

    @Override
    public synchronized void transferTo(String fromUser, String toUser,
                           int numberOfCoin) throws TransferRuntimeException {
        LOG.info("Try to transfer the coin " + numberOfCoin + " from user " + fromUser + " to " + toUser);

        try {
            // first, they should be already in our database.
            String fromUserSql = String.format(recordsQueryString, this.tableName,
                    "user_name = '" + fromUser + "'");
            String toUserSql = String.format(recordsQueryString, this.tableName,
                    "user_name = '" + toUser + "'");

            if(storeHandler.records(fromUserSql) == 0 || storeHandler.records(toUserSql) == 0) {
                throw new TransferRuntimeException(TransferRuntimeException.ERROR_USER_NOT_FOUND);
            }
            // and then, the amount coins of the fromUser should be > numberOfCoin
            // select count(1) from coins where user_name = 'lyn zhang' and coins > 60;
            String countSql = String.format(recordsQueryString,
                    this.tableName, "user_name = '"+fromUser+"' and coins >= " + numberOfCoin);

            if (storeHandler.records(countSql) == 0) {
                LOG.warn("Insufficient coin number with user " + fromUser);
                throw new TransferRuntimeException(TransferRuntimeException.ERROR_COIN_INSUFFICIENT);
            }

            String deductSql = String.format(updateQueryString, this.tableName,
                    "coins = coins - " + numberOfCoin, "user_name = '" + fromUser + "'");
            String increaseSql = String.format(updateQueryString, this.tableName,
                    "coins = coins + " + numberOfCoin, "user_name = '" + toUser + "'");
            // try to transfer the coins, this is a DB Transaction.
            Connection connection = storeHandler.getConnection();
            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                Statement stmt = connection.createStatement();

                // deduct the coins from user
                int count_1st = stmt.executeUpdate(deductSql);

                int count_2rd = stmt.executeUpdate(increaseSql);

                if(count_1st > 0 && count_2rd > 0) {
                    LOG.info("Successful to transfer the coins " +
                            numberOfCoin + " from " + fromUser + " to " + toUser);
                    connection.commit();
                }
                connection.rollback();
            } catch(SQLException se) {
                LOG.warn("Failed to transfer the coins from " + fromUser +
                        " to " + toUser + ", " + StringUtils.stringifyException(se));
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch(SQLException se) {
            throw new TransferRuntimeException(TransferRuntimeException.ERROR_RUNTIME);
        }
    }

    @Override
    public int getAmountOfCoinByUser(String userName) throws TransferRuntimeException {
        // TODO
        return 0;
    }

    static private ThreadMXBean threadBean =
            ManagementFactory.getThreadMXBean();

    public static void printThreadInfo(PrintWriter stream,
                                       String title) {
        final int STACK_DEPTH = 20;
        boolean contention = threadBean.isThreadContentionMonitoringEnabled();
        long[] threadIds = threadBean.getAllThreadIds();
        stream.println("Process Thread Dump: " + title);
        stream.println(threadIds.length + " active threads");
        for (long tid: threadIds) {
            ThreadInfo info = threadBean.getThreadInfo(tid, STACK_DEPTH);
            if (info == null) {
                stream.println("  Inactive");
                continue;
            }
            stream.println("Thread " +
                    getTaskName(info.getThreadId(),
                            info.getThreadName()) + ":");
            Thread.State state = info.getThreadState();
            stream.println("  State: " + state);
            stream.println("  Blocked count: " + info.getBlockedCount());
            stream.println("  Waited count: " + info.getWaitedCount());
            if (contention) {
                stream.println("  Blocked time: " + info.getBlockedTime());
                stream.println("  Waited time: " + info.getWaitedTime());
            }
            if (state == Thread.State.WAITING) {
                stream.println("  Waiting on " + info.getLockName());
            } else  if (state == Thread.State.BLOCKED) {
                stream.println("  Blocked on " + info.getLockName());
                stream.println("  Blocked by " +
                        getTaskName(info.getLockOwnerId(),
                                info.getLockOwnerName()));
            }
            stream.println("  Stack:");
            for (StackTraceElement frame: info.getStackTrace()) {
                stream.println("    " + frame.toString());
            }
        }
        stream.flush();
    }

    private static String getTaskName(long id, String name) {
        if (name == null) {
            return Long.toString(id);
        }
        return id + " (" + name + ")";
    }
}
