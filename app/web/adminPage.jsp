<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>
<%
    if(session.getAttribute("admin") == null || !session.getAttribute("admin").equals("admin")){   //check if user arrive page via link or through login
        response.sendRedirect("userPage.jsp");
        return;
    }
%>
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
        .right {
            text-align: right;
            float: right;
        }        
    </style>
    <body>
        <%
            String name = (String)session.getAttribute("admin");
            String timestamp = (String)session.getAttribute("timestamp");
            timestamp = name +"-"+ timestamp;
        %>        
        <div class="topnav" id="myTopnav">
            <a href="#upload">Upload new datafile</a>          
            <div class="right">
                <a href="#knp"><%="Welcome " + name +"!"%></a>
                <a href="processLogout">Logout</a>            
            </div>
        </div>               
        <%="<br>User: " + name + "<br>Session: " + timestamp%>
    </body>
</html>
