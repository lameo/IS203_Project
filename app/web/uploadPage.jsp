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
        <title>Upload page</title>
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
                    <li ><a href="adminPage.jsp">Home</a></li> <%-- set as active because user is in home page. send user to home page--%>
                        <%-- Dropdown menu for admin to boostrap and update the location data  --%>
                    <li class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#">Boostrap
                            <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="uploadPage.jsp">Initialize SLOCA</a></li> <%-- send user to upload page --%>
                            <li><a href="uploadPage.jsp">Upload Additional Data</a></li> <%-- send user to upload page --%>
                        </ul>
                    </li>
                </ul>
                </div>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="adminPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                </ul>                
            </div>
        </nav>

        <div class="container-fluid text-center">
            <h1>Initialize SLOCA</h1><br>
            <center>
                <%
                    String error = (String) request.getAttribute("error"); //error message retrieved from UploadServlet
                    if (error != null && error.length() >= 1) {
                        out.println("<font color='red'>" + "<br/>" + error + "</font>");
                    }
                %>
                <%
                    String success = (String) request.getAttribute("success"); //success message retrieved from UploadServlet
                    if (success != null && success.length() >= 1) {
                        out.println("<font color='green'>" + "<br/> SUCCESS!!</font>");
                        out.println("<font color='green'>" + "<br/>" + success + "</font>");
                    }
                %>  
                <form method="post" action="processUpload" enctype="multipart/form-data">
                    <table>
                        <tr>
                            <td align="left"><b>Select a file to upload:</b></td>
                        </tr>
                        <tr>
                            <td align="left">
                                <input type="file" name="uploadfile" size="50">
                            </td>
                        </tr>
                        <tr>
                            <td align="left">
                                <input type="hidden" name="todo" value="upload">
                                <input type="submit" name="Submit" value="Upload">
                                <input type="reset" name="Reset" value="Cancel">
                            </td>
                        </tr>
                    </table>
                </form>        
                <%="<br>User session: " + timestamp%>
            </center>
        </div>
    </body>
</html>
