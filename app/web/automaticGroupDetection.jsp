<%@page import="model.AutoGroupDAO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="model.ReportDAO"%>
<%@page import="model.Group"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>

<%
    if (session.getAttribute("admin") != null && session.getAttribute("admin").equals("admin")) { //check if admin arrive page via link or through login
        response.sendRedirect("adminPage.jsp"); //send back to admin page
        return;
    } else if (session.getAttribute("user") == null) { //check if user arrive page via link or through login
        response.sendRedirect("index.jsp"); //send back to index page
        return;
    }
%>

<!DOCTYPE html>
<%@include file="clearCache.jsp"%> <%-- clear cache, don't allow user to backpage after logging out --%>
<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/bootstrap.css" rel="stylesheet"> <%-- twitter bootstrap for designing--%>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script> <%-- twitter bootstrap for designing--%>
    <script src='js/bootstrap.js'></script> <%-- twitter bootstrap for designing--%>

    <%  //user details, get using session
        User user = (User) session.getAttribute("user");
        String name = user.getName();
    %>
    <head>
        <title>Automatic Group Detection</title>
        <link href="data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeqPVFXKh3DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFmW5gFXlup/Y57tNAAAAAAAAAAAAAAAAH1RFyF2VB8IAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJGz3j2ct9YLUpHuFUmP8NxLnP013QkADoJREaOATxGddlIhDwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB2qOQcXZjnbQAAAABRmfc4OY//72tjYMOZPgAzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFWV7T1Pk/GqAAAAAGJqfbs1jP//RJb/bAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR5b7f09+wOXMGwAnR5r/bTiH9P9AjPGvhbDbDwAAAAAAAAAAAAAAAAAAAACfttEVAAAAAAAAAAAAAAAAAAAAAAAAAABHgdDbOo38uwAAAABPk/B9Nob1/zyI8+RUlupJAAAAAAAAAAAAAAAAia3bKI2w3x4AAAAAAAAAAAAAAACCPgAFjEwFcEGS/Ms8ifLcjLLaEFWW8GY5h/T/N4f0/0KN8KRjneciAAAAAAAAAABtoedSg8vwAQAAAAAAAAAAhVYdCoNKBmsAAAAAQozwwTuI8v9Rle1GZ6LqPj2J8+Q1hfX/Ooj0/0CL8aNCi+9gRYzwsqPF3gcAAAAAAAAAAAAAAACBUhaBAAAAAAAAAABGjvGVO4nz/z6K8aBvpOQxSpDvfziG9OY2hvX/M4b1/z6L8t+nvMEpAAAAAAAAAAAAAAAAf1EUZ4JTGEYAAAAAAAAAAFiY6087ifPvOYjz/0KN8I1cmudAY5vpSE6R60xgnepaUZPquQAAAAAAAAAAAAAAAH9ODg2AUBOphlsmDgAAAAAAAAAAusvSCEeP73s7iPLxOYj0/z2J8+g9ifTCP4rx51CT7n8AAAAAAAAAAAAAAAAAAAAAg1MYO4JRErx/UhUqAAAAAAAAAAAAAAAAAAAAAE2d/zlBj/p7QorvklCU7D8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACCVxssgU8PpINPDoOBTxFFgk8RJoJLAiOMQQArmkEAIwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBThBrgU4PxIJRFN5/TxKngVMaY4RaIw8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA//8AAP//AADs/wAA8/8AANP/AADs/wAA5n8AAPMfAAD5hQAA7GEAAP8eAAD3wQAA+/sAAPz/AAD/HwAA//8AAA==" rel="icon" type="image/x-icon" />
    </head>
    <body>
        <nav class="navbar navbar-inverse"> <%-- navigation menu for user to click --%>
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="userPage.jsp">SLOCA</a>
                </div>
                <ul class="nav navbar-nav">
                    <li><a href="userPage.jsp">Home</a></li> <%-- send user to home page--%>
                    <li><a href="reportsPage.jsp">Basic Location Reports</a></li> <%-- send user to reports page --%>
                    <li><a href="heatmapPage.jsp">Heat Map</a></li> <%-- send user to heatmap page --%>
                    <li class="active"><a href="automaticGroupDetection.jsp">Automatic Group Detection</a></li> <%-- set as active because user is in automatic group detection page. send user to Automatic Group Detection page --%>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="userPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                </ul>
            </div>
        </nav>
    <center>

        <div class="container">
            <br><br>
            <!-- Form for user to input date&time for automatic group detection -->
            <form method=post action="AutoGroup">
                <!-- form input for date & time  -->
                <div class="form-group">
                    <label for="example-datetime-local-input" class="form-control-label">Enter date & time:</label>
                    <input class="form-control" type="datetime-local" id="input-datetime-local" name="timeDate" min="2013-01-01T00:00:00" max="2017-12-31T23:59:59" step="1" required>
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>
        <%  
            //If top K report is generated
            if (session.getAttribute("autoGroupsDetected") != null) {
                String timedate = (String) session.getAttribute("timeDate");
                ArrayList<Group> autoGroupsDetected = (ArrayList<Group>) (session.getAttribute("autoGroupsDetected"));
                if (autoGroupsDetected == null || autoGroupsDetected.size() == 0) {
                    out.print("<br/><div class=\"alert alert-danger\" role=\"alert\"><strong>" + "The data is not available within time " + timedate + "</strong></div>");

                } else {
                    out.print("<h3> Potential groups in the SIS building at " + timedate + "</h3>");

                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");

                    int numberOfUsersInBuilding = (int) (session.getAttribute("numberOfUsersInBuilding"));
                    out.print("<thead><tr><th colspan = 3>Number of users in the entire SIS building: " + numberOfUsersInBuilding + " <br>Total number of groups discovered: " + autoGroupsDetected.size() + "</th></tr>");
                    out.print("<tr><th>Group No.</th><th>Macaddress (Email)</th><th>Location id (Time spent in seconds)</th></tr></thead></tbody>");

                    int groupNo = 1;
                    for (Group autoGroup : autoGroupsDetected) {
                        ArrayList<String> AutoUsers = autoGroup.retrieveMacsWithEmails();
                        Map<String, Double> locationTimestamps = autoGroup.calculateTimeDuration();

                        Iterator<String> locations = locationTimestamps.keySet().iterator();
                        
                        out.print("<tr><td>" + (groupNo) + "</td>");

                        out.println("<td>");
                        for (int i = 0; i < AutoUsers.size(); i += 1) {
                            String[] AutoUser = AutoUsers.get(i).split(",");
                            String mac = AutoUser[0];
                            String email = AutoUser[1];
                            out.println(mac + " (" + email + ")<br/>");
                        }
                        out.print("</td>");
                        out.println("<td>");
                        while (locations.hasNext()) {
                            String location = locations.next();
                            Double TimeDuration = locationTimestamps.get(location);
                            out.println(location + " (" + TimeDuration + ")<br/>");
                        }

                        out.print("</td></tr>");
                        groupNo++;
                    }
                    out.print("</tbody></table></div>");

                }
            }
            session.removeAttribute("autoGroupsDetected");             
            session.removeAttribute("timeDate");
            session.removeAttribute("numberOfUsersInBuilding");
             
        %>
    </center>
</body>
</html>
