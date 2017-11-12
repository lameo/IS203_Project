package controller;

import static model.AutoGroupDAO.retrieveAutoGroups;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import model.AutoGroupDAO;
import model.Group;

public class AutoGroupServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {

        try {
            HttpSession session = request.getSession();
            String timeDate = request.getParameter("timeDate"); //retrieve time from user input
            timeDate = timeDate.replace("T", " ");

            if (timeDate.length() != 19) {
                timeDate += ":00";
            }

            int numberOfUsersInBuilding = AutoGroupDAO.retrieveUsersNumber(timeDate);//retrieve the number of users in the entire SIS building for that date and time

            //retrieve map of all the users and their location traces whom stay at SIS building in specified time window for at least 12 mins
            Map<String, Map<String, ArrayList<String>>> AutoUsers = AutoGroupDAO.retrieveAutoUsers(timeDate);
/*
            ArrayList<Group> AutoGroups = new ArrayList<Group>();

            //check if there are valid auto users
            if (AutoUsers != null && AutoUsers.size() > 0) {
                //retrieve groups formed from valid auto users
                AutoGroups = retrieveAutoGroups(AutoUsers);
            }

            if (AutoGroups != null && AutoGroups.size() > 0) {
                //check autogroups and remove sub groups
                AutoGroups = AutoGroupDAO.CheckAutoGroups(AutoGroups);
            }*/
            session.setAttribute("numberOfUsersInBuilding", numberOfUsersInBuilding);
            //session.setAttribute("AutoGroups", AutoGroups);
            session.setAttribute("timeDate", timeDate);

            response.sendRedirect("automaticGroupDetection.jsp");
        } catch (IOException ex) {
            Logger.getLogger(AutoGroupServlet.class.getName()).log(Level.SEVERE, null, ex);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
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
