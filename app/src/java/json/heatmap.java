package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HeatMap;
import model.HeatMapDAO;
import model.SharedSecretManager;

@WebServlet(urlPatterns = {"/json/heatmap"})
public class heatmap extends HttpServlet {

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

        PrintWriter out = response.getWriter();

        //creates a new gson object by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //creats a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();

        JsonArray errMsg = new JsonArray();

        String token = request.getParameter("token"); //get username from request
        String stringFloor = request.getParameter("floor"); //get password from request    
        String timeDate = request.getParameter("date"); //get password from request   

        if (token == null || token.isEmpty()) {
            errMsg.add("blank token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }
        
        if (stringFloor == null || stringFloor.isEmpty()) {
            errMsg.add("blank floor");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }
        if (timeDate == null || timeDate.isEmpty()) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        boolean validToken = SharedSecretManager.verifyUser(token);
        if (!validToken) {
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        boolean validDate = true;
        // Length check
        validDate = validDate && timeDate.length() == 19;
        // Year bigger than 2013 & smaller or equal to 2017
        validDate = validDate && (Integer.parseInt(timeDate.substring(0, 4)) > 2013) && (Integer.parseInt(timeDate.substring(0, 4)) <= 2017);
        // Month bigger than 0 & smaller or equal to 12
        validDate = validDate && (Integer.parseInt(timeDate.substring(5, 7)) > 0) && (Integer.parseInt(timeDate.substring(5, 7)) <= 12);
        // Day bigger than 0 & smaller or equal to 12
        validDate = validDate && (Integer.parseInt(timeDate.substring(8, 10)) > 0) && (Integer.parseInt(timeDate.substring(8, 10)) <= 31);
        // Hour bigger or equal 0 & smaller or equal to 24
        validDate = validDate && (Integer.parseInt(timeDate.substring(11, 13)) >= 0) && (Integer.parseInt(timeDate.substring(11, 13)) <= 23);
        // Min bigger or equal 0 & smaller or equal to 59
        validDate = validDate && (Integer.parseInt(timeDate.substring(14, 16)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
        // Second bigger or equal 0 & smaller or equal to 59
        validDate = validDate && (Integer.parseInt(timeDate.substring(17, 19)) >= 0) && (Integer.parseInt(timeDate.substring(17, 19)) <= 59);

        if (!validDate) {
            errMsg.add("invalid date");
        }
        
        int floor = Integer.parseInt(stringFloor);
        if (floor < 0 || floor > 5) {
            errMsg.add("invalid floor");
        }

        if (errMsg.size() == 0) {
            timeDate = timeDate.replaceAll("T", " ");

            String floorName = "B1";

            if (floor > 0) {
                floorName = "L" + floor;
            }

            HashMap<String, HeatMap> heatmapList = HeatMapDAO.retrieveHeatMap(timeDate, floorName);

            jsonOutput.addProperty("status", "success");
            JsonArray heatmaps = new JsonArray();

            Set<String> keys = heatmapList.keySet();
            for (String key : keys) {
                HeatMap heatmap = heatmapList.get(key);
                JsonObject heatmapObject = new JsonObject();

                heatmapObject.addProperty("semantic-place", heatmap.getPlace());
                heatmapObject.addProperty("num-people", heatmap.getQtyPax());
                heatmapObject.addProperty("crowd-density", heatmap.getHeatLevel());

                heatmaps.add(heatmapObject);

            }
            jsonOutput.add("heatmap", heatmaps);

            out.println(gson.toJson(jsonOutput));
            return;
        } else {
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
        }
        out.println(gson.toJson(jsonOutput));
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
