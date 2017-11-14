package json;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import model.SharedSecretManager;
import com.google.gson.JsonArray;
import java.util.Collections;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import model.HeatMapDAO;
import java.util.List;
import model.HeatMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/json/heatmap"})
public class heatmap extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        //creates a new gson object by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //creats a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();

        JsonArray errMsg = new JsonArray();

        String tokenEntered = request.getParameter("token"); //get username from request
        String stringFloor = request.getParameter("floor"); //get password from request    
        String timeDate = request.getParameter("date"); //get date from request   

        if (tokenEntered == null) {
            errMsg.add("missing token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (tokenEntered.isEmpty()) {
            errMsg.add("blank token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }
        
        //check if token is invalid
        if (!SharedSecretManager.verifyUser(tokenEntered)) { //if the user is not verified
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //if floor is not entered in url
        if (stringFloor == null) {
            errMsg.add("missing floor");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (stringFloor.isEmpty()) {
            errMsg.add("blank floor");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }
        
        //if date is not entered in url
        if (timeDate == null) {
            errMsg.add("missing date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (timeDate.isEmpty()) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        try {
            //check for valid date entered by user
            boolean dateValid = true;
            // Length check
            dateValid = dateValid && timeDate.length() == 19;
            // Year bigger than 2013 & smaller or equal to 2017
            dateValid = dateValid && (Integer.parseInt(timeDate.substring(0, 4)) > 2013) && (Integer.parseInt(timeDate.substring(0, 4)) <= 2017);
            // Check for dashes
            dateValid = dateValid && (timeDate.substring(4, 5).equals("-"));
            // Month bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(timeDate.substring(5, 7)) > 0) && (Integer.parseInt(timeDate.substring(5, 7)) <= 12);
            // Check for dashes
            dateValid = dateValid && (timeDate.substring(7, 8).equals("-"));
            // Day bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(timeDate.substring(8, 10)) > 0) && (Integer.parseInt(timeDate.substring(8, 10)) <= 31);
            // Check for T
            dateValid = dateValid && (timeDate.substring(10, 11).equals("T"));
            // Hour bigger or equal 0 & smaller or equal to 24
            dateValid = dateValid && (Integer.parseInt(timeDate.substring(11, 13)) >= 0) && (Integer.parseInt(timeDate.substring(11, 13)) <= 23);
            // Check for :
            dateValid = dateValid && (timeDate.substring(13, 14).equals(":"));
            // Min bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(timeDate.substring(14, 16)) >= 0) && (Integer.parseInt(timeDate.substring(14, 16)) <= 59);
            // Check for :
            dateValid = dateValid && (timeDate.substring(16, 17).equals(":"));
            // Second bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(timeDate.substring(17, 19)) >= 0) && (Integer.parseInt(timeDate.substring(17, 19)) <= 59);
            if (!dateValid) {
                errMsg.add("invalid date");
            }
        } catch (NumberFormatException e) {
            errMsg.add("invalid date");
        }

        int floor = Integer.parseInt(stringFloor);
        if (floor < 0 || floor > 5) {
            errMsg.add("invalid floor");
        }

        if (errMsg.size() == 0) {
            //from here on no error messages are recorded
            //all parameters are valid and checked
            
            timeDate = timeDate.replaceAll("T", " ");

            String floorName = "B1";

            if (floor > 0) {
                floorName = "L" + floor;
            }

            Map<String, HeatMap> heatmapList = HeatMapDAO.retrieveHeatMap(timeDate, floorName);
            jsonOutput.addProperty("status", "success");
            JsonArray heatmaps = new JsonArray();

            List<String> keys = new ArrayList<>(heatmapList.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                
                //retrieve HeatMap object from list of sorted from heatmapList.keyset()
                HeatMap heatmap = heatmapList.get(key);
                
                //create json object to add into json array for final output
                //every key will be stored into a new json object
                JsonObject heatmapObject = new JsonObject();
                
                //adding items to output
                heatmapObject.addProperty("semantic-place", heatmap.getPlace());
                heatmapObject.addProperty("num-people", heatmap.getQtyPax());
                heatmapObject.addProperty("crowd-density", heatmap.getHeatLevel());
                
                //add json object into json array for final output
                heatmaps.add(heatmapObject);

            }
            
            //add json array to final json object to output
            jsonOutput.add("heatmap", heatmaps);
        } else {
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
        }
        out.println(gson.toJson(jsonOutput));

        out.close(); //close PrintWriter
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
