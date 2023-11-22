import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionLogFile {
    private static final String FILE_CONFIG = "\\config.properties";
    private static String urlDb;
    private static String username;
    private static String password;
    private static ConnectionLogFile getConnection;
    private static Connection con;

    static {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + FILE_CONFIG);
            // load properties from file
            properties.load(inputStream);
            // get property by name
            urlDb = properties.getProperty("url_controller");
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

    private ConnectionLogFile() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(urlDb, username, password);
    }

    public static ConnectionLogFile getInstance() throws SQLException, ClassNotFoundException {
        if(getConnection==null){
            getConnection = new ConnectionLogFile();
        }
        return getConnection;
    }

    public static Connection getCon(){
        try {
            getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return con;
    }
}