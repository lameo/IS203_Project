<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
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
        <title>Top-K popular places</title>
    </head>
    <body>
        <nav class="navbar navbar-inverse"> <%-- navigation menu for user to click --%>
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="userPage.jsp">SLOCA</a>
                </div>
                <ul class="nav navbar-nav">
                    <li><a href="userPage.jsp">Home</a></li> <%-- send user to home page--%>
                    <li class="active"><a href="reportsPage.jsp">Basic Location Reports</a></li> <%-- set as active because user is in reports page. send user to reports page --%>
                    <li><a href="heatmapPage.jsp">Heat Map</a></li> <%-- send user to heatmap page --%>
                    <li><a href="automaticGroupDetection.jsp">Automatic Group Detection</a></li> <%-- send user to Automatic Group Detection page --%>
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
            <form method=post action="report">
                <!-- report type -->
                <input type="hidden" name="reportType" value="topKPopular">
                <!-- form input for date & time  -->
                <div class="form-group">
                    <label class="form-control-label" for="timing">Enter date & time:</label>
                    <input type="text" class="form-control" id="timing" name="timeDate" placeholder="Example: 2014-03-23 13:40:00" required>
                </div>
                <!-- select menu for top K 1-10, default is 3  -->
                <div class="form-group">
                    <label for="topK">Generate for top:</label>
                    <select class="form-control" id="topK" name = "topK">
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option selected value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                        <option value="6">6</option>
                        <option value="7">7</option>
                        <option value="8">8</option>
                        <option value="9">9</option>
                        <option value="10">10</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>



        </div>
        <%
            //If top K popular places report is generated
            if (session.getAttribute("topKPopular") != null) {
                String timedate = (String)session.getAttribute("timeDate");
                int topK = (Integer)session.getAttribute("topK");
                Map<Integer, String> map = (Map<Integer, String>)(session.getAttribute("topKPopular"));

                out.print("<h3>Top-" + topK + " Popular Places at " + timedate + "</h3>");
                out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                out.print("<tr><th>Rank</th><th>Semantic place</th><th>No pax</th></tr></thead><tbody>");

                ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
                int count = 1;
                for (int i = keys.size() - 1; i >= 0; i--) {
                    if (count <= topK) {
                        System.out.print(map.get(keys.get(i)));
                        out.print("<tr><td>" + count++ + "</td><td>" + map.get(keys.get(i)) + "</td><td>" + keys.get(i) + "</td></tr>");
                    }
                }
                out.print("</tbody></table></div>");
            }
            session.removeAttribute("topKPopular"); //remove session attribute from the session object
            session.removeAttribute("timeDate"); //remove session attribute from the session object
            session.removeAttribute("topK"); //remove session attribute from the session object
            
            //debugging purpose
            out.print("<br><br>Copy Paste");
            out.print("<br>2014-03-23 13:55:00");
        %>
        <%="<br><br>User session: " + timestamp%>
    </center>
</body>
</html>
