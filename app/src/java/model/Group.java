package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;
import java.util.TreeMap;

public class Group implements Comparable<Group> {

    public ArrayList<String> AutoUsersMacs;
    public Map<String, ArrayList<String>> locationTimestamps;

    public Group(ArrayList<String> AutoUsersMacs, Map<String, ArrayList<String>> locationTimestamps) {
        //AutoUsersMacs = new ArrayList<String>();
        //locationTimestamps = new HashMap<String, ArrayList<String>>();
        this.AutoUsersMacs = AutoUsersMacs;
        this.locationTimestamps = locationTimestamps;
    }

    public ArrayList<String> getAutoUsersMacs() {
        return AutoUsersMacs;
    }

    public Map<String, ArrayList<String>> getLocationTimestamps() {
        return locationTimestamps;
    }

    public String getAutoUserMac(int i) {
        return AutoUsersMacs.get(i);
    }

    public int getAutoUsersSize() {
        return AutoUsersMacs.size();
    }

    @Override
    public int compareTo(Group o) {
        if(o.getAutoUsersSize()==getAutoUsersSize()){
            return (int)(o.CalculateTotalDuration() - CalculateTotalDuration());
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
    public void setAutoUsersMacs(ArrayList<String> AutoUsersMacs) {
        this.AutoUsersMacs = AutoUsersMacs;
    }

    public void setLocationTimestamps(Map<String, ArrayList<String>> locationTimestamps) {
        this.locationTimestamps = locationTimestamps;
    }

    //calculate total time duration of the location timestamps of the group for each location
    public Map<String, Double> CalculateTimeDuration() {
        Map<String, Double> locationDuration = new TreeMap<String, Double>();
        Iterator<String> locations = locationTimestamps.keySet().iterator();
        while (locations.hasNext()) {
            String location = locations.next();
            ArrayList<String> timestamps = locationTimestamps.get(location);
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
    
    //calculate total time duration of the group
    public double CalculateTotalDuration() {
        Iterator<String> locations = locationTimestamps.keySet().iterator();
        double duration = 0;
        while (locations.hasNext()) {
            String location = locations.next();
            ArrayList<String> timestamps = locationTimestamps.get(location);
            for (int i = 0; i < timestamps.size(); i++) {
                String[] timestamp = timestamps.get(i).split(",");
                String TimestringStart = timestamp[0];
                String TimestringEnd = timestamp[1];
                duration += Double.parseDouble(timestamp[2]);
            }
        }
        return duration;
    }

    public ArrayList<String> retrieveMacsWithEmails() {
        ArrayList<String> MacsWithEmails = new ArrayList<String>();
        for (int i = 0; i < AutoUsersMacs.size(); i++) {
            String AutoUserMac = AutoUsersMacs.get(i);
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
        for (int i = 0; i < AutoUsersMacs.size(); i++) {
            String AutoUserMac = AutoUsersMacs.get(i);
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
        for (int i = 0; i < AutoUsersMacs.size(); i++) {
            String AutoUserMac = AutoUsersMacs.get(i);
            String email = ReportDAO.retrieveEmailByMacaddress(AutoUserMac);
            if (email == null || email.length() <= 0) {
                email = "";
                sortedUserMap.put(AutoUserMac, email);
            }
        }
        return sortedUserMap;
    }

    //Check if two group is the same, or is a subgroup of another larger group, return the group number to remove
    public int RemoveSubGroup(Group AutoGroup2) {
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

    //Check if two group is the same, or is a subgroup of another larger group, return the group number to remove
    public boolean CheckSubGroup(Group NewAutoGroup) {
        if (NewAutoGroup == null) {
            return false;
        }
        
        ArrayList<String> AutoUsersMacs2 = NewAutoGroup.getAutoUsersMacs();
        int AutoGroup2Size = NewAutoGroup.getAutoUsersSize();
        if(getAutoUsersSize()<AutoGroup2Size){
            return false;
        }
        int sameUser = 0;
        if(AutoUsersMacs.containsAll(AutoUsersMacs2)){
            return true;
        }
        /*for (int j = 0; j < AutoGroup2Size; j++) {
            String mac = AutoUsersMacs2.get(j);
            if(AutoUsersMacs.contains(mac)){
                sameUser += 1;
            }
        }*/
        if (sameUser == AutoGroup2Size) {
            return true;
        }
        return false;
    }

    //check if user can join the group, return the common locationstamps
    public Map<String, ArrayList<String>> JoinGroup(String macaddress2, Map<String, ArrayList<String>> locationTimestamps2) {
        Map<String, ArrayList<String>> newLocationTimestamps = new HashMap<String, ArrayList<String>>();
        //test
        if (AutoUsersMacs != null && AutoUsersMacs.size() > 0) {
            //check if user is already in the group
            if (!AutoUsersMacs.contains(macaddress2)) {
                //check if user and group has stayed for at least 12 minutes
                newLocationTimestamps = AutoGroupDAO.commonLocationTimestamps12Mins(locationTimestamps, locationTimestamps2);
            }
        }

        return newLocationTimestamps;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

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
        if (!Objects.equals(this.AutoUsersMacs, other.AutoUsersMacs)) {
            return false;
        }
        if (!Objects.equals(this.locationTimestamps, other.locationTimestamps)) {
            return false;
        }
        return true;
    }

    //add new autouser to existing group, add user macaddress and change location timestamps
    public void addAutoUser(String macaddress2, Map<String, ArrayList<String>> NewLocationTimestamps) {
        AutoUsersMacs.add(macaddress2);
        setLocationTimestamps(NewLocationTimestamps);
    }

}
