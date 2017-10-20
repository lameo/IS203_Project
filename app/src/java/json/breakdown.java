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
import javax.servlet.http.HttpSession;
import model.ReportDAO;
import model.SharedSecretManager;

@WebServlet(urlPatterns = {"/json/basic-loc-report"})
public class breakdown extends HttpServlet {

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
        ArrayList<String> errors = new ArrayList<>();
        
        //get token from request
        String token = request.getParameter("token"); 
        // Token checking
        if(token==null){
            errors.add("missing token");
        }else if(token.equals("")){
            errors.add("blank token");
        }else if(SharedSecretManager.verifyUser(token)){
            errors.add("invalid token");
        }
        
        //get order from request
        String order = request.getParameter("order"); 
        //Order checking
        if(order==null){
            errors.add("missing token");
        }else if(order.equals("")){
            errors.add("blank token");
        }
        order = order.toLowerCase();
        ArrayList<String> aaa = new ArrayList<>();
        String[] options = "year gender school".split(" ");
        for (int i = 0; i < options.length; i++) {
            aaa.add(options[i]);
            for (int j = 0; j < options.length; j++) {
                if (i != j) {
                    aaa.add(options[i] + "," + options[j]);
                    System.out.println(options[i] + "," + options[j]);
                    for (int k = 0; k < options.length; k++) {
                        if (j != k && i != k) {
                            aaa.add(options[i] + "," + options[j] + "," + options[k]);
                        }
                    }
                }
            }
        }
        if(!aaa.contains(order)){
            errors.add("invalid order");
        }
        //get date from request
        String date = request.getParameter("date");
        //Order checking
        if(order==null){
            errors.add("missing date");
        }else if(order.equals("")){
            errors.add("blank date");
        }
        
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
        
        if(!dateValid){
            errors.add("invalid date");
        }

        //if all checks are valid
        JsonObject finalAns = new JsonObject();
        if (errors.size() == 0) {
            String[] year = {"year", "2013", "2014", "2015", "2016", "2017"};                              //5
            String[] gender = {"gender", "M", "F"};                                                        //2
            String[] school = {"School", "accountancy", "business", "economics", "law", "sis", "socsc"};   //6

            String[] arr = order.split(",");
            ArrayList<Integer> temp1 = ReportDAO.notVeryBasicBreakdownJson(Arrays.copyOfRange(arr, 0, 1), date);
            ArrayList<Integer> temp2 = ReportDAO.notVeryBasicBreakdownJson(Arrays.copyOfRange(arr, 0, 2), date);
            ArrayList<Integer> temp3 = ReportDAO.notVeryBasicBreakdownJson(arr, date);
            String[] first = null;
            String[] second = null;
            String[] third = null;

            if (arr[0].equals(
                    "year")) {
                first = year;
            } else if (arr[0].equals(
                    "gender")) {
                first = gender;
            } else if (arr[0].equals(
                    "school")) {
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
            if (arr.length
                    == 1) {
                for (int i = 0; i < temp1.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty(first[0], first[i + 1]);
                    temp.addProperty("count", temp1.get(i));
                    one.add(temp);
                }
            }
            if (arr.length
                    == 2) {
                for (int i = 0; i < temp1.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty(first[0], first[i + 1]);
                    temp.addProperty("count", temp1.get(i));
                    one.add(temp);
                }
                JsonArray two = new JsonArray();
                for (int i = 0; i < temp2.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty(second[0], second[(i) % (second.length - 1) + 1]);
                    temp.addProperty("count", temp2.get(i));
                    two.add(temp);
                    if ((i + 1) % (temp2.size() / temp1.size()) == 0) {
                        one.get((i + 1) / (second.length - 1) - 1).getAsJsonObject().add("breakdown", two);
                        two = new JsonArray();
                    }
                }
            }
            if (arr.length
                    == 3) {
                JsonArray two = new JsonArray();
                for (int i = 0; i < temp2.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty(second[0], second[(i) % (second.length - 1) + 1]);
                    temp.addProperty("count", temp2.get(i));
                    two.add(temp);
                }
                JsonArray three = new JsonArray();
                for (int i = 0; i < temp3.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty(third[0], third[(i) % (third.length - 1) + 1]);
                    temp.addProperty("count", temp3.get(i));
                    three.add(temp);
                    if ((i + 1) % (temp3.size() / temp2.size()) == 0) {
                        two.get((i + 1) / (third.length - 1) - 1).getAsJsonObject().add("breakdown", three);
                        three = new JsonArray();
                    }
                }
                for (int i = 0; i < temp1.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty(first[0], first[i + 1]);
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
            //if order or date is not valid
        } else {
            finalAns.addProperty("status", "error");
            finalAns.addProperty("messages", errors.toString());
        }
        out.println(gson.toJson(finalAns));
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
