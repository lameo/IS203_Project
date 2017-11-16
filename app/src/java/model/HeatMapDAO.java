package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class that contains all the logic to generate heatmap
 */
public class HeatMapDAO {

    /**
     * Returns a map of all the rooms in the floor specified with key of the name of the semanticPlace and a value containing a heatmap object of said place 
     * 
     * @param endtimeDate String in dd/mm/yyyy hh:mm format for where it searches 15min before
     * @param floor String floor number where the map will be generated
     * @return Map with a String key containing the semanticPlace name and a value containing a heatmap object of said place
     */
    public static Map<String, HeatMap> retrieveHeatMap(String endtimeDate, String floor) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<String, HeatMap> heatmapList = new TreeMap<>();
        
        String semanticPlace = "";
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct(locationname) from locationlookup where locationname like ?");

            //set the parameters
            preparedStatement.setString(1, "%" + floor + "%");
            
            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                semanticPlace = resultSet.getString(1);
                int quantity = getStudentQuantity(semanticPlace, endtimeDate);
                int heatLevel = getHeatLevel(quantity);
                heatmapList.put(semanticPlace, new HeatMap(semanticPlace, quantity, heatLevel));
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
        }
        
        return heatmapList;
    }

    /**
     * Return the total number of student present at the location (only using the latest timestamp to avoid duplicate)
     * 
     * @param semanticPlace String location name where the it searches for qty of student
     * @param timeDate String in dd/mm/yyyy hh:mm format for where it searches 15min before
     * @return int total number of student present at the location
     */
    public static int getStudentQuantity(String semanticPlace, String timeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int ans = 0;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement 
            //retrieve number of people at the location. But only retrieve latest (max(timestamp)) location updates of user only
            preparedStatement = connection.prepareStatement("select count(DISTINCT l.macaddress) "
                    + "from location l, locationlookup ll, (SELECT max(TIMESTAMP) as TIMESTAMP, macaddress "
                        + "FROM location "
                        + "WHERE timestamp BETWEEN (SELECT DATE_SUB(?,INTERVAL 15 MINUTE)) AND (SELECT DATE_SUB(?,INTERVAL 1 SECOND)) group by macaddress) as temp "
                    + "where ll.locationname like ? and l.locationid = ll.locationid and temp.macaddress = l.macaddress and temp.timestamp = l.timestamp");

            //set the parameters
            preparedStatement.setString(1, timeDate);            
            preparedStatement.setString(2, timeDate);
            preparedStatement.setString(3, "%" + semanticPlace + "%");

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
        }
        return ans;
    }
    
    /**
     * Generates a heatlevel value according to the number of student present 
     * 
     * @param quantity int number of student present
     * @return int heatlevel value
     */
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
