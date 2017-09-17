package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    public static User retrieveUserByName(String email, String password) throws SQLException{
        //username and password checking, change to database when implemented
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        
        //get a connection to database
        connection = ConnectionManager.getConnection();            
        //out.print(connection);
        //prepare a statement
        preparedStatement = connection.prepareStatement("select * from demographics where email like ? && password = ?");   

        //set the parameters
        preparedStatement.setString(1, email+"@%");

        preparedStatement.setString(2, password);

        //execute SQL query
        resultSet = preparedStatement.executeQuery();

        while(resultSet.next()){                   
            user = new User(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5).charAt(0));
        }

        return user;
    }    
}

