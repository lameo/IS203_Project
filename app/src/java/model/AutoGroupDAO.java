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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//retreive groups of users,
public class AutoGroupDAO {

    public static boolean CommonLocationTimestamps(ArrayList<String> LocationTimestamps1, ArrayList<String> LocationTimestamps2) {

        //boolean CommonLocationTimestamps12Mins = false;
        //int count = 0;
        //if one common location found from user1 and user2, or no common locations found, exit the while loop
        //while (CommonLocationTimestamps12Mins || count <= LocationTimestamps1.size() - 1) {
        //check if they have common locations
        for (int i = 0; i < LocationTimestamps1.size(); i++) {

            String[] LocationTimestamp1 = LocationTimestamps1.get(i).split(",");
            String location1 = LocationTimestamp1[0];
            for (int j = 0; j < LocationTimestamps2.size(); j++) {
                String[] LocationTimestamp2 = LocationTimestamps1.get(j).split(",");
                String location2 = LocationTimestamp2[0];
                //if common location found from user1 and user2
                if (location1.equals(location2)) {
                    return true;

                }
            }
        }
        //count++;
        //}
        //if no common locations found from user1 and user2, it means no commonlocationtimestamps and returns false
        /*if (CommonLocationTimestamps12Mins = false) {
            return CommonLocationTimestamps12Mins;
        } else {
            return true;
        }*/
        return false;
    }

    public static Map<String, ArrayList<String>> CommonLocationTimestamps12Mins(Map<String, ArrayList<String>> locationTimestamps1, Map<String, ArrayList<String>> locationTimestamps2) {

        Map<String, ArrayList<String>> commonLocationTimestamps = new HashMap<String, ArrayList<String>>();

        double totalDuration = 0;
        Iterator<String> locations1 = locationTimestamps1.keySet().iterator();
        //check common location timestamps from user1 and user2
        while (locations1.hasNext()) {
            String location1 = locations1.next();
            ArrayList<String> timestamps1 = locationTimestamps1.get(location1);
            if (locationTimestamps2.containsKey(location1)) {//check if user 2 has visited location 1
                //if user 2 has visited location 1
                String commonLocation = location1;
                ArrayList<String> timestamps2 = locationTimestamps2.get(location1);//retrieve timestamps of users 2 at location 1
                ArrayList<String> commonTimestamps = new ArrayList<String>();
                for (int i = 0; i < timestamps1.size(); i++) {
                    String[] timestamp1 = timestamps1.get(i).split(",");
                    String timestringStart1 = timestamp1[0];
                    String timestringEnd1 = timestamp1[1];
                    for (int j = 0; j < timestamps2.size(); j++) {

                        String[] timestamp2 = timestamps2.get(j).split(",");
                        String timestringStart2 = timestamp2[0];
                        String timestringEnd2 = timestamp2[1];
                        java.util.Date timestampStart1 = null;//convert time string to Date format
                        java.util.Date timestampEnd1 = null;
                        java.util.Date timestampStart2 = null;//convert time string to Date format
                        java.util.Date timestampEnd2 = null;
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            timestampStart1 = dateFormat.parse(timestringStart1);//convert time string to Date format
                            timestampEnd1 = dateFormat.parse(timestringEnd1);
                            timestampStart2 = dateFormat.parse(timestringStart2);//convert time string to Date format
                            timestampEnd2 = dateFormat.parse(timestringEnd2);
                        } catch (ParseException ex) {
                            Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //if (location1.equals(location2)) {
                        //if common location found from user1 and user2
                        //if timestart of user 1 is before or equal to user 2 and timeend of user 1 equal or after user 2,
                        //common timestamps are from time start to time end of user 2
                        double gap = 0;
                        if (!timestampStart1.after(timestampStart2) && !timestampEnd1.before(timestampEnd2)) {

                            gap = (timestampEnd2.getTime() - timestampStart2.getTime()) / (1000.0);
                            commonTimestamps.add(timestringStart2 + "," + timestringEnd2 + "," + gap);
                            //commonTimestamps.add("total time duration 1 " + gap);
                            //if timestart of user 2 is before or equal to user 1 and timeend of user 2 equal or after user 1,
                            //common timestamps are from time start to time end of user 1
                        } else if (!timestampStart2.after(timestampStart1) && !timestampEnd2.before(timestampEnd1)) {

                            gap = (timestampEnd1.getTime() - timestampStart1.getTime()) / (1000.0);
                            commonTimestamps.add(timestringStart1 + "," + timestringEnd1 + "," + gap);
                            //commonTimestamps.add("total time duration 2 " + gap);
                            //if timestart of user 1 is before or equal to user 2 and timeend of user 1 before user 2,
                            //common timestamps are from time start of user 2 to time end of user 1
                        } else if (!timestampStart1.after(timestampStart2) && timestampEnd1.after(timestampStart2) && timestampEnd1.before(timestampEnd2)) {

                            //commonTimestamps.add("timestamp1: "+timestampStart1+timestampEnd1+" timestamp2: "+timestampStart2+timestampEnd2);
                            gap = (timestampEnd1.getTime() - timestampStart2.getTime()) / (1000.0);
                            commonTimestamps.add(timestringStart2 + "," + timestringEnd1 + "," + gap);
                            //commonTimestamps.add("total time duration 3 " + gap);
                            //if timestart of user 1 is before or equal to user 2 and timeend of user 1 before user 2,
                            //common timestamps are from time start of user 2 to time end of user 1
                        } else if (!timestampStart2.after(timestampStart1) && timestampEnd2.after(timestampStart1) && timestampEnd2.before(timestampEnd1)) {

                            gap = (timestampEnd2.getTime() - timestampStart1.getTime()) / (1000.0);
                            //commonTimestamps.add("total time duration 4 " + gap);
                            commonTimestamps.add(timestringStart1 + "," + timestringEnd2 + "," + gap);
                        }
                        //}
                        totalDuration += gap;
                        //commonTimestamps.add("total duration: "+totalDuration);
                    }
                }
                //check if there is any common timestamps at common location
                if (commonTimestamps != null && commonTimestamps.size() > 0) {
                    commonLocationTimestamps.put(commonLocation, commonTimestamps);//add common location and timestamps
                }
            }
        }
        //check if total time duration is at least 12 minutes
        if (totalDuration >= 720) {
            return commonLocationTimestamps;
        }

        return null;
    }

    //check for each user if they spend at least 12 minutes together
    public static ArrayList<Group> retrieveAutoGroups(Map<String, Map<String, ArrayList<String>>> AutoUsers) {
        ArrayList<Group> AutoGroups = new ArrayList<Group>();
        Iterator<String> AutoUsersMacs1 = AutoUsers.keySet().iterator();
        ArrayList<String> test = new ArrayList<String>();
        //int count = 0;
        while (AutoUsersMacs1.hasNext()) {
            /*count++;
            if (count > 100) {
                return AutoGroups;
            }*/
            //retrieve autouser mac1
            String AutoUserMac1 = AutoUsersMacs1.next();
            Map<String, ArrayList<String>> locationTimestamps1 = AutoUsers.get(AutoUserMac1);
            Iterator<String> AutoUsersMacs2 = AutoUsers.keySet().iterator();
            while (AutoUsersMacs2.hasNext()) {
                //retrieve autouser mac2
                String AutoUserMac2 = AutoUsersMacs2.next();
                Map<String, ArrayList<String>> locationTimestamps2 = AutoUsers.get(AutoUserMac2);
                if (!AutoUserMac1.equals(AutoUserMac2)) {
                    //check if they have common locations
                    //if (CommonLocationTimestamps(LocationTimestamps1, LocationTimestamps2)) {
                    //if two users have stayed for at least 12 minutes, record their common timestamps and durations
                    //check if they have stayed together for at least 12 minutes
                    Map<String, ArrayList<String>> commonLocationTimestamps = CommonLocationTimestamps12Mins(locationTimestamps1, locationTimestamps2);
                    //test.add("common location timestamps: "+commonLocationTimestamps);
                    if (commonLocationTimestamps != null && commonLocationTimestamps.size() > 0) {
                        //if two users have stayed for at least 12 minutes, record their common timestamps and durations
                        ArrayList<String> Users = new ArrayList<String>();
                        Users.add(AutoUserMac1);
                        Users.add(AutoUserMac2);
                        //add two users to a group
                        Group NewAutoGroup = new Group(Users, commonLocationTimestamps);
                        //test.add("new auto group: "+NewAutoGroup);
                        boolean subgroup = false;
                        //AutoGroups.add(NewAutoGroup);
                        //return AutoGroups;
                        //check if can join the autouser2 to an existing group if he/she has stayed with group at least 12 minutes
                        //check if autogroup is not empty
                        //test

                        if (AutoGroups != null && AutoGroups.size() > 0) {
                            for (Group AutoGroup : AutoGroups) {
                                //test
                                //if (AutoGroup != null && AutoUserMac2 != null && LocationTimestamps != null && LocationTimestamps.size() > 0) {
                                //find new location timestamps for new group
                                Map<String, ArrayList<String>> NewLocationTimestamps = AutoGroup.JoinGroup(AutoUserMac2, commonLocationTimestamps);
                                //test.add("new location timestamps: "+NewLocationTimestamps);
                                //if found, add this as a new group
                                if (NewLocationTimestamps != null && NewLocationTimestamps.size() > 0) {
                                    AutoGroup.addAutoUser(AutoUserMac2, NewLocationTimestamps);
                                }
                                if (AutoGroup.CheckSubGroup(NewAutoGroup)) {
                                    subgroup = true;
                                }
                                //}
                            }
                        }

                        //if no subgroup, add new group to autogroups
                        if (!subgroup) {

                            //check if this group already exists (in different sequence) or if this group is a sub group of existing group
                            //add two users to same group
                            AutoGroups.add(NewAutoGroup);
                            //test.add("autogroups: "+AutoGroups);
                        }

                    }
                    //}

                }
            }
            //After finish adding groups for macaddress1

        }
        return AutoGroups;
    }

    //
    public static ArrayList<Group> CheckAutoGroups(ArrayList<Group> autoGroups) {
        //ArrayList<Group> NewAutoGroups = AutoGroups;
        Iterator<Group> newAutoGroups = autoGroups.iterator();

        while (newAutoGroups.hasNext()) {
            Group autoGroup1 = newAutoGroups.next();
            ArrayList<String> autoGroup1Macs = autoGroup1.getAutoUsersMacs();
            while (newAutoGroups.hasNext()) {
                Group autoGroup2 = newAutoGroups.next();
                if (!autoGroup1.equals(autoGroup2)) {
                    if(autoGroup1.CheckSubGroup(autoGroup2)){
                        newAutoGroups.remove();
                    }
                }
            }
        }
        return autoGroups;
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
    public static Map<String, Map<String, ArrayList<String>>> retreiveAutoUsers(String timestringEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ans = "";
        Map<String, ArrayList<String>> AutoUsers = new HashMap<>();
        Map<String, Map<String, ArrayList<String>>> autoUsersTraces = new HashMap<>();
        ArrayList<String> test = new ArrayList<String>();
        int timeStartIndex = -1;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct macaddress, locationid, timestamp, TIMESTAMPDIFF(second,timestamp,?) as diff from location where timestamp between DATE_SUB(?, INTERVAL 15 minute) and ?");

            //set the parameters
            preparedStatement.setString(1, timestringEnd);
            preparedStatement.setString(2, timestringEnd);
            preparedStatement.setString(3, timestringEnd);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String mac = resultSet.getString(1);
                String locationid = resultSet.getString(2);
                String timeString = resultSet.getString(3);
                String timeDiff = resultSet.getString(4);
                if (AutoUsers.containsKey(mac)) {
                    ArrayList<String> locationTraces = AutoUsers.get(mac);
                    locationTraces.add(locationid + "," + timeString + "," + timeDiff);
                    AutoUsers.put(mac, locationTraces);
                } else {
                    ArrayList<String> locationTraces = new ArrayList<String>();
                    locationTraces.add(locationid + "," + timeString + "," + timeDiff);
                    AutoUsers.put(mac, locationTraces);
                }
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Iterator<String> AutoUsersMacs = AutoUsers.keySet().iterator();
        while (AutoUsersMacs.hasNext()) {
            java.util.Date timestampStart = null;
            //retrieve autouser mac
            String AutoUserMac = AutoUsersMacs.next();
            //timeStartIndex = -1;
            int totalTimeDuration = 0;//track total time duration the user has stayed at SIS building in time window
            ArrayList<String> locationTraces = AutoUsers.get(AutoUserMac);//retreive location traces of user
            //test.add("location array: " + locationTraces);
            //create hashmap of location map
            Map<String, ArrayList<String>> locationMap = new HashMap<String, ArrayList<String>>();
            //check if users has more than 2 location updates, since 2 location updates at most last 10 minutes
            if (locationTraces.size() > 2) {
                for (int i = 0; i < locationTraces.size(); i++) {
                    //timeStartIndex = -1;
                    String[] locationTrace = locationTraces.get(i).split(",");
                    //test.add("current location trace: " + locationTrace);
                    String locationid = locationTrace[0];
                    String timeString = locationTrace[1];
                    int timeDiff = Integer.parseInt(locationTrace[2]);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    //test.add("i: " + i + "location size: " + locationTraces.size());
                    Calendar cal = Calendar.getInstance();
                    java.util.Date timestamp = null;
                    java.util.Date timestampEnd = null;

                    try {
                        timestampEnd = dateFormat.parse(timestringEnd);//get end time stamps
                        timestamp = dateFormat.parse(timeString);//convert time string to Date format
                        //check if start time is before current time and retreive start time if yes
                        if (timestampStart != null) {
                            //timestampStart = dateFormat.parse(locationTraces.get(timeStartIndex));
                            timestamp = timestampStart;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (locationTraces.size() <= i + 1) {//if last location update
                        //test.add(AutoUserMac + " last one");
                        //cal.setTime(timestampEnd);
                        //cal.add(Calendar.SECOND, -1);
                        //timestampEnd = cal.getTime();
                        double timeGap = timeDiff;
                        //if time gap is more than 5 minutes
                        if (timeGap > 300.0) {
                            timeGap = 300;//add 5 mins to time duration is time gap is more than 5 mins
                            cal.setTime(timestamp);
                            cal.add(Calendar.MINUTE, 5);
                            timestampEnd = cal.getTime();
                        }
                        //if locationMap already has timestamps of this locationid
                        ArrayList<String> timelist = new ArrayList<String>();
                        if (locationMap.containsKey(locationid)) {
                            timelist = locationMap.get(locationid);
                            //test.add("current timelist" + timelist);
                            timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampEnd));
                            //test.add("added timelist" + timelist);
                            locationMap.put(locationid, timelist);
                            //if not
                        } else {
                            timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampEnd));
                            //test.add("new timelist" + timelist + locationid);
                            locationMap.put(locationid, timelist);
                        }
                        timestampStart = null;
                        //timeStartIndex = -1;
                        totalTimeDuration += timeGap;//add time gap o total time durations
                        //if the next update location is same as the current one
                    } else {//if not last location updates
                        String[] locationTraceNext = locationTraces.get(i + 1).split(",");//get next location update
                        String locationidNext = locationTraceNext[0];
                        String timeStringNext = locationTraceNext[1];
                        int timeDiffNext = Integer.parseInt(locationTraceNext[2]);
                        java.util.Date timestampNext = null;
                        try {
                            timestampNext = dateFormat.parse(timeStringNext);//get next time stamps
                            //cal.setTime(timestampNext);
                            //cal.add(Calendar.SECOND, -1);
                            //timestampNext = cal.getTime();
                        } catch (ParseException ex) {
                            Logger.getLogger(AutoGroupDAO.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //test.add(AutoUserMac + " not last one " + i + "," + locationid + "," + locationidNext);
                        if (!locationid.equals(locationidNext)) {//if current and next location not same
                            double timeGap = timeDiff - timeDiffNext;
                            //if time gap is more than 5 minutes
                            if (timeGap > 300.0) {
                                timeGap = 300;//add 5 mins to time duration is time gap is more than 5 mins
                                cal.setTime(timestamp);
                                cal.add(Calendar.MINUTE, 5);
                                timestampNext = cal.getTime();
                                //totalTimeDuration += 300;//add 5 mins to time duration is time gap is more than 5 mins
                            }
                            //if locationMap already has timestamps of this locationid
                            ArrayList<String> timelist = new ArrayList<String>();
                            if (locationMap.containsKey(locationid)) {
                                timelist = locationMap.get(locationid);
                                timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext));
                                locationMap.put(locationid, timelist);
                                //if not
                            } else {
                                timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext));
                                locationMap.put(locationid, timelist);
                            }
                            timestampStart = null;
                            //timeStartIndex = -1;
                            totalTimeDuration += timeGap;//add time gap o total time durations
                            //if the next update location is same as the current one
                            //test.add(AutoUserMac + " diff location " + locationMap);
                        } else if (locationid.equals(locationidNext)) {
                            //test.add("same location" + timeStartIndex);
                            //check if previous location is same as previous one
                            double timeGap = timeDiff - timeDiffNext;
                            //test.add("timeGap same location " + timeGap);
                            //check if start time is before current time and retreive start time if yes
                            if (timestampStart == null) {
                                timestampStart = timestamp;
                                //timeStartIndex = i;
                                //test.add("set timestartindex to current i in same locations" + timestamp);
                            }
                            //test.add("timestartindex in same location" + timeStartIndex + "," + i);
                            //if time gap is more than 5 minutes
                            if (timeGap > 300.0) {
                                timeGap = 300;//add 5 mins to time duration is time gap is more than 5 mins
                                cal.setTime(timestamp);
                                cal.add(Calendar.MINUTE, 5);
                                timestampNext = cal.getTime();
                                //if locationMap already has timestamps of this locationid
                                ArrayList<String> timelist = new ArrayList<>();
                                if (locationMap.containsKey(locationid)) {
                                    timelist = locationMap.get(locationid);
                                    timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext));
                                    locationMap.put(locationid, timelist);
                                    //if not
                                } else {
                                    timelist.add(dateFormat.format(timestamp) + "," + dateFormat.format(timestampNext));
                                    locationMap.put(locationid, timelist);
                                }
                                //timeStartIndex = -1;
                                timestampStart = null;
                            }
                            totalTimeDuration += timeGap;//add time gap o total time duration
                        }
                        //test.add("locationmap not last one" + locationMap);
                    }
                }
                //test.add("end of mac1" + AutoUserMac + "," + locationMap + totalTimeDuration);
                //check if total time duration of user is longer or equal to 12 mins
                if (totalTimeDuration >= 720) {
                    //add location traces to user
                    if (!autoUsersTraces.containsKey(AutoUserMac)) {//check if auto user traces has current user
                        autoUsersTraces.put(AutoUserMac, locationMap);
                        //test.add("end of mac more than 12 mins " + AutoUserMac + "," + locationMap + totalTimeDuration);
                    }
                }
            }

        }
        //test.add("result: "+autoUsersTraces);
        return autoUsersTraces;
    }
    
    public static int retreiveUsersNumber(String timestringEnd) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;
        try {
            //get a connection to database
            connection = ConnectionManager.getConnection();

            //prepare a statement
            preparedStatement = connection.prepareStatement("select distinct macaddress from location where timestamp between DATE_SUB(?, INTERVAL 15 minute) and DATE_SUB(?, INTERVAL 1 second)");

            //set the parameters
            preparedStatement.setString(1, timestringEnd);
            preparedStatement.setString(2, timestringEnd);

            //execute SQL query
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count += 1;
            }

            //close connections
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

}
