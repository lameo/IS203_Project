package user;
public class User {
    private String username;
    private String password;
    private String timestamp;
    
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }


    public static String validate1(String username, String password){
        //Admin
        if(username.equals("admin") && password.equals("password1")){
            return "admin";
        }else if(username.equals("user") && password.equals("password2")){
            return "user";
        }
        return "failed";
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getPassword(){
        return password;
    }
    
    public String getTimestamp(){
        return timestamp;
    }
    
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
}

