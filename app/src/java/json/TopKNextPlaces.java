package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ReportDAO;
import model.SharedSecretManager;

/**
 * A servlet that manages inputs from url and results from ReportDAO. Contains
 * processRequest, doPost, doGet, getServletInfo methods
 */
@WebServlet(urlPatterns = {"/json/top-k-next-places"})
public class TopKNextPlaces extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        //creates a new gson object by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //creats a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();

        //create a json array to store errors
        JsonArray errMsg = new JsonArray();


        //get token from request
        String tokenEntered = request.getParameter("token");
        // check if token is null (dont have ?token=something)
        if (tokenEntered == null) {
            errMsg.add("missing token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // check if token is empty (?token="")
        if (tokenEntered.isEmpty()) {
            errMsg.add("blank token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // checking if the token submitted by the user is valid
        if (!SharedSecretManager.verifyUser(tokenEntered)) {
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //get date from request
        String dateEntered = request.getParameter("date");
        // check if date is null (dont have ?date=something)
        if (dateEntered == null) {
            errMsg.add("missing date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // check if date is empty (?date="")
        if (dateEntered.isEmpty()) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //get semanticPlace from request
        String semanticPlace = request.getParameter("origin");
        // check if date is null (dont have ?origin=something)
        if (semanticPlace == null) {
            errMsg.add("missing origin");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // check if token is empty (?origin="")
        if (semanticPlace.isEmpty()) {
            errMsg.add("blank origin");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }
        
        // After this point, all variables required are not empty or null, so start checking whether they are valid format
        // topK could be empty, meaning default = 3 unless otherwise stated
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
        
        //trying to retrieve topK from request, nukl if not entered
        String topKEntered = request.getParameter("k");
        //Check if user entered a top k number
        if (topKEntered == null || topKEntered.isEmpty()) {
            topKEntered = "3";
        }

        //assign default number to topK first before try-catch
        int topK = 3;

        try {
            // get the number user entered in url as int
            topK = Integer.parseInt(topKEntered); 

            // if topK is out of bound, then add error message to JsonArray
            if (topK < 1 || topK > 10) {
                errMsg.add("invalid k");
            }
        //if a string is entered where topK is supposed to be, add error msg into JsonArray
        } catch (NumberFormatException e) {
            errMsg.add("invalid k");
        }

        //check if there is record of semantic place from location-lookup table
        ArrayList<String> validSemanticPlacesList = ReportDAO.getSemanticPlaces();
        //if semanticPlace is not inside the locationloopup table
        if (!validSemanticPlacesList.contains(semanticPlace)) { 
            errMsg.add("invalid origin");
        }

        // After this point, all variables required are not empty, null or wrong format, so start generating report
        if (errMsg.size() == 0) {
            //proper date format -> (YYYY-MM-DDTHH:MM:SS)
            //replace "T" with "" to allow system to process correctly
            dateEntered = dateEntered.replaceAll("T", " ");

            //create a json array to store errors
            JsonArray resultsArr = new JsonArray();

            // total quantity of users visiting next place
            int usersVisitingNextPlace = 0;
            //Generate list of next places users are likely to visit with the qty of user there
            Map<Integer, ArrayList<String>> topKNextPlaces = ReportDAO.retrieveTopKNextPlaces(dateEntered, semanticPlace);
            //retrieve users who are in a specific place given a specific time frame in a specific location
            ArrayList<String> usersList = ReportDAO.retrieveUserBasedOnLocation(dateEntered, semanticPlace);
            //to get the different total number of users in a next place in desc order
            Set<Integer> totalNumOfUsersSet = topKNextPlaces.keySet();

            //to match topK number
            int counter = 1;
            for (int totalNumOfUsers : totalNumOfUsersSet) {
                // gives the list of location with the same totalNumOfUsers
                ArrayList<String> locations = topKNextPlaces.get(totalNumOfUsers);

                // sort the locations list in ascending order first
                Collections.sort(locations);
                // repeat as many time as topK size
                if (counter <= topK) {
                    for (int i = 0; i < locations.size(); i++) {
                        //temp json object to store required output first before adding to resultsArr for final output
                        JsonObject topKNextPlace = new JsonObject();
                        topKNextPlace.addProperty("rank", counter);
                        topKNextPlace.addProperty("semantic-place", locations.get(i));
                        topKNextPlace.addProperty("count", totalNumOfUsers);

                        // add temp json object to final json array for output
                        resultsArr.add(topKNextPlace);
                    }
                    counter++;
                }
                //add if the user is going other places but the quantity may have multiple next locations
                usersVisitingNextPlace += totalNumOfUsers * locations.size();
            }
            jsonOutput.addProperty("status", "success");
            jsonOutput.addProperty("total-users", usersList.size());
            jsonOutput.addProperty("total-next-place-users", usersVisitingNextPlace);
            jsonOutput.add("results", resultsArr);
        } else {
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
        }
        
        
        // Returning the json output we created in a pretty print format
        out.println(gson.toJson(jsonOutput));
        // close PrintWriter
        out.close(); 
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
