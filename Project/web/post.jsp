<%-- 
    Document   : post
    Created on : 10 Sep, 2017, 3:36:07 AM
    Author     : Yang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
<form post = "post.jsp"
<obscure name=”secret”></obscure>
<input name=”secret” type=”secret”></input>
<input name=”secret” type=”password”></input>
<input name=”secret” type=”hidden”></input>
<input name=”secret” type=”obscure”></input>
<input type="submit" />
    <%
String height2 = request.getParameter("color");
String[] colors = request.getParameterValues("color");
String x = colors.toString();
out.print("<br>array<br>");
for(String y:colors){
    out.print(y);
}
out.println(colors);
out.print(x);
out.print("<br>string<br>");
out.println(height2);
%>
</form>
</body>
</html>