<%@page import="java.util.Set"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
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
        <title>Top-K Companions</title>
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
                <input type="hidden" name="reportType" value="topKCompanions">
                <!-- form input for date & time  -->
                <div class="form-group">
                    <label for="example-datetime-local-input" class="form-control-label">Enter date & time:</label>
                    <input class="form-control" type="datetime-local" id="input-datetime-local" name="timeDate" required>
                </div>
                <!-- form input for semantic place  -->
                <div class="form-group">
                    <label class="form-control-label" for="mac">Enter MAC address:</label>
                    <input type="text" class="form-control" id="mac" name="macAddress" placeholder="Example: 00453834f5c819b496cfde51450b10410769b06b" required>
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
            /*
            ArrayList<String> test = (ArrayList<String>) session.getAttribute("test");
            for (int i = 0; i < test.size(); i++) {
                out.println(test.get(i) + "<br>");
            }
            */
            //ArrayList<String> users = (ArrayList<String>)request.getAttribute("users");
            //for (int i = 0; i<users.size();i++){
            //  out.println(users.get(i)+"<br>");
            //}
            //out.println(session.getAttribute("test")+"<br>");
            //out.println(session.getAttribute("topKCompanions"));
            //out.print(session.getAttribute("users"));
            //If top K report is generated
            //session.setAttribute("topKCompanions",null);
            if (session.getAttribute("topKCompanions") != null) {

                String timedate = (String) session.getAttribute("timeDate");
                int topK = (Integer) session.getAttribute("topK");

                Map<Double, ArrayList<String>> topKCompanions = (TreeMap<Double, ArrayList<String>>) (session.getAttribute("topKCompanions"));
                if (topKCompanions == null || topKCompanions.size() == 0) {
                    String macaddress = (String) session.getAttribute("macaddress");
                    out.print("<br/><div class=\"alert alert-danger\" role=\"alert\"><strong>" + "The data is not available for macaddress " + macaddress + " within time " + timedate + "</strong></div>");

                } else {
                    out.print("<h3>Top-" + topK + " Companions at " + timedate + "</h3>");

                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    Set<Double> Times = topKCompanions.keySet();
                    //String[] y = topKPopular.split(",");
                    out.print("<tr><th>Rank</th><th>Macaddress</th><th>Email</th><th>Co-Located Time (in seconds)</th></tr></thead></tbody>");
                    int rank = 1;
                    for (double time : Times) {
                        if (rank <= topK) {
                            ArrayList<String> macaddresses = topKCompanions.get(time);
                            out.print("<tr><td rowspan=" + macaddresses.size() + ">" + (rank) + "</td>");
                            for (int i = 0; i < macaddresses.size(); i += 1) {
                                String macaddress = macaddresses.get(i);
                                String[] UserInfo = macaddress.split(",");
                                if (i == 0) {
                                    out.print("<td>" + UserInfo[0] + "</td>");
                                    out.print("<td>" + UserInfo[1] + "</td>");
                                    //add rowspan for first row of companion user
                                    out.print("<td rowspan=" + macaddresses.size() + ">" + time + "</td>");
                                } else {
                                    out.print("<tr><td>" + UserInfo[0] + "</td>");
                                    out.print("<td>" + UserInfo[1] + "</td></tr>");
                                }
                            }
                            out.print("</tr>");
                            rank += 1;
                        }
                    }

                    out.print("</tbody></table></div>");
                }
            }
            session.removeAttribute("macaddress");
            session.removeAttribute("topKCompanions"); //remove session attribute from the session object
            session.removeAttribute("timeDate"); //remove session attribute from the session object
            session.removeAttribute("topK"); //remove session attribute from the session object
        %>
        <%="<br><br>User session: " + timestamp%>
    </center>
</body>
</html>