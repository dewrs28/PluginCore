package me.dewrs.core.database;

import me.dewrs.core.PluginCore;
import me.dewrs.core.logger.LogSender;
import me.dewrs.core.utils.MessageUtils;
import me.dewrs.core.utils.ObjectUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageFactory {
    protected PluginCore plugin;

    protected ConnectionFactory setupMySQL(String host, int port, String database, String user, String password, long connectionTimeout, int maximumPoolSize, long keepAliveTime, long idleTimeout, long maxLifeTime) {
        return new MySQLConnection(host, port, database, user, password, connectionTimeout, maximumPoolSize, keepAliveTime, idleTimeout, maxLifeTime);
    }

    protected ConnectionFactory setUpSQLite(File dataFolder) {
        return new SQLiteConnection(dataFolder);
    }

    protected void initStorage(StorageType storageType, Map<String, Map<String, StorageType.ValueType>> tables) {
        try {
            Connection con = getConnection();
        } catch (SQLException e) {
            LogSender.sendErrorMessage("An error occurred while initiating the connection to the database.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        createTables(tables);
        plugin.setStorageType(storageType);
        LogSender.sendMessage("&aThe database has been successfully connected as &b"+storageType.toString()+"&a.");
    }

    protected Connection getConnection() throws SQLException {
        return plugin.getConnectionFactory().getConnection();
    }

    //Main Map: Name Table - Map of table
    //Map of table: Name Column - Value Type
    private List<String> getCreateTablesQuery(Map<String, Map<String, StorageType.ValueType>> tables) {
        List<String> querys = new ArrayList<>();
        String incrementProperty = "AUTOINCREMENT";
        if(plugin.getStorageType() == StorageType.MYSQL) incrementProperty = "AUTO_INCREMENT";
        for(Map.Entry<String, Map<String, StorageType.ValueType>> entry : tables.entrySet()){
            String tableName = entry.getKey();
            StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+" (id INTEGER PRIMARY KEY "+incrementProperty+",");
            Map<String, StorageType.ValueType> values = entry.getValue();
            List<String> columns = new ArrayList<>();
            for(Map.Entry<String, StorageType.ValueType> v : values.entrySet()){
                String column = v.getKey()+" "+entry.getValue().toString()+" NOT NULL";
                columns.add(column);
            }
            query.append(String.join(",", columns)).append(")");
            querys.add(query.toString());
        }
        return querys;
    }

    protected void createTables(Map<String, Map<String, StorageType.ValueType>> tables){
        try (Connection con = this.getConnection()) {
            List<String> querys = getCreateTablesQuery(tables);
            for(String q : querys){
                PreparedStatement statement = con.prepareStatement(q,
                        Statement.RETURN_GENERATED_KEYS);
                statement.executeUpdate();
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private String getInsertQueryFromMap(Map<String, Object> values) {
        StringBuilder query = new StringBuilder("INSERT INTO %table% (");
        List<String> nameColumns = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            nameColumns.add(entry.getKey());
            nameValues.add("?");
        }
        query.append(MessageUtils.getStringFromList(nameColumns)).append(") VALUES (");
        query.append(MessageUtils.getStringFromList(nameValues)).append(")");
        return query.toString();
    }

    protected void insertValueInTable(String table, Map<String, Object> values) {
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(getInsertQueryFromMap(values).replaceAll("%table%", table),
                    Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                Object object = entry.getValue();
                switch (ObjectUtils.getObjectType(object)){
                    case STRING: {
                        String value = (String) object;
                        statement.setString(i, value);
                        break;
                    }
                    case INTEGER: {
                        int value = (Integer) object;
                        statement.setInt(i, value);
                        break;
                    }
                    case LONG: {
                        long value = (Long) object;
                        statement.setLong(i, value);
                        break;
                    }
                }
                i++;
            }
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}