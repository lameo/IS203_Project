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

@WebServlet(urlPatterns = {"/json/bootstrap"})
public class BootstrapUpload extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //creates a new gson object
        JsonObject ans = new JsonObject();

        //by instantiating a new factory object, set pretty printing, then calling the create method
        PrintWriter out = response.getWriter();

        //creates a new json object for printing the desired json output
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray errMsg = new JsonArray();

        UploadBean upBean = new UploadBean();
        HashMap<Integer, String> demographicsError = new HashMap<>();
        HashMap<Integer, String> locationLookupError = new HashMap<>();
        HashMap<Integer, String> locationError = new HashMap<>();
        JsonArray fileUpload = new JsonArray();

        try {
            if (MultipartFormDataRequest.isMultipartFormData(request)) {
                //Uses MultipartFormDataRequest to parse the HTTP request.
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request); //specialized version of request object to interpret the data

                //get token from request
                String token = multipartRequest.getParameter("token");

                // Token checking
                if (token == null) {
                    errMsg.add("missing token");
                    ans.addProperty("status", "error");
                    ans.add("messages", errMsg);
                    out.println(gson.toJson(ans));
                    out.close(); //close PrintWriter
                    return;
                }

                if (token.isEmpty()) {
                    errMsg.add("blank token");
                    ans.addProperty("status", "error");
                    ans.add("messages", errMsg);
                    out.println(gson.toJson(ans));
                    out.close(); //close PrintWriter
                    return;
                }

                //print out all the error with null or empty string that is required but the user did not enter 
                if (!SharedSecretManager.verifyAdmin(token)) { //verify the user - if the user is not verified
                    errMsg.add("invalid token");
                    ans.addProperty("status", "error");
                    ans.add("messages", errMsg);
                    out.println(gson.toJson(ans));
                    out.close(); //close PrintWriter
                    return;
                }

                // if token is valid, continue processing
                ServletContext servletContext = this.getServletConfig().getServletContext();

                //Pathname to a scratch directory to be provided by this Context for temporary read-write use by servlets within the associated web application
                File directory = (File) servletContext.getAttribute("javax.servlet.context.tempdir");

                //String format of directory
                String outputDirectory = "" + directory;

                upBean.setFolderstore(outputDirectory); //set upBean output directory
                Long size = Long.parseLong("8589934592"); //the size limit of the file uploads
                upBean.setFilesizelimit(size);

                Hashtable files = multipartRequest.getFiles(); //get the files sent over, hastable is the older version of hashmap

                if ((files != null) && (!files.isEmpty())) {
                    UploadFile file = (UploadFile) files.get("bootstrap-file"); //get the files from bootstrapinitialize
                    if (file != null && file.getFileSize() > 0 && file.getFileName() != null) {
                        String fileName = file.getFileName();
                        String filePath = outputDirectory + File.separator + fileName; //get the file path 

                        upBean.store(multipartRequest, "bootstrap-file"); //save to directory
                        String fileExist = UploadDAO.unzip(filePath, outputDirectory); //unzip the files in the zip and save into the directory

                        if (fileExist != null && fileExist.contains("demographics.csv")) {
                            demographicsError = UploadDAO.readDemographics(outputDirectory + File.separator + "demographics.csv");
                            JsonObject temp = new JsonObject();
                            // using integer.max_value to pass the number of lines processed
                            temp.addProperty("demographics.csv", Integer.parseInt(demographicsError.get(Integer.MAX_VALUE)));
                            demographicsError.remove(Integer.MAX_VALUE);
                            fileUpload.add(temp);
                        }
                        if (fileExist != null && fileExist.contains("location-lookup.csv")) {
                            locationLookupError = UploadDAO.readLookup(outputDirectory + File.separator + "location-lookup.csv");
                            JsonObject temp = new JsonObject();
                            // using integer.max_value to pass the number of lines processed
                            temp.addProperty("location-lookup.csv", Integer.parseInt(locationLookupError.get(Integer.MAX_VALUE)));
                            locationLookupError.remove(Integer.MAX_VALUE);
                            fileUpload.add(temp);
                        }
                        if (fileExist != null && fileExist.contains("location.csv")) {
                            locationError = UploadDAO.readLocation(outputDirectory + File.separator + "location.csv");
                            JsonObject temp = new JsonObject();
                            // using integer.max_value to pass the number of lines processed
                            temp.addProperty("location.csv", Integer.parseInt(locationError.get(Integer.MAX_VALUE)));
                            locationError.remove(Integer.MAX_VALUE);
                            fileUpload.add(temp);
                        }

                        if (demographicsError.isEmpty() && locationError.isEmpty() && locationLookupError.isEmpty()) {
                            // if successful
                            ans.addProperty("status", "success");
                            ans.add("num-record-loaded", fileUpload);
                        } else {
                            // if contains error message
                            ans.addProperty("status", "error");
                            ans.add("num-record-loaded", fileUpload);
                            JsonArray error = new JsonArray();

                            // demographics.csv file errors
                            Set<Integer> demographicsKey = demographicsError.keySet();
                            for (Integer key : demographicsKey) {
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

                            // Location-lookup.csv file errors
                            Set<Integer> lookupKeys = locationLookupError.keySet();
                            for (Integer key : lookupKeys) {
                                JsonObject tempJson = new JsonObject();
                                tempJson.addProperty("file", "location-lookup.csv");
                                tempJson.addProperty("line", key);

                                String err = locationLookupError.get(key);
                                String[] errors = err.split(",");
                                JsonArray erro = new JsonArray();
                                for (String msg : errors) {
                                    erro.add(msg);
                                }
                                tempJson.add("messages", erro);
                                error.add(tempJson);
                            }

                            // Location.csv file errors
                            Set<Integer> locationKeys = locationError.keySet();
                            for (Integer key : locationKeys) {
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
                        file = null;
                    }
                }

                // Deleting temp file so as to prevent issue with reuploading
                if (outputDirectory == null) {
                    return;
                }
                File dir = new File(outputDirectory);
                if (dir.exists() && dir.isDirectory()) {
                    File[] allFiles = dir.listFiles();
                    if (null != allFiles) {
                        for (File file : allFiles) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (UploadException e) {
            out.println("error, Unable to upload. Please try again later");
        }

        out.println(gson.toJson(ans));

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
