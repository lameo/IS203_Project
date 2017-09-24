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
                    <li class="active"><a href="userPage.jsp">Home</a></li> <%-- set as active because user is in home page. send user to home page--%>
                    <li><a href="reportsPage.jsp">Basic Location Reports</a></li> <%-- send user to reports page --%>
                    <li><a href="heatmapPage.jsp">Heat Map</a></li> <%-- send user to heatmap page --%>                  
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="userPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                </ul>                
            </div>
        </nav>
    <center>


        <br><br>
        <form method=post action="report">
            <table>
                <input type="hidden" name="reportType" value="basicReport">
                <!-- first row -->
                <tr>
                    <td colspan = "3">Starting date & time:</td>
                    <td colspan = "3"><input type="text" name="starttimeDate" size="25" placeholder="Enter date and time" required/></td>
                </tr>
                <tr>
                    <td colspan = "3">Ending date & time:</td>
                    <td colspan = "3"><input type="text" name="endtimeDate" size="25" placeholder="Enter date and time" required/></td>
                </tr>
                <tr><td><br></td></tr>
                <tr>
                    <td>First:</td>
                    <td>
                        <select name = "order">
                            <option selected value="year">Year</option>
                            <option value="gender">Gender</option>
                            <option value="school">School</option>
                        </select>
                    </td>
                    <td>&ensp;Second:</td>
                    <td>
                        <select name = "order">
                            <option selected value="none">None</option>
                            <option value="year">Year</option>
                            <option value="gender">Gender</option>
                            <option value="school">School</option>
                        </select>
                    </td>
                    <td>&ensp;Third:</td>
                    <td>
                        <select name = "order">
                            <option selected value="none">None</option>
                            <option value="year">Year</option>
                            <option value="gender">Gender</option>
                            <option value="school">School</option>
                        </select>
                    </td>
                </tr>
            </table>
            <right><input type="submit" value ="Generate"/></right>
        </form>

        <%
            //If top K report is generated
            if (request.getAttribute("breakdownReport") != null) {
                String breakdownReport = (String) (request.getAttribute("breakdownReport"));
                out.print(breakdownReport);
            }
        %>







        <%="<br>User session: " + timestamp%>
    </center>
</body>
</html>
