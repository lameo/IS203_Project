package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HeatMap;
import model.HeatMapDAO;
import org.json.JSONException;
import org.json.JSONObject;

public class HeatMapServlet extends HttpServlet {

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
        HttpSession session = request.getSession();         

        String endtimeDate = request.getParameter("endtimeDate"); //retrieve time from user input
        int floor = Integer.parseInt(request.getParameter("floor")); //retrieve floor from user input
        String floorName = "B1";

        if(floor>0){
            floorName = "L" + floor;
        }

        HashMap<String, HeatMap> heatmapList = HeatMapDAO.retrieveHeatMap(endtimeDate, floorName);

        session.setAttribute("floorName", floorName);
        session.setAttribute("endtimeDate", endtimeDate);
        session.setAttribute("heatmapList", heatmapList);

        response.sendRedirect("heatmapPage.jsp"); //send back to heatmapPage
        
    }

    protected void processRequest2(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();         
        String heatmap = request.getParameter("heatmap");
        if(heatmap!=null && heatmap.length()>0){
            String filename = (String)session.getAttribute("floorName");            
            try(
                FileWriter fileWriter = new FileWriter("D:\\testt\\app\\web\\resource\\" + filename + ".json");
            ){        
                JSONObject obj = new JSONObject(); 
                JSONObject obj2 = new JSONObject(heatmap); 
                obj.put("heatmap", obj2);
                fileWriter.write(obj.toString());
                fileWriter.close();
                response.setContentType("text/plain");
                response.getWriter().write("true");
            } catch (JSONException e){
                e.printStackTrace();
            }
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
            processRequest2(request, response);
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
