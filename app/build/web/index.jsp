<%@page import="is203.JWTUtility"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>
<%@page import="java.awt.SystemColor.*"%>

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
        html {
            display: table;
        }
        body {
            display: table-cell;
            vertical-align: middle;
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
            <!-- Form for user to input date&time and top K for top K popular places report -->
            <form method=post action="processLogin">
                <!-- form input for username  -->
                <div class="form-group">
                    <label class="form-control-label" for="username">Username:</label>
                    <input type="text" class="form-control" id="username" name="username" placeholder="Enter your username" required>
                </div>
                <!-- form input for password  -->
                <div class="form-group">
                    <label class="form-control-label" for="password">Password:</label>
                    <input type="text" class="form-control" id="password" name="password" placeholder="Enter your password" required>
                </div>
                <input type="hidden" name="timestamp" value="<%= new Timestamp(System.currentTimeMillis()).toString()%>">
                <button type="submit" class="btn btn-danger">Login</button>
            </form>
        </div>
    </form>





    <br><br><h4>Test accounts:</h4><h5>Admin:<br>admin<br>password</h5>
    <form method=post action="processLogin">
        <input type="hidden" name="username" value ="admin" required/>
        <input type="hidden" name="password" value ="password" required/>
        <input type="hidden" name="timestamp" value="<%= new Timestamp(System.currentTimeMillis()).toString()%>">
        <div align="left"><input type="submit" value ="Admin Login"/>
    </form>
    <h5>User:<br>zorro.fan.2013<br>zxcvbn1284</h5>
    <form method=post action="processLogin">
        <input type="hidden" name="username" value ="zorro.fan.2013" required/>
        <input type="hidden" name="password" value ="zxcvbn1284" required/>
        <input type="hidden" name="timestamp" value="<%= new Timestamp(System.currentTimeMillis()).toString()%>">
        <div align="left"><input type="submit" value ="Zorro fan Login"/>
    </form>
    <form method=post action="processLogin">
    </form>
</body>
</html>
