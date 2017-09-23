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
<style type="text/css">
    @import url('http://dciarletta.github.io/d3-floorplan/d3.floorplan.css');
</style>
<!DOCTYPE html>
<%@include file="clearCache.jsp"%> <%-- clear cache, don't allow user to backpage after logging out --%>
<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/bootstrap.css" rel="stylesheet"> <%-- twitter bootstrap for designing--%>    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script> <%-- twitter bootstrap for designing--%>
    <script src='js/bootstrap.js'></script> <%-- twitter bootstrap for designing--%>

    <%-- javascript library to manipulate documents based on data for heat map--%>
    <script data-require="d3@2.10.0" data-semver="2.10.0" src="//cdnjs.cloudflare.com/ajax/libs/d3/2.10.0/d3.v2.js"></script>     
    <script type="text/javascript" src="http://dciarletta.github.io/d3-floorplan/d3.floorplan.min.js"></script>        

    <%  //user details, get using session
        User user = (User) session.getAttribute("user");
        String name = user.getName();
        String timestamp = (String) session.getAttribute("timestamp");
    %>  
    <head>
        <title>Heat Map Page</title>
    </head>
    <body>
        <nav class="navbar navbar-inverse"> <%-- navigation menu for user to click --%>
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="userPage.jsp">SLOCA</a>
                </div>
                <ul class="nav navbar-nav">
                    <li><a href="userPage.jsp">Home</a></li> <%-- send user to home page--%>
                    <li><a href="reportsPage.jsp">Basic Location Reports</a></li> <%-- send user to reports page --%>
                    <li class="active"><a href="heatmapPage.jsp">Heat Map</a></li> <%-- set as active because user is in heat map page. send user to heatmap page --%>                  
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="userPage.jsp"><%="Welcome " + name + "!"%></a></li>
                    <li><a href="processLogout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li> <%-- send user to logout servlet and process logout --%>
                </ul>                
            </div>
        </nav>
        <div id="demo"></div>
        <script id="demo-code" type="text/javascript">
            var xscale = d3.scale.linear()
                    .domain([0, 50.0])
                    .range([0, 720]),
                    yscale = d3.scale.linear()
                    .domain([0, 33.79])
                    .range([0, 487]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale),
                    imagelayer = d3.floorplan.imagelayer(),
                    heatmap = d3.floorplan.heatmap(),
                    vectorfield = d3.floorplan.vectorfield(),
                    pathplot = d3.floorplan.pathplot(),
                    overlays = d3.floorplan.overlays().editMode(true),
                    mapdata = {};

            mapdata[imagelayer.id()] = [{
                    url: 'resource/image/SISLevel1.jpg',
                    x: 0,
                    y: 0,
                    height: 33.79,
                    width: 50.0
                }];

            map.addLayer(imagelayer)
                    .addLayer(heatmap)
                    .addLayer(vectorfield)
                    .addLayer(pathplot)
                    .addLayer(overlays);

            d3.json("http://dciarletta.github.io/d3-floorplan/demo-data.json", function (data) {
                mapdata[heatmap.id()] = data.heatmap;
                mapdata[overlays.id()] = data.overlays;
                mapdata[vectorfield.id()] = data.vectorfield;
                mapdata[pathplot.id()] = data.pathplot;
                d3.select("#demo").append("svg")
                        .attr("height", 487).attr("width", 720)
                        .datum(mapdata).call(map);
            });
        </script>
        <center><%="<br>User session: " + timestamp%></center>
</body>
</html>
