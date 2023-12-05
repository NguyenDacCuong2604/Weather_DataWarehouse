package DB;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

/**
 * DBConnection class provides methods to establish a connection to a MySQL database.
 * It utilizes Apache Commons DBCP2 BasicDataSource for connection pooling.
 */
public class DBConnection {

    private static DataSource dataSource;

    /**
     * Static block to initialize the DataSource with database configuration.
     */
    static {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://127.0.0.1:3307/datamart");
        ds.setUsername("root");
        ds.setPassword("root");
        dataSource = ds;
    }

    /**
     * Retrieves the configured DataSource for database connections.
     *
     * @return The configured DataSource object.
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
}
