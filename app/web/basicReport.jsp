<%@page import="java.util.List"%>
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
        <title>Breakdown Report</title>
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
            <form method=post action="report">
                <table>
                    <!-- report type -->
                    <input type="hidden" name="reportType" value="basicReport">

                    <!-- ending time and date field -->
                    <tr>
                        <!-- form input for date & time  -->
                    <div class="form-group">
                        <label for="example-datetime-local-input" class="form-control-label">Enter date & time:</label>
                        <input class="form-control" type="datetime-local" id="input-datetime-local" name="timeDate" required>
                    </div>
                    </tr>
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <!-- first category box -->
                            <label for="order">Sort by (first):</label>
                            <select name="order" class="form-control">
                                <option value = "year">Year</option>
                                <option value = "gender">Gender</option>
                                <option value = "school">School</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <!-- second category box -->
                            <label for="order">Sort by (second):</label>
                            <select name="order" class="form-control">
                                <option value = "none">(Optional)</option>
                                <option value = "year">Year</option>
                                <option value = "gender">Gender</option>
                                <option value = "school">School</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <!-- third category box -->
                            <label for="order">Sort by (third):</label>
                            <select name="order" class="form-control">
                                <option value = "none">(Optional)</option>
                                <option value = "year">Year</option>
                                <option value = "gender">Gender</option>
                                <option value = "school">School</option>
                            </select>
                        </div>
                    </div>
                </table>
                <br>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>

        <%
            //If breakdown report is generated
            if (session.getAttribute("breakdownReport") != null) {
                List<String> options = (List<String>) session.getAttribute("orderList");
                String errMsg = "duplicated option found: ";
                boolean duplicate = false;

                if (options != null) {
                    for (int i = 0; i < options.size(); i++) {
                        for (int j = 1; j < options.size(); j++) {
                            if (options.get(i).equals(options.get(j)) && !options.get(i).equals("none") && i != j) {
                                // duplicate element found
                                duplicate = true;
                                errMsg += options.get(j);
                                break; //once a duplicate element is found, exit the loop
                            }
                        }
                        if (duplicate) {
                            break; //once a duplicate element is found, exit the loop
                        }
                    }
                }

                if (duplicate) {
                    out.print("<br/><div class=\"alert alert-danger\" role=\"alert\"><strong>" + "The data is not available because " + errMsg + "</strong></div>");
                } else {
                    String breakdownReport = (String) (session.getAttribute("breakdownReport"));
                    out.print(breakdownReport);
                }
                session.removeAttribute("breakdownReport"); //remove session attribute from the session object
                session.removeAttribute("order"); //remove session attribute from the session object
            }
        %>        
        <%="<br><br>User session: " + timestamp%>
    </center>
</body>
</html>
