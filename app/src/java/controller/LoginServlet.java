package controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.io.IOException;
import model.UserDAO;
import model.User;

public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String timestamp = request.getParameter("timestamp");        
        HttpSession session = request.getSession();          

        try {
            if (username.equals("admin") && password.equals("Password!SE888")) { //admin
                session.setAttribute("admin", username);
                session.setAttribute("timestamp", timestamp);
                response.sendRedirect("adminPage.jsp"); //changes url
                return;
            }                        
            if(UserDAO.validateUsername(username)){ //if username is valid e.g. john.doe.2016
                User user = UserDAO.retrieveUserByName(username, password);
            
                if (user instanceof User){ //if user in database
                    session.setAttribute("user", user);
                    session.setAttribute("timestamp", timestamp);
                    response.sendRedirect("userPage.jsp"); //changes url
                    return;
                } 
            }
            session.setAttribute("error", "Invalid Login."); //send error messsage to index.jsp           
            response.sendRedirect("index.jsp"); //changes url                   
        } catch (SQLException e){
            session.setAttribute("error", "Server is currently unavailable, please try again later. Thank you."); //send error messsage to index.jsp     
            response.sendRedirect("index.jsp"); //changes url 
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "This is a Login Servlet that processes user login";
    }// </editor-fold>

}