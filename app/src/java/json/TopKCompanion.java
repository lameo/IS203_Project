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
@WebServlet(urlPatterns = {"/json/top-k-companions"})
public class TopKCompanion extends HttpServlet {

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

        String topKEntered = request.getParameter("k"); //get topK from url

        //get token from request
        String token = request.getParameter("token");
        // check if token is null (dont have ?token=something)
        if (token == null) {
            errMsg.add("missing token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // check if token is empty (?token="")
        if (token.isEmpty()) {
            errMsg.add("blank token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // checking if the token submitted by the user is valid
        if (!SharedSecretManager.verifyUser(token)) {
            // if token given is not valid
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //get date from request
        String date = request.getParameter("date");
        // check if date is null (dont have ?date=something)
        if (date == null) {
            errMsg.add("missing date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // check if date is empty (?date="")
        if (date.isEmpty()) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //get macaddress from request
        String macaddress = request.getParameter("mac-address");
        // check if macaddress is null (dont have ?date=something)
        if (macaddress == null) {
            errMsg.add("missing mac address");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // check if macaddress is empty (?date="")
        if (macaddress.isEmpty()) {
            errMsg.add("blank mac address");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        // After this point, all variables required are not empty or null, so start checking whether they are valid format
        try {
            //check for valid date entered by user
            boolean dateValid = true;
            // Length check
            dateValid = dateValid && date.length() == 19;
            // Year bigger than 2013 & smaller or equal to 2017
            dateValid = dateValid && (Integer.parseInt(date.substring(0, 4)) > 2013) && (Integer.parseInt(date.substring(0, 4)) <= 2017);
            // Check for dashes
            dateValid = dateValid && (date.substring(4, 5).equals("-"));
            // Month bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(date.substring(5, 7)) > 0) && (Integer.parseInt(date.substring(5, 7)) <= 12);
            // Check for dashes
            dateValid = dateValid && (date.substring(7, 8).equals("-"));
            // Day bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(date.substring(8, 10)) > 0) && (Integer.parseInt(date.substring(8, 10)) <= 31);
            // Check for T
            dateValid = dateValid && (date.substring(10, 11).equals("T"));
            // Hour bigger or equal 0 & smaller or equal to 24
            dateValid = dateValid && (Integer.parseInt(date.substring(11, 13)) >= 0) && (Integer.parseInt(date.substring(11, 13)) <= 23);
            // Check for :
            dateValid = dateValid && (date.substring(13, 14).equals(":"));
            // Min bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(date.substring(14, 16)) >= 0) && (Integer.parseInt(date.substring(14, 16)) <= 59);
            // Check for :
            dateValid = dateValid && (date.substring(16, 17).equals(":"));
            // Second bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(date.substring(17, 19)) >= 0) && (Integer.parseInt(date.substring(17, 19)) <= 59);
            if (!dateValid) {
                errMsg.add("invalid date");
            }
        } catch (NumberFormatException e) {
            errMsg.add("invalid date");
        }

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
        // if a string is entered where topK is supposed to be, add error msg into JsonArray
        } catch (NumberFormatException e) {
            errMsg.add("invalid k");
        }

        //retrieve all existing macaddresses from location.csv 
        //Not all macaddresses will belong to students so must get from location.csv instead from demographics.csv
        ArrayList<String> allMacaddressList = ReportDAO.getAllMacaddress();
        // check if macaddress is inside location database, else return error
        if (!allMacaddressList.contains(macaddress)) { 
            errMsg.add("invalid mac address");
        }

        //from here on, user is verified
        //topk number is between 1 - 10 inclusive with default as 3 if no k is entered
        //mac-address is valid
        if (errMsg.size() == 0) {
            //proper date format -> (YYYY-MM-DDTHH:MM:SS)
            //replace "T" with "" to allow system to process correctly
            date = date.replaceAll("T", " ");

            //create a json array to store results
            JsonArray resultsArr = new JsonArray();

            //to match topK number
            int count = 1;
            //retrieve all topK companions sorted by the time they spend with the particular user
            Map<Double, ArrayList<String>> topKCompanionMap = ReportDAO.retrieveTopKCompanions(date, macaddress);
            // to get the time spend with particular macaddress so as to iterate down later on
            Set<Double> timeSpentByCompanionsList = topKCompanionMap.keySet();
            for (Double timeSpentByCompanions : timeSpentByCompanionsList) {
                // repeat as many time as topK size, also serves as row number
                if (count <= topK) {

                    //list is required for storing data into json object for final json array output
                    //to add in macaddress and email pair for sorting
                    ArrayList<String> unsortedMacEmailPair = new ArrayList<>();

                    //get the arraylist out from map by using the key
                    //Arraylist contains macaddress and email pair together
                    ArrayList<String> currTimeCompanionList = topKCompanionMap.get(timeSpentByCompanions);

                    //loop through arraylist
                    for (int i = 0; i < currTimeCompanionList.size(); i++) {
                        //get individual string from list
                        String macaddressEmailPair = currTimeCompanionList.get(i);

                        //use string.split(",") mtd to retrive String[] of macaddress and email
                        String[] allMacaddressEmailPairs = macaddressEmailPair.split(",");

                        //add in all macaddress and email retrieved from topKCompanionMap after getting the timeSpentByCompanions
                        unsortedMacEmailPair.add(allMacaddressEmailPairs[0] + "," + allMacaddressEmailPairs[1]);
                    }

                    //sort the arraylist unsortedMacEmailPair in ascending order by macaddress
                    //Collections.sort sorts data from left to right hence all the accompanying email will not be affected
                    Collections.sort(unsortedMacEmailPair);

                    //retrieve sorted macaddresses with their emails and add into the jsonobject
                    for (String eachMacEmail : unsortedMacEmailPair) {
                        String[] allMacaddressEmailPairs = eachMacEmail.split(",");

                        //temp json object to store required output first before adding to resultsArr for final output
                        //Every iteration looped through will be stored in a json object
                        JsonObject topKCompanions = new JsonObject();
                        topKCompanions.addProperty("rank", count);

                        //check if corresponding email has an email or not
                        if (allMacaddressEmailPairs[1].equals("No email found")) {
                            topKCompanions.addProperty("companion", "");
                        //email is present
                        } else { 
                            topKCompanions.addProperty("companion", allMacaddressEmailPairs[1]);
                        }

                        topKCompanions.addProperty("mac-address", allMacaddressEmailPairs[0]);
                        topKCompanions.addProperty("time-together", timeSpentByCompanions.intValue());

                        // add temp json object to final json array for output
                        resultsArr.add(topKCompanions);
                    }
                }
                count++;
            }
            jsonOutput.addProperty("status", "success");
            jsonOutput.add("results", resultsArr);
            
            
        //if date, topK or macaddress is not valid, send error message
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
