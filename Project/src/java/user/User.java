/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yang
 */
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

