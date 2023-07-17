package org.example.util;

import org.postgresql.Driver;
import org.springframework.cglib.proxy.Proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {
    private String DB_LOGIN;
    private String DB_PASSWORD;
    private String DB_URL;
    private String DB_POOL;
    private final int DEFAULT_POOL_SIZE = 3;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> connectionList;

    public ConnectionManager(String DB_LOGIN, String DB_PASSWORD, String DB_URL, String DB_POOL) {
        this.DB_LOGIN = DB_LOGIN;
        this.DB_PASSWORD = DB_PASSWORD;
        this.DB_URL = DB_URL;
        this.DB_POOL = DB_POOL;
    }
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    static {
        loadDriver();
    }
    public Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void closePool() {
        for (Connection pool : connectionList) {
            try {
                pool.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void initConnectionPool() {
        int poolSize = DB_POOL == null ? DEFAULT_POOL_SIZE : Integer.parseInt(DB_POOL);
        pool = new ArrayBlockingQueue<>(poolSize);
        connectionList = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            try {
                Connection connection = DriverManager.getConnection(DB_URL, DB_LOGIN, DB_PASSWORD);
                var proxyConnection = (Connection)
                        Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(), new Class[]{Connection.class},
                                (proxy, method, args) -> method.getName().equals("close")
                                        ? pool.add((Connection) proxy)
                                        : method.invoke(connection, args));
                pool.add(proxyConnection);
                connectionList.add(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
