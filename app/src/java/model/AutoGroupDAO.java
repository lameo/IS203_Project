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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xuying
 */
//retreive groups of users,
public class AutoGroupDAO {

    public static boolean CommonLocationTimestamps(ArrayList<String> LocationTimestamps1, ArrayList<String> LocationTimestamps2) {

        boolean CommonLocationTimestamps12Mins = false;
        int count = 0;
        //if one common location found from user1 and user2, or no common locations found, exit the while loop
        while (CommonLocationTimestamps12Mins = true || count >= LocationTimestamps1.size() - 1) {
            //check if they have common locations
            for (int i = 0; i < LocationTimestamps1.size(); i++) {
                count++;
                String[] LocationTimestamp1 = LocationTimestamps1.get(i).split(",");
                String location1 = LocationTimestamp1[0];
                for (int j = 0; j < LocationTimestamps2.size(); j++) {
                    String[] LocationTimestamp2 = LocationTimestamps1.get(i).split(",");
                    String location2 = LocationTimestamp2[0];
                    //if common location found from user1 and user2
                    if (location1.equals(location2)) {
                        return true;
                    }
                }
            }
        }
        //if no common locations found from user1 and user2, it means no commonlocationtimestamps and returns false
        if (CommonLocationTimestamps12Mins = false) {
            return false;
        } else {
            return true;
        }
    }

    public static ArrayList<String> CommonLocationTimestamps12Mins(ArrayList<String> LocationTimestamps1, ArrayList<String> LocationTimestamps2) {

        ArrayList<String> CommonLocationTimestamps = new ArrayList<String>();
        double totalDuration = 0;
        try {
            //check common location timestamps from user1 and user2
            for (int i = 0; i < LocationTimestamps1.size(); i++) {
                String[] LocationTimestamp1 = LocationTimestamps1.get(i).split(",");
                String location1 = LocationTimestamp1[0];
                String timestringStart1 = LocationTimestamp1[1];
                String timestringEnd1 = LocationTimestamp1[2];
                for (int j = 0; j < LocationTimestamps2.size(); j++) {
                    String[] LocationTimestamp2 = LocationTimestamps1.get(i).split(",");
                    String location2 = LocationTimestamp2[0];
                    String timestringStart2 = LocationTimestamp1[1];
                    String timestringEnd2 = LocationTimestamp1[2];
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date timestampStart1 = dateFormat.parse(timestringStart1);//convert time string to Date format
                    java.util.Date timestampEnd1 = dateFormat.parse(timestringEnd1);
                    java.util.Date timestampStart2 = dateFormat.parse(timestringStart2);//convert time string to Date format
                    java.util.Date timestampEnd2 = dateFormat.parse(timestringEnd2);
                    if (location1.equals(location2)) {
                        //if common location found from user1 and user2
                        //if timestart of user 1 is before or equal to user 2 and timeend of user 1 equal or after user 2,
                        //common timestamps are from time start to time end of user 2
                        if (!timestampStart1.after(timestampStart2) && !timestampEnd1.before(timestampEnd2)) {
                            CommonLocationTimestamps.add(location2 + "," + timestringStart2 + "," + timestringEnd2);
                            totalDuration += (timestampEnd2.getTime() - timestampStart2.getTime()) / (1000.0);
                            //if timestart of user 2 is before or equal to user 1 and timeend of user 2 equal or after user 1,
                            //common timestamps are from time start to time end of user 1
                        } else if (!timestampStart2.after(timestampStart1) && timestampEnd2.before(timestampEnd1)) {
                            CommonLocationTimestamps.add(location1 + "," + timestringStart1 + "," + timestringEnd1);
                            totalDuration += (timestampEnd1.getTime() - timestampStart1.getTime()) / (1000.0);
                            //if timestart of user 1 is before or equal to user 2 and timeend of user 1 before user 2,
                            //common timestamps are from time start of user 2 to time end of user 1
                        } else if (!timestampStart1.after(timestampStart2) && timestampEnd1.before(timestampEnd2)) {
                            CommonLocationTimestamps.add(location1 + "," + timestringStart2 + "," + timestringEnd1);
                            totalDuration += (timestampEnd1.getTime() - timestampStart2.getTime()) / (1000.0);
                            //if timestart of user 1 is before or equal to user 2 and timeend of user 1 before user 2,
                            //common timestamps are from time start of user 2 to time end of user 1
                        } else if (!timestampStart2.after(timestampStart2) && timestampEnd2.before(timestampEnd1)) {
                            CommonLocationTimestamps.add(location1 + "," + timestringStart1 + "," + timestringEnd2);
                            totalDuration += (timestampEnd2.getTime() - timestampStart1.getTime()) / (1000.0);
                        }
                    }
                }

            }
            //check if total time duration is at least 12 minutes
            if (totalDuration >= 720) {
                return CommonLocationTimestamps;
            }
        } catch (ParseException ex) {
            Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return CommonLocationTimestamps;
    }

    //check for each user if they spend at least 12 minutes together
    public static Group retrieveAutoGroups(Map<String, ArrayList<String>> ValidAutoUsers) {
        Group group = null;
        Set<String> macaddresses = ValidAutoUsers.keySet();
        for (String macaddress1 : macaddresses) {
            ArrayList<String> LocationTimestamps1 = ValidAutoUsers.get(macaddress1);
            for (String macaddress2 : macaddresses) {
                ArrayList<String> LocationTimestamps2 = ValidAutoUsers.get(macaddress2);
                if (!macaddress2.equals(macaddress1)) {
                    //check if they have common locations
                    if (CommonLocationTimestamps(LocationTimestamps1, LocationTimestamps2)) {
                        //if two users have stayed for at least 12 minutes, record their common timestamps and durations
                        //check if they have stayed together for at least 12 minutes
                        ArrayList<String> LocationTimestamps = CommonLocationTimestamps12Mins(LocationTimestamps1, LocationTimestamps2);
                        if (LocationTimestamps != null || LocationTimestamps.size() >= 1) {
                            //if two users have stayed for at least 12 minutes, record their common timestamps and durations
                            ArrayList<String> Users = new ArrayList<String>();
                            Users.add(macaddress1);
                            Users.add(macaddress2);
                            group = new Group(Users, LocationTimestamps);
                        }
                    }
                }
            }
        }
        return group;
    }

    //Valify if user has stayed at least 12 minutes at SIS building in specified time window
    public static boolean AutoUser12Mins(ArrayList<String> AutoUserLocationTimestamps) {
        double timeDuration = 0;
        for (int i = 0; i < AutoUserLocationTimestamps.size(); i++) {
            try {
                String[] LocationTimestamp = AutoUserLocationTimestamps.get(i).split(",");
                String timeStart = LocationTimestamp[1];
                String timeEnd = LocationTimestamp[2];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date timestampStart = dateFormat.parse(timeStart);//convert time string to Date format
                java.util.Date timestampEnd = dateFormat.parse(timeEnd);
                //calculate the time duration for each user location trace in seconds
                timeDuration += (timestampEnd.getTime() - timestampStart.getTime()) / (1000.0);
            } catch (ParseException ex) {
                Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //check if total time duration is at least 12 minutes
        if (timeDuration >= 720) {
            return true;
        } else {
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
