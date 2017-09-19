<%@page import="model.User"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.time.Instant"%>
<%@page import="javazoom.upload.*,java.util.*" %>
<jsp:useBean id="upBean" scope="page" class="javazoom.upload.UploadBean" >
  <jsp:setProperty name="upBean" property="folderstore" value="d:/testt/dontupload" />
  <jsp:setProperty name="upBean" property="filesizelimit" value="8589934592"/>  
</jsp:useBean>
<%
    //check if user arrive page via link or through login
    if(session.getAttribute("admin") == null || !session.getAttribute("admin").equals("admin")){
        response.sendRedirect("userPage.jsp"); //send back to user page
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
            text-align: left;
            float: right;
        }        
    </style>
    <body>
        <%
            String name = (String)session.getAttribute("admin");
            String timestamp = (String)session.getAttribute("timestamp");
            timestamp = name + "-" + timestamp;
        %>        
        <div class="topnav" id="myTopnav">
            <a href="#upload">Upload new datafile</a>          
            <div class="right">
                <a href="#knp"><%="Welcome " + name +"!"%></a>
                <a href="processLogout">Logout</a>            
            </div>
        </div>    
        <ul>
        <%
            if (MultipartFormDataRequest.isMultipartFormData(request)){
                // Uses MultipartFormDataRequest to parse the HTTP request.
                MultipartFormDataRequest mrequest = new MultipartFormDataRequest(request);
                String todo = null;
                if (mrequest != null) {
                    todo = mrequest.getParameter("todo");
                }
                if ((todo != null) && (todo.equalsIgnoreCase("upload"))){
                    Hashtable files = mrequest.getFiles();
                    if ((files != null) && (!files.isEmpty())){
                            UploadFile file = (UploadFile) files.get("uploadfile");
                            if (file != null) out.println("<li>Form field : uploadfile"+"<BR> Uploaded file : "+file.getFileName()+" ("+file.getFileSize()+" bytes)"+"<BR> Content Type : "+file.getContentType());
                            // Uses the bean now to store specified by jsp:setProperty at the top.
                            upBean.store(mrequest, "uploadfile");
                    } else{
                        out.println("<li>No uploaded files");
                    }
                } else {
                    out.println("<BR> todo="+todo);
                }
            }
        %>
        </ul>
        <form method="post" action="adminPage.jsp" name="upform" enctype="multipart/form-data">
            <table>
                <tr>
                    <td align="left"><b>Select a file to upload :</b></td>
                </tr>
                <tr>
                    <td align="left">
                        <input type="file" name="uploadfile" size="50">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        <input type="hidden" name="todo" value="upload">
                        <input type="submit" name="Submit" value="Upload">
                        <input type="reset" name="Reset" value="Cancel">
                    </td>
                </tr>
            </table>
        </form>  
        <%="<br>User: " + name + "<br>Session: " + timestamp%>
    </body>
</html>
