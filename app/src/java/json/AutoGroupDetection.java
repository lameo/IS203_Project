package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AutoGroupDAO;
import static model.AutoGroupDAO.retrieveAutoGroups;
import model.Group;
import model.SharedSecretManager;

/**
 * A servlet that manages inputs from url and results from AutoGroupDAO.
 * Contains processRequest, doPost, doGet, getServletInfo methods
 */
@WebServlet(urlPatterns = {"/json/group_detect"})
public class AutoGroupDetection extends HttpServlet {

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

        //creates a new gson object
        //by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //creats a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();

        //create a json array to store errors
        JsonArray errMsg = new JsonArray();

        //get token from request
        String tokenEntered = request.getParameter("token"); //get token from url
        String dateEntered = request.getParameter("date"); //get date from url

        //token = null represents missing token
        if (tokenEntered == null) {
            errMsg.add("missing token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //token = "" represents blank token entered
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

        //if the dateEntered not the right format
        if (dateEntered == null) {
            errMsg.add("missing date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            out.close(); //close PrintWriter
            return;
        }

        //if the dateEntered not the right format
        if (dateEntered.isEmpty()) {
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

        //only run with valid token and valid date entered by user
        if (errMsg.size() == 0) {
            //at this point, dateEntered is valid and is in the right format
            dateEntered = dateEntered.replaceAll("T", " ");

            //create a json array to store errors
            JsonArray resultsArr = new JsonArray();

            int usersNumber = AutoGroupDAO.retrieveUsersNumber(dateEntered);//retrieve the number of users in the entire SIS building for that date and time

            //retrieve map of all the users and their location traces whom stay at SIS building in specified time window for at least 12 mins
            Map<String, Map<String, ArrayList<String>>> autoUsers = AutoGroupDAO.retrieveUsersWith12MinutesData(dateEntered);

            ArrayList<Group> autoGroups = new ArrayList<Group>();

            //check if there are valid users
            if (autoUsers != null && autoUsers.size() > 0) {
                //retrieve groups formed from valid users
                autoGroups = retrieveAutoGroups(autoUsers);
            }
            //check if there are valid groups formed from valid users
            if (autoGroups != null && autoGroups.size() > 0) {
                //check groups and remove sub groups or same groups to return the valid groups
                autoGroups = AutoGroupDAO.checkAutoGroups(autoGroups);
            }
            //sort thegroup list in group size, for groups with same group size, sort by total time spent
            Collections.sort(autoGroups);
            //loop through the group list
            for (Group autoGroup : autoGroups) {
                //temp json object to store each group first before adding to resultsArr for final output
                JsonObject autoGroupObject = new JsonObject();
                //add group size to each group object
                autoGroupObject.addProperty("size", autoGroup.getAutoUsersSize());
                //add total time spent to each group object
                autoGroupObject.addProperty("total-time-spent", (int) autoGroup.calculateTotalDuration());
                //create a json array to store users in group
                JsonArray membersArr = new JsonArray();
                //retrieve users in group with email found
                TreeMap<String, String> sortedUsersWithEmails = autoGroup.retrieveEmailsWithMacs();
                //retrieve emails of users
                Iterator<String> usersWithEmails = sortedUsersWithEmails.keySet().iterator();
                //loop through emails of users
                while (usersWithEmails.hasNext()) {
                    //temp json object to store each user email and macaddress first before adding to membersArr for final output
                    JsonObject membersObject = new JsonObject();
                    //retrieve user email
                    String email = usersWithEmails.next();
                    //retrieve user macaddress
                    String mac = sortedUsersWithEmails.get(email);
                    //add user email to membersObject
                    membersObject.addProperty("email", email);
                    //add user macaddress to membersObject
                    membersObject.addProperty("mac-address", mac);
                    //add each user object to membersArr for output
                    membersArr.add(membersObject);
                }
                //retrieve users in group with no email found
                TreeMap<String, String> sortedUsersNoEmails = autoGroup.retrieveMacsNoEmails();
                //retrieve macaddresses of users
                Iterator<String> usersNoEmails = sortedUsersNoEmails.keySet().iterator();
                //loop through macaddresses of users
                while (usersNoEmails.hasNext()) {
                    //temp json object to store each user email and macaddress first before adding to membersArr for final output
                    JsonObject membersObject = new JsonObject();
                    //retrieve user email
                    String mac = usersNoEmails.next();
                    //retrieve user email ("")
                    String email = sortedUsersNoEmails.get(mac);
                    //add user email to membersObject
                    membersObject.addProperty("email", "");
                    //add user macaddress to membersObject
                    membersObject.addProperty("mac-address", mac);
                    //add each user object to membersArr for output
                    membersArr.add(membersObject);
                }
                //add temp group users array to autoGroupObject for final output
                autoGroupObject.add("members", membersArr);
                //create a json array to store results of group locations
                JsonArray locationsArr = new JsonArray();
                //retrieve common locations and time spent of the group
                Map<String, Double> locationsDuration = autoGroup.calculateTimeDuration();
                //retrieve common locations of the group
                Iterator<String> locations = locationsDuration.keySet().iterator();
                //loop through the common locations of the group
                while (locations.hasNext()) {
                    //temp json object to store each common location and time spent first before adding to locationsArr for final output
                    JsonObject locationsObject = new JsonObject();
                    //retrieve common location of the group
                    String location = locations.next();
                    //retrieve time spent at common location
                    double duration = locationsDuration.get(location);
                    //add common location to locationsObject
                    locationsObject.addProperty("location", location);
                    //convert time spent from double to int and add to locationsObject
                    locationsObject.addProperty("time-spent", (int) (duration));
                    //add each locaion object to locationsArr for output
                    locationsArr.add(locationsObject);
                }
                //add temp group common locations and time spent array to autoGroupObject for final output
                autoGroupObject.add("locations", locationsArr);
                // add each temp json group object to final json array for output
                resultsArr.add(autoGroupObject);
            }
            //final output for viewing 
            jsonOutput.addProperty("status", "success");
            jsonOutput.addProperty("total-users", usersNumber);
            jsonOutput.addProperty("total-groups", autoGroups.size());
            jsonOutput.add("groups", resultsArr);
        } else {
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
        }
        out.println(gson.toJson(jsonOutput));

        //close PrintWriter
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
