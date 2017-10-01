/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author xuying
 */
public class AutoGroupDAO {
    public static ArrayList<String> retrieveAutoIndividual(String endtimeDate){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> AutoGroupList = new ArrayList<String>();
        
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select macaddress, email, locationid, timestamp"
                    + "from user u, location l"
                    + "where u.macaddress = l.macaddress and timestamp between DATE_SUB(?, INTERVAL 15 MINUTE)"
                    + "and DATE_SUB(?, INTERVAL 1 SECOND) order by timestamp");

            //set the parameters
            preparedStatement.setString(1, endtimeDate);
            preparedStatement.setString(2, endtimeDate);
            
            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AutoGroupList.add(resultSet.getString(1)+","+resultSet.getString(2)+","+resultSet.getString(3)+","+resultSet.getString(4));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
}
