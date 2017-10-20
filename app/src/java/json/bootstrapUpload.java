package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        //creates a new gson object
        //by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //creats a new json object for printing the desired json output
        ArrayList<String> errors = new ArrayList<>();
        /*
        //get token from request
        String token = request.getParameter("token"); 
        // Token checking
        if(token==null){
            errors.add("missing token");
        }else if(token.equals("")){
            errors.add("blank token");
        }else if(SharedSecretManager.verifyUser(token)){
            errors.add("invalid token");
        }*/

        HttpSession session = request.getSession();
        UploadBean upBean = new UploadBean();
        String success = "";
        String uploadType = "";
        HashMap<Integer, String> demographicsError = new HashMap<>();
        HashMap<Integer, String> locationLookupError = new HashMap<>();
        HashMap<Integer, String> locationError = new HashMap<>();

        JsonObject fileUpload = new JsonObject();

        try {
            ServletContext servletContext = this.getServletConfig().getServletContext();
            File directory = (File) servletContext.getAttribute("javax.servlet.context.tempdir"); //Pathname to a scratch directory to be provided by this Context for temporary read-write use by servlets within the associated web application
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
                                fileUpload.addProperty("demographics.csv", demographicsError.get(Integer.MAX_VALUE));
                                demographicsError.remove(Integer.MAX_VALUE);
                            }
                            if (fileExist != null && fileExist.contains("location-lookup.csv")) {
                                locationLookupError = UploadDAO.readLookup(outputDirectory + File.separator + "location-lookup.csv");
                                fileUpload.addProperty("location-lookup.csv", locationLookupError.get(Integer.MAX_VALUE));
                                locationLookupError.remove(Integer.MAX_VALUE);
                            }
                            if (fileExist != null && fileExist.contains("location.csv")) {
                                locationError = UploadDAO.readLocation(outputDirectory + File.separator + "location.csv");
                                fileUpload.addProperty("location.csv", locationError.get(Integer.MAX_VALUE));
                                locationError.remove(Integer.MAX_VALUE);
                            }
                            if (fileExist == null || !(fileExist.contains("demographics.csv") && fileExist.contains("location-lookup.csv") && fileExist.contains("location.csv"))) {
                                session.setAttribute("error", "Wrong file name or type"); //send error messsage        
                            }

                        } else if (UploadDAO.checkFileName(fileName) != null && UploadDAO.checkFileName(fileName).length() > 0) { //if location.csv or location-lookup.csv or demographics.csv
                            upBean.store(multipartRequest, "uploadfile"); //save to directory
                            switch (fileName) {
                                case "demographics.csv":
                                    demographicsError = UploadDAO.readDemographics(outputDirectory + File.separator + "demographics.csv");
                                    fileUpload.addProperty("demographics.csv", demographicsError.get(Integer.MAX_VALUE));
                                    demographicsError.remove(Integer.MAX_VALUE);
                                    break;
                                case "location-lookup.csv":
                                    locationLookupError = UploadDAO.readLookup(outputDirectory + File.separator + "location-lookup.csv");
                                    fileUpload.addProperty("location-lookup.csv", locationLookupError.get(Integer.MAX_VALUE));
                                    locationLookupError.remove(Integer.MAX_VALUE);
                                    break;
                                case "location.csv":
                                    locationError = UploadDAO.readLocation(outputDirectory + File.separator + "location.csv");
                                    fileUpload.addProperty("location.csv", locationError.get(Integer.MAX_VALUE));
                                    locationError.remove(Integer.MAX_VALUE);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            session.setAttribute("error", "Wrong file name or type"); //send error messsage                                  
                        }

                        if (demographicsError.isEmpty() && locationError.isEmpty() && locationLookupError.isEmpty()) {
                            // if successful
                            JsonObject ans = new JsonObject();
                            ans.addProperty("status", "success");
                            ans.add("num-record-loaded", fileUpload);
                            out.println(gson.toJson(ans));
                        } else {
                            // if contains error message
                            JsonObject ans = new JsonObject();
                            ans.addProperty("status", "error");
                            ans.add("num-record-loaded", fileUpload);
                            JsonArray error = new JsonArray();
                            for (Map.Entry<Integer, String> temp : demographicsError.entrySet()) {
                                int key = temp.getKey();
                                String value = temp.getValue();
                                JsonObject tempJson = new JsonObject();
                                tempJson.addProperty("file", "demographics.csv");
                                tempJson.addProperty("line",key);
                                String[] messages = value.split(",");
                                ArrayList<String> err = new ArrayList<>();
                                for(String msg : messages){
                                    err.add(msg);
                                }
                                Collections.sort(err);
                                tempJson.addProperty("message", err.toString());
                                error.add(tempJson);
                            }
                            for (Map.Entry<Integer, String> temp : locationLookupError.entrySet()) {
                                int key = temp.getKey();
                                String value = temp.getValue();
                                JsonObject tempJson = new JsonObject();
                                tempJson.addProperty("file", "locationLookupError.csv");
                                tempJson.addProperty("line",key);
                                String[] messages = value.split(",");
                                ArrayList<String> err = new ArrayList<>();
                                for(String msg : messages){
                                    err.add(msg);
                                }
                                Collections.sort(err);
                                tempJson.addProperty("message", err.toString());
                                error.add(tempJson);
                            }
                            for (Map.Entry<Integer, String> temp : locationError.entrySet()) {
                                int key = temp.getKey();
                                String value = temp.getValue();
                                JsonObject tempJson = new JsonObject();
                                tempJson.addProperty("file", "locationError.csv");
                                tempJson.addProperty("line",key);
                                String[] messages = value.split(",");
                                ArrayList<String> err = new ArrayList<>();
                                for(String msg : messages){
                                    err.add(msg);
                                }
                                Collections.sort(err);
                                tempJson.addProperty("message", err.toString());
                                error.add(tempJson);
                            }
                            ans.add("error", error);
                            out.println(gson.toJson(ans));
                        }
                        file = null;
                    }
                }
            }
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
            session.setAttribute("error", "Unable to upload. Please try again later"); //send error messsage
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
