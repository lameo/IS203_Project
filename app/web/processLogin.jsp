<%@page import="user.User"%>
<%@page import="java.sql.*"%>
<%            
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    
    if((request.getParameter("username"))==null || (request.getParameter("password"))==null){
        session.setAttribute("error", "Invalid Login");
        response.sendRedirect("index.jsp");   
        return;
    }
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String timestamp = request.getParameter("timestamp");

    try{
        //get a connection to database
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "");            

        //prepare a statement
        preparedStatement = connection.prepareStatement("select * from demographics where name = ? && password = ?");   

        //set the parameters
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        //execute SQL query
        resultSet = preparedStatement.executeQuery();

        while(resultSet.next()){
            User user = new User(username, password);
            user.setTimestamp(timestamp);
            session.setAttribute("user", user); //send user object to userPage.jsp
            response.sendRedirect("userPage.jsp");         
            return;
        }

        if(username!=null && password!=null){
            User user = new User(username, password);
            user.setTimestamp(timestamp);            
            String userType = user.validate1(username, password);
            if(userType.equals("admin")){
                session.setAttribute("user", user); //send user object to adminPage.jsp
                response.sendRedirect("adminPage.jsp");               
            } else {
                session.setAttribute("error", "Invalid Login"); //send error messsage to index.jsp
                response.sendRedirect("index.jsp");                   
            }
        }
    } catch (SQLException e){
        session.setAttribute("error", "Server Down. Please Try Again Later. Thank You"); //send error messsage to index.jsp
        response.sendRedirect("index.jsp");  
    }
    
%>
