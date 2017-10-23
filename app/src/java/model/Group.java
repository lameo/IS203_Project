/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author xuying
 */
public class Group {

    ArrayList<String> AutoUsersMacs;
    ArrayList<String> locationTimestamps;

    public Group(ArrayList<String> AutoUsersMac, ArrayList<String> locationTimestamps) {
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
        if(sameUser==AutoGroup2Size){
            return true;
        }
        return false;
    }

    //check if user can join the group, return the common locationstamps
    public ArrayList<String> JoinGroup(String macaddress2, ArrayList<String> LocationTimestamps2) {
        ArrayList<String> LocationTimestamps = null;
        //check if user is already in the group
        if (AutoUsersMacs.contains(macaddress2)) {
            //check if user and group has stayed for at least 12 minutes
            LocationTimestamps = AutoGroupDAO.CommonLocationTimestamps12Mins(locationTimestamps, LocationTimestamps2);
        }

        return LocationTimestamps;
    }

    //add new autouser to existing group, add user macaddress and change location timestamps
    public void addAutoUser(String macaddress2, ArrayList<String> NewLocationTimestamps) {
        AutoUsersMacs.add(macaddress2);
        setLocationTimestamps(NewLocationTimestamps);
    }
}
