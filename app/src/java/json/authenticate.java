package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import model.UserDAO;
import model.SharedSecretManager;

@WebServlet(urlPatterns = {"/json/authenticate"})
public class authenticate extends HttpServlet {

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
        
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        //creates a new gson object
        //by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //creats a new json object for printing the desired json output
        JsonObject jsonOutput = new JsonObject();
        //create a json array to store errors
        JsonArray errMsg = new JsonArray();

        String username = request.getParameter("username"); //get username from request
        String password = request.getParameter("password"); //get password from request        

        try {
            if (username.equals("admin") && password.equals("password")) { //admin
                String token = SharedSecretManager.authenticateAdmin();
                session.setAttribute("token", token);
                jsonOutput.addProperty("Status", "Success"); 
                jsonOutput.addProperty("token", token);                 
            }                        
            if(UserDAO.validateUsername(username)){ //if username is valid e.g. john.doe.2016
                User user = UserDAO.retrieveUserByName(username, password);
            
                if (user instanceof User){ //if user in database
                    String token = SharedSecretManager.authenticateUser(user.getName());
                    session.setAttribute("token", token); 
                    jsonOutput.addProperty("Status", "Success"); 
                    jsonOutput.addProperty("token", token);                      
                } 
            } else {
                jsonOutput.addProperty("Status", "Error"); 
                jsonOutput.addProperty("Messages", "invalid username/password");                    
            }
            
        } catch (SQLException e){
                jsonOutput.addProperty("Status", "Error"); 
                jsonOutput.addProperty("Messages", "Server is currently unavailable, please try again later. Thank you."); 
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
