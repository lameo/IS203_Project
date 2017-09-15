package model;
public class User {
    private String macaddress;
    private String name;
    private String password;
    private String email;
    private char gender;
    private String timestamp;
    
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

    public User(String name, String password){ //REMOVE THIS LATER, PUT HERE FOR NOW TO MAKE CURRENT CODE WORK~~
        this.name = name;
        this.password = password;
    }

    public static String validate1(String username, String password){ //REMOVE THIS AND PUT IN LoginServlet.java
        //Admin
        if(username.equals("admin") && password.equals("password1")){
            return "admin";
        }else if(username.equals("user") && password.equals("password2")){
            return "user";
        }
        return "failed";
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
 
     /**
     *
     * Get timestamp
     *
     * @return timestamp - The time user login to the website
     */    
    public String getTimestamp(){
        return timestamp;
    }
    
     /**
     *
     * Set timestamp of the user
     *
     * 
     */        
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
}

