<%@page import="user.User"%>
<%@page import="java.sql.*"%>
<%            
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    String userName = request.getParameter("userName");
    String password = request.getParameter("password");
    //userName = validate(userName);
    //password = validate(password);
    if(userName==null || password==null) {
        request.getRequestDispatcher("index.jsp").forward(request, response);           
    } 
    //username and password checking, change to database when implemented
    try{
        //get a connection to database
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "");            

        //prepare a statement
        preparedStatement = connection.prepareStatement("select * from demographics where name = ? && password = ?");   

        //set the parameters
        preparedStatement.setString(1, userName);
        preparedStatement.setString(2, password);

        //execute SQL query
        resultSet = preparedStatement.executeQuery();

        while(resultSet.next()){
            request.getRequestDispatcher("userPage.jsp").forward(request, response);  
            return;
        }

        if(userName!=null){
            User user = new User(userName, password);
            String userType = user.validate1(userName,password);
            if(userType.equals("admin")){
                request.getRequestDispatcher("adminPage.jsp").forward(request, response);
            }
        } 
    } catch (SQLException e){
        out.println("Server Down. Please Try Again Later. Thank You");
    }
%>
