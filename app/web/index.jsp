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
        <link href="data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeqPVFXKh3DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFmW5gFXlup/Y57tNAAAAAAAAAAAAAAAAH1RFyF2VB8IAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJGz3j2ct9YLUpHuFUmP8NxLnP013QkADoJREaOATxGddlIhDwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB2qOQcXZjnbQAAAABRmfc4OY//72tjYMOZPgAzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFWV7T1Pk/GqAAAAAGJqfbs1jP//RJb/bAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR5b7f09+wOXMGwAnR5r/bTiH9P9AjPGvhbDbDwAAAAAAAAAAAAAAAAAAAACfttEVAAAAAAAAAAAAAAAAAAAAAAAAAABHgdDbOo38uwAAAABPk/B9Nob1/zyI8+RUlupJAAAAAAAAAAAAAAAAia3bKI2w3x4AAAAAAAAAAAAAAACCPgAFjEwFcEGS/Ms8ifLcjLLaEFWW8GY5h/T/N4f0/0KN8KRjneciAAAAAAAAAABtoedSg8vwAQAAAAAAAAAAhVYdCoNKBmsAAAAAQozwwTuI8v9Rle1GZ6LqPj2J8+Q1hfX/Ooj0/0CL8aNCi+9gRYzwsqPF3gcAAAAAAAAAAAAAAACBUhaBAAAAAAAAAABGjvGVO4nz/z6K8aBvpOQxSpDvfziG9OY2hvX/M4b1/z6L8t+nvMEpAAAAAAAAAAAAAAAAf1EUZ4JTGEYAAAAAAAAAAFiY6087ifPvOYjz/0KN8I1cmudAY5vpSE6R60xgnepaUZPquQAAAAAAAAAAAAAAAH9ODg2AUBOphlsmDgAAAAAAAAAAusvSCEeP73s7iPLxOYj0/z2J8+g9ifTCP4rx51CT7n8AAAAAAAAAAAAAAAAAAAAAg1MYO4JRErx/UhUqAAAAAAAAAAAAAAAAAAAAAE2d/zlBj/p7QorvklCU7D8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACCVxssgU8PpINPDoOBTxFFgk8RJoJLAiOMQQArmkEAIwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBThBrgU4PxIJRFN5/TxKngVMaY4RaIw8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA//8AAP//AADs/wAA8/8AANP/AADs/wAA5n8AAPMfAAD5hQAA7GEAAP8eAAD3wQAA+/sAAPz/AAD/HwAA//8AAA==" rel="icon" type="image/x-icon" />
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
        boolean aws = false;
        if (debug) {
            out.print("<br><br><form method=post action=\"processLogin\"><input type=\"hidden\" name=\"username\" value=\"admin\"><input type=\"hidden\" name=\"password\" value=\"Password!SE888\"><button type=\"submit\" class=\"btn btn-danger\">Admin login</button></form><br>");
            out.print("<form method=post action=\"processLogin\"><input type=\"hidden\" name=\"username\" value=\"zorro.fan.2013\"><input type=\"hidden\" name=\"password\" value=\"zxcvbn1284\"><button type=\"submit\" class=\"btn btn-danger\">User login</button></form><br>");

            String token = SharedSecretManager.authenticateUser("zorro.fan.2013");
            out.print("<form method=post action=");
            if (aws) {
                out.print("http://127.0.0.1:8888");
            } else {
                out.print("http://localhost:8084");
            }
            out.print("/app/json/heatmap?floor=2&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" + token + ">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("heatmap");
            out.print("</button>\r\n</form><br>");

            out.print("<form method=post action=");
            if (aws) {
                out.print("http://127.0.0.1:8888");
            } else {
                out.print("http://localhost:8084");
            }
            out.print("/app/json/basic-loc-report?order=year,gender,school&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" + token + ">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("breakdown");
            out.print("</button>\r\n</form><br>");

            out.print("<form method=post action=");
            if (aws) {
                out.print("http://127.0.0.1:8888");
            } else {
                out.print("http://localhost:8084");
            }
            out.print("/app/json/top-k-popular-places?k=10&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" + token + ">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("TopK Popular Places");
            out.print("</button>\r\n</form><br>");

            out.print("<form method=post action=");
            if (aws) {
                out.print("http://127.0.0.1:8888");
            } else {
                out.print("http://localhost:8084");
            }
            out.print("/app/json/top-k-companions?k=10&mac-address=a2935f43f2227c7adba65c18888c4553c70d0462&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" + token + ">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("TopK Companions");
            out.print("</button>\r\n</form><br>");

            out.print("<form method=post action=");
            if (aws) {
                out.print("http://127.0.0.1:8888");
            } else {
                out.print("http://localhost:8084");
            }
            out.print("/app/json/top-k-next-places?k=10&origin=SMUSISB1NearATM&date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" + token + ">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("TopK Next Places");
            out.print("</button>\r\n</form><br>");

            out.print("<form method=post action=");
            if (aws) {
                out.print("http://127.0.0.1:8888");
            } else {
                out.print("http://localhost:8084");
            }
            out.print("/app/json/group_detect?date=");
            out.print("2017-02-06T11:00:00");
            out.print("&token=" + token + ">\r\n<button type=\"submit\" class=\"btn btn-danger\">");
            out.print("AGD");
            out.print("</button>\r\n</form><br>");
        }

    %>
</body>
</html>
