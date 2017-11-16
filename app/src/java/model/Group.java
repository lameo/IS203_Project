package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Represents group object containing variable autoUsersMacs and
 * commonLocationTimestamps
 */
public class Group implements Comparable<Group> {

    private ArrayList<String> autoUsersMacs;
    private Map<String, ArrayList<String>> commonLocationTimestamps;

    /**
     * Constructor for new Group object
     *
     * @param autoUsersMacs ArrayList containing all macaddress to lookup for
     * @param commonLocationTimestamps ArrayList object containing the shared
     * timestamp between the specific user and user in the list
     */
    public Group(ArrayList<String> autoUsersMacs, Map<String, ArrayList<String>> commonLocationTimestamps) {
        this.autoUsersMacs = autoUsersMacs;
        this.commonLocationTimestamps = commonLocationTimestamps;
    }

    /**
     * Getter method for ArrayList of all the other users who are group together
     * with the specific user
     *
     * @return ArrayList autoUsersMacs
     */
    public ArrayList<String> getAutoUsersMacs() {
        return this.autoUsersMacs;
    }

    /**
     * Getter method for ArrayList of all common location timestamp of the users
     * present and specific user
     *
     * @return ArrayList common location timestamp
     */
    public Map<String, ArrayList<String>> getLocationTimestamps() {
        return this.commonLocationTimestamps;
    }

    /**
     * Retrieve a specific macaddress from the list of all the other users who
     * are group together with the specific user
     *
     * @param i int Index of arrayList to return
     * @return String macaddress of the particular user present at the index
     */
    public String getAutoUserMac(int i) {
        return this.autoUsersMacs.get(i);
    }

    /**
     * Getter method for the qty of user who are present in the same location as
     * the specific user
     *
     * @return int number of user
     */
    public int getAutoUsersSize() {
        return this.autoUsersMacs.size();
    }

    /**
     * Compares group with another group for order. Returns a negative integer,
     * zero, or a positive integer as the second group is less than, equal to,
     * or greater than the first group
     *
     * @param o the object to be compared.
     * @return int a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Group o) {
        if (o.getAutoUsersSize() == getAutoUsersSize()) {
            return (int) (o.calculateTotalDuration() - calculateTotalDuration());
        } else {
            return o.getAutoUsersSize() - getAutoUsersSize();
        }

    }

    /**
     * Setter method for AutoUsersMacs
     *
     * @param autoUsersMacs ArrayList to be updated to
     */
    public void setAutoUsersMacs(ArrayList<String> autoUsersMacs) {
        this.autoUsersMacs = autoUsersMacs;
    }

    /**
     * Setter method for locationTimestamps
     *
     * @param locationTimestamps ArrayList to be updated to
     */
    public void setLocationTimestamps(Map<String, ArrayList<String>> locationTimestamps) {
        this.commonLocationTimestamps = locationTimestamps;
    }

    /**
     * Calculate total time duration of the location timestamps of the group for
     * each location
     *
     * @return Map containing key of the location and value of the duration
     * where the 2 (or more) users are together
     */
    public Map<String, Double> calculateTimeDuration() {
        Map<String, Double> locationDuration = new TreeMap<String, Double>();
        Iterator<String> locations = commonLocationTimestamps.keySet().iterator();
        while (locations.hasNext()) {
            String location = locations.next();
            ArrayList<String> timestamps = commonLocationTimestamps.get(location);
            double duration = 0;
            for (int i = 0; i < timestamps.size(); i++) {
                String[] timestamp = timestamps.get(i).split(",");
                duration += Double.parseDouble(timestamp[2]);
            }
            if (!locationDuration.containsKey(location)) {
                locationDuration.put(location, duration);
            }
        }
        return locationDuration;
    }

    /**
     * Calculate total time spend together of the group
     *
     * @return double total time spend together of the group
     */
    public double calculateTotalDuration() {
        Iterator<String> locations = commonLocationTimestamps.keySet().iterator();
        double duration = 0;
        while (locations.hasNext()) {
            String location = locations.next();
            ArrayList<String> timestamps = commonLocationTimestamps.get(location);
            for (int i = 0; i < timestamps.size(); i++) {
                String[] timestamp = timestamps.get(i).split(",");
                duration += Double.parseDouble(timestamp[2]);
            }
        }
        return duration;
    }

    /**
     * Retrieves email of the user (or error message if none found) in a csv
     * format
     *
     * @return ArrayList list of macaddress and email in csv format
     */
    public ArrayList<String> retrieveMacsWithEmails() {
        ArrayList<String> MacsWithEmails = new ArrayList<String>();
        for (int i = 0; i < autoUsersMacs.size(); i++) {
            String AutoUserMac = autoUsersMacs.get(i);
            String email = ReportDAO.retrieveEmailByMacaddress(AutoUserMac);
            if (email == null || email.length() <= 0) {
                email = "No email found";
            }
            MacsWithEmails.add(AutoUserMac + "," + email);
        }
        return MacsWithEmails;
    }

    /**
     * retrieve macaddresses of users with email stored in database
     *
     * @return TreeMap of users with email stored in database, key:
     * macaddresses; value: email ("")
     */
    public TreeMap<String, String> retrieveEmailsWithMacs() {
        //create a sorted map to store users with email with case insensitive ascending order
        TreeMap<String, String> sortedUserMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        //loop through users in group
        for (int i = 0; i < autoUsersMacs.size(); i++) {
            //retrieve user in group
            String AutoUserMac = autoUsersMacs.get(i);
            //retrieve email of user
            String email = ReportDAO.retrieveEmailByMacaddress(AutoUserMac);
            //check if email of user is found
            if (email != null && email.length() > 0) {
                //store user macaddress as key and email as value to sorted maps
                sortedUserMap.put(email, AutoUserMac);
            }

        }
        return sortedUserMap;
    }

    /**
     * retrieve macaddresses of users without email stored in database
     *
     * @return TreeMap of users without email stored in database, key:
     * macaddresses; value: email ("")
     */
    public TreeMap<String, String> retrieveMacsNoEmails() {
        //create a sorted map to store users without email with case insensitive ascending order
        TreeMap<String, String> sortedUserMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        //loop through users in group
        for (int i = 0; i < autoUsersMacs.size(); i++) {
            //retrieve user in group
            String AutoUserMac = autoUsersMacs.get(i);
            //retrieve email of user
            String email = ReportDAO.retrieveEmailByMacaddress(AutoUserMac);
            //check if email of user is not found
            if (email == null || email.length() <= 0) {
                //set user email to empty ""
                email = "";
                //store user macaddress as key and email as value to sorted map
                sortedUserMap.put(AutoUserMac, email);
            }
        }
        return sortedUserMap;
    }

    /**
     * check if new user can join the existing group, if yes return the new
     * common timestamps with the existing group, if no return null
     *
     * @param macaddress2 macaddress of the new user
     * @param locationTimestamps2 timestamps of the new user at locations
     * @return Map of new common timestamps at same locations between the next
     * user and existing group, key: locations; value: timestamps at each
     * location
     */
    public Map<String, ArrayList<String>> joinGroup(String macaddress2, Map<String, ArrayList<String>> locationTimestamps2) {
        Map<String, ArrayList<String>> newLocationTimestamps = new HashMap<String, ArrayList<String>>();
        //check if existing group has users
        if (autoUsersMacs != null && autoUsersMacs.size() > 0) {
            //check if new user is already in the existing group
            if (!autoUsersMacs.contains(macaddress2)) {
                //check if new user and group has common timestamps at locations for at least 12 minutes
                newLocationTimestamps = AutoGroupDAO.commonLocationTimestamps12Mins(commonLocationTimestamps, locationTimestamps2);
            }
        }
        return newLocationTimestamps;
    }

    //
    /**
     * add new user to existing group
     *
     * @param macaddress2 macaddress of the new user
     * @param newLocationTimestamps new common timestamps at locations, key:
     * locations; value: timestamps at each location
     */
    public void addAutoUser(String macaddress2, Map<String, ArrayList<String>> newLocationTimestamps) {
        //add user macaddress to current user lists of existing group
        autoUsersMacs.add(macaddress2);
        //change timeline of group to new common timeline
        setLocationTimestamps(newLocationTimestamps);
    }

    /**
     * Return boolean value whether the two groups are the same or is a subgroup
     * of another larger group
     *
     * @param newAutoGroup Group second group to compare to
     * @return boolean True if one of the group is subgroup of another one
     */
    public boolean checkSubGroup(Group newAutoGroup) {
        if (newAutoGroup == null) {
            return false;
        }

        ArrayList<String> newAutoUsersMacs = newAutoGroup.getAutoUsersMacs();
        int newAutoGroupSize = newAutoGroup.getAutoUsersSize();

        if (getAutoUsersSize() < newAutoGroupSize) {
            return false;
        }

        return autoUsersMacs.containsAll(newAutoUsersMacs);
    }

    /**
     * Compare whether the 2 group object are identical
     *
     * @param obj Object to compare to
     * @return boolean whether the 2 objects are identical
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if (!Objects.equals(this.autoUsersMacs, other.autoUsersMacs)) {
            return false;
        }
        if (!Objects.equals(this.commonLocationTimestamps, other.commonLocationTimestamps)) {
            return false;
        }
        return true;
    }

}
