<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>

<%
    //check if user arrive page via link or through login
    if (session.getAttribute("admin") == null || !session.getAttribute("admin").equals("admin")) {
        response.sendRedirect("userPage.jsp"); //send back to user page
        return;
    }
%>

<style>
    .topnav {
        background-color: #333;
        overflow: hidden;
    }

    .topnav a {
        float: left;
        display: block;
        color: #f2f2f2;
        text-align: center;
        padding: 14px 16px;
        text-decoration: none;
        font-size: 17px;
    }

    .topnav a:hover {
        background-color: #ddd;
        color: black;
    }

    .topnav a.active {
        background-color: #4CAF50;
        color: white;
    }
    .right {
        text-align: left;
        float: right;
    }        
</style>

<!DOCTYPE html>
<%@include file="clearCache.jsp"%>   
<html> 
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin page</title>
    </head>

    <body>
        <%
            String name = (String) session.getAttribute("admin");
            String timestamp = (String) session.getAttribute("timestamp");
        %>        
        <div class="topnav" id="myTopnav">
            <a href="adminPage.jsp">Upload new datafile</a>          
            <div class="right">
                <a href="#knp"><%="Welcome " + name + "!"%></a>
                <a href="processLogout">Logout</a>            
            </div>
        </div>    
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
        <%="<br>User: " + name + "<br>Session: " + timestamp%>
    </body>
</html>
