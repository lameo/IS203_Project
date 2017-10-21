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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        //HashMap<String, TreeMap<Timestamp, ArrayList<Long>>> AutoUsersbyTimestampStart = retreiveAutoUsersByTimestampStart(AutoUsers, endtimeDate);
        //ArrayList<Group> Groups = retreiveGroups(AutoUsersbyTimestampStart);
        return groups;
    }

    //retreive users' location traces including location, timestart, timeend at SIS building in specified time window
    public static HashMap<String, ArrayList<String>> retrieveAutoUsers(String endtimeDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<String> UsersInfo = new ArrayList<String>();

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select macaddress, locationid, timestamp from location where timestamp between DATE_SUB(?, INTERVAL 20 MINUTE) and DATE_SUB(?, INTERVAL 1 SECOND) order by timestamp");

            //set the parameters
            preparedStatement.setString(1, endtimeDate);
            preparedStatement.setString(2, endtimeDate);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String macaddress = resultSet.getString(1);
                String locationid = resultSet.getString(2);
                String timestring = resultSet.getString(3);
                //create UserInfo arraylist contains all user timelines
                UsersInfo.add(macaddress + "," + locationid + "," + timestring);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        HashMap<String, ArrayList<String>> AutoUsers = new HashMap<String, ArrayList<String>>();
        for (int i = 0; i < UsersInfo.size(); i += 1) {
            String[] UserInfo = UsersInfo.get(i).split(",");
            String macaddress = UserInfo[0];
            String locationid = UserInfo[1];
            String timestring = UserInfo[2];
            if (AutoUsers.containsKey(macaddress)) {
                ArrayList<String> timeline = AutoUsers.get(macaddress);
                timeline.add(locationid + "," + timestring);
                AutoUsers.put(macaddress, timeline);
            } else {
                ArrayList<String> timeline = new ArrayList<String>();
                timeline.add(locationid + "," + timestring);
                AutoUsers.put(macaddress, timeline);
            }
        }
        return AutoUsers;
    }
    
    //Valify if user has stayed at least 12 minutes at SIS building in specified time window
    public static boolean AutoUser12Mins(ArrayList<String> AutoUserLocationTimestamps) throws ParseException{
        double timeDuration = 0;
        for (int i=0;i<AutoUserLocationTimestamps.size();i++){
            String[] LocationTimestamp = AutoUserLocationTimestamps.get(i).split(",");
            String timeStart = LocationTimestamp[1];
            String timeEnd = LocationTimestamp[2];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
            java.util.Date timestampStart = dateFormat.parse(timeStart);//convert time string to Date format
            java.util.Date timestampEnd = dateFormat.parse(timeEnd);
            //calculate the time duration for each user location trace in seconds
            timeDuration += (timestampEnd.getTime() - timestampStart.getTime()) / (1000.0);
        }
        //check if total time duration is at least 12 minutes
        if(timeDuration>=720){
            return true;
        }else{
            return false;
        }
    }
    
    //retreive all the users whom stay at SIS building in specified time window
    public static ArrayList<String> retreiveAutoUserMacaddresses(String timestringEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        ArrayList<String> AutoUsers = new ArrayList<String>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct macaddress from location where timestamp between DATE_SUB(?, INTERVAL 15 minute) and DATE_SUB(?,INTERVAL 1 second)");

            //set the parameters
            preparedStatement.setString(1, timestringEnd);
            preparedStatement.setString(2, timestringEnd);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AutoUsers.add(resultSet.getString(1));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //UserLocationTimestamps.add("1010110032"+","+"014-03-24 09:07:27.000000"+","+"1");
        return AutoUsers;
    }

}
