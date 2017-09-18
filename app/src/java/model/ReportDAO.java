package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {

    public static String retrieveQtyByYearAndGender(String time){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try{
            //get a connection to database
            connection = ConnectionManager.getConnection();            
            
            //prepare a statement
            preparedStatement = connection.prepareStatement("SELECT count(DISTINCT d.macaddress) FROM (SELECT * FROM data.location where timestamp between ? AND (SELECT DATE_ADD(?, INTERVAL 15 MINUTE))) l, demographics d where l.macaddress = d.macaddress and d.email like ? and d.gender = ?");
            
            
            String[] years = {"%@%", "%2010%", "%2011%", "%2012%", "%2013%", "%2014%"};
            String[] genders = {"m","f"};
            
            
            for(int i = 0; i<12; i++){
                String studentYear = years[i/2];
                String gender = genders[i%2];

                //set the parameters
                preparedStatement.setString(1, time);
                preparedStatement.setString(2, time);
                preparedStatement.setString(3, studentYear);
                preparedStatement.setString(4, gender);

                //execute SQL query
                resultSet = preparedStatement.executeQuery();
                
                //fencing
                while(resultSet.next()){
                    if(!ans.equals("")){
                        ans += "," + resultSet.getString(1);
                    }else{
                        ans = resultSet.getString(1);
                    }
                }
            }      
        } catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    public static String retrieveTopKPopularPlaces(String time, String topK){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        try{
            //get a connection to database
            connection = ConnectionManager.getConnection();            
            //prepare a statement
            preparedStatement = connection.prepareStatement("select n.locationname from (SELECT max(TIMESTAMP) as TIMESTAMP, macaddress FROM location WHERE timestamp BETWEEN ? AND (SELECT DATE_ADD(?,INTERVAL 15 MINUTE)) group by macaddress) l, location m, locationlookup n where l.macaddress = m.macaddress and m.timestamp = l.timestamp and m.locationid = n.locationid group by n.locationname order by count(n.locationname) desc limit ? ");

            //set the parameters
            preparedStatement.setString(1, time);
            preparedStatement.setString(2, time);
            int x = Integer.parseInt(topK);
            preparedStatement.setInt(3, x);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //fencing
                if(!ans.equals("")){
                    ans += "," + resultSet.getString(1);
                }else{
                    ans = resultSet.getString(1);
                }
            }       
        } catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

}
