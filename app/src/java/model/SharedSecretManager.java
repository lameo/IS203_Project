package model;

import is203.JWTException;
import is203.JWTUtility;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A class that generates and validates unique token to validate that authorized users
 */
public class SharedSecretManager {
    private static final String SHAREDSECRET = "DQjq5Dv5DRrt4vAB";

    /**
     *
     * Generates unique token that could be verified if it is a valid user
     * 
     * @param name String username
     * @return String unique token
     *
     */ 
    public static String authenticateUser(String name){
        String token = JWTUtility.sign(SHAREDSECRET, name);
        return token;
    }

    /**
     *
     * Generates unique token that could be verified if it is a valid admin
     * 
     * @return String unique token
     *
     */ 
    public static String authenticateAdmin(){
        String token = JWTUtility.sign(SHAREDSECRET, "admin");
        return token;
    }
    
    /**
     *
     * Checks whether it is a valid user
     * 
     * @param token String token of the user
     * @return Boolean value of the validation status of the user 
     *
     */  
    public static boolean verifyUser(String token) {
        try {
            String valid = JWTUtility.verify(token, SHAREDSECRET);
            if(valid==null){
                return false;
            }
        } catch (JWTException ex) {
            Logger.getLogger(SharedSecretManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     *
     * Checks whether it is a valid admin
     * 
     * @param token String token of the user
     * @return Boolean value of the validation status of the user 
     *
     */ 
    public static boolean verifyAdmin(String token) {
        try {
            String valid = JWTUtility.verify(token, SHAREDSECRET);
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
