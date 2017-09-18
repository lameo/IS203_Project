package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ReportDAO;
import model.User;

public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reportType = request.getParameter("reportType");
        String timeDate = request.getParameter("timeDate");
        String topK = request.getParameter("topK");
        RequestDispatcher view = null;
        
        //to retrieve session's user
        HttpSession session = request.getSession();
        request.setAttribute("timeDate", timeDate);

        switch (reportType) {
            case "breakdownReport":
                String breakdownReport = ReportDAO.retrieveQtyByYearAndGender(timeDate);
                request.setAttribute("breakdownReport", breakdownReport);
                break;
            case "topKPopular":
                String topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate, topK);
                request.setAttribute("topKPopular", topKPopular);
                request.setAttribute("topK", topK);
                break;
            default:
                break;
        }
        view = request.getRequestDispatcher("userPage.jsp");
        view.forward(request, response);        
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

