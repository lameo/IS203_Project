package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     *
     * Constructs a UserDAO object
     * 
     *
     */  
    public UserDAO(){
        
    }
    
    
    
    public static User retrieveUserByName(String email, String password){
        //username and password checking, change to database when implemented
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
            try{
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
                    //request.getRequestDispatcher("userPage.jsp").forward(request, response);                    
                    user = new User(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5).charAt(0));
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
    /**
     *
     * Get user
     * 
     * @param email
     * @param password
     * @return user - a valid user of the website
     */     
    public static boolean validateUser(String email, String password){
        //Admin
        User user = UserDAO.retrieveUserByName(email, password);
        
        boolean validateUser = false;
        if (user != null){
            validateUser = true;
        } else if (email.equals("admin")&&password.equals("admin")){
            validateUser = true;
        }
        return validateUser;
    }
}

