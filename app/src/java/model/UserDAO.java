package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDAO {
    
    public static User retrieveUserByName(String username, String password) throws SQLException{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        
        //get a connection to database
        Connection connection = ConnectionManager.getConnection();            

        //prepare a statement
        preparedStatement = connection.prepareStatement("select * from demographics where email like ? && password = ?"); //email and password from the database

        //set the parameters
        preparedStatement.setString(1, username+"@%"); //e.g. john.doe.2016@%
        preparedStatement.setString(2, password);

        //execute SQL query
        resultSet = preparedStatement.executeQuery();

        while(resultSet.next()){ //get query from database              
            user = new User(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5).charAt(0));
        }
        return user;
    }    
    
    public static boolean validateUsername(String username){
        if(username==null && username.length()==0){
            return false;
        }
        Pattern specialCharactersCheck = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]"); //check if the username contain any of these special characters
        int lastDotIndex = username.lastIndexOf("."); //john.doe.2016 or john.2016 get the index of the last dot
        if(lastDotIndex>=0){ //make sure the dot exists
            String checkYear = username.substring(lastDotIndex+1, username.length());
            Matcher hasSpecial = specialCharactersCheck.matcher(username);
            if(!hasSpecial.find()){ //if no special found
                return true;
            }
        }
        return false;
    }
}

