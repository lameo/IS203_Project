package controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import model.ReportDAO;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            
            String reportType = request.getParameter("reportType"); //to retrieve which basic location report the user selected
            String timeDate = request.getParameter("timeDate"); //retrieve time from user input
            
            timeDate = timeDate.replace("T", " ");
            SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Date timestamp = (Date) readFormat.parse(timeDate);
            timeDate = writeFormat.format(timestamp);

            switch (reportType) {
                case "basicReport":

                    String[] order = request.getParameterValues("order"); //retrieve order from user input

                    String breakdownReport = ReportDAO.notVeryBasicBreakdown(order, timeDate); //retrieve HTML table from reportDAO after getting data from SQL
                    List<String> orderList = Arrays.asList(order); //changing array into list object so that it can be transferred over through session

                    //setting attributes to use to display results at basicReport.jsp
                    session.setAttribute("breakdownReport", breakdownReport);
                    session.setAttribute("orderList", orderList);
                    session.setAttribute("timeDate", timeDate);
                    response.sendRedirect("basicReport.jsp");  //send back to basicReport
                    break;
                case "topKPopular":
                    int topK = Integer.parseInt(request.getParameter("topK"));

                    Map<Integer, String> topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate);

                    //setting attributes to use to display results at topKPopularPlaces.jsp
                    session.setAttribute("topKPopular", topKPopular);
                    session.setAttribute("timeDate", timeDate);
                    session.setAttribute("topK", topK);
                    response.sendRedirect("topKPopularPlaces.jsp");  //send back to topKPopularPlaces
                    break;
                case "topKCompanions":
                    String macaddress = request.getParameter("macAddress"); //retrieve macaddress from user input
                    topK = Integer.parseInt(request.getParameter("topK")); //retrieve topk number from user input

                    Map<Double, ArrayList<String>> topKCompanions = ReportDAO.retrieveTopKCompanions(timeDate, macaddress);

                    session.setAttribute("macaddress", macaddress);
                    session.setAttribute("topK", topK);
                    session.setAttribute("timeDate", timeDate);
                    session.setAttribute("topKCompanions", topKCompanions);

                    response.sendRedirect("topKCompanions.jsp");  //send back to topKCompanions
                    break;
                case "topKNextPlaces":
                    String locationname = request.getParameter("locationname"); //retrieve location name from user. Eg: SMUSISB1NearCSRAndTowardsMRT
                    topK = Integer.parseInt(request.getParameter("topK")); //retrieve which number(represents the k) user selected

                    Map<Integer, ArrayList<String>> topKNextPlaces = ReportDAO.retrieveTopKNextPlaces(timeDate, locationname);
                    ArrayList<String> usersList = ReportDAO.retrieveUserBasedOnLocation(timeDate, locationname);

                    //setting attributes to use to display results at topKNextPlaces.jsp
                    session.setAttribute("topKNextPlaces", topKNextPlaces);
                    session.setAttribute("timeDate", timeDate);
                    session.setAttribute("topK", topK);
                    session.setAttribute("total", usersList.size());
                    session.setAttribute("locationname", locationname);
                    
                    response.sendRedirect("topKNextPlaces.jsp");  //send back to topKNextPlaces
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
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
