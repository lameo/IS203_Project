package controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HeatMap;
import model.HeatMapDAO;

public class HeatMapServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieving user timedate input from session
        HttpSession session = request.getSession();
        String timeDate = request.getParameter("timeDate");
        //retrieve floor from user input
        String floorName = request.getParameter("floor");


        // Standardizing timedate string
        timeDate = timeDate.replace("T", " ");
        if (timeDate.length() != 19) {
            timeDate += ":00";
        }


        // Run method to retrieve qty, heatlevel of all rooms in the level selected
        Map<String, HeatMap> heatmapList = HeatMapDAO.retrieveHeatMap(timeDate, floorName);


        // Saving result to session and sending back to heatmapPage
        session.setAttribute("heatmapList", heatmapList);
        session.setAttribute("floorName", floorName);
        session.setAttribute("timeDate", timeDate);
        response.sendRedirect("heatmapPage.jsp");
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
        return "This is a Heatmap Servlet to process heatmap";
    }// </editor-fold>

}
