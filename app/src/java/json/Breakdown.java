package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ReportDAO;
import model.SharedSecretManager;

@WebServlet(urlPatterns = {"/json/basic-loc-report"})
public class Breakdown extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        //creates a new gson object by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //creats a new json object for printing the desired json output
        JsonObject finalAns = new JsonObject();
        //create a json array to store errors
        JsonArray errMsg = new JsonArray();


        //get token from request
        String token = request.getParameter("token");
        // check if token is null (dont have ?token=something)
        if (token == null) {
            errMsg.add("missing token");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }

        // check if token is empty (?token="")
        if (token.isEmpty()) {
            errMsg.add("blank token");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }

        // checking if the token submitted by the user is valid
        if (!SharedSecretManager.verifyUser(token)) {
            // if token given is not valid
            errMsg.add("invalid token");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }

        
        //get order from request
        String order = request.getParameter("order");
        // check if order is null (dont have ?order=something)
        if (order == null) {
            errMsg.add("missing order");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }

        // check if order is empty (?order="")
        if (order.isEmpty()) {
            errMsg.add("blank order");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }
        //get date from request
        String timeDate = request.getParameter("date");
        // check if date is null (dont have ?date=something)
        if (timeDate == null) {
            errMsg.add("missing date");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }

        // check if date is empty (?date="")
        if (timeDate.isEmpty()) {
            errMsg.add("blank date");
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
            out.println(gson.toJson(finalAns));
            out.close(); //close PrintWriter
            return;
        }
        
        // After this point, all variables required are not empty or null, so start checking whether they are valid format
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
            // if any of the number can't be parsed means a String is at where a number is supposed to be at
            errMsg.add("invalid date");
        }

        // checking if orders only contains any combination of year, gender, school
        // eg ygs, gys, sgy, ys, gs where y - year; g - gender; s - school
        order = order.toLowerCase();
        ArrayList<String> aaa = new ArrayList<>();
        String[] options = "year gender school".split(" ");
        for (int i = 0; i < options.length; i++) {
            aaa.add(options[i]);
            for (int j = 0; j < options.length; j++) {
                if (i != j) {
                    aaa.add(options[i] + "," + options[j]);
                    for (int k = 0; k < options.length; k++) {
                        if (j != k && i != k) {
                            aaa.add(options[i] + "," + options[j] + "," + options[k]);
                        }
                    }
                }
            }
        }
        if (!aaa.contains(order)) {
            errMsg.add("invalid order");
        }

        //if all checks are valid, continue processing
        if (errMsg.size() == 0) {
            //proper date format -> (YYYY-MM-DDTHH:MM:SS)
            //replace "T" with "" to allow system to process correctly
            timeDate = timeDate.replaceAll("T", " ");

            String[] year = {"year", "2013", "2014", "2015", "2016", "2017"};                              //5
            String[] gender = {"gender", "M", "F"};                                                        //2
            String[] school = {"school", "accountancy", "business", "economics", "law", "sis", "socsc"};   //6

            String[] arr = order.split(",");
            // submit the first var of the order for processing
            ArrayList<Integer> temp1 = ReportDAO.notVeryBasicBreakdownJson(Arrays.copyOfRange(arr, 0, 1), timeDate);
            // submit the first and second var of the order for processing ( returns null if only 1 var is submitted by user)
            ArrayList<Integer> temp2 = ReportDAO.notVeryBasicBreakdownJson(Arrays.copyOfRange(arr, 0, 2), timeDate);
            // submit all three var of the var for processing ( returns null if less than 3 var is submitted by user)
            ArrayList<Integer> temp3 = ReportDAO.notVeryBasicBreakdownJson(arr, timeDate);
            String[] first = null;
            String[] second = null;
            String[] third = null;

            // checking the order of variable submitted by the user and saving them as first, second & third
            if (arr[0].equals("year")) {
                first = year;
            } else if (arr[0].equals("gender")) {
                first = gender;
            } else if (arr[0].equals("school")) {
                first = school;
            }

            try {
                if (arr[1].equals("year")) {
                    second = year;
                }
                if (arr[1].equals("gender")) {
                    second = gender;
                }
                if (arr[1].equals("school")) {
                    second = school;
                }
                
                if (arr[2].equals("year")) {
                    third = year;
                } else if (arr[2].equals("gender")) {
                    third = gender;
                } else if (arr[2].equals("school")) {
                    third = school;
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }

            JsonArray one = new JsonArray();
            // if there is only one variable
            // run though temp1 (breakdown by only variable)
            // add property of variable name (first[0]) & amount first[i+1]
            if (arr.length == 1) {
                for (int i = 0; i < temp1.size(); i++) {
                    JsonObject temp = new JsonObject();
                    if (first[0].equals("year")) {
                        temp.addProperty(first[0], Integer.parseInt(first[i + 1]));
                    } else {
                        temp.addProperty(first[0], first[i + 1]);
                    }
                    temp.addProperty("count", temp1.get(i));
                    one.add(temp);
                }
            }

            // if there is 2 variable submitted
            // run through the outer variable (temp1)
            // then run through the inner variable and add the result set to outer variable to create a nest
            if (arr.length == 2) {
                for (int i = 0; i < temp1.size(); i++) {
                    JsonObject temp = new JsonObject();
                    if (first[0].equals("year")) {
                        temp.addProperty(first[0], Integer.parseInt(first[i + 1]));
                    } else {
                        temp.addProperty(first[0], first[i + 1]);
                    }
                    temp.addProperty("count", temp1.get(i));
                    one.add(temp);
                }
                JsonArray two = new JsonArray();
                for (int i = 0; i < temp2.size(); i++) {
                    JsonObject temp = new JsonObject();
                    if (second[0].equals("year")) {
                        temp.addProperty(second[0], Integer.parseInt(second[(i) % (second.length - 1) + 1]));
                    } else {
                        temp.addProperty(second[0], second[(i) % (second.length - 1) + 1]);
                    }
                    temp.addProperty("count", temp2.get(i));
                    two.add(temp);
                    if ((i + 1) % (temp2.size() / temp1.size()) == 0) {
                        one.get((i + 1) / (second.length - 1) - 1).getAsJsonObject().add("breakdown", two);
                        two = new JsonArray();
                    }
                }
            }

            // if there is 3 variables submitted
            if (arr.length == 3) {
                JsonArray two = new JsonArray();
                for (int i = 0; i < temp2.size(); i++) {
                    JsonObject temp = new JsonObject();
                    if (second[0].equals("year")) {
                        temp.addProperty(second[0], Integer.parseInt(second[(i) % (second.length - 1) + 1]));
                    } else {
                        temp.addProperty(second[0], second[(i) % (second.length - 1) + 1]);
                    }
                    temp.addProperty("count", temp2.get(i));
                    two.add(temp);
                }
                JsonArray three = new JsonArray();
                for (int i = 0; i < temp3.size(); i++) {
                    JsonObject temp = new JsonObject();
                    if (third[0].equals("year")) {
                        temp.addProperty(third[0], Integer.parseInt(third[(i) % (third.length - 1) + 1]));
                    } else {
                        temp.addProperty(third[0], third[(i) % (third.length - 1) + 1]);
                    }
                    temp.addProperty("count", temp3.get(i));
                    three.add(temp);
                    if ((i + 1) % (temp3.size() / temp2.size()) == 0) {
                        two.get((i + 1) / (third.length - 1) - 1).getAsJsonObject().add("breakdown", three);
                        three = new JsonArray();
                    }
                }
                for (int i = 0; i < temp1.size(); i++) {
                    JsonObject temp = new JsonObject();
                    if (first[0].equals("year")) {
                        temp.addProperty(first[0], Integer.parseInt(first[i + 1]));
                    } else {
                        temp.addProperty(first[0], Integer.parseInt(first[i + 1]));
                    }
                    temp.addProperty("count", temp1.get(i));
                    one.add(temp);
                }
                JsonArray tempo = new JsonArray();
                for (int i = 0; i < two.size(); i++) {
                    tempo.add(two.get(i));
                    if ((i + 1) % (temp2.size() / temp1.size()) == 0) {
                        one.get((i + 1) / (second.length - 1) - 1).getAsJsonObject().add("breakdown", tempo);
                        tempo = new JsonArray();
                    }
                }
            }
            finalAns.addProperty("status", "success");
            finalAns.add("breakdown", one);
            
            
        //if order or date is not valid, send error message
        } else {
            finalAns.addProperty("status", "error");
            finalAns.add("messages", errMsg);
        }
        
        
        // Returning the json output we created in a pretty print format
        out.println(gson.toJson(finalAns));
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
