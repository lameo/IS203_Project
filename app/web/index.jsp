<%@page import="model.SharedSecretManager"%>
<%@page import="java.awt.SystemColor.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>
<%@page import="is203.JWTUtility"%>

<!DOCTYPE html>
<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="https://bootswatch.com/darkly/bootstrap.min.css" rel="stylesheet"> <%-- twitter bootstrap for designing--%>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script> <%-- twitter bootstrap for designing--%>
    <script src='js/bootstrap.js'></script> <%-- twitter bootstrap for designing--%>
    <head>
        <title>SLOCA Login Page</title>
    </head>
    <style>
        body {
            margin: 0;
            position: absolute;
            top: 50%;
            left: 50%;
            margin-right: -50%;
            transform: translate(-50%, -50%)
        }
    </style>
    <body>
    <center>
        <img src="resource/image/Capture.JPG" width="1029" height="204"> <%-- smu logo for login page --%>
        <%
            String error = (String) session.getAttribute("error"); //error message retrieved from LoginServlet
            if (error != null && error.length() >= 1) { //if there is an error
                out.println("<font color='red'>" + "<br/>" + error + "</font>");
                session.invalidate(); //clear user session
            }
        %>

    </center>
    <form method=post action="processLogin"> <%-- send data to LoginServlet M-V-C model --%>
        <div class="container">
            <br><br>
            <!-- Form for user to input username and password -->
            <form method=post action="processLogin">
                <!-- form input for username  -->
                <div class="form-group">
                    <label class="form-control-label" for="username">Username:</label>
                    <input type="text" class="form-control" id="username" name="username" placeholder="Enter your username" required>
                </div>
                <!-- form input for password  -->
                <div class="form-group">
                    <label class="form-control-label" for="password">Password:</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="Enter your password" required>
                </div>
                <button type="submit" class="btn btn-danger">Login</button>
            </form>
        </div>
    </form>

    <%
        // quick login buttons
        boolean debug = true;
        if (debug) {
            out.print("<br><br><form method=post action=\"processLogin\"><input type=\"hidden\" name=\"username\" value=\"admin\"><input type=\"hidden\" name=\"password\" value=\"Password!SE888\"><button type=\"submit\" class=\"btn btn-danger\">Admin login</button></form><br>");
            out.print("<form method=post action=\"processLogin\"><input type=\"hidden\" name=\"username\" value=\"zorro.fan.2013\"><input type=\"hidden\" name=\"password\" value=\"zxcvbn1284\"><button type=\"submit\" class=\"btn btn-danger\">User login</button></form><br>");
            
            String token = SharedSecretManager.authenticateUser("zorro.fan.2013");
            out.print("<form method=post action=");
            out.print("http://localhost:8084/app/json/heatmap?floor=2&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" +token +">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("heatmap");
            out.print("</button>\r\n</form><br>");
            
            
            out.print("<form method=post action=");
            out.print("http://localhost:8084/app/json/basic-loc-report?order=year,gender,school&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" +token +">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("breakdown");
            out.print("</button>\r\n</form><br>");
            
            out.print("<form method=post action=");
            out.print("http://localhost:8084/app/json/top-k-popular-places?k=10&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" +token +">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("TopK Popular Places");
            out.print("</button>\r\n</form><br>");
            
            out.print("<form method=post action=");
            out.print("http://localhost:8084/app/json/top-k-companions?k=10&mac-address=a2935f43f2227c7adba65c18888c4553c70d0462&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" +token +">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("TopK Companions");
            out.print("</button>\r\n</form><br>");
            
            out.print("<form method=post action=");
            out.print("http://localhost:8084/app/json/top-k-next-places?k=10&origin=SMUSISB1NearATM&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" +token +">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("TopK Next Places");
            out.print("</button>\r\n</form><br>");
            
            out.print("<form method=post action=");
            out.print("http://localhost:8084/app/json/group_detect?date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" +token +">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("AGD");
            out.print("</button>\r\n</form><br>");
        }
        
    %>
</body>
</html>
