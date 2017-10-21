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
            <!-- Form for user to input date&time and top K for top K popular places report -->
            <form method=post action="AutoGroup">
                <!-- report type -->
                <input type="hidden" name="andHere" value="xyChangeHereTooooooo">
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

                String timedate = (String)session.getAttribute("timeDate");
                out.print("<h3> Potential groups in the SIS builind at " + timedate + "</h3>");
                
                
                
                out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                HashMap<String, ArrayList<String>> AutoGroups = (HashMap<String, ArrayList<String>>) (session.getAttribute("test")); 
                out.print("<tr><th>Group No.</th><th>Macaddress</th><th>Email</th><th>Location id</th><th>Time spent</th></tr></thead></tbody>");
                Set<String> AutoGroup = AutoGroups.keySet();
                int GroupNo = 1;
                for (String group : AutoGroup) {
                            ArrayList<String> LocationTimestamps = AutoGroups.get(group);
                            out.print("<tr><td rowspan=" + LocationTimestamps.size() + ">" + (GroupNo) + "</td>");
                            for (int i = 0; i < LocationTimestamps.size(); i += 1) {
                                String[] LocationTimestamp = LocationTimestamps.get(i).split(","); 
                                if (i == 0) {
                                    out.print("<td>" + LocationTimestamp[0] + "</td>");
                                    out.print("<td>" + LocationTimestamp[1] + "</td>");
                                    //add rowspan for first row of companion user
                                    out.print("<td rowspan=" + LocationTimestamps.size() + ">" + LocationTimestamp[2] + "</td>");
                                } else {
                                    out.print("<tr><td>" + LocationTimestamp[0] + "</td>");
                                    out.print("<td>" + LocationTimestamp[1] + "</td></tr>");
                                }
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
