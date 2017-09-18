<%@page import="java.sql.Timestamp"%>
<%@page import="model.User"%>
<%@page import="java.time.Instant"%>
<%@page import="java.awt.SystemColor.*"%>
<!DOCTYPE html>
<html>
    <head>
        <title>SLOCA Login Page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <form method=post action="processLogin"> <%-- send data to LoginServlet M-V-C model --%>
        <center>
            <table>
                <tr>
                    <img src="resource/image/logo.png" width="260" height="100" />
                    <%
                        String error = (String)session.getAttribute("error"); //error message retrieved from LoginServlet
                        if(error!=null && error.length()>=1){
                            out.println("<font color='red'>" + "<br/>" + error + "</font");
                            session.invalidate(); //clear user session
                        }
                    %>
                </tr><tr>
                    <td>Username:</td>
                    <td><input type="text" name="username" size="20" placeholder="Enter your username" required/></td> <%-- username textbox --%>
                </tr><tr>
                    <td>Password:</td>
                    <td><input type="password" name="password" size="20" placeholder="Enter your password" required/></td> <%-- password textbox --%>
                </tr><tr>
                    <td></td>
                    <td><div align="right"><input type="submit" value ="Login"/></div></td> <%-- submit button --%>
                </tr>
            </table>
        </center>
            <input type="hidden" name="timestamp" value="<%= new Timestamp(System.currentTimeMillis()).toString() %>">
        </form>
        <%
            //debugging purpose
            out.print("<br><br><br><br><br><br><br><br><br><br><br>");
            out.print("<br><br><h4>Test accounts:</h4><h5>Admin:<br>username: admin<br>password: password</h5>");
            out.print("<h5>User:<br>username: zorro.fan.2010<br>password: zxcvbn1284</h5>");

        %>
    </body>
</html>
