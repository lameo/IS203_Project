/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author xuying
 */
//retreive groups of users,
public class AutoGroupDAO {

    public static ArrayList<Group> retrieveAutoGroups(String endtimeDate) {
        HashMap<String, ArrayList<String>> AutoUsers = retrieveAutoUsers(endtimeDate);
        ArrayList<Group> groups = new ArrayList<Group>();
        HashMap<String, HashMap<String, ArrayList<String>>> AutoUserbyLocation = retreiveAutoUsersByLocation(AutoUsers, endtimeDate);

        return groups;
    }

    //retreive users in hashmap form, hashmap key is macaddress and hashmap value is array of email, locationid and timestamp
    public static HashMap<String, ArrayList<String>> retrieveAutoUsers(String endtimeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        HashMap<String, ArrayList<String>> AutoUsers = new HashMap<String, ArrayList<String>>();
        ArrayList<String> UserInfo = new ArrayList<String>();

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select macaddress, locationid, timestamp"
                    + "from user u, location l"
                    + "where u.macaddress = l.macaddress and timestamp between DATE_SUB(?, INTERVAL 15 MINUTE)"
                    + "and DATE_SUB(?, INTERVAL 1 SECOND) order by timestamp");

            //set the parameters
            preparedStatement.setString(1, endtimeDate);
            preparedStatement.setString(2, endtimeDate);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //create arraylist of locationid and timestamp
                UserInfo.add(resultSet.getString(2) + "," + resultSet.getString(3));
                //create hashmap of macaddress
                AutoUsers.put(resultSet.getString(1), UserInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return AutoUsers;
    }

    //retreive auto users in hashmap form, key is macaddress, value is a hashmap, key is location, value is time start and time end
    public static HashMap<String, ArrayList<String>> retreiveAutoUsersByLocation(HashMap<String, ArrayList<String>> AutoUsers, String endtimeDate) {
        //loop for each Automatic user identification
        String timeDateStart = null;
        Date timeDateEnd = null;
        String[] locationtimestamp = null;
        Timestamp timestampStart = null;
        Timestamp timestampEnd = null;
        HashMap<String, HashMap<String, ArrayList<String>>> AutoUserByLocation = new HashMap<String, HashMap<String, ArrayList<String>>>();
        HashMap<String, ArrayList<String>> Locations = new HashMap<String, ArrayList<String>>();
        ArrayList<String> LocationTime = new ArrayList
        for (Map.Entry<String, ArrayList<String>> AutoUser : AutoUsers.entrySet()) {
            //a set of user macaddress
            String Macaddresses = AutoUser.getKey();
            //a collection of locationid and timestamp
            ArrayList<String> LocationTimestamps = AutoUser.getValue();

            //loop for each location and timestamp under the specific user
            for (int i = 0; i < LocationTimestamps.size(); i += 2) {
                String LocationTimestamp = LocationTimestamps.get(i);
                //split string into location and timestamp
                locationtimestamp = LocationTimestamp.split(",");
                //location stores location id string
                String location = locationtimestamp[0];
                //timeDateStart store first timestamp string under user
                timeDateStart = locationtimestamp[1];
                //add 5 minuts to startdatetime
                try {

                    //situation where only one location update under user, users is assumed to stay at location for 5 minutes
                    if (LocationTimestamps.size() == 1) {
                        //timeDateEnd is 5 minutes after timeDateStart
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                        Date parsedStartDate = (Date) dateFormat.parse(timeDateStart);
                        timestampStart = new java.sql.Timestamp(parsedStartDate.getTime());
                        timeDateEnd = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(parsedStartDate);
                        cal.add(Calendar.MINUTE, 5);
                        timeDateEnd = (Date) cal.getTime();
                        //get the time in long
                        timestampEnd = new java.sql.Timestamp(timeDateEnd.getTime());
                    }
                } catch (Exception e) { //this generic but you can control another types of exception
                    // look the origin of excption 
                }
                //get the time duration between start and end in minutes
                long duration = TimeUnit.MILLISECONDS.toMinutes(timestampStart.getTime()-timestampEnd.getTime);
                //
                if (i == LocationTimestamps.size() - 1) {
                    timeDateEnd = endtimeDate;
                }
            }
        }

    }

}
