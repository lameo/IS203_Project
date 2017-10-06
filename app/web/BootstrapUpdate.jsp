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
                        <li ><a href="adminPage.jsp">Home</a></li> <%-- send user to home page--%>
                            <%-- Dropdown menu for admin to boostrap and update the location data  --%>
                        <li class="dropdown active"> <%-- set as active because user is in bootstrap page. --%>
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
        <div class="container-fluid text-center">
            <h1>Upload additional data to SLOCA</h1><br>
            <center>
                <%
                    String error = (String) session.getAttribute("error"); //error message retrieved from UploadServlet
                    if (error != null && error.length() >= 1) {
                        out.println("<font color='red'>" + "<br/>" + error + "</font>");
                        session.removeAttribute("error");                              
                    }
                %>
                <%
                    String success = (String) session.getAttribute("success"); //success message retrieved from UploadServlet
                    if (success != null && success.length() >= 1) {
                        out.println("<font color='green'>" + "<br/> SUCCESS!!</font>");
                        out.println("<font color='green'>" + "<br/>" + success + "</font>");
                        session.removeAttribute("success");                        
                    }
                %>  
                <form method="post" action="processUpload" enctype="multipart/form-data">
                    <div class="form-group">
                        <label for="exampleFormControlFile1">Choose file&hellip;</label>
                        <input type="file" name="uploadfile" class="form-control-file" id="exampleFormControlFile1">
                    </div>
                    <br>
                    <br>
                    <input type="hidden" name="uploadType" value="update">  
                    <input type="hidden" name="todo" value="upload">
                    <button type="submit" name="Submit" class="btn btn-primary">Submit</button>
                    <button type="reset" name="Reset" class="btn btn-primary">Cancel</button>
                </form>        
                <%="<br>User session: " + timestamp%>
            </center>
        </div>
    </body>
</html>
