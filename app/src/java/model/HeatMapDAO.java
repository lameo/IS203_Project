package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class HeatMapDAO {

    public static HashMap<String, HeatMap> retrieveHeatMap(String endtimeDate, String floor) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<String, HeatMap> heatmapList = new HashMap<>();
        
        String ans = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct(locationname) "
                    + "from location l, locationlookup ll "
                    + "where l.locationid = ll.locationid and timestamp between DATE_SUB(?,INTERVAL 15 MINUTE) and DATE_SUB(?,INTERVAL 1 SECOND) and locationname like ?");

            //set the parameters
            preparedStatement.setString(1, endtimeDate);
            preparedStatement.setString(2, endtimeDate);
            preparedStatement.setString(3, "%" + floor + "%");
            
            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans = resultSet.getString(1);
                int quantity = getStudentQuantity(ans, endtimeDate);
                int heatLevel = getHeatLevel(quantity);
                heatmapList.put(ans, new HeatMap(ans, quantity, heatLevel));
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return heatmapList;
    }

    public static int getStudentQuantity(String semanticPlace, String timeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int ans = 0;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(DISTINCT macaddress) "
                    + "from location l, locationlookup ll "
                    + "where ll.locationname like ? and l.locationid = ll.locationid and l.timestamp BETWEEN DATE_SUB(?,INTERVAL 15 MINUTE) and DATE_SUB(?,INTERVAL 1 SECOND)");

            //set the parameters
            preparedStatement.setString(1, "%" + semanticPlace + "%");
            preparedStatement.setString(2, timeDate);
            preparedStatement.setString(3, timeDate);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ans = resultSet.getInt(1);
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }
    
    public static int getHeatLevel(int quantity){
        if (quantity <= 0) {
            return 0;
        } else if (quantity <= 2) {
            return 1;
        } else if (quantity <= 5) {
            return 2;
        } else if (quantity <= 10) {
            return 3;
        } else if (quantity <= 20) {
            return 4;
        } else if (quantity <= 30) {
            return 5;
        }
        return 6;        
    }
}
