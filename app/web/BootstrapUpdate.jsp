<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
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
        <title>Update page</title>
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
                            <%-- Dropdown menu for admin to bootstrap and update the location data  --%>
                        <li class="dropdown active"> <%-- set as active because user is in bootstrap page. --%>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#">Bootstrap
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
            <%
                HashMap<Integer, String> demographicsError = (HashMap<Integer, String>) session.getAttribute("demographicsError");
                HashMap<Integer, String> locationError = (HashMap<Integer, String>) session.getAttribute("locationError");
                HashMap<String, String> processedLines = (HashMap<String, String>) session.getAttribute("processedLines");
                String success = (String) session.getAttribute("success"); //success message retrieved from UploadServlet
                
                //error message retrieved from UploadServlet
                String error = (String) session.getAttribute("error"); 
                if (error != null && error.length() >= 1) {
                    out.println("<font color='red'>" + "<br/>" + error + "</font>");
                    session.removeAttribute("error");
                }else{
                    if(success!=null){                        
                        out.println("<center><font color='green'>" + "<br/> Success!!</font>");
                        out.println("<font color='green'>" + "<br/>" + success + "</font></center>");
                        session.removeAttribute("success");
                    }
                }
                // Table for processed line
                if(processedLines!= null && processedLines.size()>0){
                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th>File</th><th># of Records Loaded</th></tr></thead><tbody>");
                    if(processedLines.containsKey("location.csv")){
                        out.print("<tr><td>location.csv</td><td>" + processedLines.get("location.csv") + "</td></tr>");
                    }
                    if(processedLines.containsKey("demographics.csv")){
                        out.print("<tr><td>demographics.csv</td><td>" + processedLines.get("demographics.csv") + "</td></tr>");
                    }
                    out.print("</tbody></table></div><br>");
                    session.removeAttribute("processedLines");
                }

                // Table for demographic error
                if (demographicsError != null && demographicsError.size() > 0) {
                    Set<Integer> keys = demographicsError.keySet();
                    keys = new TreeSet(keys);
                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th colspan=\"2\"><center>Demographics Error</center></th></tr><tr><th><center>Row</center></th><th><center>Errors</center></th></tr>");
                    for (Integer key : keys) {
                        out.print("<tr><td>" + key + "</td><td>" + demographicsError.get(key) + "</td></tr>");
                    }
                    out.print("</tbody></table></div><br>");
                    session.removeAttribute("demographicsError");
                }

                // Table for location error
                if (locationError != null && locationError.size() > 0) {
                    Set<Integer> keys = locationError.keySet();
                    keys = new TreeSet(keys);
                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th colspan=\"2\"><center>Location Error</center></th></tr><tr><th><center>Row</center></th><th><center>Errors</center></th></tr>");
                    for (Integer key : keys) {
                        out.print("<tr><td>" + key + "</td><td>" + locationError.get(key) + "</td></tr>");
                    }
                    out.print("</tbody></table></div><br>");
                    session.removeAttribute("locationError");
                }
            %>
            <center>
                <form method="post" action="processUpdate" enctype="multipart/form-data">
                    <div class="form-group">
                        <label for="exampleFormControlFile1">Choose file&hellip;</label>
                        <input type="file" name="uploadfile" class="form-control-file" id="exampleFormControlFile1">
                    </div>
                    <br>
                    <br>
                    <input type="hidden" name="uploadType" value="initialize">
                    <input type="hidden" name="todo" value="upload">
                    <button type="submit" name="Submit" class="btn btn-primary">Submit</button>
                    <button type="reset" name="Reset" class="btn btn-primary">Cancel</button>
                </form>
                <%="<br>User session: " + timestamp%>
            </center>
        </div>
    </body>
</html>
