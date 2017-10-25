package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Group {

    private ArrayList<String> AutoUsersMacs;
    private ArrayList<String> locationTimestamps;

    public Group(ArrayList<String> AutoUsersMacs, ArrayList<String> locationTimestamps) {
        AutoUsersMacs = new ArrayList<String>();
        locationTimestamps = new ArrayList<String>();
        this.AutoUsersMacs = AutoUsersMacs;
        this.locationTimestamps = locationTimestamps;
    }

    public ArrayList<String> getAutoUsersMacs() {
        return AutoUsersMacs;
    }

    public ArrayList<String> getLocationTimestamps() {
        return locationTimestamps;
    }

    public String getAutoUserMac(int i) {
        return AutoUsersMacs.get(i);
    }

    public int getAutoUsersSize() {
        return AutoUsersMacs.size();
    }

    //retreive macaddress of groups AutoUser is in
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

    public void setLocationTimestamps(ArrayList<String> locationTimestamps) {
        this.locationTimestamps = locationTimestamps;
    }

    //calculate total time duration of the location timestamps of the group for each location
    public Map<String, Double> CalculateTimeDuration() {
        Map<String, Double> LocationDuration = new HashMap<String, Double>();
        for (int i = 0; i < locationTimestamps.size(); i++) {
            try {
                String[] LocationTimestamp = locationTimestamps.get(i).split(",");
                String locationid = LocationTimestamp[0];
                String TimestringStart = LocationTimestamp[1];
                String TimestringEnd = LocationTimestamp[2];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date timestampStart = dateFormat.parse(TimestringStart);//convert time string to Date format
                java.util.Date timestampEnd = dateFormat.parse(TimestringEnd);
                double duration = (timestampEnd.getTime() - timestampStart.getTime()) / 1000.0;
                if (LocationDuration.containsKey(locationid)) {
                    double CurrentDuration = LocationDuration.get(locationid);
                    CurrentDuration += duration;
                    //LocationDuration.remove(locationid);
                    LocationDuration.put(locationid, CurrentDuration);
                } else {
                    LocationDuration.put(locationid, duration);
                }
            } catch (ParseException ex) {
                Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return LocationDuration;
    }

    public ArrayList<String> retreiveMacsWithEmails() {
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
        if(NewAutoGroup==null){
            return false;
        }
        ArrayList<String> AutoUsersMacs2 = NewAutoGroup.getAutoUsersMacs();
        int AutoGroup2Size = NewAutoGroup.getAutoUsersSize();
        int sameUser = 0;
        for (int j = 0; j < getAutoUsersSize(); j++) {
            for (int i = 0; i < AutoUsersMacs2.size() - 1; i++) {
                if (AutoUsersMacs2.get(i).equals(getAutoUserMac(j))) {
                    sameUser++;
                }
            }
        }
        if (sameUser == AutoGroup2Size) {
            return true;
        }
        return false;
    }

    //check if user can join the group, return the common locationstamps
    public ArrayList<String> JoinGroup(String macaddress2, ArrayList<String> LocationTimestamps2) {
        ArrayList<String> LocationTimestamps = null;
        //test
        if (AutoUsersMacs != null && AutoUsersMacs.size() > 0) {
            //check if user is already in the group
            if (!AutoUsersMacs.contains(macaddress2)) {
                //check if user and group has stayed for at least 12 minutes
                LocationTimestamps = AutoGroupDAO.CommonLocationTimestamps12Mins(locationTimestamps, LocationTimestamps2);
            }
        }

        return LocationTimestamps;
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
    public void addAutoUser(String macaddress2, ArrayList<String> NewLocationTimestamps) {
        AutoUsersMacs.add(macaddress2);
        setLocationTimestamps(NewLocationTimestamps);
    }
    
    
}
