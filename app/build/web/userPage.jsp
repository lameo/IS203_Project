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
        <%="<title>" + name + "'s Home Page</title>"%>
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
                    <li><a href="automaticGroupDetection.jsp">Automatic Group Detection</a></li> <%-- send user to Automatic Group Detection page --%>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="userPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                </ul>                
            </div>
        </nav>
        <div class="panel-group" id="mainpanel">        
            <div class="panel panel-default"> 
                <div class="panel-heading">                
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#mainpanel" href="#collapse1"><b>Announcements</b></a>
                    </h4>    
                </div>
                <div id="collapse1" class="panel-collapse collapse">                
                    <div class="panel-body">
                        There are no announcements to display
                    </div>
                </div>
            </div>    
            <div class="panel panel-default"> 
                <div class="panel-heading">                
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#mainpanel" href="#collapse2"><b>About SLOCA</b></a>
                    </h4>    
                </div>
                <div id="collapse2" class="panel-collapse collapse">                
                    <div class="panel-body">
                        SLOCA is an web application that can be used by any valid user to obtain diverse statistics of the locations of people inside the SIS building
                    </div>
                </div>
            </div>             
        </div>
    <center><%="<br>User session: " + timestamp%></center>
</body>
</html>
