package me.dewrs.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection extends ConnectionFactory{
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private long connectionTimeout;
    private int maximumPoolSize;
    private long keepAliveTime;
    private long idleTimeout;
    private long maxLifeTime;

    private HikariDataSource hikari;

    public MySQLConnection(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public MySQLConnection(String host, int port, String database, String user, String password, long connectionTimeout, int maximumPoolSize, long keepAliveTime, long idleTimeout, long maxLifeTime) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.connectionTimeout = connectionTimeout;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.idleTimeout = idleTimeout;
        this.maxLifeTime = maxLifeTime;
    }

    @Override
    public void connect(){
        if(existPool()) return;
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("serverName", host);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", user);
        hikariConfig.addDataSourceProperty("password", password);

        if(connectionTimeout != 0) hikariConfig.setConnectionTimeout(connectionTimeout);
        if(maximumPoolSize != 0) hikariConfig.setMaximumPoolSize(maximumPoolSize);
        if(keepAliveTime != 0) hikariConfig.setKeepaliveTime(keepAliveTime);
        if(idleTimeout != 0) hikariConfig.setIdleTimeout(idleTimeout);
        if(maxLifeTime != 0) hikariConfig.setMaxLifetime(maxLifeTime);

        hikari = new HikariDataSource(hikariConfig);
    }

    @Override
    public void close(){
        if(existPool()){
            hikari.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(!existPool()){
            connect();
        }
        return hikari.getConnection();
    }

    private boolean existPool(){
        return hikari != null && !hikari.isClosed();
    }
}