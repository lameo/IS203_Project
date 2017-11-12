package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;
import java.util.TreeMap;

public class Group implements Comparable<Group> {

    private ArrayList<String> autoUsersMacs;
    private Map<String, ArrayList<String>> commonLocationTimestamps;

    /**
     * Constructor for new Group object 
     * @param autoUsersMacs ArrayList containing all macaddress to lookup for
     * @param commonLocationTimestamps ArrayList object containing the shared timestamp between the specific user and user in the list
     */
    public Group(ArrayList<String> autoUsersMacs, Map<String, ArrayList<String>> commonLocationTimestamps) {
        this.autoUsersMacs = autoUsersMacs;
        this.commonLocationTimestamps = commonLocationTimestamps;
    }

    /**
     * Getter method for ArrayList of all the other users who are group together with the specific user
     * @return ArrayList autoUsersMacs
     */
    public ArrayList<String> getAutoUsersMacs() {
        return this.autoUsersMacs;
    }

    /**
     * Getter method for ArrayList of all common location timestamp of the users present and specific user
     * @return ArrayList common location timestamp
     */
    public Map<String, ArrayList<String>> getLocationTimestamps() {
        return this.commonLocationTimestamps;
    }

    /**
     * Retrieve a specific macaddress from the list of all the other users who are group together with the specific user
     * @param i int Index of arrayList to return
     * @return String macaddress of the particular user present at the index
     */
    public String getAutoUserMac(int i) {
        return this.autoUsersMacs.get(i);
    }

    /**
     * Getter method for the qty of user who are present in the same location as the specific user
     * @return int number of user
     */
    public int getAutoUsersSize() {
        return this.autoUsersMacs.size();
    }

    /**
     * Compares group with another group for order. Returns a negative integer, zero, or a positive integer as the second group is less than, equal to, or greater than the first group
     * @param o the object to be compared.
     * @return int a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Group o) {
        if(o.getAutoUsersSize()==getAutoUsersSize()){
            return (int)(o.calculateTotalDuration() - calculateTotalDuration());
        }else{
           return o.getAutoUsersSize() - getAutoUsersSize(); 
        }
        
    }

    //retrieve macaddress of groups AutoUser is in
    /*
    public HashMap<String,ArrayList<String>> RetrieveAutoGroups(){
        HashMap<String,ArrayList<String>> AutoGroups = new HashMap<String,ArrayList<String>>();
        for(int i=0; i<getAutoUsersSize()-1; i++){
            if(AutoUserMac.equals(AutoUsersMac.get(i))){
                put()
            }
        }
        return null;
    }
     */
    /**
     * Setter method for AutoUsersMacs
     * @param autoUsersMacs ArrayList to be updated to 
     */
    public void setAutoUsersMacs(ArrayList<String> autoUsersMacs) {
        this.autoUsersMacs = autoUsersMacs;
    }

    /**
     * Setter method for locationTimestamps
     * @param locationTimestamps ArrayList to be updated to
     */
    public void setLocationTimestamps(Map<String, ArrayList<String>> locationTimestamps) {
        this.commonLocationTimestamps = locationTimestamps;
    }

    /**
     * Calculate total time duration of the location timestamps of the group for each location
     * @return Map containing key of the location and value of the duration where the 2 (or more) users are together
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
                String TimestringStart = timestamp[0];
                String TimestringEnd = timestamp[1];
                duration += Double.parseDouble(timestamp[2]);
                java.util.Date timestampStart = null;//convert time string to Date format
                java.util.Date timestampEnd = null;
                /*try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    timestampStart = dateFormat.parse(TimestringStart);//convert time string to Date format
                    timestampEnd = dateFormat.parse(TimestringEnd);
                } catch (ParseException ex) {
                    Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                //duration = (timestampEnd.getTime() - timestampStart.getTime()) / 1000.0 + 1;

            }
            if (!locationDuration.containsKey(location)) {
                locationDuration.put(location, duration);
            } 
        }
        return locationDuration;
    }
    
    /**
     * Calculate total time spend together of the group
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
                String TimestringStart = timestamp[0];
                String TimestringEnd = timestamp[1];
                duration += Double.parseDouble(timestamp[2]);
            }
        }
        return duration;
    }

    /**
     * Retrieves email of the user (or error message if none found) in a csv format
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
    
    public TreeMap<String, String> retrieveEmailsWithMacs(){
        TreeMap<String, String> sortedUserMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < autoUsersMacs.size(); i++) {
            String AutoUserMac = autoUsersMacs.get(i);
            String email = ReportDAO.retrieveEmailByMacaddress(AutoUserMac);
            if (email != null && email.length() > 0) {
                //email = "No email found";
                sortedUserMap.put(email, AutoUserMac);
            }
            
        }
        return sortedUserMap;
    }
    
    public TreeMap<String, String> retrieveMacsNoEmails(){
        TreeMap<String, String> sortedUserMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < autoUsersMacs.size(); i++) {
            String AutoUserMac = autoUsersMacs.get(i);
            String email = ReportDAO.retrieveEmailByMacaddress(AutoUserMac);
            if (email == null || email.length() <= 0) {
                email = "";
                sortedUserMap.put(AutoUserMac, email);
            }
        }
        return sortedUserMap;
    }

    //check if user can join the group, return the common locationstamps
    public Map<String, ArrayList<String>> joinGroup(String macaddress2, Map<String, ArrayList<String>> locationTimestamps2) {
        Map<String, ArrayList<String>> newLocationTimestamps = new HashMap<String, ArrayList<String>>();
        if (autoUsersMacs != null && autoUsersMacs.size() > 0) {
            //check if user is already in the group
            if (!autoUsersMacs.contains(macaddress2)) {
                //check if user and group has stayed for at least 12 minutes
                newLocationTimestamps = AutoGroupDAO.commonLocationTimestamps12Mins(commonLocationTimestamps, locationTimestamps2);
            }
        }
        return newLocationTimestamps;
    }
    
    //add new autouser to existing group, add user macaddress and change location timestamps
    public void addAutoUser(String macaddress2, Map<String, ArrayList<String>> newLocationTimestamps) {
        autoUsersMacs.add(macaddress2);
        setLocationTimestamps(newLocationTimestamps);
    }

    /**
     * Return boolean value whether the two groups are the same or is a subgroup of another larger group
     * @param newAutoGroup Group second group to compare to
     * @return boolean True if one of the group is subgroup of another one
     */
    public boolean checkSubGroup(Group newAutoGroup) {
        if (newAutoGroup == null) {
            return false;
        }
        
        ArrayList<String> newAutoUsersMacs = newAutoGroup.getAutoUsersMacs();
        int newAutoGroupSize = newAutoGroup.getAutoUsersSize();
        
        if(getAutoUsersSize()<newAutoGroupSize){
            return false;
        }
        
        return autoUsersMacs.containsAll(newAutoUsersMacs);
    }       
    
    /**
     * Check if two group is the same or is a subgroup of another larger group, returning the group number to remove
     * @param AutoGroup2 Group second group to compare to
     * @return int Group number that is a subgroup of the other group object
     */
    public int removeSubGroup(Group AutoGroup2) {
        Map<ArrayList<String>, Integer> AutoGroups = new HashMap<ArrayList<String>, Integer>();
        ArrayList<String> AutoUsersMacs2 = AutoGroup2.getAutoUsersMacs();
        int AutoGroup2Size = AutoGroup2.getAutoUsersSize();
        int sameUser = 0;
        for (int j = 0; j < getAutoUsersSize(); j++) {
            
            for (int i = 0; i < AutoUsersMacs2.size() - 1; i++) {
                if (AutoUsersMacs2.get(i).equals(getAutoUserMac(j))) {
                    sameUser++;
                }
            }
        }
        if (getAutoUsersSize() >= AutoGroup2Size) {
            return 2;
        } else {
            return 1;
        }
    }
 
    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    /**
     * Compare whether the 2 group object are identical
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
