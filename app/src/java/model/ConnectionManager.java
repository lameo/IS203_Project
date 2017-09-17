package model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException{
        try{
            InputStream inputStream = ConnectionManager.class.getClassLoader().getResourceAsStream("/connection.properties");
            Properties properties = new Properties();
            if(properties != null){
                properties.load(inputStream);
                String dbDriver = properties.getProperty("dbDriver");
                String connectionUrl =properties.getProperty("connectionUrl");
                String userName = properties.getProperty("userName");
                String password = properties.getProperty("password");
                Class.forName(dbDriver).newInstance();
                //get a connection to database
                connection = DriverManager.getConnection(connectionUrl, userName, password); 
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
        return connection;
    }
}
