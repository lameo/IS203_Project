/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.sql.*;
import java.io.*;
import java.util.*;

public class UserDAO {
    
    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static User user = null;
    
    public static User retrieveUserByName(String username, String password){
        //username and password checking, change to database when implemented
            try{
                //get a connection to database
                connection = LoginDAO.getConnection();            
                //out.print(connection);
                //prepare a statement
                preparedStatement = connection.prepareStatement("select * from demographics where name = ? && password = ?");   
                
                //set the parameters
                preparedStatement.setString(1, username);
                
                preparedStatement.setString(2, password);
                
                //execute SQL query
                resultSet = preparedStatement.executeQuery();
                
                while(resultSet.next()){
                    //request.getRequestDispatcher("userPage.jsp").forward(request, response);                    
                    user = new User(resultSet.getString(1),resultSet.getString(2));
                }
                
                
                
                /*if(userName!=null){
                    User user = new User(userName, password);
                    String userType = user.validate1(username,password);
                    if(userType.equals("admin")){
                        request.getRequestDispatcher("adminPage.jsp").forward(request, response);
                    }
                    if(userType.equals("user")){
                        request.getRequestDispatcher("userPage.jsp").forward(request, response);
                    }
                }*/
                
                
                
            } catch (SQLException e){
                e.printStackTrace();
            }
            return user;
    }
    
}

