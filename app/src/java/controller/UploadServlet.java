package controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Hashtable;
import javazoom.upload.*;
import java.util.HashMap;
import model.UploadDAO;
import java.io.File;

public class UploadServlet extends HttpServlet implements java.io.Serializable {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // Initializing upload function - allowing files to be sent from browser
        UploadBean upBean = new UploadBean();
        String success = "";
        
        // Initializing error messages to be return later on if any
        HashMap<Integer, String> locationLookupError = new HashMap<>();
        HashMap<Integer, String> demographicsError = new HashMap<>();
        HashMap<Integer, String> locationError = new HashMap<>();
        HashMap<String, String> processedLines = new HashMap<>();
        try {
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
            
            // if a file upload is detected 
            if (MultipartFormDataRequest.isMultipartFormData(request)) {
                // Uses MultipartFormDataRequest which is a special type of request to get the file
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request);

                // Saving the files being send over in an older form of hashmap
                Hashtable files = multipartRequest.getFiles();
                // If file exist and is not an empty csv file
                if ((files != null) && (!files.isEmpty())) {
                    // Get file from bootstrapinitialize.jsp
                    UploadFile file = (UploadFile) files.get("uploadfile");
                    // if the file is not null, or empty size (means no data) & has a proper name (cos need to compare name later)
                    if (file != null && file.getFileSize() > 0 && file.getFileName() != null) {
                        // Get file type (eg .csv/.doc/.xlsx)
                        String contentType = file.getContentType();
                        // Get file name
                        String fileName = file.getFileName();
                        // Get the absolute path by adding the directory path to file name
                        String filePath = outputDirectory + File.separator + fileName;

                        // case 1: if it is a zip file
                        if (contentType.equals("application/x-zip-compressed")|| contentType.equals("application/zip")) {
                            // save it to temp directory
                            upBean.store(multipartRequest, "uploadfile");

                            // Unzip the file and save the contents into the temp directory
                            String fileExist = UploadDAO.unzip(filePath, outputDirectory);
                            // if file is invalid, send error message
                            if (fileExist == null || fileExist.length() <= 0) {
                                session.setAttribute("error", "Wrong file name or type");
                            } else {
                                // Check what file is it
                                // perform respective upload function
                                // Retrieve number of rows successfully processed kept in a hash with Interger.max_value as key
                                // remove away Integer.max_value key from errorMessage to get the proper errorMessage (if any)

                                if (fileExist.contains("location-lookup.csv")) {
                                    locationLookupError = UploadDAO.readLookup(outputDirectory + File.separator + "location-lookup.csv");
                                    processedLines.put("location-lookup.csv", locationLookupError.get(Integer.MAX_VALUE));
                                    locationLookupError.remove(Integer.MAX_VALUE);
                                }
                                if (fileExist.contains("demographics.csv")) {
                                    demographicsError = UploadDAO.readDemographics(outputDirectory + File.separator + "demographics.csv");
                                    processedLines.put("demographics.csv", demographicsError.get(Integer.MAX_VALUE));
                                    demographicsError.remove(Integer.MAX_VALUE);
                                }
                                if (fileExist.contains("location.csv")) {
                                    locationError = UploadDAO.readLocation(outputDirectory + File.separator + "location.csv");
                                    processedLines.put("location.csv", locationError.get(Integer.MAX_VALUE));
                                    locationError.remove(Integer.MAX_VALUE);
                                }
                            }
                            
                            // If no errors were detected, send success message
                            if (demographicsError.isEmpty() && locationError.isEmpty() && locationLookupError.isEmpty()) {
                                success = "Uploaded file: " + fileName + " (" + file.getFileSize() + " bytes)";
                                session.setAttribute("success", success); 
                            // If there was error, send error message
                            } else {
                                session.setAttribute("locationLookupError", locationLookupError);
                                session.setAttribute("demographicsError", demographicsError);
                                session.setAttribute("locationError", locationError);
                            }
                            
                        // Case 2: if it is not a zip file, but contains a valid name that is not null or length 0
                        } else if (UploadDAO.checkFileName(fileName) != null && UploadDAO.checkFileName(fileName).length() > 0) {
                            // Save it to the temp directory
                            upBean.store(multipartRequest, "uploadfile");
                            // Check what file is it
                            // perform respective upload function
                            // Retrieve number of rows successfully processed kept in a hash with Interger.max_value as key
                            // remove away Integer.max_value key from errorMessage to get the proper errorMessage (if any)
                            switch (fileName) {
                                case "location-lookup.csv":
                                    locationLookupError = UploadDAO.readLookup(outputDirectory + File.separator + "location-lookup.csv");
                                    processedLines.put("location-lookup.csv", locationLookupError.get(Integer.MAX_VALUE));
                                    locationLookupError.remove(Integer.MAX_VALUE);
                                    break;
                                case "demographics.csv":
                                    demographicsError = UploadDAO.readDemographics(outputDirectory + File.separator + "demographics.csv");
                                    processedLines.put("demographics.csv", demographicsError.get(Integer.MAX_VALUE));
                                    demographicsError.remove(Integer.MAX_VALUE);
                                    break;
                                case "location.csv":
                                    locationError = UploadDAO.readLocation(outputDirectory + File.separator + "location.csv");
                                    processedLines.put("location.csv", locationError.get(Integer.MAX_VALUE));
                                    locationError.remove(Integer.MAX_VALUE);
                                    break;
                                default:
                                    // if file does not have any valid name, send error message
                                    session.setAttribute("error", "Wrong file name or type");
                                    break;
                            }
                            
                            // If no errors were detected, send success message
                            if (demographicsError.isEmpty() && locationError.isEmpty() && locationLookupError.isEmpty()) {
                                success = "Uploaded file: " + fileName + " (" + file.getFileSize() + " bytes)";
                                session.setAttribute("success", success);
                            // If there was error, send error message
                            } else {
                                session.setAttribute("locationLookupError", locationLookupError);
                                session.setAttribute("demographicsError", demographicsError);
                                session.setAttribute("locationError", locationError);
                            }

                        // Case 3: not zip file or files that are valid
                        } else {
                            //send error messsage         
                            session.setAttribute("error", "Wrong file name or type");                          
                        }
                        
                        // Save line processed to session (or nth for case 3)
                        session.setAttribute("processedLines", processedLines); //send lines processed
                        // Invalidating the file to prevent reupload bugs
                        file = null;
                    } else {
                        // if the file was invalid before we even check whether it was zip, send error message
                        session.setAttribute("error", "No uploaded files");
                    }
                } else {
                    // if the hashtable returned was invalid before we even check whether it was zip, send error message
                    session.setAttribute("error", "No uploaded files");
                }
            }
            
            /*if (outputDirectory == null) {
                return;
            }*/
            
            // Deleteing the whole directory so if user were to upload
            // a diff file under the same name, windows will not try to
            // save memory and give the old version of file back
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
            //send error messsage
            session.setAttribute("error", "Unable to upload. Please try again later"); //send error messsage
        }
        
        // Goes back to the webpage after all files is processed
        // Now with errorMessage/number of line processed saved in session
        response.sendRedirect("BootstrapInitialize.jsp");
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
        return "This is a Upload Servlet that processes uploading of data by admin";
    }// </editor-fold>

}
