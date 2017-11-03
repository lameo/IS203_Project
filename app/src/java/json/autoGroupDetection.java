/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import model.ReportDAO;
import model.SharedSecretManager;

/**
 *
 * @author HongYuan
 */
@WebServlet(urlPatterns = {"/json/group_detect"})
public class autoGroupDetection extends HttpServlet {

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
        if(errMsg.size() == 0) {
            //at this point, dateEntered is valid and is in the right format
            dateEntered = dateEntered.replaceAll("T", " ");
            
            //create a json array to store errors
            JsonArray resultsArr = new JsonArray();
            
            int UsersNumber = AutoGroupDAO.retreiveUsersNumber(dateEntered);//retreive the number of users in the entire SIS building for that date and time
            
            //retreive map of all the users and their location traces whom stay at SIS building in specified time window for at least 12 mins
            Map<String, Map<String, ArrayList<String>>> AutoUsers = AutoGroupDAO.retreiveAutoUsers(dateEntered);
            
            ArrayList<Group> autoGroups = new ArrayList<Group>();
            //test
            //ArrayList<String> AutoGroups = new ArrayList<String>();
            //check if there are valid auto users
            if (AutoUsers != null && AutoUsers.size() > 0) {
                //retrieve groups formed from valid auto users
                autoGroups = retrieveAutoGroups(AutoUsers);
            }
            
            //session.setAttribute("test", AutoGroups);
            if (autoGroups != null && autoGroups.size() > 0) {
                //check autogroups and remove sub groups
                autoGroups = AutoGroupDAO.CheckAutoGroups(autoGroups);
            }
            // sort the autogroup list in group size, total time duration order first
            Collections.sort(autoGroups);
            for (Group autoGroup:autoGroups){
                //temp json object to store required output first before adding to resultsArr for final output
                JsonObject autoGroupObject = new JsonObject();
                autoGroupObject.addProperty("size",autoGroup.getAutoUsersSize());
                autoGroupObject.addProperty("total-time-spent",(int)autoGroup.CalculateTotalDuration());
                JsonArray membersArr = new JsonArray();
                TreeMap<String, String> sortedUsersWithEmails = autoGroup.retreiveEmailsWithMacs();
                Iterator<String> usersWithEmails = sortedUsersWithEmails.keySet().iterator();
                while(usersWithEmails.hasNext()){
                    JsonObject membersObject = new JsonObject();
                    String email = usersWithEmails.next();
                    String mac = sortedUsersWithEmails.get(email);
                    membersObject.addProperty("email",email);
                    membersObject.addProperty("mac-address",mac);
                    membersArr.add(membersObject);
                }
                TreeMap<String, String> sortedUsersNoEmails = autoGroup.retreiveMacsNoEmails();
                Iterator<String> usersNoEmails = sortedUsersNoEmails.keySet().iterator();
                while(usersNoEmails.hasNext()){
                    JsonObject membersObject = new JsonObject();
                    String mac = usersNoEmails.next();
                    String email = sortedUsersNoEmails.get(mac);
                    membersObject.addProperty("email","");
                    membersObject.addProperty("mac-address",mac);
                    membersArr.add(membersObject);
                }
                
                autoGroupObject.add("members",membersArr);
                JsonArray locationsArr = new JsonArray();
                
                Map<String, Double> locationsDuration =  autoGroup.CalculateTimeDuration();
                Iterator<String> locations = locationsDuration.keySet().iterator();
                while(locations.hasNext()){
                    JsonObject locationsObject = new JsonObject();
                    String location = locations.next();
                    double duration = locationsDuration.get(location);
                    locationsObject.addProperty("location",location);
                    locationsObject.addProperty("time-spent",(int)(duration));
                    locationsArr.add(locationsObject);
                }
                
                autoGroupObject.add("locations",locationsArr);
                resultsArr.add(autoGroupObject);
            }
            
            jsonOutput.addProperty("status", "success");
            jsonOutput.addProperty("total-users", UsersNumber);
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
