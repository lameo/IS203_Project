package user;
public class User {
    private String USERNAME;
    private String password;
    
    public User(String USERNAME, String password){
        this.USERNAME = USERNAME;
        this.password = password;
    }


    public static String validate1(String USERNAME, String password){
        //Admin
        if(USERNAME.equals("admin") && password.equals("password1")){
            return "admin";
        }else if(USERNAME.equals("user") && password.equals("password2")){
            return "user";
        }
        return "failed";
    }
}

