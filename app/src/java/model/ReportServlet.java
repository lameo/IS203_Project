package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.ConnectionManager;

public class ReportServlet {

    /**
     *
     * Constructs a UserDAO object
     * 
     *
     */  
    public ReportServlet(){
        
    }
    
    
    
    public static int retrieveQtyByYearAndGender(String time, String studentYear, String gender){
        //username and password checking, change to database when implemented
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        connection = ConnectionManager.getConnection();           
        studentYear = "%" + studentYear + "%";
        int totalUser = -1;
            try{
                //get a connection to database
                //out.print(connection);
                //prepare a statement
                preparedStatement = connection.prepareStatement("SELECT COUNT(DISTINCT D.MACADDRESS) FROM (SELECT * FROM DATA.LOCATION WHERE TIMESTAMP BETWEEN ? AND SELECT DATE_ADD(?, INTERVAL 15 MINUTE)) L, DEMOGRAPHICS D WHERE L.MACADDRESS = D.MACADDRESS AND D.EMAIL LIKE ? AND D.GENDER = ?;");   
                
                //set the parameters
                preparedStatement.setString(1, time);
                preparedStatement.setString(2, time);
                preparedStatement.setString(3, studentYear);
                preparedStatement.setString(3, gender);
                
                //execute SQL query
                resultSet = preparedStatement.executeQuery();
                totalUser = Integer.parseInt(resultSet.getString(1));
                
                
                
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
            return totalUser;
    }
}

