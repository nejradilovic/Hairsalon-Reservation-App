package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.domain.Idable;
import ba.unsa.etf.rpr.exceptions.HairsalonException;

import java.sql.*;
import java.util.*;
/**
 * Abstract class that implements core DAO CRUD methods for every entity
 * @param <T>
 * @author Nejra Adilovic
 */

public abstract class AbstractDao<T extends Idable> implements Dao<T>{

    private static Connection connection = null;
    private String tableName;
    /**
     * Constructor for class AbstractDao that sets connection name and calls createConnection method.
     * @param tableName String
     */
    public AbstractDao(String tableName) {
        this.tableName = tableName;
        if(connection==null) createConnection();
    }
    /**
     * Creates connection to database using properties file called db.properties.
     */
    private static void createConnection(){
        if(AbstractDao.connection==null) {
            try {
                Properties p = new Properties();
                p.load(ClassLoader.getSystemResource("db.properties").openStream());
                String url = p.getProperty("db.url");
                String username = p.getProperty("db.user");
                String password = p.getProperty("db.password");
                AbstractDao.connection = DriverManager.getConnection(url, username, password);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
    /**
     * gets Connection
     * @return connection to database
     */
    public static Connection getConnection(){
        return AbstractDao.connection;
    }
    /**
     * For singleton pattern, we have only one connection on the database which will be closed automatically when our program ends
     * But if we want to close connection manually, then we will call this method which should be called from finally block
     */
    public static void closeConnection() {
        if(connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                //throw new RuntimeException(e);
                e.printStackTrace();
                System.out.println("REMOVE CONNECTION METHOD ERROR: Unable to close connection on database");
            }
        }
    }
    /**
     * Method for mapping ResultSet into Object
     * @param rs - result set from database
     * @return a Bean object for specific table
     * @throws HairsalonException in case of error with db
     */
    public abstract T row2object(ResultSet rs) throws HairsalonException;
    /**
     * Method for mapping Object into Map
     * @param object - a bean object for specific table
     * @return key, value sorted map of object
     */
    public abstract Map<String, Object> object2row(T object);
    /**
     * Fetches object defined by the id given.
     * @param id primary key of entity
     * @return object that has the given id
     * @throws HairsalonException in case of an error with db
     */
    public T getById(int id) throws HairsalonException {
        return executeQueryUnique("SELECT * FROM "+this.tableName+" WHERE " + this.tableName + "_id = ?", new Object[]{id});
    }
    /**
     * Fetches all objects from the given table.
     * @return List of objects
     * @throws HairsalonException in case of an error with db
     */
    public List<T> getAll() throws HairsalonException {
        return executeQuery("SELECT * FROM "+ tableName, null);
    }
    /**
     * Deletes object defined by the given id from the table.
     * @param id - primary key of entity
     * @throws HairsalonException in case of an error with db
     */
    public void delete(int id) throws HairsalonException {
        String sql = "DELETE FROM "+tableName+" WHERE " + this.tableName + "_id = ?";
        try{
            PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new HairsalonException(e.getMessage(), e);
        }
    }
    /**
     * Adds given object to a table.
     * @param item bean for saving to database
     * @return item bean
     * @throws HairsalonException in case of an error with db
     */
    public T add(T item) throws HairsalonException{
        Map<String, Object> row = object2row(item);
        Map.Entry<String, String> columns = prepareInsertParts(row);

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(tableName);
        builder.append(" (").append(columns.getKey()).append(") ");
        builder.append("VALUES (").append(columns.getValue()).append(")");

        try{
            PreparedStatement stmt = getConnection().prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);
            // bind params. IMPORTANT treeMap is used to keep columns sorted so params are bind correctly
            int counter = 1;
            for (Map.Entry<String, Object> entry: row.entrySet()) {
                if (entry.getKey().equals("id")) continue; // skip ID
                stmt.setObject(counter, entry.getValue());
                counter++;
            }
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next(); // we know that there is one key
            item.setId(rs.getInt(1)); //set id to return it back */

            return item;
        }catch (SQLException e){
            throw new HairsalonException(e.getMessage(), e);
        }
    }
    /**
     * Updates object defined by the given id.
     * @param item - bean to be updated. id must be populated
     * @return item bean
     * @throws HairsalonException in case of an error with db
     */
    public T update(T item) throws HairsalonException{
        Map<String, Object> row = object2row(item);
        String updateColumns = prepareUpdateParts(row);
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ")
                .append(tableName)
                .append(" SET ")
                .append(updateColumns)
                .append(" WHERE ")
                .append(tableName)
                .append("_id = ?");

        try{
            PreparedStatement stmt = getConnection().prepareStatement(builder.toString());
            int counter = 1;
            for (Map.Entry<String, Object> entry: row.entrySet()) {
                if (entry.getKey().equals("id")) continue; // skip ID
                stmt.setObject(counter, entry.getValue());
                counter++;
            }
            stmt.setObject(counter, item.getId());
            stmt.executeUpdate();
            return item;
        }catch (SQLException e){
            throw new HairsalonException(e.getMessage(), e);
        }
    }
    /**
     * Utility method for executing any kind of query
     * @param query - SQL query
     * @param params - params for query
     * @return List of objects from database
     * @throws HairsalonException in case of error with db
     */
    public List<T> executeQuery(String query, Object[] params) throws HairsalonException{
        try {
            PreparedStatement stmt = getConnection().prepareStatement(query);
            if (params != null){
                for(int i = 1; i <= params.length; i++){
                    stmt.setObject(i, params[i-1]);
                }
            }
            ResultSet rs = stmt.executeQuery();
            ArrayList<T> resultList = new ArrayList<>();
            while (rs.next()) {
                resultList.add(row2object(rs));
            }
            return resultList;
        } catch (SQLException e) {
            throw new HairsalonException(e.getMessage(), e);
        }
    }
    /**
     * Utility for query execution that always return single record
     * @param query - query that returns single record
     * @param params - list of params for sql query
     * @return Object
     * @throws HairsalonException in case when object is not found
     */
    public T executeQueryUnique(String query, Object[] params) throws HairsalonException{
        List<T> result = executeQuery(query, params);
        if (result != null && result.size() == 1){
            return result.get(0);
        }else{
            throw new HairsalonException("Object not found");
        }
    }
    /**
     * Accepts KV storage of column names and return CSV of columns and question marks for insert statement
     * Example: (id, name, date) ?,?,?
     */
    private Map.Entry<String, String> prepareInsertParts(Map<String, Object> row){
        StringBuilder columns = new StringBuilder();
        StringBuilder questions = new StringBuilder();

        int counter = 0;
        for (Map.Entry<String, Object> entry: row.entrySet()) {
            counter++;
            if (entry.getKey().equals("id")) continue; //skip insertion of id due autoincrement
            columns.append(entry.getKey());
            questions.append("?");
            if (row.size() != counter) {
                columns.append(",");
                questions.append(",");
            }
        }
        return new AbstractMap.SimpleEntry<>(columns.toString(), questions.toString());
    }
    /**
     * Accepts KV storage of column names and return CSV of columns and question marks for insert statement
     */
    private String prepareUpdateParts(Map<String, Object> row){
        StringBuilder columns = new StringBuilder();

        int counter = 0;
        for (Map.Entry<String, Object> entry: row.entrySet()) {
            counter++;
            if (entry.getKey().equals("id")) continue; //skip update of id due where clause
            columns.append(entry.getKey()).append("= ?");
            if (row.size() != counter) {
                columns.append(",");
            }
        }
        return columns.toString();
    }
}