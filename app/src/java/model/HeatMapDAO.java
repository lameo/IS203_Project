package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HeatMapDAO {
    public static ArrayList<HeatMap> retrieveHeatMap(String endtimeDate, int floor) {
        return null;
    }
    
    public static int heatLevel(String semanticPlace, String timeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT macaddress) from location l, locationlookup ll where ll.locationname = ? and l.locationid = ll.locationid and l.timestamp BETWEEN (SELECT DATE_sub( ? ,INTERVAL 15 MINUTE)) AND ?");

            //set the parameters
            preparedStatement.setString(1, semanticPlace);
            preparedStatement.setString(2, timeDate);
            preparedStatement.setString(3, timeDate);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans = resultSet.getString(1);
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        int level = Integer.parseInt(ans);
        if(level <= 0){
            return 0;
        }else if(level <=2){
            return 1;
        }else if(level <=5){
            return 2;
        }else if(level <=10){
            return 3;
        }else if(level <=20){
            return 4;
        }else if(level <=20){
            return 5;
        }
        return 6;
    }
}
