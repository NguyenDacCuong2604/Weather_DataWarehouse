package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String FILE_CONFIG = "\\config.properties";
    private static String urlDb;
    private static String db;
    private static String host;
    private static String port;
    private static String nameDB;
    private static String username;
    private static String password;
    private static Connection connection;
    static {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + FILE_CONFIG);
            // load properties from file
            properties.load(inputStream);
            // get property by name
            db = properties.getProperty("db");
            host = properties.getProperty("host");
            port = properties.getProperty("port");
            nameDB = properties.getProperty("name_database");

            urlDb = "jdbc:"+db+"://"+host+":"+port+"/"+nameDB;

            username = properties.getProperty("username");
            password = properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close objects
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DBConnection(){

    }

    public static Connection getConnection(){
        if (connection == null) {
            try {
                // connect
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(urlDb, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public static void closeConnection(){
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Close Connection");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
