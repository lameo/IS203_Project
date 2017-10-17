<%-- 
    Document   : authenticate
    Created on : 17 Oct, 2017, 3:06:45 PM
    Author     : Yang
--%>

<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.google.gson.JsonObject"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="is203.JWTUtility"%>
<%@page import="model.UploadDAO"%>
<%@page import="model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Authenticate</title>
    </head>
    <body>

        <%
            //creates a new gson object
            //by instantiating a new factory object, set pretty printing, then calling the create method
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //creats a new json object for printing the desired json output
            JsonObject jsonOutput = new JsonObject();
            //create a json array to store errors
            JsonArray errMsg = new JsonArray();
            
            //user details, get using session
            User user = (User) session.getAttribute("user");
            String name = user.getName();
            String password = user.getPassword();
            jsonOutput.addProperty("Status", "Success");
            if(UploadDAO.verifyPassword(name, password)){
                String token = JWTUtility.sign("DQjq5Dv5DRrt4vAB", name);
                jsonOutput.addProperty("token", token);
                session.setAttribute("token", token);
            }else{
                jsonOutput.addProperty("token", "invalid username/password");
            }
            out.println(gson.toJson(jsonOutput));
        %> 
    </body>
</html>
