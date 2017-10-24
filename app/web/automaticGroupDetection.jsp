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
        String timestamp = (String) session.getAttribute("timestamp");
    %>
    <head>
        <title>Automatic Group Detection</title>
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
                    <input class="form-control" type="datetime-local" id="input-datetime-local" name="timeDate" required>
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>



        </div>
        <%
            //If top K report is generated
            if (session.getAttribute("test") != null) {

                String timedate = (String) session.getAttribute("timeDate");
                out.print("<h3> Potential groups in the SIS builind at " + timedate + "</h3>");

                out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                ArrayList<Group> AutoGroups = (ArrayList<Group>) (session.getAttribute("test"));
                out.print("<tr><th>Group No.</th><th>Macaddress</th><th>Email</th><th>Location id</th><th>Time spent</th></tr></thead></tbody>");

                int GroupNo = 1;
                for (Group AutoGroup : AutoGroups) {
                    ArrayList<String> AutoUsers = AutoGroup.retreiveMacsWithEmails();
                    Map<String, Double> locationTimestamps = AutoGroup.CalculateTimeDuration();
                    Set<String> Locations = locationTimestamps.keySet();
                    int AutoUsersNum = AutoGroup.getAutoUsersSize();
                    double rowspanMac = 0;
                    double rowspanLocation = 0;
                    double rowspan = 0;
                    if (AutoUsers.size() >= locationTimestamps.size()) {
                        rowspanMac = AutoUsers.size();
                        rowspanLocation = rowspanMac / locationTimestamps.size();
                        rowspan = rowspanMac;
                    } else {
                        rowspanLocation = locationTimestamps.size();
                        rowspanMac = rowspanLocation / AutoUsers.size();
                        rowspan = rowspanLocation;
                    }
                    out.print("<tr><td rowspan=" + rowspan + ">" + (GroupNo) + "</td>");
                    for (int i = 0; i < AutoUsers.size(); i += 1) {
                        String[] AutoUser = AutoUsers.get(i).split(",");
                        String mac = AutoUser[0];
                        String email = AutoUser[1];
                        out.print("<td rowspan=" + rowspanMac + ">" + mac + "</td>");
                        out.print("<td rowspan=" + rowspanMac + ">" + email + "</td>");

                    }
                    for (String Location : Locations) {
                        Double TimeDuration = locationTimestamps.get(Location);
                        out.print("<td rowspan=" + rowspanLocation + ">" + Location + "</td>");
                        out.print("<td rowspan=" + rowspanLocation + ">" + TimeDuration + "</td>");
                    }
                    out.print("</tr>");
                    GroupNo++;
                }
                out.print("</tbody></table></div>");
            }
            session.removeAttribute("timeDate");
            session.removeAttribute("test");
        %>

        <%="<br>Example: 2017-02-06 11:34:43.000000"%>
        <%="<br>Mac address: 009562b08360d78848a977dc26368b53cc0f1d44"%>
        <%="<br><br>User session: " + timestamp%>
    </center>
</body>
</html>
