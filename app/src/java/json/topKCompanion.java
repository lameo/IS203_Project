package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ReportDAO;
import model.SharedSecretManager;

@WebServlet(urlPatterns = {"/json/top-k-companions"})
public class topKCompanion extends HttpServlet {

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

        //ArrayList<String> errors = new ArrayList<>();
        //get token from request
        String token = request.getParameter("token");
        //check if token is valid
        if (!SharedSecretManager.verifyUser(token)) { //user is not verified
            errMsg.add("invalid token");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //get macaddress from request
        String macaddress = request.getParameter("mac-address");
        //retrieve all existing macaddresses from demographics.csv 
        ArrayList<String> allMacaddressList = ReportDAO.getAllMacaddress();
        if (!allMacaddressList.contains(macaddress)) { //check if macaddress is inside demographics.csv
            errMsg.add("invalid mac-address");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //get date from request
        String date = request.getParameter("date");
        //check if date is entered by user to url
        if (date == null || date.equals("")) {
            errMsg.add("blank date");
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
            out.println(gson.toJson(jsonOutput));
            return;
        }

        //get k value from request 
        String topKEntered = request.getParameter("k");
        //check top k added is correct
        if (topKEntered == null || topKEntered.equals("")) { // if not specified, set default value to 3
            topKEntered = "3";
        }
        int topK = Integer.parseInt(topKEntered); //get the number user entered in url as an int
        if (topK < 1 || topK > 10) {
            errMsg.add("invalid k"); //add error msg into JsonArray
        } else {
            //from here on, user is verified
            //topk number is between 1 - 10 inclusive with default as 3 if no k is entered
            //semantic place is valid

            //check for valid date entered by user
            boolean dateValid = true;
            // Length check
            dateValid = dateValid && date.length() == 19;
            // Year bigger than 2013 & smaller or equal to 2017
            dateValid = dateValid && (Integer.parseInt(date.substring(0, 4)) > 2013) && (Integer.parseInt(date.substring(0, 4)) <= 2017);
            // Month bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(date.substring(5, 7)) > 0) && (Integer.parseInt(date.substring(5, 7)) <= 12);
            // Day bigger than 0 & smaller or equal to 12
            dateValid = dateValid && (Integer.parseInt(date.substring(8, 10)) > 0) && (Integer.parseInt(date.substring(8, 10)) <= 31);
            // Hour bigger or equal 0 & smaller or equal to 24
            dateValid = dateValid && (Integer.parseInt(date.substring(8, 10)) >= 0) && (Integer.parseInt(date.substring(11, 13)) <= 23);
            // Min bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(date.substring(8, 10)) >= 0) && (Integer.parseInt(date.substring(14, 16)) <= 59);
            // Second bigger or equal 0 & smaller or equal to 59
            dateValid = dateValid && (Integer.parseInt(date.substring(8, 10)) >= 0) && (Integer.parseInt(date.substring(14, 16)) <= 59);
            if (!dateValid) {
                errMsg.add("invalid date");
            } else {
                //at this point, dateEntered is valid and is in the right format 
                date = date.replaceAll("T", " ");

                JsonArray list = new JsonArray();
                int count = 1;
                Map<Double, ArrayList<String>> results;
                try {
                    results = ReportDAO.retrieveTopKCompanions(date, macaddress, topK);
                    System.out.println(results);
                    System.out.println("trying");
                    System.out.println("trying");
                    System.out.println("trying");

                    Set<Double> as = results.keySet();
                    for (Double temp : results.keySet()) {
                        JsonObject tempJson = new JsonObject();
                        tempJson.addProperty("rank", count++);
                        tempJson.addProperty("email", results.get(temp).get(0));
                        tempJson.addProperty("mac-address", results.get(temp).get(1));
                        tempJson.addProperty("time-together", temp);
                        list.add(tempJson);
                    }

                    for (Map.Entry<Double, ArrayList<String>> temp : results.entrySet()) {
                        double key = temp.getKey();
                        System.out.println(key);

                    }
                } catch (ParseException ex) {
                    Logger.getLogger(topKCompanion.class.getName()).log(Level.SEVERE, null, ex);
                }

                /*
                JsonObject ans = new JsonObject();
                ans.addProperty("status", "success");
                ans.add("results", list);
                out.println(gson.toJson(ans));
                */
            }
            
        }

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
