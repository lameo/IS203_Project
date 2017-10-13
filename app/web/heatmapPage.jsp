<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
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
    <%-- heatmap css --%>
    /* map menu controls */
    .ui-active rect {fill: #212121}
    .ui-active text {fill: #fff}
    .ui-default rect {fill: #e6e6e6}
    .ui-default text {fill: #000}

    /* heatmap colors (courtesy of colorbrewer2.org) */
    .RdYlBu .d6-6 {fill:#ff0000;background:#ff0000;color:#121212}
    .RdYlBu .d6-5 {fill:#e11f27;background:#e11f27;color:#121212}
    .RdYlBu .d6-4 {fill:#F08080;background:#F08080;color:#121212}
    .RdYlBu .d6-3 {fill:#FFA07A;background:#FFA07A;color:#121212}
    .RdYlBu .d6-2 {fill:#ffe5e5;background:#ffe5e5;color:#121212}
    .RdYlBu .d6-1 {fill:#fffeb7;background:#fffeb7;color:#121212}      
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
            <!-- Form for user to input date&time and level for heat map -->
            <form method=post action="processHeatMap">
                <!-- form input for date & time  -->
                <div class="form-group">
                    <label class="form-control-label" for="timing">Enter date & time:</label>
                    <input type="text" class="form-control" id="timing" name="endtimeDate" placeholder="Example: 2017-02-06 11:00:00" required>
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
            HashMap<String, HeatMap> heatmapList = (HashMap<String, HeatMap>) session.getAttribute("heatmapList");
            if (floor != null && date != null) {
                out.print("<h3>Floor:" + floor + " Date:" + date + "</h3>");
                out.print("<div class=\"container\"><table class=\"table table-bordered\"><thead>");
                out.print("<tr><th>Semantic Place</th><th>Quantity</th><th>Heat Level</th></thead><tbody>");

                if (heatmapList != null) {
                    Set<String> keys = heatmapList.keySet();
                    for (String key : keys) {
                        HeatMap heatMap = heatmapList.get(key);
                        out.print("<tr><td>" + heatMap.getPlace() + "</td><td>" + heatMap.getQtyPax() + "</td><td>"  + heatMap.getHeatLevel() + "</td></tr>");
                    }
                }
                out.print("</tbody></table></div>");

        %>
        <script>
            var xscale = d3.scale.linear()
                    .domain([0, 50])
                    .range([0, 1205]),
                    yscale = d3.scale.linear()
                    .domain([0, 38])
                    .range([0, 1106]),
                    map = d3.floorplan().xScale(xscale).yScale(yscale), //setup a floor plan map to hold layers and manage pan/zoom functionality
                    imagelayer = d3.floorplan.imagelayer(), //create a new image layer
                    heatmap = d3.floorplan.heatmap(), //create a heat map layer
                    mapdata = {};            
            
            var obj = new Object();            
        </script>
        <%if (floor.equals("B1")) {%>
        <div id="B1HeatMap"></div>            
        <script>
            $.getJSON("resource/B1.json", function (data) { //Jquery to get json
                obj = data.heatmap; //set json data into variable object
                var array = obj.map; //get map array from obj
                for (i = 0; i < array.length; i++) { //change the values according to SQL data
                    var locationname = obj.map[i].locationname;
                    if (locationname === "SMUSISB1CORRIDORTOSOE") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISB1CORRIDORTOSOE")!=null){out.print(heatmapList.get("SMUSISB1CORRIDORTOSOE").getHeatLevel());}else{out.print("0");}%>;
                    } else if (locationname === "SMUSISB1NEAROSL") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISB1NEAROSL")!=null){out.print(heatmapList.get("SMUSISB1NEAROSL").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISB1STUDYAREA") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISB1STUDYAREA")!=null){out.print(heatmapList.get("SMUSISB1STUDYAREA").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISB1CORRIDORTOLKS") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISB1CORRIDORTOLKS")!=null){out.print(heatmapList.get("SMUSISB1CORRIDORTOLKS").getHeatLevel());}else{out.print("0");}%>;                
                    } else if (locationname === "SMUSISB1LIFTLOBBY") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISB1LIFTLOBBY")!=null){out.print(heatmapList.get("SMUSISB1LIFTLOBBY").getHeatLevel());}else{out.print("0");}%>;                   
                    }
                }
                
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
                    
                heatmap.colorMode(['custom']); //set custom mode for colors
                heatmap.customThresholds([1, 2, 3, 4, 5, 6]); //set colors to heat levels 
                
                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#B1HeatMap").append("svg")
                        .attr("height", 1106).attr("width", 1205)
                        .datum(mapdata).call(map);
                    
                               
            });
        </script>    
        <% } else if (floor.equals("L1")) {%>
        <div id="L1HeatMap"></div>
        <script>
            $.getJSON("resource/L1.json", function (data) { //Jquery to get json
                obj = data.heatmap; //set json data into variable object
                var array = obj.map; //get map array from obj
                for (i = 0; i < array.length; i++) { //change the values according to SQL data
                    var locationname = obj.map[i].locationname;
                    if (locationname === "SMUSISL1RECEPTION") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL1RECEPTION")!=null){out.print(heatmapList.get("SMUSISL1RECEPTION").getHeatLevel());}else{out.print("0");}%>;
                    } else if (locationname === "SMUSISL1LOBBY") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL1LOBBY")!=null){out.print(heatmapList.get("SMUSISL1LOBBY").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL1WAITINGAREA") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL1WAITINGAREA")!=null){out.print(heatmapList.get("SMUSISL1WAITINGAREA").getHeatLevel());}else{out.print("0");}%>;                 
                    } 
                }
                
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

                heatmap.colorMode(['custom']); //set custom mode for colors
                heatmap.customThresholds([1, 2, 3, 4, 5, 6]); //set colors to heat levels 

                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L1HeatMap").append("svg")
                        .attr("height", 1106).attr("width", 1205)
                        .datum(mapdata).call(map);
            });
        </script>  
        <% } else if (floor.equals("L2")) {%>       
        <div id="L2HeatMap"></div>
        <script>
            $.getJSON("resource/L2.json", function (data) { //Jquery to get json
                obj = data.heatmap; //set json data into variable object
                var array = obj.map; //get map array from obj
                for (i = 0; i < array.length; i++) { //change the values according to SQL data
                    var locationname = obj.map[i].locationname;
                    if (locationname === "SMUSISL2LOBBY") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2LOBBY")!=null){out.print(heatmapList.get("SMUSISL2LOBBY").getHeatLevel());}else{out.print("0");}%>;
                    } else if (locationname === "SMUSISL2STUDYAREA1") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2STUDYAREA1")!=null){out.print(heatmapList.get("SMUSISL2STUDYAREA1").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL2STUDYAREA2") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2STUDYAREA2")!=null){out.print(heatmapList.get("SMUSISL2STUDYAREA2").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL2SR2-4") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2SR2-4")!=null){out.print(heatmapList.get("SMUSISL2SR2-4").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL2SR2-3") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2SR2-3")!=null){out.print(heatmapList.get("SMUSISL2SR2-3").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL2SR2-2") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2SR2-2")!=null){out.print(heatmapList.get("SMUSISL2SR2-2").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL2SR2-1") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL2SR2-1")!=null){out.print(heatmapList.get("SMUSISL2SR2-1").getHeatLevel());}else{out.print("0");}%>;                 
                    } 
                }
                
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

                heatmap.colorMode(['custom']); //set custom mode for colors
                heatmap.customThresholds([1, 2, 3, 4, 5, 6]); //set colors to heat levels 

                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L2HeatMap").append("svg")
                        .attr("height", 1106).attr("width", 1205)
                        .datum(mapdata).call(map);
            });
        </script>     
        <% } else if (floor.equals("L3")) {%>
        <div id="L3HeatMap"></div>       
        <script>
            $.getJSON("resource/L3.json", function (data) { //Jquery to get json
                obj = data.heatmap; //set json data into variable object
                var array = obj.map; //get map array from obj
                for (i = 0; i < array.length; i++) { //change the values according to SQL data
                    var locationname = obj.map[i].locationname;
                    if (locationname === "SMUSISL3LOBBY") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3LOBBY")!=null){out.print(heatmapList.get("SMUSISL3LOBBY").getHeatLevel());}else{out.print("0");}%>;
                    } else if (locationname === "SMUSISL3STUDYAREA1") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3STUDYAREA1")!=null){out.print(heatmapList.get("SMUSISL3STUDYAREA1").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL3STUDYAREA2") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3STUDYAREA2")!=null){out.print(heatmapList.get("SMUSISL3STUDYAREA2").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL3SR3-4") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3SR3-4")!=null){out.print(heatmapList.get("SMUSISL3SR3-4").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL3CLSRM") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3CLSRM")!=null){out.print(heatmapList.get("SMUSISL3CLSRM").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL3SR3-3") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3SR3-3")!=null){out.print(heatmapList.get("SMUSISL3SR3-3").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL3SR3-2") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3SR3-2")!=null){out.print(heatmapList.get("SMUSISL3SR3-2").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL3SR3-1") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL3SR3-1")!=null){out.print(heatmapList.get("SMUSISL3SR3-1").getHeatLevel());}else{out.print("0");}%>;                 
                    }
                }
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

                heatmap.colorMode(['custom']); //set custom mode for colors
                heatmap.customThresholds([1, 2, 3, 4, 5, 6]); //set colors to heat levels 

                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L3HeatMap").append("svg")
                        .attr("height", 1106).attr("width", 1205)
                        .datum(mapdata).call(map);
            });
        </script> 
        <% } else if (floor.equals("L4")) {%>
        <div id="L4HeatMap"></div>       
        <script>
            $.getJSON("resource/L4.json", function (data) { //Jquery to get json
                obj = data.heatmap; //set json data into variable object
                var array = obj.map; //get map array from obj
                for (i = 0; i < array.length; i++) { //change the values according to SQL data
                    var locationname = obj.map[i].locationname;
                    if (locationname === "SMUSISL4LOBBY") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL4LOBBY")!=null){out.print(heatmapList.get("SMUSISL4LOBBY").getHeatLevel());}else{out.print("0");}%>;
                    } else if (locationname === "SMUSISL4STUDYAREA1") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL4STUDYAREA1")!=null){out.print(heatmapList.get("SMUSISL4STUDYAREA1").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL4STUDYAREA2") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL4STUDYAREA2")!=null){out.print(heatmapList.get("SMUSISL4STUDYAREA2").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL4ACADOFFICE") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL4ACADOFFICE")!=null){out.print(heatmapList.get("SMUSISL4ACADOFFICE").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL4STUDYAREA3") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL4STUDYAREA3")!=null){out.print(heatmapList.get("SMUSISL4STUDYAREA3").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL4STUDYAREA4") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL4STUDYAREA4")!=null){out.print(heatmapList.get("SMUSISL4STUDYAREA4").getHeatLevel());}else{out.print("0");}%>;                    
                    }
                }
                
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

                heatmap.colorMode(['custom']); //set custom mode for colors
                heatmap.customThresholds([1, 2, 3, 4, 5, 6]); //set colors to heat levels 

                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L4HeatMap").append("svg")
                        .attr("height", 1106).attr("width", 1205)
                        .datum(mapdata).call(map);
            });
        </script> 
        <% } else if (floor.equals("L5")) {%>  
        <div id="L5HeatMap"></div>
        <script>
            $.getJSON("resource/L5.json", function (data) { //Jquery to get json
                obj = data.heatmap; //set json data into variable object
                var array = obj.map; //get map array from obj
                for (i = 0; i < array.length; i++) { //change the values according to SQL data
                    var locationname = obj.map[i].locationname;
                    if (locationname === "SMUSISL5LOBBY") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL5LOBBY")!=null){out.print(heatmapList.get("SMUSISL5LOBBY").getHeatLevel());}else{out.print("0");}%>;
                    } else if (locationname === "SMUSISL5STUDYAREA1") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL5STUDYAREA1")!=null){out.print(heatmapList.get("SMUSISL5STUDYAREA1").getHeatLevel());}else{out.print("0");}%>;                    
                    } else if (locationname === "SMUSISL5ACADOFFICE") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL5ACADOFFICE")!=null){out.print(heatmapList.get("SMUSISL5ACADOFFICE").getHeatLevel());}else{out.print("0");}%>;                 
                    } else if (locationname === "SMUSISL5STUDYAREA2") {
                            obj.map[i].value = <%if(heatmapList.get("SMUSISL5STUDYAREA2")!=null){out.print(heatmapList.get("SMUSISL5STUDYAREA2").getHeatLevel());}else{out.print("0");}%>;                    
                    }
                }
                
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

                heatmap.colorMode(['custom']); //set custom mode for colors
                heatmap.customThresholds([1, 2, 3, 4, 5, 6]); //set colors to heat levels 

                mapdata[heatmap.id()] = data.heatmap; //set variable from json
                d3.select("#L5HeatMap").append("svg")
                        .attr("height", 1106).attr("width", 1205)
                        .datum(mapdata).call(map);
            });
        </script>         
        <%}}%>
        <%
            session.removeAttribute("heatmapList");
            session.removeAttribute("endtimeDate");
            session.removeAttribute("floor");
        %>
        <%="<br><br>Copy Paste"%>
        <%="<br>2017-02-06 11:00:00"%>
        <%="<br><br>User session: " + timestamp%>           
    </center>      
</body>
</html>
