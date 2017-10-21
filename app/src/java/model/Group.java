/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author xuying
 */
public class Group {
    ArrayList<String> AutoUsers;
    ArrayList<String> locationTimestamps;

    public Group(ArrayList<String> AutoUsers, ArrayList<String> locationTimestamps) {
        this.AutoUsers = AutoUsers;
        this.locationTimestamps = locationTimestamps;
    }

    public ArrayList<String> getAutoUsers() {
        return AutoUsers;
    }

    public ArrayList<String> getLocationTimestamps() {
        return locationTimestamps;
    }

    
    
    
    
    
}
