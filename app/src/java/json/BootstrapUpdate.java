package json;

import javazoom.upload.MultipartFormDataRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.WebServlet;
import javazoom.upload.UploadException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import javazoom.upload.UploadBean;
import javazoom.upload.UploadFile;
import model.SharedSecretManager;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Hashtable;
import java.io.PrintWriter;
import java.util.HashMap;
import model.UploadDAO;
import java.io.File;
import java.util.Set;

/**
 * A servlet that manages inputs from multipart form and results from UploadDAO.
 * Contains processRequest, doPost, doGet, getServletInfo methods
 */
@WebServlet(urlPatterns = {"/json/update"})
public class BootstrapUpdate extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Initializing upload function - allowing files to be sent from browser
        UploadBean upBean = new UploadBean();
        
        // by instantiating a new factory object, set pretty printing, then calling the create method
        PrintWriter out = response.getWriter();
        
        //creates a new json object for printing the desired json output
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // creates a new gson object
        JsonObject ans = new JsonObject();
        //create a json array to store errors
        JsonArray errMsg = new JsonArray();
        //create a json array to store file processed
        JsonArray fileUpload = new JsonArray();

        // Initializing error messages to be return later on if any
        HashMap<Integer, String> demographicsError = new HashMap<>();
        HashMap<Integer, String> locationError = new HashMap<>();

        try {
            // Checks if json was send through a MultipartFormDataRequest
            if (MultipartFormDataRequest.isMultipartFormData(request)) {
                // specialized version of request object to interpret the data
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request);

                //get token from request
                String token = multipartRequest.getParameter("token");

                // check if token is null (dont have ?token=something)
                if (token == null) {
                    errMsg.add("missing token");
                    ans.addProperty("status", "error");
                    ans.add("messages", errMsg);
                    out.println(gson.toJson(ans));
                    out.close(); //close PrintWriter
                    return;
                }

                // check if token is empty (?token="")
                if (token.isEmpty()) {
                    errMsg.add("blank token");
                    ans.addProperty("status", "error");
                    ans.add("messages", errMsg);
                    out.println(gson.toJson(ans));
                    out.close(); //close PrintWriter
                    return;
                }

                // checking if the token submitted by the user is valid
                if (!SharedSecretManager.verifyAdmin(token)) {
                    // if token given is not valid
                    errMsg.add("invalid token");
                    ans.addProperty("status", "error");
                    ans.add("messages", errMsg);
                    out.println(gson.toJson(ans));
                    out.close(); //close PrintWriter
                    return;
                }

                // if token is valid, continue processing
                // Creating a temp directory to be provided by this servletContext for the uploaded file
                ServletContext servletContext = this.getServletConfig().getServletContext();
                File directory = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
                
                // Location of directory in string format
                String outputDirectory = "" + directory;
                // Setting uploadBean output directory
                upBean.setFolderstore(outputDirectory);
                // Setting size limit of the file uploaded (equals around 1gb)
                Long size = Long.parseLong("8589934592");
                upBean.setFilesizelimit(size);

                // Getting the files send over though multipartRequest, file is in hastable which is the older version of hashmap
                Hashtable files = multipartRequest.getFiles();
                // If file exist and is not an empty file
                if ((files != null) && (!files.isEmpty())) {
                    //get the files from <host>/app/json/update
                    UploadFile file = (UploadFile) files.get("bootstrap-file"); 
                    // if the file is not null, or empty size (means no data) & has a proper name (cos need to compare name later)
                    if (file != null && file.getFileSize() > 0 && file.getFileName() != null) {
                        // Get file name
                        String fileName = file.getFileName();
                        // Get the absolute path by adding the directory path to file name
                        String filePath = outputDirectory + File.separator + fileName;

                        // Saving the file recieved to temp directory
                        upBean.store(multipartRequest, "bootstrap-file");
                        // Unzip the file and save the contents into the temp directory
                        String fileExist = UploadDAO.unzip(filePath, outputDirectory);
                        
                        // for all the files present
                        // perform respective upload function
                        // Retrieve number of rows successfully processed kept in a hash with Interger.max_value as key
                        // Save number of lines processed to JsonObject temp
                        // remove away Integer.max_value key from errorMessage to get the proper errorMessage (if any)
                        if (fileExist != null && fileExist.contains("demographics.csv")) {
                            demographicsError = UploadDAO.updateDemographics(outputDirectory + File.separator + "demographics.csv");
                            JsonObject temp = new JsonObject();
                            temp.addProperty("demographics.csv", Integer.parseInt(demographicsError.get(Integer.MAX_VALUE)));
                            demographicsError.remove(Integer.MAX_VALUE);
                            fileUpload.add(temp);
                        }
                        if (fileExist != null && fileExist.contains("location.csv")) {
                            locationError = UploadDAO.updateLocation(outputDirectory + File.separator + "location.csv");
                            JsonObject temp = new JsonObject();
                            temp.addProperty("location.csv", Integer.parseInt(locationError.get(Integer.MAX_VALUE)));
                            locationError.remove(Integer.MAX_VALUE);
                            fileUpload.add(temp);
                        }

                        // if there is no error from demographics.csv or location.csv, return success message
                        if (demographicsError.isEmpty() && locationError.isEmpty()) {
                            ans.addProperty("status", "success");
                            ans.add("num-record-loaded", fileUpload);
                        // if there is error from either demographics.csv or location.csv,
                        // retrieve errorMessage and add it to json
                        } else {
                            ans.addProperty("status", "error");
                            ans.add("num-record-loaded", fileUpload);
                            JsonArray error = new JsonArray();
                            
                            
                            // demographics.csv file errors
                            // Retrieving the keys so as to loop through them
                            Set<Integer> demographicsKey = demographicsError.keySet();
                            for (Integer key : demographicsKey) {
                                // for each key (row number), create new jsonObject tempJson
                                // Get the error associated -> value
                                // Split the value into an array format and add them into jsonarray
                                // add the jsonarray to tempJson and then merge it back to the main return
                                JsonObject tempJson = new JsonObject();
                                tempJson.addProperty("file", "demographics.csv");
                                tempJson.addProperty("line", key);

                                String err = demographicsError.get(key);
                                String[] errors = err.split(",");
                                JsonArray erro = new JsonArray();
                                for (String msg : errors) {
                                    erro.add(msg);
                                }
                                tempJson.add("messages", erro);
                                error.add(tempJson);
                            }

                            // Location.csv file errors
                            // Retrieving the keys so as to loop through them
                            Set<Integer> locationKeys = locationError.keySet();
                            for (Integer key : locationKeys) {
                                // for each key (row number), create new jsonObject tempJson
                                // Get the error associated -> value
                                // Split the value into an array format and add them into jsonarray
                                // add the jsonarray to tempJson and then merge it back to the main return
                                JsonObject tempJson = new JsonObject();
                                tempJson.addProperty("file", "location.csv");
                                tempJson.addProperty("line", key);

                                String err = locationError.get(key);
                                String[] errors = err.split(",");
                                JsonArray erro = new JsonArray();
                                for (String msg : errors) {
                                    erro.add(msg);
                                }
                                tempJson.add("messages", erro);
                                error.add(tempJson);
                            }
                            ans.add("error", error);

                        }
                    }
                }

                // Deleting temp file so as to prevent issue with reuploading
                File dir = new File(outputDirectory);
                if (dir.exists() && dir.isDirectory()) {
                    File[] allFiles = dir.listFiles();
                    if (null != files) {
                        for (File file : allFiles) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (UploadException e) {
            //send error messsage if unable to upload to uploadbeans
            out.println("error, Unable to upload. Please try again later");
        }
        
        // Returning the json output we created in a pretty print format
        out.println(gson.toJson(ans));
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
