package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import model.UserDAO;

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");   
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String timestamp = request.getParameter("timestamp");        
        
        String error = null;
        HttpSession session = request.getSession();  
        try (PrintWriter out = response.getWriter()) {
            User user = UserDAO.retrieveUserByName(email,password, timestamp);
            
            if (user instanceof User){
                session.setAttribute("user",user);
                session.setAttribute("timestamp",timestamp);
                response.sendRedirect("userPage.jsp");
            } else if (email.equals("admin") && password.equals("password")) {  
                session.setAttribute("admin",email);
                session.setAttribute("timestamp",timestamp);
                response.sendRedirect("adminPage.jsp");
            }
            else {
                session.setAttribute("error", "Invalid Login"); //send error messsage to index.jsp           
                response.sendRedirect("index.jsp");                   
            }
            /**
            while(resultSet.next()){
                User user = new User(username, password);
                user.setTimestamp(timestamp);
                session.setAttribute("user", user); //send user object to userPage.jsp
                response.sendRedirect("userPage.jsp");         
                return;
            }
            **/
        } catch (IOException e){
            session.setAttribute("error", "Server Down. Please Try Again Later. Thank You"); //send error messsage to index.jsp     
            response.sendRedirect("index.jsp");  
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
        return "Short description";
    }// </editor-fold>

}