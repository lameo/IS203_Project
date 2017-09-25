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

                    <!-- first row, starting time and date field -->
                    <tr>
                    <div class="form-group">
                        <label class="form-control-label" for="formGroupExampleInput">Enter starting date & time:</label>
                        <input type="text" class="form-control" id="formGroupExampleInput" name="starttimeDate" placeholder="Example: 2014-03-23 13:40:00" required>
                    </div>
                    </tr>

                    <!-- second row, ending time and date field -->
                    <tr>
                    <div class="form-group">
                        <label class="form-control-label" for="formGroupExampleInput">Enter ending date & time:</label>
                        <input type="text" class="form-control" id="formGroupExampleInput" name="endtimeDate" placeholder="Example: 2014-03-23 13:55:00" required>
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
            //If top K report is generated
            if (request.getAttribute("breakdownReport") != null) {
                String[] options = request.getParameterValues("order");

                String errMsg = "Duplicated option found: ";
                boolean duplicate = false;
                for (int i = 0; i < options.length; i++) {
                    for (int j = 1; j < options.length; j++) {
                        if (options[i].equals(options[j]) && !options[i].equals("none") && i != j) {
                            // duplicate element found
                            duplicate = true;
                            errMsg += options[j];
                        }
                    }
                }

                if (duplicate) {
                    out.print("<p style=\"color:red;\">Report Generation failed<br>" + errMsg + "</p>");
                } else {
                    String breakdownReport = (String) (request.getAttribute("breakdownReport"));
                    out.print(breakdownReport);
                }
            }
        %>

        <%="<br>User session: " + timestamp%>
    </center>
</body>
</html>
