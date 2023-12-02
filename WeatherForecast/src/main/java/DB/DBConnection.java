package DB;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DataSource dataSource;

    static {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://127.0.0.1:3307/datamart");
        ds.setUsername("root");
        ds.setPassword("root");
        dataSource = ds;
    }
    public static DataSource getDataSource() {
        return dataSource;
    }
}
