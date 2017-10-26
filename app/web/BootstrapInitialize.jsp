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
            <h1>Initialize SLOCA</h1><br>
            <%
                HashMap<Integer, String> demographicsError = (HashMap<Integer, String>) session.getAttribute("demographicsError");
                HashMap<Integer, String> locationLookupError = (HashMap<Integer, String>) session.getAttribute("locationLookupError");
                HashMap<Integer, String> locationError = (HashMap<Integer, String>) session.getAttribute("locationError");
                String success = (String) session.getAttribute("success"); //success message retrieved from UploadServlet

                String error = (String) session.getAttribute("error"); //error message retrieved from UploadServlet
                if (error != null && error.length() >= 1) {
                    out.println("<font color='red'>" + "<br/>" + error + "</font>");
                    session.removeAttribute("error");
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
                    session.removeAttribute("demographicsError");
                }
                out.print("</tbody></table></div><br>");

                // Table for locationLookup error
                if (locationLookupError != null && locationLookupError.size() > 0) {
                    Set<Integer> keys = locationLookupError.keySet();
                    keys = new TreeSet(keys);
                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th colspan=\"2\"><center>Location Lookup Error</center></th></tr><tr><th><center>Row</center></th><th><center>Errors</center></th></tr>");
                    for (Integer key : keys) {
                        out.print("<tr><td>" + key + "</td><td>" + locationLookupError.get(key) + "</td></tr>");
                    }
                    session.removeAttribute("locationLookupError");
                }
                out.print("</tbody></table></div><br>");

                // Table for location error
                if (locationError != null && locationError.size() > 0) {
                    Set<Integer> keys = locationError.keySet();
                    keys = new TreeSet(keys);
                    out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                    out.print("<tr><th colspan=\"2\"><center>Location Error</center></th></tr><tr><th><center>Row</center></th><th><center>Errors</center></th></tr>");
                    for (Integer key : keys) {
                        out.print("<tr><td>" + key + "</td><td>" + locationError.get(key) + "</td></tr>");
                    }
                    session.removeAttribute("locationError");
                }
                out.print("</tbody></table></div><br>");

                if (success != null) {
                    out.println("<center><font color='green'>" + "<br/> SUCCESS!!</font>");
                    out.println("<font color='green'>" + "<br/>" + success + "</font></center>");
                    session.removeAttribute("success");
                }
            %>
            <center>
                <form method="post" action="processUpload" enctype="multipart/form-data">
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
                <%
                    // delete before submission
                    // upload 
                    out.print("<br><br><br>");
                    out.print("<h1>debugging</h1>");
                    out.print("<h2>upload function (change line 44 before testing)</h2>");
                    out.print("<form method=\"post\" action=\"http://localhost:8084/app/json/bootstrap\" enctype=\"multipart/form-data\">\r\n"
                            + "<div class=\"form-group\">\r\n"
                            + "<label for=\"exampleFormControlFile1\">Choose file&hellip;</label>\r\n"
                            + "<input type=\"file\" name=\"uploadfile\" class=\"form-control-file\" id=\"exampleFormControlFile1\">\r\n"
                            + "</div>\r\n"
                            + "<input type=\"hidden\" name=\"uploadType\" value=\"initialize\">\r\n"
                            + "<input type=\"hidden\" name=\"todo\" value=\"upload\">\r\n"
                            + "<button type=\"submit\" name=\"Submit\" class=\"btn btn-primary\">Submit</button>\r\n"
                            + "<button type=\"reset\" name=\"Reset\" class=\"btn btn-primary\">Cancel</button>\r\n"
                            + "</form>");
                    
                    
                    // delete before submission
                    // update
                    out.print("<br><br>");
                    out.print("<h2>update function (change line 44 before testing)</h2>");
                    out.print("<form method=\"post\" action=\"http://localhost:8084/app/json/update\" enctype=\"multipart/form-data\">\r\n"
                            + "<div class=\"form-group\">\r\n"
                            + "<label for=\"exampleFormControlFile1\">Choose file&hellip;</label>\r\n"
                            + "<input type=\"file\" name=\"uploadfile\" class=\"form-control-file\" id=\"exampleFormControlFile1\">\r\n"
                            + "</div>\r\n"
                            + "<input type=\"hidden\" name=\"uploadType\" value=\"initialize\">\r\n"
                            + "<input type=\"hidden\" name=\"todo\" value=\"upload\">\r\n"
                            + "<button type=\"submit\" name=\"Submit\" class=\"btn btn-primary\">Submit</button>\r\n"
                            + "<button type=\"reset\" name=\"Reset\" class=\"btn btn-primary\">Cancel</button>\r\n"
                            + "</form>");
                %>

                <%="<br>User session: " + timestamp%>
            </center>
        </div>
    </body>
</html>
