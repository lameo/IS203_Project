package controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import model.ReportDAO;
import java.util.List;
import java.util.Map;

/**
 * A servlet that manages inputs from basicReport, topKPopularPlaces,
 * topKCompanions, topKNextPlaces and results from ReportDAO. Contains
 * processRequest, doPost, doGet, getServletInfo methods
 */
public class ReportServlet extends HttpServlet {

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
            // Retrieving user timedate input & type of report to generate from user input
            HttpSession session = request.getSession();
            String reportType = request.getParameter("reportType");
            String timeDate = request.getParameter("timeDate");

            // Standardizing timedate string
            timeDate = timeDate.replace("T", " ");
            if (timeDate.length() != 19) {
                timeDate += ":00";
            }

            switch (reportType) {
                case "basicReport":
                    // retrieve order from user input
                    String[] order = request.getParameterValues("order");

                    // Retrieve completed HTML table from reportDAO after getting data from SQL
                    String breakdownReport = ReportDAO.notVeryBasicBreakdown(order, timeDate);

                    // Saving the input order of the user so that it can be transferred over through session
                    List<String> orderList = Arrays.asList(order);

                    // setting attributes to use to display results at basicReport.jsp
                    session.setAttribute("breakdownReport", breakdownReport);
                    session.setAttribute("orderList", orderList);
                    session.setAttribute("timeDate", timeDate);
                    response.sendRedirect("basicReport.jsp");
                    break;

                case "topKPopular":
                    // Retrieve all location sorted by qty of user present 15mins before the timedate
                    Map<Integer, String> topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate);

                    // Saving the topk of the user so that it can be transferred over through session
                    int topK = Integer.parseInt(request.getParameter("topK"));
                    session.setAttribute("topK", topK);

                    // setting attributes to use to display results at topKPopularPlaces.jsp
                    session.setAttribute("topKPopular", topKPopular);
                    session.setAttribute("timeDate", timeDate);
                    response.sendRedirect("topKPopularPlaces.jsp");  //send back to topKPopularPlaces
                    break;

                case "topKCompanions":
                    // Retrieve macaddress from user input
                    String macaddress = request.getParameter("macAddress");

                    // Generating list of all companisons to the specified macAddress
                    Map<Double, ArrayList<String>> topKCompanions = ReportDAO.retrieveTopKCompanions(timeDate, macaddress);

                    // Saving the topk of the user so that it can be transferred over through session
                    topK = Integer.parseInt(request.getParameter("topK"));
                    session.setAttribute("topK", topK);

                    // setting attributes to use to display results at topKPopularPlaces.jsp
                    session.setAttribute("topKCompanions", topKCompanions);
                    session.setAttribute("macaddress", macaddress);
                    session.setAttribute("timeDate", timeDate);

                    // Send back to topKCompanions
                    response.sendRedirect("topKCompanions.jsp");
                    break;

                case "topKNextPlaces":
                    // Retrieve location name from user. Eg: SMUSISB1NearCSRAndTowardsMRT
                    String locationname = request.getParameter("locationname");

                    // Retrieve which number(represents the k) user selected
                    // Saving the topk of the user so that it can be transferred over through session
                    topK = Integer.parseInt(request.getParameter("topK"));
                    session.setAttribute("topK", topK);

                    // Generating list of all possible next places to the specified location
                    Map<Integer, ArrayList<String>> topKNextPlaces = ReportDAO.retrieveTopKNextPlaces(timeDate, locationname);

                    // To get the total number of users in the semantic place being queried later
                    ArrayList<String> usersList = ReportDAO.retrieveUserBasedOnLocation(timeDate, locationname);
                    int total = usersList.size();

                    // Setting attributes to use to display results at topKNextPlaces.jsp
                    session.setAttribute("topKNextPlaces", topKNextPlaces);
                    session.setAttribute("timeDate", timeDate);
                    session.setAttribute("locationname", locationname);
                    session.setAttribute("total", total);

                    // Send back to topKNextPlaces
                    response.sendRedirect("topKNextPlaces.jsp");
                    break;

                // if report type doesnt matches any of the report type
                // not possible to get here unintentionally
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        return "This is a Report Servlet that processes basic location report";
    }// </editor-fold>

}
