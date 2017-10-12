package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        HttpSession session = request.getSession(); 
        
        switch (reportType) {
            case "basicReport":
                String endtimeDate = request.getParameter("endtimeDate"); //retrieve time from user input
                String[] order = request.getParameterValues("order"); //retrieve order from user input
                
                String breakdownReport = ReportDAO.notVeryBasicBreakdown(order, endtimeDate); //retrieve HTML table from reportDAO after getting data from SQL
                List<String> orderList = Arrays.asList(order); //changing array into list object so that it can be transferred over through session             
                
                session.setAttribute("breakdownReport", breakdownReport);
                session.setAttribute("orderList", orderList);                
                response.sendRedirect("basicReport.jsp");  //send back to basicReport
                break;
            case "topKPopular":
                String timeDate = request.getParameter("timeDate"); //retrieve time from user input
                int topK = Integer.parseInt(request.getParameter("topK"));
                
                Map<Integer, String> topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate);
                session.setAttribute("topKPopular", topKPopular);
                session.setAttribute("timeDate", timeDate);
                session.setAttribute("topK", topK);                
                response.sendRedirect("topKPopularPlaces.jsp");  //send back to topKPopularPlaces
                break;
            case "topKCompanions":
                String macaddress = request.getParameter("macAddress");
                timeDate = request.getParameter("timeDate");
                topK = Integer.parseInt(request.getParameter("topK"));
                
                Map<ArrayList<String>, ArrayList<Integer>> topKCompanions = ReportDAO.retrieveTopKCompanions(timeDate,macaddress, topK);
                //Map<ArrayList<String>, ArrayList<Integer>> topKCompanions = null;
                session.setAttribute("macaddress", macaddress);
                session.setAttribute("topK", topK); 
                session.setAttribute("timeDate", timeDate);
                session.setAttribute("topKCompanions", topKCompanions);
                response.sendRedirect("topKCompanions.jsp");  //send back to topKCompanions
                break;
            case "topKNextPlaces":
                timeDate = request.getParameter("timeDate");
                String locationname = request.getParameter("locationname");
                topK = Integer.parseInt(request.getParameter("topK"));
                
                Map<Integer, ArrayList<String>> topKNextPlaces = ReportDAO.retrieveTopKNextPlaces(timeDate, locationname);
                session.setAttribute("topKNextPlaces", topKNextPlaces);
                session.setAttribute("timeDate", timeDate);                
                session.setAttribute("topK", topK);                
                response.sendRedirect("topKNextPlaces.jsp");  //send back to topKNextPlaces
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
