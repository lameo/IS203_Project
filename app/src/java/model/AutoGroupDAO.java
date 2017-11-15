package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

//retrieve groups of users,
public class AutoGroupDAO {

    //retrieve all the users whom stay at SIS building in specified time window
    public static Map<String, Map<String, ArrayList<String>>> retrieveUsersWith12MinutesData(String userInputTime) {

        Map<String, ArrayList<String>> allUsersLocationAndTimestampMap = retrieveAllUsersLocationAndTimestamp(userInputTime);
        Map<String, Map<String, ArrayList<String>>> autoUsersTraces = new HashMap<>();

        Set<String> macaddresses = allUsersLocationAndTimestampMap.keySet();
        try {
            for (String eachMacaddress : macaddresses) {

                java.util.Date timestampStart = null;

                int totalTimeDuration = 0; //track total time duration the user has stayed at SIS building in time window
                ArrayList<String> eachLocationAndTimestamp = allUsersLocationAndTimestampMap.get(eachMacaddress); //retrieve locationid, timestamp, timediff

                Map<String, ArrayList<String>> locationMap = new HashMap<String, ArrayList<String>>(); //locationid, timestamp, timestampNext

                //check if users has more than 2 location updates, since 2 location updates only last 10 minutes, we need 12 minutes
                if (eachLocationAndTimestamp.size() > 2) {
                    for (int i = 0; i < eachLocationAndTimestamp.size(); i++) {
                        String[] locationTraces = eachLocationAndTimestamp.get(i).split(",");

                        String locationid = locationTraces[0];
                        String timestring = locationTraces[1];
                        int timeDiff = Integer.parseInt(locationTraces[2]);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Calendar cal = Calendar.getInstance();
                        java.util.Date timestamp = dateFormat.parse(timestring);//convert time string to Date format
                        java.util.Date timestampEnd = dateFormat.parse(userInputTime); //get end time stamps

                        //check if start time is before current time and retrieve start time if yes
                        if (timestampStart != null) {
                            timestamp = timestampStart;
                        }

                        if (i + 1 >= eachLocationAndTimestamp.size()) {//if last location update
                            double timeGap = timeDiff;

                            //if time gap is more than 5 minutes
                            if (timeGap > 300.0) {
                                timeGap = 300;//add 5 mins to time duration is time gap is more than 5 mins
                                cal.setTime(timestamp);
                                cal.add(Calendar.MINUTE, 5);
                                timestampEnd = cal.getTime();
                            }

                            ArrayList<String> timelist = locationMap.get(locationid);
                            //if locationMap already has timestamps of this locationid
                            if (timelist == null || timelist.size() <= 0) {
                                timelist = new ArrayList<String>();
                            }

                            timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampEnd));
                            locationMap.put(locationid, timelist);

                            timestampStart = null;
                            totalTimeDuration += timeGap;//add time gap of total time durations
                        } else { //if not last location updates
                            String[] locationTracesNext = eachLocationAndTimestamp.get(i + 1).split(","); //get next location update

                            String locationidNext = locationTracesNext[0];
                            String timestringNext = locationTracesNext[1];
                            int timeDiffNext = Integer.parseInt(locationTracesNext[2]);

                            java.util.Date timestampNext = dateFormat.parse(timestringNext); //get next time stamps

                            if (!locationid.equals(locationidNext)) { //if current and next location not same
                                double timeGap = timeDiff - timeDiffNext;

                                //if time gap is more than 5 minutes
                                if (timeGap > 300.0) {
                                    timeGap = 300; //add 5 mins to time duration is time gap is more than 5 mins
                                    cal.setTime(timestamp);
                                    cal.add(Calendar.MINUTE, 5);
                                    timestampNext = cal.getTime();
                                }

                                ArrayList<String> timelist = locationMap.get(locationid);
                                //if locationMap already has timestamps of this locationid
                                if (timelist == null || timelist.size() <= 0) {
                                    timelist = new ArrayList<String>();
                                }

                                timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext));
                                locationMap.put(locationid, timelist);

                                timestampStart = null;
                                totalTimeDuration += timeGap;//add time gap of total time durations

                            } else if (locationid.equals(locationidNext)) { //if the next update location is same as the current one
                                double timeGap = timeDiff - timeDiffNext;

                                //check if start time is before current time and retrieve start time if yes
                                if (timestampStart == null) {
                                    timestampStart = timestamp;
                                }

                                //if time gap is more than 5 minutes
                                if (timeGap > 300.0) {
                                    timeGap = 300; //add 5 mins to time duration is time gap is more than 5 mins
                                    cal.setTime(timestamp);
                                    cal.add(Calendar.MINUTE, 5);
                                    timestampNext = cal.getTime();
                                }
                                ArrayList<String> timelist = locationMap.get(locationid);
                                //if locationMap already has timestamps of this locationid
                                if (timelist == null || timelist.size() <= 0) {
                                    timelist = new ArrayList<String>();
                                }

                                timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext));
                                locationMap.put(locationid, timelist);

                                timestampStart = null;

                                totalTimeDuration += timeGap; //add time gap of total time duration
                            }
                        }
                    }
                    //check if total time duration of user is longer or equal to 12 mins
                    if (totalTimeDuration >= 720) {
                        //add location traces to user
                        autoUsersTraces.put(eachMacaddress, locationMap);
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autoUsersTraces;
    }

    public static Map<String, ArrayList<String>> retrieveAllUsersLocationAndTimestamp(String userInputTime) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, ArrayList<String>> allUsersLocationAndTimestampMap = new HashMap<>();
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct macaddress, locationid, timestamp, TIMESTAMPDIFF(second,timestamp,?) as diff from location where timestamp between DATE_SUB(?, INTERVAL 15 minute) and ? order by macaddress, timestamp, locationid");

            //set the parameters
            preparedStatement.setString(1, userInputTime);
            preparedStatement.setString(2, userInputTime);
            preparedStatement.setString(3, userInputTime);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String macaddress = resultSet.getString(1);
                String locationid = resultSet.getString(2);
                String timestamp = resultSet.getString(3);
                String timeDiff = resultSet.getString(4);

                ArrayList<String> locationTraces = allUsersLocationAndTimestampMap.get(macaddress);
                if (locationTraces == null || locationTraces.size() <= 0) {
                    locationTraces = new ArrayList<String>();
                }
                locationTraces.add(locationid + "," + timestamp + "," + timeDiff);
                allUsersLocationAndTimestampMap.put(macaddress, locationTraces);
            }
            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
        }
        return allUsersLocationAndTimestampMap;
    }

    //check for each user if they spend at least 12 minutes together
    public static ArrayList<Group> retrieveAutoGroups(Map<String, Map<String, ArrayList<String>>> listOfUsersWith12MinutesData) {
        ArrayList<Group> autoGroups = new ArrayList<Group>();
        Set<String> macaddresses = listOfUsersWith12MinutesData.keySet();

        for (String macaddress : macaddresses) {
            Map<String, ArrayList<String>> locationsMap = listOfUsersWith12MinutesData.get(macaddress);

            Set<String> nextMacaddresses = listOfUsersWith12MinutesData.keySet();
            for (String nextMacaddress : nextMacaddresses) {
                Map<String, ArrayList<String>> nextLocationsMap = listOfUsersWith12MinutesData.get(nextMacaddress);

                if (!macaddress.equals(nextMacaddress)) { //make sure is different macaddress
                    //check if they have common locations
                    //if (CommonLocationTimestamps(LocationTimestamps1, LocationTimestamps2)) {

                    //if two users have stayed for at least 12 minutes, record their common timestamps and durations
                    //check if they have stayed together for at least 12 minutes
                    Map<String, ArrayList<String>> commonLocationTimestamps = commonLocationTimestamps12Mins(locationsMap, nextLocationsMap);

                    if (commonLocationTimestamps != null && commonLocationTimestamps.size() > 0) {
                        //if two users have stayed for at least 12 minutes, record their common timestamps and durations
                        ArrayList<String> autoUsersMacs = new ArrayList<String>();
                        autoUsersMacs.add(macaddress);
                        autoUsersMacs.add(nextMacaddress);

                        //add two users to a group
                        Group newAutoGroup = new Group(autoUsersMacs, commonLocationTimestamps);

                        boolean subgroup = false;

                        //check if can join the autouser2 to an existing group if he/she has stayed with group at least 12 minutes
                        //check if autogroup is not empty
                        if (autoGroups.size() > 0) {
                            for (Group eachAutoGroup : autoGroups) {
                                //find new location timestamps for new group
                                Map<String, ArrayList<String>> newLocationTimestamps = eachAutoGroup.joinGroup(nextMacaddress, commonLocationTimestamps);

                                //if found, add this as a new group
                                if (newLocationTimestamps != null && newLocationTimestamps.size() > 0) {
                                    eachAutoGroup.addAutoUser(nextMacaddress, newLocationTimestamps);
                                }
                                subgroup = eachAutoGroup.checkSubGroup(newAutoGroup); //true or false
                            }
                        }

                        //if no subgroup, add new group to autogroups
                        if (!subgroup) {
                            //check if this group already exists (in different sequence) or if this group is a sub group of existing group
                            //add two users to same group
                            autoGroups.add(newAutoGroup);
                        }
                    }
                }
            }
            //After finish adding groups for macaddress1
        }
        return autoGroups;
    }

    /**
     * retrieve common timestamps at same location between two users if they stay together
     * at same locations for at least 12 minutes
     * @param locationsMap ArrayList of timestamps with locations 
     * @param nextLocationsMap ArrayList of all the automatic groups found
     * @return Map of common timestamps at same location between two users if they stay together
     * at same locations for at least 12 minutes, key is each common location and value is 
     * time start, time end and time gap between time start and end
     */
    public static Map<String, ArrayList<String>> commonLocationTimestamps12Mins(Map<String, ArrayList<String>> locationsMap, Map<String, ArrayList<String>> nextLocationsMap) {
        
        Map<String, ArrayList<String>> commonLocationTimestamps = new HashMap<String, ArrayList<String>>();
        //record the total time duration for each user
        double totalDuration = 0;
        //retrieve all the locations of first user
        Set<String> locations = locationsMap.keySet();
        try {
            //loop through all the locations of first user
            for (String location : locations) {
                //retrieve the timestamps of the location
                ArrayList<String> timestamps = locationsMap.get(location);
                //check if next user has visited the location
                if (nextLocationsMap.containsKey(location)) { 
                    //iif both first user and next user has visited the location
                    //this location is set to the common location between 2 users
                    String commonLocation = location;
                    //retrieve the timestamps of the next user at common location
                    ArrayList<String> nextTimestamps = nextLocationsMap.get(location);
                    ArrayList<String> commonTimestamps = new ArrayList<String>();
                    //loop through the timestamps of first user at common location
                    for (int i = 0; i < timestamps.size(); i++) {
                        //retrieve the timestamp of first user
                        String[] timestamps1 = timestamps.get(i).split(",");
                        //retrieve the time start of first user
                        String timestringStart = timestamps1[0];
                        //retrieve the time end of first user
                        String timestringEnd = timestamps1[1];
                        //loop through the timestamps of next user at common location
                        for (int j = 0; j < nextTimestamps.size(); j++) {
                            //retrieve the timestamp of next user
                            String[] timestamps2 = nextTimestamps.get(j).split(",");
                            //retrieve the time start of next user
                            String nextTimestringStart = timestamps2[0];
                            //retrieve the time end of next user
                            String nextTimestringEnd = timestamps2[1];
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //convert the time start of first user to date format
                            java.util.Date timestampStart = dateFormat.parse(timestringStart);
                            //convert the time end of first user to date format
                            java.util.Date timestampEnd = dateFormat.parse(timestringEnd);
                            //convert the time start of next user to date format
                            java.util.Date nextTimestampStart = dateFormat.parse(nextTimestringStart);
                            //convert the time end of first user to date format
                            java.util.Date nextTimestampEnd = dateFormat.parse(nextTimestringEnd);
                            //if timestart of first user is before or equal to next user
                            //and time end of first user equal or after next user,
                            //common timestamps are from time start to time end of next user
                            double gap = 0;
                            if (!timestampStart.after(nextTimestampStart) && !timestampEnd.before(nextTimestampEnd)) {
                                //calculate the time gap
                                gap = (nextTimestampEnd.getTime() - nextTimestampStart.getTime()) / (1000.0);
                                commonTimestamps.add(nextTimestringStart + "," + nextTimestringEnd + "," + gap);

                                //if timestart of next user is before or equal to first user 
                                //and time end of next user equal or after first user,
                                //common timestamps are from time start to time end of first user
                            } else if (!nextTimestampStart.after(timestampStart) && !nextTimestampEnd.before(timestampEnd)) {
                                //calculate the time gap
                                gap = (timestampEnd.getTime() - timestampStart.getTime()) / (1000.0);
                                commonTimestamps.add(timestringStart + "," + timestringEnd + "," + gap);

                                //if timestart of first user is before or equal to next user 
                                //and time end of first user before next user,
                                //common timestamps are from time start of next user to time end of first user
                            } else if (!timestampStart.after(nextTimestampStart) && timestampEnd.after(nextTimestampStart) && !timestampEnd.after(nextTimestampEnd)) {
                                //calculate the time gap
                                gap = (timestampEnd.getTime() - nextTimestampStart.getTime()) / (1000.0);
                                commonTimestamps.add(nextTimestringStart + "," + timestringEnd + "," + gap);

                                //if timestart of first user is before or equal to next users 
                                ///and time end of first user before next user,
                                //common timestamps are from time start of next user to time end of first user
                            } else if (!nextTimestampStart.after(timestampStart) && nextTimestampEnd.after(timestampStart) && !nextTimestampEnd.after(timestampEnd)) {
                                //calculate the time gap
                                gap = (nextTimestampEnd.getTime() - timestampStart.getTime()) / (1000.0);
                                commonTimestamps.add(timestringStart + "," + nextTimestringEnd + "," + gap);
                            }
                            //add the time gap to the total time duration
                            totalDuration += gap;
                        }
                    }
                    //check if there are any common timestamps at common location
                    if (commonTimestamps.size() > 0) {
                        //add common timestamps at common location to commonLocationTimestamps
                        commonLocationTimestamps.put(commonLocation, commonTimestamps);
                    }
                }
            }
            //return commonLocationTimestamps if total common time duration is at least 12 minutes
            if (totalDuration >= 720) {
                return commonLocationTimestamps;
            }
        } catch (ParseException ex) {
            Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        //return null if total common time duration is less than 12 minutes
        return null;
    }

    /**
     * check the automatic groups, remove the sub groups and same groups if any, return 
     * new unique automatic groups
     * @param autoGroups ArrayList of all the automatic groups found
     * @return ArrayList of groups for all the unique automatic groups with no 
     * same groups and sub groups
     */
    public static ArrayList<Group> checkAutoGroups(ArrayList<Group> autoGroups) {
        ArrayList<Group> newAutoGroups = new ArrayList<>();
        //loop through all the automatic groups found
        for (int i = 0; i < autoGroups.size(); i++) {
            //retrieve the first automatic group
            Group eachGroup = autoGroups.get(i);
            //retrieve the macaddresses of the first automatic group
            ArrayList<String> groupMacaddresses = eachGroup.getAutoUsersMacs();
            //sort the group macaddresses alphbetically
            Collections.sort(groupMacaddresses);
            //loop through all the automatic groups found
            for (int k = 0; k < autoGroups.size(); k++) {
                //retreive the next automatic group
                Group nextGroup = autoGroups.get(k);
                //retrieve the macaddresses of the next automatic group
                ArrayList<String> nextGroupMacaddresses = nextGroup.getAutoUsersMacs();
                //sort the group macaddresses alphbetically
                Collections.sort(nextGroupMacaddresses);
                //check if first group size is same or greater than the next group
                if (groupMacaddresses.size() >= nextGroupMacaddresses.size()) {
                    //check if first group contains all users of the next group
                    if (groupMacaddresses.containsAll(nextGroupMacaddresses)) {
                        //check if new automatic group contains the first group, if no, 
                        //add the first group to the new automatic group
                        if (!newAutoGroups.contains(eachGroup)) {
                            newAutoGroups.add(eachGroup);
                        }
                    //check if first group doesn't contains all users of the next group
                    } else {
                        //check if new automatic group contains the first group, if no, 
                        //add the first group to the new automatic group
                        if(!newAutoGroups.contains(eachGroup)){
                            newAutoGroups.add(eachGroup);
                        }
                        //check if new automatic group contains the next group, if no, 
                        //add the next group to the new automatic group
                        if(!newAutoGroups.contains(nextGroup)){
                            newAutoGroups.add(nextGroup);
                        }                        
                    }
                }
            }
        }
        return newAutoGroups;
    }
    
    /**
     * retrieve the number of users in the entire SIS building 15mins before the
     * specified time
     *
     * @param timestringEnd String in dd/mm/yyyy hh:mm:ss format for when the report is
     * generated
     * @return int of the number of users in the entire SIS building for that date and time
     *
     */
    public static int retrieveUsersNumber(String timestringEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select count(distinct macaddress) from location where timestamp between DATE_SUB(?, INTERVAL 15 minute) and DATE_SUB(?, INTERVAL 1 second)");

            //set the parameters
            preparedStatement.setString(1, timestringEnd);
            preparedStatement.setString(2, timestringEnd);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
        }
        return count;
    }

}
