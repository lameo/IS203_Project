package json;

import javazoom.upload.MultipartFormDataRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.WebServlet;
import javazoom.upload.UploadException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import model.SharedSecretManager;
import java.sql.SQLException;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.IOException;
import model.UserDAO;
import model.User;

/**
 * A servlet that manages inputs from url and results from SharedSecretManager.
 * Contains processRequest, doPost, doGet, getServletInfo methods
 */
@WebServlet(urlPatterns = {"/json/authenticate"})
public class Authenticate extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        // creates a new gson object by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // creates a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();

        JsonArray errMsg = new JsonArray();

        try {
            String username = null;
            String password = null;
            // Checks if json was send through a MultipartFormDataRequest
            if (MultipartFormDataRequest.isMultipartFormData(request)) {
                // specialized version of request object to interpret the data
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request);
                username = multipartRequest.getParameter("username"); //get username from request
                password = multipartRequest.getParameter("password"); //get password from request

            // if no multipart request detected, just do normal request
            } else {
                username = request.getParameter("username"); //get username from request
                password = request.getParameter("password"); //get password from request
            }

            // check if username is null (dont have ?username=something)
            if (username == null) {
                errMsg.add("missing username");
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", errMsg);
                out.println(gson.toJson(jsonOutput));
                out.close(); //close PrintWriter
                return;
            }

            // check if username is empty (?username="")
            if (username.isEmpty()) {
                errMsg.add("blank username");
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", errMsg);
                out.println(gson.toJson(jsonOutput));
                out.close(); //close PrintWriter
                return;
            }

            // check if password is null (dont have ?password=something)
            if (password == null) {
                errMsg.add("missing password");
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", errMsg);
                out.println(gson.toJson(jsonOutput));
                out.close(); //close PrintWriter
                return;
            }

            // check if password is empty (?password="")
            if (password.isEmpty()) {
                errMsg.add("blank password");
                jsonOutput.addProperty("status", "error");
                jsonOutput.add("messages", errMsg);
                out.println(gson.toJson(jsonOutput));
                out.close(); //close PrintWriter
                return;
            }

            // if trying to login as admin
            // validate admin credentials
            if (username.equals("admin") && password.equals("Password!SE888")) {
                jsonOutput.addProperty("status", "success");
                // Create an admin token
                String token = SharedSecretManager.authenticateAdmin();
                jsonOutput.addProperty("token", token);

            // if trying to login as a user
            // checking if username is valid format (e.g. john.doe.2016)
            } else if (UserDAO.validateUsername(username)) {

                User user = UserDAO.retrieveUserByName(username, password);
                // Checking if it is a valid user (record found in database w matching password)
                // if valid user
                if (user instanceof User) {
                    String token = SharedSecretManager.authenticateUser(user.getName());
                    jsonOutput.addProperty("status", "success");
                    jsonOutput.addProperty("token", token);
                // if not valid user
                } else {
                    errMsg.add("invalid username/password");
                }
            // if somehow not valid user or admin
            } else {
                errMsg.add("invalid username/password");
            }

        } catch (SQLException e) {
            // if connection to database cannot be establish, send error message
            errMsg.add("server is currently unavailable, please try again later. Thank you.");
        } catch (UploadException e) {
            // if something wrong with recieving MultipartFormDataRequest
            out.println("error, Unable to upload. Please try again later");
        }

        if (errMsg.size() > 0) {
            // if there is more than one error message, print json is another format
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("messages", errMsg);
        }
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
