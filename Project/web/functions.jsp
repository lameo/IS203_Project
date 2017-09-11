<%@page import="org.eclipse.jdt.internal.compiler.batch.Main"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%!
Connection connection = null;
Statement statement = null;
%>
<% Class.forName("com.mysql.jdbc.Driver");%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%!
            //removes white spaces & throws esception if too long
            public static String validate(String text) throws Exception{
                if(text==null){
                    return "";
                }
                text = text.trim();
                if(text.length()>40){
                    throw new Exception("e");
                }
                return text;
            }
        %>
    </body>
</html>
