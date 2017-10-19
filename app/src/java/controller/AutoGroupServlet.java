/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static model.AutoGroupDAO.retrieveAutoGroups;
import model.Group;

/**
 *
 * @author xuying
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();

            String timeDate = request.getParameter("timeDate"); //retrieve time from user input
            SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            Date timestamp = null;
            try {
                timestamp = (Date) readFormat.parse(timeDate);
                timeDate = writeFormat.format(timestamp);
                //System.out.println("Retrieved and formatted dateTime: " + timestamp.toString());
            } catch (ParseException e) {
                System.out.println("Date formatter failed to parse chosen sendTime.");
                e.printStackTrace();
            }

            //retrieve group of users whom stay at SIS in processing window
            ArrayList<Group> AutoGroups = retrieveAutoGroups(timeDate);

            session.setAttribute("timeDate", timeDate);
            session.setAttribute("AutoGroups", AutoGroups);

            response.sendRedirect("automaticGroupDetection.jsp");
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
