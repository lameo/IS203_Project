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
import javax.servlet.http.HttpSession;
import model.ReportDAO;
import model.SharedSecretManager;

/**
 *
 * @author HongYuan
 */
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

        HttpSession session = request.getSession();
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

        //print out all the error with null or empty string that is required but the user did not enter 
        if (!SharedSecretManager.verifyUser(tokenEntered)) { //verify the user - if the user is not verified
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        if (dateEntered == null || dateEntered.equals("")) { //if the dateEntered not the right format
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        if (topKEntered == null || topKEntered.equals("")) {
            topKEntered = "3";
        }
        int topK = Integer.parseInt(topKEntered); //get the number user entered in url in int
        if (topK < 1 || topK > 10) {
            errMsg.add("invalid k"); //add error msg into JsonArray
        } else { //only run with valid k
            //check for valid date entered
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
            } else {
                //at this point, dateEntered is valid and is in the right format
                dateEntered = dateEntered.replaceAll("T", " ");
                
                Map<Integer, String> topKPopularMap = ReportDAO.retrieveTopKPopularPlaces(dateEntered);

                //create a json array to store errors
                JsonArray resultsArr = new JsonArray();
                ArrayList<Integer> keys = new ArrayList<Integer>(topKPopularMap.keySet());
                //Collections.sort(keys, Collections.reverseOrder());
                int count = 1;
                for (int i = keys.size() - 1; i >= 0; i--) { //get the max value which is at the bottom
                    if (count <= topK) {
                        //System.out.print(topKPopularMap.get(keys.get(i)));
                        JsonObject topKPopPlaces = new JsonObject();
                        topKPopPlaces.addProperty("rank", count);
                        
                        //To add popular places into an array for output
                        JsonArray popularSemanticPlaces = new JsonArray();
                        
                        //add every popular place in accordance to every key(integer) found in topKPopularMap
                        popularSemanticPlaces.add(topKPopularMap.get(keys.get(i)));
                        
                        //add back JsonArray object popularSemanticPlaces into JsonObject topKPopPlaces for viewing
                        topKPopPlaces.add("semantic-places", popularSemanticPlaces);
                        
                        topKPopPlaces.addProperty("count", keys.get(i));
                        resultsArr.add(topKPopPlaces);
                    }
                    count++;
                }
                jsonOutput.addProperty("status", "success");
                jsonOutput.add("results", resultsArr);
                out.println(gson.toJson(jsonOutput));
                return;
            }
        }
        if (errMsg.size() > 0) {
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
