<%@page import="java.util.Arrays"%>
<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>
<%
    //check if admin arrive page via link or through login
    if(session.getAttribute("admin") != null && session.getAttribute("admin").equals("admin")){
        response.sendRedirect("adminPage.jsp"); //send back to admin page
        return;
    //check if user arrive page via link or through login
    } else if(session.getAttribute("user")==null){
        response.sendRedirect("index.jsp"); //send back to index page
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
            User user = (User)session.getAttribute("user");
            String timedate = (String)session.getAttribute("timeDate");
            String topK = (String)session.getAttribute("topK");
            String name = user.getName();
            String timestamp = (String)session.getAttribute("timestamp");
            timestamp = name + "-" + timestamp;
        %>
        <div class="topnav" id="myTopnav">
            <a href="#heatmap">Basic Location Reports</a>
            <a href="#heatmap">Heat Map</a>
            <div class="right">
                <a href="#knp"><%="Welcome " + name +"!"%></a>
                <a href="processLogout">Logout</a>
            </div>
        </div><br><br>
        <%
            //if basic report is generated
            float totalCount = 0;
            if(request.getAttribute("breakdownReport")!=null){
                String breakdownReport = (String)(request.getAttribute("breakdownReport"));
                String[] y = breakdownReport.split(",");
                totalCount = Integer.parseInt(y[0]) + Integer.parseInt(y[1]);
                out.print("Breakdown by Year & Gender at " + timedate);
                out.print("<table border=\"1\">");
                out.print("<tr><td></td><td>Male</td><td>Female</td><tr>");
                out.print("<tr><td>2010</td><td>" + Math.round((Integer.parseInt(y[2])/totalCount*100)*10)/10.0 + "%</td><td>" + Math.round((Integer.parseInt(y[3])/totalCount*100)*10)/10.0 + "%</td><tr>");
                out.print("<tr><td>2011</td><td>" + Math.round((Integer.parseInt(y[4])/totalCount*100)*10)/10.0 + "%</td><td>" + Math.round((Integer.parseInt(y[5])/totalCount*100)*10)/10.0 + "%</td><tr>");
                out.print("<tr><td>2012</td><td>" + Math.round((Integer.parseInt(y[6])/totalCount*100)*10)/10.0 + "%</td><td>" + Math.round((Integer.parseInt(y[7])/totalCount*100)*10)/10.0 + "%</td><tr>");
                out.print("<tr><td>2013</td><td>" + Math.round((Integer.parseInt(y[8])/totalCount*100)*10)/10.0 + "%</td><td>" + Math.round((Integer.parseInt(y[9])/totalCount*100)*10)/10.0 + "%</td><tr>");
                out.print("<tr><td>2014</td><td>" + Math.round((Integer.parseInt(y[10])/totalCount*100)*10)/10.0 + "%</td><td>" + Math.round((Integer.parseInt(y[11])/totalCount*100)*10)/10.0 + "%</td><tr>");
                out.print("<tr><td>Total:</td><td>" + Math.round((Integer.parseInt(y[0])/totalCount*100)*10)/10.0 + "%</td><td>" + Math.round((Integer.parseInt(y[1])/totalCount*100)*10)/10.0 + "%</td><tr>");
                out.print("</table>");
            }
        %>


        <%
            //If top K report is generated
            if(request.getAttribute("topKPopular")!=null){
                out.print("Top-" + topK +" Popular Places at " + timedate);
                out.print("<table border=\"1\">");
                String topKPopular = (String)(request.getAttribute("topKPopular"));
                String[] y = topKPopular.split(",");
                out.print("<table border=\"1\">");
                for(String next: y){
                    out.print("<tr><td>" + next + "</td></tr>");
                }
                out.print("</table>");
            }
        %>


        <h2>Basic Location Reports</h2>
        <form method=post action="report">
        <table>
            <!-- first row -->
            <tr>
                <td><input type="radio" name="reportType" value="breakdownReport" checked></td>
                <td>Breakdown by Year & Gender&ensp;&ensp;</td>
                <td><input type="text" name="timeDate" size="25" placeholder="Enter date and time" required/></td>
            </tr>
            <!-- second row -->
            <tr>
                <td><input type="radio" name="reportType" value="topKPopular"></td>
                <td>Top-K Popular Places</td>
                <td><select name = "topK">
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option selected value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                        <option value="6">6</option>
                        <option value="7">7</option>
                        <option value="8">8</option>
                        <option value="9">9</option>
                        <option value="10">10</option>
                </select></td>
            </tr>
            <!-- third row -->
            <tr>
                <td><input type="radio" name="reportType" value="tnp"></td>
                <td>Top-K Next Places</td>
                <td><input type="submit" value ="Generate"/></td>
            </tr>
            <!-- forth row -->
            <tr>
                <td><input type="radio" name="reportType" value="tcc"></td>
                <td>Top-K Companions</td>
            </tr>
        </table>
        </form>


        <%
            //debug
            out.print("<br>Top-K Companions & Top-K Next Places not working yet ");
            out.print("<br>Enter in this format: 2014-03-23 13:40:00");
            out.print("<br><br>User: &ensp;&ensp;" + name);
            out.print("<br>Session: " + timestamp);
        %>
    </body>
</html>
