package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    public static User retrieveUserByName(String username, String password) throws SQLException{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        
        //get a connection to database
        Connection connection = ConnectionManager.getConnection();            

        //prepare a statement
        preparedStatement = connection.prepareStatement("select * from demographics where email like ? && password = ?"); //email from the database

        //set the parameters
        preparedStatement.setString(1, username+"@%"); //e.g. john.doe.2016@%
        preparedStatement.setString(2, password);

        //execute SQL query
        resultSet = preparedStatement.executeQuery();

        while(resultSet.next()){                   
            user = new User(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5).charAt(0));
        }

        return user;
    }    
}

