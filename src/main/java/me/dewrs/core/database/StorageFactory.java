package me.dewrs.core.database;

import me.dewrs.core.PluginCore;
import me.dewrs.core.logger.LogSender;
import me.dewrs.core.utils.MessageUtils;
import me.dewrs.core.utils.ObjectUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

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
        } catch (Exception e) {
            LogSender.sendErrorMessage("An error occurred while initiating the connection to the database.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        plugin.setStorageType(storageType);
        createTables(tables);
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
                String column = v.getKey()+" "+v.getValue().name()+" NOT NULL";
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
                LogSender.sendMessage(q);
                PreparedStatement statement = con.prepareStatement(q,
                        Statement.RETURN_GENERATED_KEYS);
                statement.executeUpdate();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getInsertQuery(Map<String, Object> values) {
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

    protected void insertValueInTable(String table, Map<String, Object> values, Runnable callBack) {
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(getInsertQuery(values).replaceAll("%table%", table),
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
            callBack.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void appendConditions(StringBuilder query, Map<String, Object> conditions){
        if (!conditions.isEmpty()) {
            List<String> conditionStrings = new ArrayList<>();
            for (String column : conditions.keySet()) {
                conditionStrings.add(column + " = ?");
            }
            query.append(" WHERE ").append(String.join(" AND ", conditionStrings));
        }
    }

    private String getSelectQuery(List<String> columns, Map<String, Object> conditions){
        StringBuilder query;
        if(columns.isEmpty()){
            query = new StringBuilder("SELECT * FROM %table%");
        }else{
            query = new StringBuilder("SELECT ");
            query.append(MessageUtils.getStringFromList(columns)).append(" FROM %table%");
        }
        appendConditions(query, conditions);
        return query.toString();
    }

    //Main Map: Id - Map of Values
    //Map of Values: Column - Value
    protected void selectInTable(String table, List<String> columns, Map<String, Object> conditions, Consumer<TreeMap<Integer, Map<String, Object>>> callBack){
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(getSelectQuery(columns, conditions).replaceAll("%table%", table),
                    Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (Object value : conditions.values()) {
                statement.setObject(index++, value);
            }
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int maxColumns = metaData.getColumnCount();
            TreeMap<Integer, Map<String, Object>> result = new TreeMap<>();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                Map<String, Object> values = new LinkedHashMap<>();
                for (int i = 1; i <= maxColumns; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    values.put(columnName, value);
                }
                result.put(id, values);
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                callBack.accept(result);
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getExistQuery(Map<String, Object> conditions){
        StringBuilder query = new StringBuilder("SELECT 1 FROM %table%");
        appendConditions(query, conditions);
        return query.toString();
    }

    protected void valueExist(String table, Map<String, Object> conditions, Consumer<Boolean> callBack){
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(getExistQuery(conditions).replaceAll("%table%", table),
                    Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (Object value : conditions.values()) {
                statement.setObject(index++, value);
            }
            ResultSet resultSet = statement.executeQuery();
            callBack.accept(resultSet.next());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getUpdateQuery(Map<String, Object> changes, Map<String, Object> conditions){
        if(changes.isEmpty()){
            throw new RuntimeException();
        }
        StringBuilder query = new StringBuilder("UPDATE %table% SET ");
        List<String> setParts = new ArrayList<>();
        for (String column : changes.keySet()) {
            setParts.add(column + " = ?");
        }
        query.append(MessageUtils.getStringFromList(setParts));
        appendConditions(query, conditions);
        return query.toString();
    }

    protected void updateInTable(String table, Map<String, Object> changes, Map<String, Object> conditions, Runnable callBack){
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(getUpdateQuery(changes, conditions).replaceAll("%table%", table),
                    Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (Object value : changes.values()) {
                statement.setObject(index++, value);
            }
            for (Object value : conditions.values()) {
                statement.setObject(index++, value);
            }
            statement.executeUpdate();
            callBack.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getDeleteQuery(Map<String, Object> conditions){
        StringBuilder query = new StringBuilder("DELETE FROM %table%");
        appendConditions(query, conditions);
        return query.toString();
    }

    protected void deleteInTable(String table, Map<String, Object> conditions, Runnable callBack){
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(getDeleteQuery(conditions).replaceAll("%table%", table),
                    Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (Object value : conditions.values()) {
                statement.setObject(index++, value);
            }
            statement.executeUpdate();
            callBack.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    protected void dropTable(String table, Runnable callBack){
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement("DROP TABLE "+table,
                    Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            callBack.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}