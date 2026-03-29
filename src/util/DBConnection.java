package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.Properties;

public class DBConnection {

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()) {

            try {
                Properties props = new Properties();
                props.load(new FileInputStream("src/config.properties")); /* this loads the file and props.load
                 reads the content of file*/

                String url      = props.getProperty("db.url");
                String user     = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password); //creates connection
                System.out.println("Connected to database successfully.");

            } catch (Exception e) {
                System.out.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}