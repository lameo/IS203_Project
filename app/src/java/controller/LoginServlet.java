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

/**
 * A servlet that manages inputs from index and results from UserDAO. Contains
 * processRequest, doPost, doGet, getServletInfo methods
 */
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Getting user input for username & password
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();

        try {
            // if trying to login as admin & password + username matches
            if (username.equals("admin") && password.equals("Password!SE888")) {
                session.setAttribute("admin", username);
                response.sendRedirect("adminPage.jsp");
                return;
            }

            // else if trying to login as user
            // true if username is an entry in the database
            if (UserDAO.validateUsername(username)) {
                // validate whether password for that particular username is correct and return a user object if true
                User user = UserDAO.retrieveUserByName(username, password);

                // if user is in database
                if (user instanceof User) {
                    session.setAttribute("user", user);
                    response.sendRedirect("userPage.jsp");
                    return;
                }
            }

            // if not admin or valid user
            // send an error message back to index.jsp
            session.setAttribute("error", "Invalid Login.");
            response.sendRedirect("index.jsp"); //changes url
        } catch (SQLException e) {
            // if can't establish connection to database
            // send error messsage to index.jsp
            session.setAttribute("error", "Server is currently unavailable, please try again later. Thank you.");
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
