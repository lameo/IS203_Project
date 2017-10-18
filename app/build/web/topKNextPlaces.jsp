<%@page import="model.ReportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
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
        <title>Top-K next places</title>
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
                <!-- report type for ReportServlet to determine which reportType is selected by user -->
                <input type="hidden" name="reportType" value="topKNextPlaces">
                <!-- form input for date & time  -->
                <div class="form-group">
                    <label for="example-datetime-local-input" class="form-control-label">Enter date & time:</label>
                    <input class="form-control" type="datetime-local" id="input-datetime-local" name="timeDate" step="1" required>
                </div>
                <!-- form input for semantic place  -->
                <div class="form-group">
                    <label for="placeSelector">Enter location name:</label>
                    <select class="form-control" id="placeSelector" name = "locationname">                    
                        <%
                            ArrayList<String> list = ReportDAO.getSemanticPlaces();
                            for (String location : list) {
                                out.print("<option value=\"" + location + "\">" + location + "</option>");
                            }
                        %>
                    </select>                        
                </div>
                <!-- select menu for top K 1-10, default is 3  -->
                <div class="form-group">
                    <label for="kSelector">Generate for top:</label>
                    <select class="form-control" id="kSelector" name = "topK">
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
            //If top K report is generated
            if (session.getAttribute("topK") != null) {

                String timedate = (String) session.getAttribute("timeDate"); //retrieve date and time given from input
                int topK = (int) session.getAttribute("topK"); //retrieve the number given fom input
                int total = (int) session.getAttribute("total"); //output the total number of users in the semantic place being queried
                String locationname = (String) session.getAttribute("locationname"); //retieve the location given from input
                int usersVisitingNextPlace = 0; // total quantity of users visiting next place

                Map<Integer, ArrayList<String>> topKNextPlaces = (Map<Integer, ArrayList<String>>) (session.getAttribute("topKNextPlaces"));
                if (topKNextPlaces == null || topKNextPlaces.size() == 0) {
                    out.print("<br/><div class=\"alert alert-danger\" role=\"alert\"><strong>" + "The data is not available for location name " + locationname + " within time " + timedate + "</strong></div>");
                } else if (topKNextPlaces != null && topKNextPlaces.size() > 0) { // topKNextPlaces map is not empty
                    Set<Integer> totalNumOfUsersSet = topKNextPlaces.keySet(); // to get the different total number of users in a place in desc order
                    int counter = 1; // to match topk number after incrementation
                    out.print("<h3>Top-" + topK + " Next Places at " + timedate + "</h3>");

                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th>Rank</th><th>Semantic Place</th><th>No. of Pax</th><th>% of Users Visiting This Semantic Place</th></tr></thead></tbody>");
                    for (int totalNumOfUsers : totalNumOfUsersSet) {
                        ArrayList<String> locations = topKNextPlaces.get(totalNumOfUsers); // gives the list of location with the same totalNumOfUsers
                        if (counter <= topK) { // to only display till topk number
                            out.print("<tr><td>" + counter + "</td><td>");
                            for (int i = 0; i < locations.size(); i++) {
                                out.print(locations.get(i)); // show the current location with totalNumOfUsers
                                if (locations.get(i).equals(locationname)) { // if the locations is the same, find the number of users who visited another place (exclude those left the place but have not visited another place) in the query window
                                    usersVisitingNextPlace -= totalNumOfUsers; // minus off if the user is staying at the same place
                                }
                                if (i + 1 < locations.size()) { //fence-post method to add the comma
                                    out.print(", ");
                                }
                            }
                            out.print("</td><td>" + totalNumOfUsers + "</td>");
                            out.print("<td>" + Math.round((double) totalNumOfUsers / total * 100.0) + "%</td></tr>");
                            counter++;
                        }
                        usersVisitingNextPlace += totalNumOfUsers * locations.size(); // add if the user is going other places but the quantity may have multiple next locations
                    }
                    out.print("</tbody></table></div>");

                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th>Semantic Place Queried</th><th>No. of Pax in Semantic Place Queried</th><th>No. Pax Who Visited Next Semantic Place</th></tr></thead></tbody>");
                    out.print("<tr><td>" + locationname + "</td><td>" + total + "</td><td>" + usersVisitingNextPlace + "</td></tr>");
                    out.print("</tbody></table></div>");
                }
            }
            //removes previous shown outputs if refresh is clicked
            session.removeAttribute("topK");
            session.removeAttribute("timeDate");
            session.removeAttribute("topKNextPlaces");
            session.removeAttribute("total");
            session.removeAttribute("locationname");

        %>
        <%="<br><br>User session: " + timestamp%>
    </center>
</body>
</html>
