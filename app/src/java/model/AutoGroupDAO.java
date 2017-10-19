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
        HashMap<String, TreeMap<Timestamp, ArrayList<Long>>> AutoUsersbyTimestampStart = retreiveAutoUsersByTimestampStart(AutoUsers, endtimeDate);
        ArrayList<Group> Groups = retreiveGroups(AutoUsersbyTimestampStart);
        return groups;
    }

    //retreive users with timeline in hashmap form, hashmap key is macaddress and hashmap value is array of locationid and timestamp
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

    //retreive auto users in hashmap form, key is macaddress, value is a hashmap, key is location, value is time start and time end
    /**
     *
     * @param AutoUsers
     * @param endtimeDate
     * @return
     */
    public static HashMap<String, TreeMap<Timestamp, ArrayList<Long>>> retreiveAutoUsersByTimestampStart(HashMap<String, ArrayList<String>> AutoUsers, String endtimeDate) {
        //loop for each Automatic user identification
        String timeDateStart = null;
        Date timeDateEnd = null;
        String[] locationtimestamp = null;
        Timestamp timestampStart = null;
        Timestamp timestampEnd = null;
        long duration = 0L;
        boolean update = true;
        HashMap<String, TreeMap<Timestamp, ArrayList<Long>>> AutoUsersByTimestampStart = new HashMap<String, TreeMap<Timestamp, ArrayList<Long>>>();
        TreeMap<Timestamp, ArrayList<Long>> TimestampStarts = new TreeMap<Timestamp, ArrayList<Long>>();
        ArrayList<Long> LocationTime = new ArrayList<Long>();
        return null;
    }

}
