<%@page import="java.util.List"%>
<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>

<%
    if (session.getAttribute("admin") != null && session.getAttribute("admin").equals("admin")) { //check if admin arrive page via link or through login
        response.sendRedirect("adminPage.jsp"); //send back to admin page
        return;
    } else if (session.getAttribute("user") == null) { //check if user arrive page via link or through login
        response.sendRedirect("index.jsp"); //send back to index page
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

    <%  //user details, get using session
        User user = (User) session.getAttribute("user");
        String name = user.getName();
    %>  
    <head>
        <title>Breakdown Report</title>
        <link href="data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeqPVFXKh3DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFmW5gFXlup/Y57tNAAAAAAAAAAAAAAAAH1RFyF2VB8IAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJGz3j2ct9YLUpHuFUmP8NxLnP013QkADoJREaOATxGddlIhDwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB2qOQcXZjnbQAAAABRmfc4OY//72tjYMOZPgAzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFWV7T1Pk/GqAAAAAGJqfbs1jP//RJb/bAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR5b7f09+wOXMGwAnR5r/bTiH9P9AjPGvhbDbDwAAAAAAAAAAAAAAAAAAAACfttEVAAAAAAAAAAAAAAAAAAAAAAAAAABHgdDbOo38uwAAAABPk/B9Nob1/zyI8+RUlupJAAAAAAAAAAAAAAAAia3bKI2w3x4AAAAAAAAAAAAAAACCPgAFjEwFcEGS/Ms8ifLcjLLaEFWW8GY5h/T/N4f0/0KN8KRjneciAAAAAAAAAABtoedSg8vwAQAAAAAAAAAAhVYdCoNKBmsAAAAAQozwwTuI8v9Rle1GZ6LqPj2J8+Q1hfX/Ooj0/0CL8aNCi+9gRYzwsqPF3gcAAAAAAAAAAAAAAACBUhaBAAAAAAAAAABGjvGVO4nz/z6K8aBvpOQxSpDvfziG9OY2hvX/M4b1/z6L8t+nvMEpAAAAAAAAAAAAAAAAf1EUZ4JTGEYAAAAAAAAAAFiY6087ifPvOYjz/0KN8I1cmudAY5vpSE6R60xgnepaUZPquQAAAAAAAAAAAAAAAH9ODg2AUBOphlsmDgAAAAAAAAAAusvSCEeP73s7iPLxOYj0/z2J8+g9ifTCP4rx51CT7n8AAAAAAAAAAAAAAAAAAAAAg1MYO4JRErx/UhUqAAAAAAAAAAAAAAAAAAAAAE2d/zlBj/p7QorvklCU7D8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACCVxssgU8PpINPDoOBTxFFgk8RJoJLAiOMQQArmkEAIwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBThBrgU4PxIJRFN5/TxKngVMaY4RaIw8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA//8AAP//AADs/wAA8/8AANP/AADs/wAA5n8AAPMfAAD5hQAA7GEAAP8eAAD3wQAA+/sAAPz/AAD/HwAA//8AAA==" rel="icon" type="image/x-icon" />
    </head>
    <body>
        <table id="excelDataTable" border="1">
        </table>
        <nav class="navbar navbar-inverse"> <%-- navigation menu for user to click --%>
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="userPage.jsp">SLOCA</a>
                </div>
                <ul class="nav navbar-nav">
                    <li><a href="userPage.jsp">Home</a></li> <%-- send user to home page--%>
                    <li class="active"><a href="reportsPage.jsp">Basic Location Reports</a></li> <%-- set as active because user is in reports page. send user to reports page --%>
                    <li><a href="heatmapPage.jsp">Heat Map</a></li> <%-- send user to heatmap page --%>      
                    <li><a href="automaticGroupDetection.jsp">Automatic Group Detection</a></li> <%-- send user to Automatic Group Detection page --%>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="userPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                </ul>                
            </div>
        </nav>
    <center>

        <div class="container">
            <br><br>
            <form method=post action="report">
                <table>
                    <!-- report type -->
                    <input type="hidden" name="reportType" value="basicReport">

                    <!-- ending time and date field -->
                    <tr>
                        <!-- form input for date & time  -->
                    <div class="form-group">
                        <label for="example-datetime-local-input" class="form-control-label">Enter date & time:</label>
                        <input class="form-control" type="datetime-local" id="input-datetime-local" name="timeDate" min="2013-01-01T00:00:00" max="2017-12-31T23:59:59" step="1" required>
                    </div>
                    </tr>
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <!-- first category box -->
                            <label for="order">Sort by (first):</label>
                            <select name="order" class="form-control">
                                <option value = "year">Year</option>
                                <option value = "gender">Gender</option>
                                <option value = "school">School</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <!-- second category box -->
                            <label for="order">Sort by (second):</label>
                            <select name="order" class="form-control">
                                <option value = "none">(Optional)</option>
                                <option value = "year">Year</option>
                                <option value = "gender">Gender</option>
                                <option value = "school">School</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <!-- third category box -->
                            <label for="order">Sort by (third):</label>
                            <select name="order" class="form-control">
                                <option value = "none">(Optional)</option>
                                <option value = "year">Year</option>
                                <option value = "gender">Gender</option>
                                <option value = "school">School</option>
                            </select>
                        </div>
                    </div>
                </table>
                <br>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>


        <%
            List<String> options = (List<String>) session.getAttribute("orderList");
            if (options != null) {
                String timeDate = (String) session.getAttribute("timeDate");
                //If breakdown report is generated
                String breakdownReport = (String) session.getAttribute("breakdownReport");
                if (breakdownReport != null && breakdownReport.length() > 0) {
                    out.print("<h3>Breakdown Report at " + timeDate + "</h3>");
                    out.print(breakdownReport);
                } else {
                    String errMsg = "duplicated option found: ";
                    boolean duplicate = false;

                    for (int i = 0; i < options.size(); i++) {
                        for (int j = 1; j < options.size(); j++) {
                            if (options.get(i).equals(options.get(j)) && !options.get(i).equals("none") && i != j) {
                                // duplicate element found
                                duplicate = true;
                                errMsg += options.get(j);
                                break; //once a duplicate element is found, exit the loop
                            }
                        }
                        if (duplicate) {
                            break; //once a duplicate element is found, exit the loop
                        }
                    }

                    if (duplicate) {
                        out.print("<br/><div class=\"alert alert-danger\" role=\"alert\"><strong>" + "The data is not available within time " + timeDate + " because " + errMsg + "</strong></div>");
                    } else {
                        out.print("<br/><div class=\"alert alert-danger\" role=\"alert\"><strong>" + "The data is not available within time " + timeDate + "</strong></div>");
                    }
                }
            }
            session.removeAttribute("breakdownReport"); //remove session attribute from the session object
            session.removeAttribute("orderList"); //remove session attribute from the session object            
            session.removeAttribute("timeDate"); //remove session attribute from the session object 
        %>        
    </center>
</body>
</html>
