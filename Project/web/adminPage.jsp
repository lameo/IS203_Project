<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User page</title>
    </head>
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
        
    </style>
    <body>
        
        <div class="topnav" id="myTopnav">
            <a href="#upload">Upload new datafile</a>
            <a href="#knp"><%="Welcome " + request.getParameter("userName") +"!"%></a>
        </div>
        <%
            String userName = request.getParameter("userName");
            String hide = request.getParameter("hide");
            hide = userName +"-"+ hide;
            out.print("<br>User: " + userName+"<br>Session: " + hide);
        %>
    </body>
</html>
