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
        HashMap<String, HashMap<Timestamp, ArrayList<Long>>> AutoUserbyTimestampStart = retreiveAutoUsersByTimestampStart(AutoUsers, endtimeDate);
        ArrayList<Group> Groups = retreiveGroups(AutoUserbyTimestampStart);
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
    /**
     *
     * @param AutoUsers
     * @param endtimeDate
     * @return
     */
    public static HashMap<String, HashMap<Timestamp, ArrayList<Long>>> retreiveAutoUsersByTimestampStart(HashMap<String, ArrayList<String>> AutoUsers, String endtimeDate) {
        //loop for each Automatic user identification
        String timeDateStart = null;
        Date timeDateEnd = null;
        String[] locationtimestamp = null;
        Timestamp timestampStart = null;
        Timestamp timestampEnd = null;
        long duration = 0L;
        boolean update = true;
        HashMap<String, HashMap<Timestamp, ArrayList<Long>>> AutoUserByTimestampStart = new HashMap<String, HashMap<Timestamp, ArrayList<Long>>>();
        HashMap<Timestamp, ArrayList<Long>> TimestampStarts = new HashMap<Timestamp, ArrayList<Long>>();
        ArrayList<Long> LocationTime = new ArrayList<Long>();
        for (Map.Entry<String, ArrayList<String>> AutoUser : AutoUsers.entrySet()) {
            //a set of user macaddress
            String Macaddress = AutoUser.getKey();
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Date parsedStartDate = (Date) dateFormat.parse(timeDateStart);
                    timestampStart = new java.sql.Timestamp(parsedStartDate.getTime());
                    //situation where only one location update under user, users is assumed to stay at location for 5 minutes
                    if (LocationTimestamps.size() == 1) {
                        timeDateEnd = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(parsedStartDate);
                        cal.add(Calendar.MINUTE, 5);
                        //timeDateEnd is 5 minutes after timeDateStart
                        timeDateEnd = (Date) cal.getTime();
                        //get the time in long
                        timestampEnd = new java.sql.Timestamp(timeDateEnd.getTime());
                    } else {
                        //check the next update location
                        String LocationTimestampNext = LocationTimestamps.get(i + 2);
                        //split string into location and timestamp
                        String[] locationtimestampNext = LocationTimestamp.split(",");
                        //location stores location id string
                        String locationNext = locationtimestamp[0];
                        //timeDateStart store first timestamp string under user
                        String timeDateStartNext = locationtimestamp[1];
                        //timedateend of this update is the start of the next location

                        timeDateEnd = (Date) dateFormat.parse(timeDateStartNext);

                    }
                } catch (Exception e) { //this generic but you can control another types of exception
                    // look the origin of excption 
                }
                //check the previous update location
                //if the previous update location is not this update location
                if (i == 0 || LocationTime.get(LocationTime.size() - 3) != Long.parseLong(location)) {
                    //get the time duration between start and end in minutes
                    duration = TimeUnit.MILLISECONDS.toMinutes(timestampEnd.getTime() - timestampStart.getTime());
                    //convert long from string to long and add to arraylist locationtime
                    LocationTime.add(Long.parseLong(location));
                    //add duration of start and end datetime to arraylist locationtime
                    LocationTime.add(duration);
                    //add timedateend to arraylist locationtime
                    LocationTime.add(timeDateEnd.getTime());
                    //add timestampStart in TimestampStarts as key, ArrayList LocationTime as values
                    TimestampStarts.put(timestampStart, LocationTime);
                } else {
                    //if the previous update location is this update location, 
                    update = false;
                    duration += TimeUnit.MILLISECONDS.toMinutes(timestampEnd.getTime() - timestampStart.getTime());
                    //add duration of start and end datetime to arraylist locationtime
                    LocationTime.set(LocationTime.size()-2,duration);
                    //add timedateend to arraylist locationtime
                    LocationTime.set(LocationTime.size()-1,timeDateEnd.getTime());
                }

            }
            if(update){
                //add timestampStart in TimestampStarts as key, ArrayList LocationTime as values
                AutoUserByTimestampStart.put(Macaddress, TimestampStarts);
            }
            
        }
        return AutoUserByTimestampStart;
    }
    
    public static ArrayList<Group> retreiveGroups(HashMap<String, HashMap<Timestamp, ArrayList<Long>>> AutoUserbyTimestampStart){
        ArrayList<Group> groups = new ArrayList<Group>();
        return groups;
    }

}
