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

        //get token from request
        String token = request.getParameter("token");
        String date = request.getParameter("date");
        String macaddress = request.getParameter("mac-address");
        String topKEntered = request.getParameter("k");

        if (token == null) {
            errMsg.add("missing token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (token.isEmpty()) {
            errMsg.add("blank token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //check if token is valid
        if (!SharedSecretManager.verifyUser(token)) { //user is not verified
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //check if date is entered by user to url
        if (date == null) {
            errMsg.add("missing date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (date.isEmpty()) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //check if macaddress is entered by user to url
        if (macaddress == null) {
            errMsg.add("missing macaddress");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        if (macaddress.isEmpty()) {
            errMsg.add("blank macaddress");
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

        //retrieve all existing macaddresses from location.csv 
        //Not all macaddresses will belong to students so must get from location.csv instead from demographics.csv
        ArrayList<String> allMacaddressList = ReportDAO.getAllMacaddress();
        if (!allMacaddressList.contains(macaddress)) { //check if macaddress is inside location.csv
            errMsg.add("invalid mac-address");
        }

        //from here on, user is verified
        //topk number is between 1 - 10 inclusive with default as 3 if no k is entered
        //mac-address is valid
        if (errMsg.size() == 0) {
            //at this point, date entered is valid and is in the right format 
            date = date.replaceAll("T", " ");

            //create a json array to store results
            JsonArray resultsArr = new JsonArray();

            int count = 1; //to match topk number after incrementation
            Map<Double, ArrayList<String>> topKCompanionMap = ReportDAO.retrieveTopKCompanions(date, macaddress);

            Set<Double> timeSpentByCompanionsList = topKCompanionMap.keySet();
            for (Double timeSpentByCompanions : timeSpentByCompanionsList) {
                if (count <= topK) {// to only display till topk number
                    //to add in macaddress and email pair for sorting
                    ArrayList<String> unsortedMacEmailPair = new ArrayList<>();

                    //get the arraylist out from map
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
                        JsonObject topKCompanions = new JsonObject();
                        topKCompanions.addProperty("rank", count);
                        
                        //check if corresponding email has email or not
                        if(allMacaddressEmailPairs[1].equals("No email found")) {
                            topKCompanions.addProperty("companion", "");
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

            //final output for viewing 
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
