package model;

import java.util.logging.Logger;
import java.util.logging.Level;
import is203.JWTException;
import is203.JWTUtility;

public class SharedSecretManager {
    private static final String sharedSecret = "DQjq5Dv5DRrt4vAB";

    public static String authenticateUser(String name){
        String token = JWTUtility.sign(sharedSecret, name);
        return token;
    }

    public static String authenticateAdmin(){
        String token = JWTUtility.sign(sharedSecret, "admin");
        return token;
    }
    
    public static boolean verifyUser(String token) {
        try {
            String valid = JWTUtility.verify(token, sharedSecret);
            if(valid==null){
                return false;
            }
        } catch (JWTException ex) {
            Logger.getLogger(SharedSecretManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static boolean verifyAdmin(String token) {
        try {
            String valid = JWTUtility.verify(token, sharedSecret);
            if(valid==null){
                return false;
            }
            return valid.equals("admin");
        } catch (JWTException ex) {
            Logger.getLogger(SharedSecretManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
