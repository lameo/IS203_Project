package user;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author xuying
 */
import java.sql.*;
import java.io.*;
import java.util.*;

public class LoginDAO {
    private static Connection connection = null;
    
    public static Connection getConnection(){
        if(connection != null){
            return connection;
        }else{
            try{
                InputStream inputStream = LoginDAO.class.getClassLoader().getResourceAsStream("db.properties");
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
                
            } catch (Exception e){
                e.printStackTrace();
            }
            return connection;
        }
    }
}
