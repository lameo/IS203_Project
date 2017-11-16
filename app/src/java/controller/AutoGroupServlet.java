package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.AutoGroupDAO;
import model.Group;

/**
 * A servlet that manages inputs from automaticGroupDetection and results from
 * AutoGroupDAO. Contains processRequest, doPost, doGet, getServletInfo methods
 */
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Retrieving user timedate input from session
            HttpSession session = request.getSession();
            String timeDate = request.getParameter("timeDate");

            // Standardizing timedate string
            timeDate = timeDate.replace("T", " ");
            if (timeDate.length() != 19) {
                timeDate += ":00";
            }

            // Retrieve the number of users in the entire SIS building for that date and time
            int numberOfUsersInBuilding = AutoGroupDAO.retrieveUsersNumber(timeDate);

            // Retrieve map of all the users and their location traces whom stay at SIS building in specified time window for at least 12 mins
            Map<String, Map<String, ArrayList<String>>> listOfUsersWith12MinutesData = AutoGroupDAO.retrieveUsersWith12MinutesData(timeDate);

            ArrayList<Group> autoGroupsDetected = new ArrayList<Group>();

            // Check if there are users who stay in sis for more than 15mins & the list is not size 0
            if (!listOfUsersWith12MinutesData.isEmpty()) {
                // Retrieve groups formed from valid auto users
                autoGroupsDetected = AutoGroupDAO.retrieveAutoGroups(listOfUsersWith12MinutesData);
            }

            // if the group form is not null and list of group detected is more than 0
            if (!autoGroupsDetected.isEmpty()) {
                // Remove sub groups
                autoGroupsDetected = AutoGroupDAO.checkAutoGroups(autoGroupsDetected);
            }

            // Saving result to session and returning back to jsp page
            session.setAttribute("numberOfUsersInBuilding", numberOfUsersInBuilding);
            session.setAttribute("autoGroupsDetected", autoGroupsDetected);
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
        return "This is a Auto Group Report Servlet that processes auto group report";
    }// </editor-fold>

}
