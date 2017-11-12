package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ReportDAO;
import model.SharedSecretManager;

@WebServlet(urlPatterns = {"/json/top-k-popular-places"})
public class topKPopularPlace extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        //creates a new gson object
        //by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //creats a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();

        //create a json array to store errors
        JsonArray errMsg = new JsonArray();

        String tokenEntered = request.getParameter("token"); //get token from url
        String topKEntered = request.getParameter("k"); //get topK from url
        String dateEntered = request.getParameter("date"); //get date from url

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

        //print out all the error with null or empty string that is required but the user did not enter 
        if (!SharedSecretManager.verifyUser(tokenEntered)) { //verify the user - if the user is not verified
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (dateEntered == null) { //if the dateEntered not the right format
            errMsg.add("missing date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (dateEntered.isEmpty()) { //if the dateEntered not the right format
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
            dateValid = dateValid && dateEntered.length() == 19;
            // Year bigger than 2013 & smaller or equal to 2017
            dateValid = dateValid && (Integer.parseInt(dateEntered.substring(0, 4)) > 2013) && (Integer.parseInt(dateEntered.substring(0, 4)) <= 2017);
            // Check for dashes
            dateValid = dateValid && (dateEntered.substring(4, 5).equals("-"));
            // Month bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(dateEntered.substring(5, 7)) > 0) && (Integer.parseInt(dateEntered.substring(5, 7)) <= 12);
            // Check for dashes
            dateValid = dateValid && (dateEntered.substring(7, 8).equals("-"));
            // Day bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(dateEntered.substring(8, 10)) > 0) && (Integer.parseInt(dateEntered.substring(8, 10)) <= 31);
            // Check for T
            dateValid = dateValid && (dateEntered.substring(10, 11).equals("T"));
            // Hour bigger or equal 0 & smaller or equal to 24
            dateValid = dateValid && (Integer.parseInt(dateEntered.substring(11, 13)) >= 0) && (Integer.parseInt(dateEntered.substring(11, 13)) <= 23);
            // Check for :
            dateValid = dateValid && (dateEntered.substring(13, 14).equals(":"));
            // Min bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(dateEntered.substring(14, 16)) >= 0) && (Integer.parseInt(dateEntered.substring(14, 16)) <= 59);
            // Check for :
            dateValid = dateValid && (dateEntered.substring(16, 17).equals(":"));
            // Second bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(dateEntered.substring(17, 19)) >= 0) && (Integer.parseInt(dateEntered.substring(17, 19)) <= 59);
            if (!dateValid) {
                errMsg.add("invalid date");
            }
        } catch (NumberFormatException e) {
            errMsg.add("invalid date");
        }

        //Check if user entered a top k number
        if (topKEntered == null || topKEntered.equals("")) {
            topKEntered = "3";
        }

        //assign default number to topK first before try-catch
        int topK = 3;

        //Check if user entered in a number as a string instead of spelling it out as a whole
        //Eg: k=1 is correct but k=one is wrong
        try {
            topK = Integer.parseInt(topKEntered); //get the number user entered in url in int

            if (topK < 1 || topK > 10) {
                errMsg.add("invalid k"); //add error msg into JsonArray
            }
        } catch (NumberFormatException e) {
            errMsg.add("invalid k"); //add error msg into JsonArray
        }

        //only run with valid k
        if (errMsg.size() == 0) {
            //at this point, dateEntered is valid and is in the right format
            dateEntered = dateEntered.replaceAll("T", " ");

            Map<Integer, String> topKPopularMap = ReportDAO.retrieveTopKPopularPlaces(dateEntered);

            //create a json array to store errors
            JsonArray resultsArr = new JsonArray();
            ArrayList<Integer> keys = new ArrayList<Integer>(topKPopularMap.keySet());

            int count = 1;
            for (int i = 0; i < keys.size(); i++) {
                if (count <= topK) {
                    //retrieve all semantic places found from map
                    String allLocationFound = topKPopularMap.get(keys.get(i));
                    
                    //get all locations in String[] to for-loop
                    String[] allLocationFoundArr = allLocationFound.split(", ");
                    
                    for (String location : allLocationFoundArr) {
                        //temp json object to store required output first before adding to resultsArr for final output
                        JsonObject topKPopPlaces = new JsonObject();
                        topKPopPlaces.addProperty("rank", count);

                        //add every location to semantic-places for each rank if rank has 2 or more locations
                        //Eg: if rank 1 has 2 locations, 2 jsonobjects will be created for each location and added to resultsArr jsonarray respectively
                        topKPopPlaces.addProperty("semantic-place", location);
                        topKPopPlaces.addProperty("count", keys.get(i));
                        
                        // add temp json object to final json array for output
                        resultsArr.add(topKPopPlaces);
                    }
                }
                count++;
            }
            jsonOutput.addProperty("status", "success");
            jsonOutput.add("results", resultsArr);
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
