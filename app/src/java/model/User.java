package model;
public class User {
    private String macaddress;
    private String name;
    private String password;
    private String email;
    private char gender;
    
    /**
     *
     * Constructs a User object, which has a mac-address, name, password, email
     * and gender
     *
     */    
    public User(String macaddress, String name, String password, String email, char gender){
        this.macaddress = macaddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
    }
    
    /**
     * Get name
     *
     * @return name - full name of user
     */    
    public String getName(){
        return name;
    }
    
    /**
     *
     * Get password
     *
     * @return password of the user
     */    
    public String getPassword(){
        return password;
    }
    
    /**
     * Get macaddress
     *
     * @return macaddress - the MAC address indicating the unique id of
     * a user's device
     */
    public String getMacaddress() {
        return macaddress;
    }
    
     /**
     * Get email
     *
     * @return email - email of user
     */
    public String getEmail() {
        return email;
    } 
    
     /**
     *
     * Get gender
     *
     * @return gender - gender of user
     */
    public char getGender() {
        return gender;
    }
}

