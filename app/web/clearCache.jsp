<%
    //Clear cache, don't allow user to backpage after logging out
    response.setHeader("Cache-Control", "no-cache"); //for firefox(https), Internet Explorer
    response.setHeader("Cache-Control", "no-store"); //for chrome, firefox(https), safari(https), Internet Explorer
    response.setHeader("Cache-Control", "must-revalidate"); //for opera(https), Internet Explorer
    response.setHeader("Pragma", "no-cache"); //for firefox(https), Internet Explorer(https)
    response.setDateHeader("Expires", 0); //for Internet Explorer
%>