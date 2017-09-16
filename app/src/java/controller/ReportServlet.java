package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ReportDAO;
import model.User;

public class ReportServlet extends HttpServlet {

    public ReportServlet(){}


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reportType = request.getParameter("reportType");
        String timeDate = request.getParameter("timeDate");
        String topK = request.getParameter("topK");

        //to retrieve session's user
        HttpSession sess = request.getSession(false);
        User user = (User)sess.getAttribute("user");

        String error = null;
        HttpSession session = request.getSession();


        try (PrintWriter out = response.getWriter()) {
            session.setAttribute("user",user);
            session.setAttribute("timeDate",timeDate);

            switch (reportType) {
                case "breakdownReport":
                    String breakdownReport = ReportDAO.retrieveQtyByYearAndGender(timeDate);
                    session.setAttribute("breakdownReport",breakdownReport);
                    //to prevent topKPopular Report from showing again
                    session.setAttribute("topKPopular",null);
                    response.sendRedirect("userPage.jsp");
                    break;
                case "topKPopular":
                    String topKPopular = ReportDAO.retrieveTopKPopularPlaces(timeDate, topK);
                    session.setAttribute("topKPopular",topKPopular);
                    session.setAttribute("topK",topK);
                    //to prevent breakdown Report from showing again
                    session.setAttribute("breakdownReport",null);
                    response.sendRedirect("userPage.jsp");
                    break;
                default:
                    response.sendRedirect("index.jsp");
                    break;
            }
        } catch (IOException e){
            session.setAttribute("error", "Server Down. Please Try Again Later. Thank You"); //send error messsage to index.jsp
            response.sendRedirect("index.jsp");
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

