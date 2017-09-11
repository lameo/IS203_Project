<%@page import="java.sql.*"%>
<%@page import="user.User"%>
<%@page import="java.time.Instant"%>
<%@page import="java.awt.SystemColor.*"%>
<%@ include file="functions.jsp"%>

<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>SLOCA Login Page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <form method=post action="index.jsp">
        <center>
            <table>
                <tr>
                    <img src="resource/image/logo.png" width="260" height="100" />
                    <%  
                        /*String userName = request.getParameter("userName");                         //login failed message
                        if(userName==null){
                            out.print("<h1>Login Failed</h1>");
                        }*/
                    %>
                </tr><tr>
                    <td>Username:</td>
                    <td><input type="text" name="userName" size="20" /></td>                        <%-- username textbox --%>
                </tr><tr>
                    <td>Password</td>
                    <td><input type="password" name="password" size="20" /></td>                    <%-- password textbox --%>
                </tr><tr>
                    <td></td>
                    <td><div align="right"><input type="submit" value ="Login"/></div></td>         <%-- submit button --%>
                </tr>
            </table>
        </center>
            <input type="hidden" name="hide" value="<%= new Timestamp(System.currentTimeMillis()).toString() %>">
            
        </form>
        <%
            //Login details, change to database asap
            String aUser = "admin";
            String aPass = "password1";
            String uUser = "testUser";
            String uPass = "password2";
            
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            
            String userName = request.getParameter("userName");
            String password = request.getParameter("password");
            //userName = validate(userName);
            //password = validate(password);
            
            //username and password checking, change to database when implemented
            try{
                //get a connection to database
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "");            
                
                //prepare a statement
                preparedStatement = connection.prepareStatement("select * from demographics where name = ? && password = ?");   
                
                //set the parameters
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, password);
                
                //execute SQL query
                resultSet = preparedStatement.executeQuery();
                
                while(resultSet.next()){
                    request.getRequestDispatcher("userPage.jsp").forward(request, response);                    
                }
               
                /*if(userName!=null){
                    User user = new User(userName, password);
                    String userType = user.validate1(userName,password);
                    if(userType.equals("admin")){
                        request.getRequestDispatcher("adminPage.jsp").forward(request, response);
                    }
                    if(userType.equals("user")){
                        request.getRequestDispatcher("userPage.jsp").forward(request, response);
                    }
                }*/      
            } catch (SQLException e){
                e.printStackTrace();
            }
            //debugging purpose
            out.print("<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>#Debug inputted value:<br>username: " + userName + "<br>password: " + password+"<br>");            
            out.print("<br><br><h4>Test accounts:</h4><h5>Admin:<br>username: admin<br>password: password1</h5>");
            out.print("<h5>User:<br>username: Zorro Fan<br>password: zxcvbn1284</h5>");
            
        %>
    </body>
</html>
