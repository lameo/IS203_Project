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
import java.util.HashMap;

/**
 *
 * @author xuying
 */
//retreive groups of users
public class AutoGroupDAO {
    public static ArrayList<Group> retrieveAutoGroups(String endtimeDate){
        HashMap<String,ArrayList<String>> AutoUsers = retrieveAutoUsers(endtimeDate);
        ArrayList<Group> groups = new ArrayList<Group>();
        
        
        
        
        return groups;
    }
    
    //retreive users in hashmap form, hashmap key is macaddress and hashmap value is array of email, locationid and timestamp
    public static HashMap<String,ArrayList<String>> retrieveAutoUsers(String endtimeDate){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<String,ArrayList<String>> AutoUsers = new HashMap<String,ArrayList<String>>();
        ArrayList<String> UserInfo = new ArrayList<String>();
        
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
                //create arraylist of email, locationid and timestamp
                UserInfo.add(resultSet.getString(2)+","+resultSet.getString(3)+","+resultSet.getString(4));
                //create hashmap of macaddress
                AutoUsers.put(resultSet.getString(1),UserInfo);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return AutoUsers;
    }
    
    
    
}