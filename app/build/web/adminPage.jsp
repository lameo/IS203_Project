<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>

<%
    if (session.getAttribute("admin") == null || !session.getAttribute("admin").equals("admin")) { //check if user arrive page via link or through login
        response.sendRedirect("userPage.jsp"); //send back to user page
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

    <%  //admin details, get using session
        String name = (String) session.getAttribute("admin");
        String timestamp = (String) session.getAttribute("timestamp");
    %>    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin page</title>
    </head>
    <body>               
        <nav class="navbar navbar-inverse"> <%-- navigation menu for user to click --%>
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span> 
                    </button>
                    <a class="navbar-brand" href="adminPage.jsp">SLOCA</a>
                </div>
                <div class="collapse navbar-collapse" id="myNavbar">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="adminPage.jsp">Home</a></li> <%-- set as active because user is in home page. send user to home page--%>
                            <%-- Dropdown menu for admin to boostrap and update the location data  --%>
                        <li class="dropdown">
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#">Boostrap
                                <span class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li><a href="BootstrapInitialize.jsp">Initialize SLOCA</a></li> <%-- send user to BootstrapInitialize page --%>
                                <li><a href="BootstrapUpdate.jsp">Upload Additional Data</a></li> <%-- send user to BootstrapUpdate page --%>
                            </ul>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="adminPage.jsp"><%="Welcome " + name + "!"%></a></li>
                        <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                    </ul>   
                </div>

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
                        <a data-toggle="collapse" data-parent="#mainpanel" href="#collapse2"><b>About Admin Permissions</b></a>
                    </h4>    
                </div>
                <div id="collapse2" class="panel-collapse collapse">                
                    <div class="panel-body">
                        The administrator can bootstrap the SLOCA system with location & demographics data.
                    </div>
                </div>
            </div>             
        </div>        
    <center><%="<br>User session: " + timestamp%></center>
</body>
</html>
