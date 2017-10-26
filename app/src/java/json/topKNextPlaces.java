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
 *
 * @author HongYuan
 */
@WebServlet(urlPatterns = {"/json/top-k-next-places"})
public class topKNextPlaces extends HttpServlet {

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
        String semanticPlace = request.getParameter("origin"); //get the semantic place from url

        if (tokenEntered == null || tokenEntered.isEmpty()) {
            errMsg.add("blank token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //check if token is invalid
        if (!SharedSecretManager.verifyUser(tokenEntered)) { //if the user is not verified
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //check if dateEntered is entered by user from url
        if (dateEntered == null || dateEntered.equals("")) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //check if origin is entered by user from url
        if (semanticPlace == null || semanticPlace.equals("")) {
            errMsg.add("blank origin");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //check for valid date entered by user
        boolean valid = true;
        // Length check
        valid = valid && dateEntered.length() == 19;
        // Year bigger than 2013 & smaller or equal to 2017
        valid = valid && (Integer.parseInt(dateEntered.substring(0, 4)) > 2013) && (Integer.parseInt(dateEntered.substring(0, 4)) <= 2017);
        // Month bigger than 0 & smaller or equal to 12
        valid = valid && (Integer.parseInt(dateEntered.substring(5, 7)) > 0) && (Integer.parseInt(dateEntered.substring(5, 7)) <= 12);
        // Day bigger than 0 & smaller or equal to 12
        valid = valid && (Integer.parseInt(dateEntered.substring(8, 10)) > 0) && (Integer.parseInt(dateEntered.substring(8, 10)) <= 31);
        // Hour bigger or equal 0 & smaller or equal to 24
        valid = valid && (Integer.parseInt(dateEntered.substring(11, 13)) >= 0) && (Integer.parseInt(dateEntered.substring(11, 13)) <= 23);
        // Min bigger or equal 0 & smaller or equal to 59
        valid = valid && (Integer.parseInt(dateEntered.substring(14, 16)) >= 0) && (Integer.parseInt(dateEntered.substring(14, 16)) <= 59);
        // Second bigger or equal 0 & smaller or equal to 59
        valid = valid && (Integer.parseInt(dateEntered.substring(17, 19)) >= 0) && (Integer.parseInt(dateEntered.substring(17, 19)) <= 59);
        if (!valid) {
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

        //check if semantic place comes from location-lookup.csv
        ArrayList<String> validSemanticPlacesList = ReportDAO.getSemanticPlaces();
        if (!validSemanticPlacesList.contains(semanticPlace)) { //if semanticPlace is not inside the locationloopup table
            errMsg.add("invalid origin");
        }

        //from here on, user is verified
        //topk number is between 1 - 10 inclusive with default as 3 if no k is entered
        //semantic place is valid
        if (errMsg.size() == 0) {
            //at this point, dateEntered is valid and is in the right format 
            dateEntered = dateEntered.replaceAll("T", " ");

            //create a json array to store errors
            JsonArray resultsArr = new JsonArray();

            int usersVisitingNextPlace = 0; // total quantity of users visiting next place
            Map<Integer, ArrayList<String>> topKNextPlaces = ReportDAO.retrieveTopKNextPlaces(dateEntered, semanticPlace);

            //retrieve users who are in a specific place given a specific time frame in a specific location
            ArrayList<String> usersList = ReportDAO.retrieveUserBasedOnLocation(dateEntered, semanticPlace);

            Set<Integer> totalNumOfUsersSet = topKNextPlaces.keySet(); // to get the different total number of users in a next place in desc order
            int counter = 1; // to match topk number after incrementation
            for (int totalNumOfUsers : totalNumOfUsersSet) {
                ArrayList<String> locations = topKNextPlaces.get(totalNumOfUsers); // gives the list of location with the same totalNumOfUsers
                Collections.sort(locations); // sort the locations list in ascending order first
                if (counter <= topK) { // to only display till topk number
                    JsonObject topKNextPlace = new JsonObject();

                    //to add all locations with the same count inside to output results as an array
                    JsonArray chainAllSemanticPlaces = new JsonArray();
                    topKNextPlace.addProperty("rank", counter);

                    for (int i = 0; i < locations.size(); i++) {
                        if (locations.get(i).equals(semanticPlace)) { // if the locations is the same, find the number of users who visited another place (exclude those left the place but have not visited another place) in the query window
                            usersVisitingNextPlace -= totalNumOfUsers; // minus off if the user is staying at the same place
                        }
                        chainAllSemanticPlaces.add(locations.get(i));
                        //if (i + 1 < locations.size()) { //fence-post method to add the comma
                        //    chainAllSemanticPlaces+=", ";
                        //}
                    }
                    topKNextPlace.add("semantic-place", chainAllSemanticPlaces); //add the JsonArray of locations into the JsonObject
                    topKNextPlace.addProperty("count", totalNumOfUsers);
                    resultsArr.add(topKNextPlace);
                    counter++;
                }
                usersVisitingNextPlace += totalNumOfUsers * locations.size(); // add if the user is going other places but the quantity may have multiple next locations
            }
            jsonOutput.addProperty("status", "success");
            jsonOutput.addProperty("total-users", usersList.size());
            jsonOutput.addProperty("total-next-place-users", usersVisitingNextPlace);
            jsonOutput.add("results", resultsArr);
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
