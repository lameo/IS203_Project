package controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ReportDAO;

public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reportType = request.getParameter("reportType");
        RequestDispatcher view = null;

        //to retrieve session's user
        HttpSession session = request.getSession();

        switch (reportType) {
            case "basicReport":
                String endtimeDate = request.getParameter("endtimeDate");
                String[] order = request.getParameterValues("order");

                String breakdownReport = ReportDAO.notVeryBasicBreakdown(order, endtimeDate);
                request.setAttribute("breakdownReport", breakdownReport);
                view = request.getRequestDispatcher("basicReport.jsp");  //send back with same URL
                view.forward(request, response);
                break;
            case "topKPopular":
                String timeDate = request.getParameter("timeDate");

                Map<Integer, String> topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate);
                request.setAttribute("topKPopular", topKPopular);
                view = request.getRequestDispatcher("topKPopularPlaces.jsp");  //send back with same URL
                view.forward(request, response);
                break;
            case "topKCompanions":
                timeDate = request.getParameter("timeDate");

                Map<Integer, String> topKCompanions = ReportDAO.retrieveTopKCompanions(timeDate);
                request.setAttribute("topKCompanions", topKCompanions);
                view = request.getRequestDispatcher("topKCompanions.jsp");  //send back with same URL
                view.forward(request, response);
                break;
            case "topKNextPlaces":
                timeDate = request.getParameter("timeDate");

                Map<Integer, String> topKNextPlaces = ReportDAO.retrieveTopKNextPlaces(timeDate);
                request.setAttribute("topKNextPlaces", topKNextPlaces);
                view = request.getRequestDispatcher("topKNextPlaces.jsp");  //send back with same URL
                view.forward(request, response);
                break;                
            default:
                break;
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
