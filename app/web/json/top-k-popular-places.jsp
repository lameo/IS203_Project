<%-- 
    Document   : top-k-popular-places
    Created on : 17 Oct, 2017, 4:11:22 PM
    Author     : Yang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Top-k popular places</title>
    </head>
    <body>
        <%
            String token = request.getParameter("token");
            String k = request.getParameter("k");
            String date = request.getParameter("date");
        %>
    </body>
</html>
