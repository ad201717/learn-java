package com.howe.learn.trace.mysql;

import com.mysql.jdbc.Driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author Karl
 * @Date 2017/3/16 15:35
 */
public class Main {

    private static String url;
    private static String driver;
    private static String username;
    private static String password;

    public static void readConfig() throws IOException {
        Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("jdbc.properties"));
        url = prop.getProperty("jdbc.url");
        driver = prop.getProperty("jdbc.driverClassName");
        username = prop.getProperty("jdbc.username");
        password = prop.getProperty("jdbc.password");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
        readConfig();

        Class.forName(driver);

        Connection connection = DriverManager.getConnection(url, username, password);
        Statement statement = connection.createStatement();
        statement.execute("select 1 from dual");
        statement.close();
        connection.close();
    }
}
