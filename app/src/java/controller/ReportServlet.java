package controller;

import java.io.IOException;
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
                String starttimeDate = request.getParameter("starttimeDate");
                String endtimeDate = request.getParameter("endtimeDate");
                String[] order = request.getParameterValues("order");

                String breakdownReport = ReportDAO.notVeryBasicBreakdown(order, starttimeDate, endtimeDate);
                request.setAttribute("breakdownReport", breakdownReport);
                view = request.getRequestDispatcher("basicReport.jsp");  //send back to userPage but same URL
                view.forward(request, response);
                break;
            case "topKPopular":
                String timeDate = request.getParameter("timeDate");
                String topK = request.getParameter("topK");

                String topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate, topK);
                request.setAttribute("topKPopular", topKPopular);
                request.setAttribute("topK", topK);
                view = request.getRequestDispatcher("topKPopularPlaces.jsp");  //send back to userPage but same URL
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
