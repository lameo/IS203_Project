<%@page import="java.util.Arrays"%>
<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>

<%
    //check if admin arrive page via link or through login
    if (session.getAttribute("admin") != null && session.getAttribute("admin").equals("admin")) {
        response.sendRedirect("adminPage.jsp"); //send back to admin page
        return;
        //check if user arrive page via link or through login
    } else if (session.getAttribute("user") == null) {
        response.sendRedirect("index.jsp"); //send back to index page
        return;
    }
%>

<!DOCTYPE html>
<%@include file="clearCache.jsp"%>
<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/bootstrap.css" rel="stylesheet">    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src='js/bootstrap.js'></script>

    <%  //user details, get using session
        User user = (User) session.getAttribute("user");
        String name = user.getName();
        String timestamp = (String) session.getAttribute("timestamp");
    %>  
    <head>
        <%="<title>" + name + "'s Home Page</title>"%>
    </head>
    <body>
        <nav class="navbar navbar-inverse"> <%-- navigation menu for user to click --%>
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#">SLOCA</a>
                </div>
                <ul class="nav navbar-nav">
                    <li class="active"><a href="userPage.jsp">Home</a></li>
                    <li><a href="reportsPage.jsp">Basic Location Reports</a></li> <%-- send user to reports page --%>
                    <li><a href="heatmapPage.jsp">Heat Map</a></li> <%-- send user to heatmap page --%>                  
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="userPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>  <%-- send user to logout servlet and process logout --%>
                </ul>                
            </div>
        </nav>
        <div class="panel-group">        
            <div class="panel panel-default">
                <div class="panel-heading"><b>Announcements:</b></div>            
                <div class="panel-body">
                    There are no announcements to display
                </div>
            </div>    
        </div>
    <center>
        <%
            out.print("<br>User session: " + timestamp);
        %>
    </center>
</body>
</html>
