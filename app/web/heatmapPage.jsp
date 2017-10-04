<%@page import="java.util.ArrayList"%>
<%@page import="model.HeatMap"%>
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

    <center>
        <div class="container">
            <br><br>
            <!-- Form for user to input date&time and level for heat map -->
            <form method=post action="processHeatMap">
                <!-- form input for date & time  -->
                <div class="form-group">
                    <label class="form-control-label" for="timing">Enter date & time:</label>
                    <input type="text" class="form-control" id="timing" name="endtimeDate" placeholder="Example: 2014-03-23 13:40:00" required>
                </div>
                <!-- select menu for level, default is B1  -->
                <div class="form-group">
                    <label for="floor">Generate Level in SIS Building:</label>
                    <select class="form-control" id="floor" name = "floor">
                        <option selected value="0">B1</option>
                        <option value="1">Level 1</option>
                        <option value="2">Level 2</option>
                        <option value="3">Level 3</option>                        
                        <option value="4">Level 4</option>
                        <option value="5">Level 5</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>
        <%
            String floor = (String) session.getAttribute("floorName");
            String date = (String) session.getAttribute("endtimeDate");
            ArrayList<HeatMap> heatmapList = (ArrayList<HeatMap>) session.getAttribute("heatmapList");
            if (floor != null && date != null) {
                out.print("<h3>Floor:" + floor + " Date:" + date + "</h3>");
                out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                out.print("<tr><th>Areas</th></thead><tbody>");

                if (heatmapList != null) {
                    for (HeatMap heatmap : heatmapList) {
                        out.print("<tr><td>" + heatmap + "<tr></td>");
                    }
                }
                out.print("</tbody></table></div>");

        %>
        <%if (floor.equals("B1")) {%>
        <div id="B1HeatMap"></div>            
        <script>
            window.setTimeout(function myFunction() {

                var xscale = d3.scale.linear()
                        .domain([0, 50.0])
                        .range([0, 720]),
                        yscale = d3.scale.linear()
                        .domain([0, 33.79])
                        .range([0, 487]),
                        map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                        imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                        heatmap = d3.floorplan.heatmap(), //create a heat map layer
                        mapdata = {};

                mapdata[imagelayer.id()] = [{
                        url: 'resource/image/SISB1.jpg', //URL of the image to display
                        x: 0, //X coordinate of the upper left corner of the image (in xScale coordinates)
                        y: 0, //Y coordinate of the upper left corner of the image (in yScale coordinates)
                        height: 33.79, //height of the image (in yScale coordinates)
                        width: 50.0 //width of the image (in xScale coordinates)
                                // (optional) opacity of the displayed image [0.0-1.0] (default: 1.0)
                    }];

                map.addLayer(imagelayer) //add layer to the image
                        .addLayer(heatmap);

                d3.json("resource/B1.json", function (data) {
                    mapdata[heatmap.id()] = data.heatmap; //set variable from json
                    d3.select("#B1HeatMap").append("svg")
                            .attr("height", 487).attr("width", 720)
                            .datum(mapdata).call(map);
                });
            }, 5000);


            var obj = new Object();
            $.getJSON("resource/B1.json", function (data) {
                obj = data.heatmap;
                obj.map[0].value = <%=heatmapList.get(0).getHeatLevel()%>;
                obj.map[1].value = <%=heatmapList.get(1).getHeatLevel()%>;
                var JSONString = JSON.stringify(obj);
                $.ajax({
                    url: 'processHeatMap',
                    type: 'get',
                    dataType: 'json',
                    data: {
                        heatmap: JSONString,
                    },
                    success: function (data) {
                        myFunction();
                    },
                    error: function () {
                        alert("error");
                    }
                });
            });
        </script>    
        <% } else if (floor.equals("L1")) {%>
        <script>
            var obj = new Object();
            $.getJSON("resource/L1.json", function (data) {
                obj = data.heatmap;
                obj.map[0].value = <%=heatmapList.get(0).getHeatLevel()%>;
                obj.map[1].value = <%=heatmapList.get(1).getHeatLevel()%>;
                var JSONString = JSON.stringify(obj);
                $.ajax({
                    url: 'processHeatMap',
                    type: 'get',
                    dataType: 'json',
                    data: {
                        heatmap: JSONString,
                    }
                });
            });
        </script>        

        <div id="L1HeatMap"></div>
        <script type="text/javascript">
            var xscale = d3.scale.linear()
                    .domain([0, 50.0])
                    .range([0, 720]),
                    yscale = d3.scale.linear()
                    .domain([0, 33.79])
                    .range([0, 487]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                    imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                    heatmap = d3.floorplan.heatmap(), //create a heat map layer
                    mapdata = {};

            mapdata[imagelayer.id()] = [{
                    url: 'resource/image/SISL1.jpg', //URL of the image to display
                    x: 0, //X coordinate of the upper left corner of the image (in xScale coordinates)
                    y: 0, //Y coordinate of the upper left corner of the image (in yScale coordinates)
                    height: 33.79, //height of the image (in yScale coordinates)
                    width: 50.0 //width of the image (in xScale coordinates)
                            // (optional) opacity of the displayed image [0.0-1.0] (default: 1.0)
                }];

            map.addLayer(imagelayer) //add layer to the image
                    .addLayer(heatmap);

            d3.json("resource/L1.json", function (data) {
                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L1HeatMap").append("svg")
                        .attr("height", 487).attr("width", 720)
                        .datum(mapdata).call(map);
            });
        </script>        

        <% } else if (floor.equals("L2")) {%>
        <script>
            var obj = new Object();
            $.getJSON("resource/L2.json", function (data) {
                obj = data.heatmap;
                obj.map[0].value = <%=heatmapList.get(0).getHeatLevel()%>;
                obj.map[1].value = <%=heatmapList.get(1).getHeatLevel()%>;
                var JSONString = JSON.stringify(obj);
                $.ajax({
                    url: 'processHeatMap',
                    type: 'get',
                    dataType: 'json',
                    data: {
                        heatmap: JSONString,
                    }
                });
            });
        </script>        

        <div id="L2HeatMap"></div>
        <script type="text/javascript">
            var xscale = d3.scale.linear()
                    .domain([0, 50.0])
                    .range([0, 720]),
                    yscale = d3.scale.linear()
                    .domain([0, 33.79])
                    .range([0, 487]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                    imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                    heatmap = d3.floorplan.heatmap(), //create a heat map layer
                    mapdata = {};

            mapdata[imagelayer.id()] = [{
                    url: 'resource/image/SISL2.jpg', //URL of the image to display
                    x: 0, //X coordinate of the upper left corner of the image (in xScale coordinates)
                    y: 0, //Y coordinate of the upper left corner of the image (in yScale coordinates)
                    height: 33.79, //height of the image (in yScale coordinates)
                    width: 50.0 //width of the image (in xScale coordinates)
                            // (optional) opacity of the displayed image [0.0-1.0] (default: 1.0)
                }];

            map.addLayer(imagelayer) //add layer to the image
                    .addLayer(heatmap);

            d3.json("resource/L2.json", function (data) {
                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L2HeatMap").append("svg")
                        .attr("height", 487).attr("width", 720)
                        .datum(mapdata).call(map);
            });
        </script>        

        <% } else if (floor.equals("L3")) {%>
        <script>
            var obj = new Object();
            $.getJSON("resource/L3.json", function (data) {
                obj = data.heatmap;
                obj.map[0].value = <%=heatmapList.get(0).getHeatLevel()%>;
                obj.map[1].value = <%=heatmapList.get(1).getHeatLevel()%>;
                var JSONString = JSON.stringify(obj);
                $.ajax({
                    url: 'processHeatMap',
                    type: 'get',
                    dataType: 'json',
                    data: {
                        heatmap: JSONString,
                    }
                });
            });
        </script>        

        <div id="L3HeatMap"></div>
        <script type="text/javascript">
            var xscale = d3.scale.linear()
                    .domain([0, 50.0])
                    .range([0, 720]),
                    yscale = d3.scale.linear()
                    .domain([0, 33.79])
                    .range([0, 487]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                    imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                    heatmap = d3.floorplan.heatmap(), //create a heat map layer
                    mapdata = {};

            mapdata[imagelayer.id()] = [{
                    url: 'resource/image/SISL3.jpg', //URL of the image to display
                    x: 0, //X coordinate of the upper left corner of the image (in xScale coordinates)
                    y: 0, //Y coordinate of the upper left corner of the image (in yScale coordinates)
                    height: 33.79, //height of the image (in yScale coordinates)
                    width: 50.0 //width of the image (in xScale coordinates)
                            // (optional) opacity of the displayed image [0.0-1.0] (default: 1.0)
                }];

            map.addLayer(imagelayer) //add layer to the image
                    .addLayer(heatmap);

            d3.json("resource/L3.json", function (data) {
                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L3HeatMap").append("svg")
                        .attr("height", 487).attr("width", 720)
                        .datum(mapdata).call(map);
            });
        </script>        

        <% } else if (floor.equals("L4")) {%>
        <script>
            var obj = new Object();
            $.getJSON("resource/L4.json", function (data) {
                obj = data.heatmap;
                obj.map[0].value = <%=heatmapList.get(0).getHeatLevel()%>;
                obj.map[1].value = <%=heatmapList.get(1).getHeatLevel()%>;
                var JSONString = JSON.stringify(obj);
                $.ajax({
                    url: 'processHeatMap',
                    type: 'get',
                    dataType: 'json',
                    data: {
                        heatmap: JSONString,
                    }
                });
            });
        </script>        

        <div id="L4HeatMap"></div>
        <script type="text/javascript">
            var xscale = d3.scale.linear()
                    .domain([0, 50.0])
                    .range([0, 720]),
                    yscale = d3.scale.linear()
                    .domain([0, 33.79])
                    .range([0, 487]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                    imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                    heatmap = d3.floorplan.heatmap(), //create a heat map layer
                    mapdata = {};

            mapdata[imagelayer.id()] = [{
                    url: 'resource/image/SISL4.jpg', //URL of the image to display
                    x: 0, //X coordinate of the upper left corner of the image (in xScale coordinates)
                    y: 0, //Y coordinate of the upper left corner of the image (in yScale coordinates)
                    height: 33.79, //height of the image (in yScale coordinates)
                    width: 50.0 //width of the image (in xScale coordinates)
                            // (optional) opacity of the displayed image [0.0-1.0] (default: 1.0)
                }];

            map.addLayer(imagelayer) //add layer to the image
                    .addLayer(heatmap);

            d3.json("resource/L4.json", function (data) {
                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L4HeatMap").append("svg")
                        .attr("height", 487).attr("width", 720)
                        .datum(mapdata).call(map);
            });
        </script>        

        <% } else if (floor.equals("L5")) {%>  
        <div id="L5HeatMap"></div>
        <script>
            var obj = new Object();
            $.getJSON("resource/L5.json", function (data) {
                obj = data.heatmap;
                obj.map[0].value = <%=heatmapList.get(0).getHeatLevel()%>;
                obj.map[1].value = <%=heatmapList.get(1).getHeatLevel()%>;
                var JSONString = JSON.stringify(obj);
                $.ajax({
                    url: 'processHeatMap',
                    type: 'get',
                    dataType: 'json',
                    data: {
                        heatmap: JSONString,
                    }
                });
            });
        </script>        

        <script type="text/javascript">
            var xscale = d3.scale.linear()
                    .domain([0, 50.0])
                    .range([0, 720]),
                    yscale = d3.scale.linear()
                    .domain([0, 33.79])
                    .range([0, 487]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                    imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                    heatmap = d3.floorplan.heatmap(), //create a heat map layer
                    mapdata = {};

            mapdata[imagelayer.id()] = [{
                    url: 'resource/image/SISL5.jpg', //URL of the image to display
                    x: 0, //X coordinate of the upper left corner of the image (in xScale coordinates)
                    y: 0, //Y coordinate of the upper left corner of the image (in yScale coordinates)
                    height: 33.79, //height of the image (in yScale coordinates)
                    width: 50.0 //width of the image (in xScale coordinates)
                            // (optional) opacity of the displayed image [0.0-1.0] (default: 1.0)
                }];

            map.addLayer(imagelayer) //add layer to the image
                    .addLayer(heatmap);

            d3.json("resource/L5.json", function (data) {
                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L5HeatMap").append("svg")
                        .attr("height", 487).attr("width", 720)
                        .datum(mapdata).call(map);
            });
        </script> 
        <%}
            }%>
        <%
            session.removeAttribute("heatmapList");
            session.removeAttribute("endtimeDate");
            session.removeAttribute("floor");
        %>
        <%="<br><br>Copy Paste"%>
        <%="<br>2014-03-23 13:55:00"%>
        <%="<br><br>User session: " + timestamp%>           
    </center>      
</body>
</html>
