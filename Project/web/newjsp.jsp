<%-- 
    Document   : newjsp
    Created on : 10 Sep, 2017, 3:32:04 AM
    Author     : Yang
--%>


<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
	
	
	
	
	
	
<table>
<tr></tr>
<td> a </td> <td> b </td> <td> c </td>
<tr></tr>
<td> d </td> <td> e </td> <td> f </td>
</table>

    
<form action="post.jsp" >
	
	
	
<input name="color" value="r" />Red
<input name="color" value="g" />Green
<input name="color" value="b" />Blue
<input type="submit" />
</form>
    
    <%
        String colors = request.getParameter("color");
        if(colors!=null){
            out.print(colors.toString());
            
        }
    %>
        
</body>
</html>