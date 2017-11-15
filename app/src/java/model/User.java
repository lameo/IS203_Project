package model;

/**
 * Represents user object containing variable name and password
 */
public class User {
    private String name;
    private String password;
    
    /**
     *
     * Constructs a User object, with variable name, password
     * 
     * @param name name of the user
     * @param password password of the user
     *
     */    
    public User(String name, String password){
        this.name = name;
        this.password = password;
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
}

