package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javazoom.upload.MultipartFormDataRequest;
import javazoom.upload.UploadBean;
import javazoom.upload.UploadException;
import javazoom.upload.UploadFile;
import model.SharedSecretManager;
import model.UploadDAO;

@WebServlet(urlPatterns = {"/json/bootstrap"})
public class bootstrapUpload extends HttpServlet {

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
        //creates a new gson object
        JsonObject ans = new JsonObject();
        //by instantiating a new factory object, set pretty printing, then calling the create method
        PrintWriter out = response.getWriter();
        //creates a new json object for printing the desired json output
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //get token from request
        String token = request.getParameter("token");
        boolean tokenValid = true;
        // Token checking
        if (token == null) {
            tokenValid = false;
        } else if (token.equals("")) {
            tokenValid = false;
        } else if (SharedSecretManager.verifyUser(token)) {
            tokenValid = false;
        }
        

        // set to "if(tokenValid)" to debug without token, default "if(!tokenValid)"
        if (tokenValid) {
            ans.addProperty("status", "error");
            JsonArray message = new JsonArray();
            message.add("invalid token");
            ans.add("messages", message);
        } else {
            HttpSession session = request.getSession();
            UploadBean upBean = new UploadBean();
            HashMap<Integer, String> demographicsError = new HashMap<>();
            HashMap<Integer, String> locationLookupError = new HashMap<>();
            HashMap<Integer, String> locationError = new HashMap<>();
            JsonArray fileUpload = new JsonArray();
            

            try {
                ServletContext servletContext = this.getServletConfig().getServletContext();
                //Pathname to a scratch directory to be provided by this Context for temporary read-write use by servlets within the associated web application
                File directory = (File) servletContext.getAttribute("javax.servlet.context.tempdir"); 
                String outputDirectory = "" + directory; //String format of directory

                upBean.setFolderstore(outputDirectory); //set upBean output directory
                Long size = Long.parseLong("8589934592"); //the size limit of the file uploads
                upBean.setFilesizelimit(size);
                if (MultipartFormDataRequest.isMultipartFormData(request)) {
                    //Uses MultipartFormDataRequest to parse the HTTP request.
                    MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request); //specialized version of request object to interpret the data
                    

                    Hashtable files = multipartRequest.getFiles(); //get the files sent over, hastable is the older version of hashmap
                    if ((files != null) && (!files.isEmpty())) {
                        UploadFile file = (UploadFile) files.get("uploadfile"); //get the files from bootstrapinitialize
                        if (file != null && file.getFileSize() > 0 && file.getFileName() != null) {
                            String fileName = file.getFileName();
                            String contentType = file.getContentType(); //Get the file type
                            String filePath = outputDirectory + File.separator + fileName; //get the file path 
                            

                            if (contentType.equals("application/x-zip-compressed")) { //if it is a zip file
                                upBean.store(multipartRequest, "uploadfile"); //save to directory
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
                                
                                
                            //if location.csv or location-lookup.csv or demographics.csv
                            } else if (UploadDAO.checkFileName(fileName) != null && UploadDAO.checkFileName(fileName).length() > 0) { upBean.store(multipartRequest, "uploadfile"); //save to directory
                                JsonObject temp = new JsonObject();
                                switch (fileName) {
                                    case "demographics.csv":
                                        demographicsError = UploadDAO.readDemographics(outputDirectory + File.separator + "demographics.csv");
                                        // using integer.max_value to pass the number of lines processed
                                        temp.addProperty("demographics.csv", Integer.parseInt(demographicsError.get(Integer.MAX_VALUE)));
                                        demographicsError.remove(Integer.MAX_VALUE);
                                        fileUpload.add(temp);
                                        break;
                                    case "location-lookup.csv":
                                        locationLookupError = UploadDAO.readLookup(outputDirectory + File.separator + "location-lookup.csv");
                                        // using integer.max_value to pass the number of lines processed
                                        temp.addProperty("location-lookup.csv", Integer.parseInt(locationLookupError.get(Integer.MAX_VALUE)));
                                        locationLookupError.remove(Integer.MAX_VALUE);
                                        fileUpload.add(temp);
                                        break;
                                    case "location.csv":
                                        locationError = UploadDAO.readLocation(outputDirectory + File.separator + "location.csv");
                                        // using integer.max_value to pass the number of lines processed
                                        temp.addProperty("location.csv", Integer.parseInt(locationError.get(Integer.MAX_VALUE)));
                                        locationError.remove(Integer.MAX_VALUE);
                                        fileUpload.add(temp);
                                        break;
                                    default:
                                        break;
                                }
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
                                Map<Integer, String> demographicsErrorSorted = new TreeMap<>(demographicsError);
                                for (Map.Entry<Integer, String> temp : demographicsErrorSorted.entrySet()) {
                                    int key = temp.getKey();
                                    String value = temp.getValue();
                                    JsonObject tempJson = new JsonObject();
                                    tempJson.addProperty("file", "demographics.csv");
                                    tempJson.addProperty("line", key);
                                    //Adding row errors into arraylist so they could be sorted
                                    //After they are sorted, they are added into JSONarray and commited
                                    ArrayList<String> err = new ArrayList<>();
                                    err.addAll(Arrays.asList(value.split(",")));
                                    Collections.sort(err);
                                    JsonArray erro = new JsonArray();
                                    for (String msg : err) {
                                        erro.add(msg);
                                    }
                                    tempJson.add("message", erro);
                                    error.add(tempJson);
                                }
                                

                                // Location-lookup.csv file errors
                                Map<Integer, String> locationLookupErrorSorted = new TreeMap<>(demographicsError);
                                for (Map.Entry<Integer, String> temp : locationLookupErrorSorted.entrySet()) {
                                    int key = temp.getKey();
                                    String value = temp.getValue();
                                    JsonObject tempJson = new JsonObject();
                                    tempJson.addProperty("file", "location-lookup.csv");
                                    tempJson.addProperty("line", key);
                                    //Adding row errors into arraylist so they could be sorted
                                    //After they are sorted, they are added into JSONarray and commited
                                    ArrayList<String> err = new ArrayList<>();
                                    err.addAll(Arrays.asList(value.split(",")));
                                    Collections.sort(err);
                                    JsonArray erro = new JsonArray();
                                    for (String msg : err) {
                                        erro.add(msg);
                                    }
                                    tempJson.add("message", erro);
                                    error.add(tempJson);
                                }
                                

                                // Location.csv file errors
                                Map<Integer, String> locationErrorSored = new TreeMap<>(locationError);
                                for (Map.Entry<Integer, String> temp : locationErrorSored.entrySet()) {
                                    int key = temp.getKey();
                                    String value = temp.getValue();
                                    JsonObject tempJson = new JsonObject();
                                    tempJson.addProperty("file", "location.csv");
                                    tempJson.addProperty("line", key);
                                    //Adding row errors into arraylist so they could be sorted
                                    //After they are sorted, they are added into JSONarray and commited
                                    ArrayList<String> err = new ArrayList<>();
                                    err.addAll(Arrays.asList(value.split(",")));
                                    Collections.sort(err);
                                    JsonArray erro = new JsonArray();
                                    for (String msg : err) {
                                        erro.add(msg);
                                    }
                                    tempJson.add("message", erro);
                                    error.add(tempJson);
                                }
                                ans.add("error", error);
                            }
                            file = null;
                        }
                    }
                }
                
                
                // Deleting temp file so as to prevent issue with reuploading
                if (outputDirectory == null) {
                    return;
                }
                File dir = new File(outputDirectory);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (null != files) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                }
            } catch (UploadException e) {
                out.println("error, Unable to upload. Please try again later");
            }
        }
        out.println(gson.toJson(ans));
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
