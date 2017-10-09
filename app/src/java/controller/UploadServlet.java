package controller;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javazoom.upload.*;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import model.UploadDAO;

public class UploadServlet extends HttpServlet implements java.io.Serializable {

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
        HttpSession session = request.getSession();
        UploadBean upBean = new UploadBean();
        String success = "";
        String uploadType = "";
        ArrayList<String[]> demographicsError = new ArrayList<>();
        //ArrayList<List<String>> locationError = new ArrayList<List<String>>();
        //ArrayList<List<String>> lookupError = new ArrayList<List<String>>();        
        try {
            ServletContext servletContext = this.getServletConfig().getServletContext();
            File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir"); //Pathname to a scratch directory to be provided by this Context for temporary read-write use by servlets within the associated web application
            String outputDirectory = "" + repository;

            upBean.setFolderstore(outputDirectory); //the location of where documents will be stored
            Long size = Long.parseLong("8589934592"); //the size limit of the file uploads
            upBean.setFilesizelimit(size);

            if (MultipartFormDataRequest.isMultipartFormData(request)) {
                //Uses MultipartFormDataRequest to parse the HTTP request.
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request); //specialized version of request object to interpret the data
                uploadType = multipartRequest.getParameter("uploadType"); //either intialize or update              
                String todo = multipartRequest.getParameter("todo");

                if (todo.equalsIgnoreCase("upload")) {
                    Hashtable files = multipartRequest.getFiles(); //get the files sent over, hastable is the older version of hashmap
                    if ((files != null) && (!files.isEmpty())) {
                        UploadFile file = (UploadFile) files.get("uploadfile"); //get the files from bootstrapinitialize or bootstrapupdate
                        if (file != null && file.getFileSize() > 0 && file.getFileName() != null) {
                            String fileName = file.getFileName();
                            String contentType = file.getContentType(); //Get the file type
                            String filePath = outputDirectory + File.separator + fileName; //get the zip file directory 

                            if (contentType.equals("application/x-zip-compressed")) { //if it is a zip file
                                upBean.store(multipartRequest, "uploadfile"); //save to directory

                                String fileExist = UploadDAO.unzip(filePath, outputDirectory); //unzip the files in the zip and save into the directory    

                                if (fileExist != null && fileExist.contains("demographics.csv")) {
                                        UploadDAO.demographicsImport1(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport2(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport3(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport4(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport5(outputDirectory + File.separator + "demographics.csv");
                                    demographicsError = UploadDAO.demographicsNilChecking();
                                } else if (fileExist != null && fileExist.contains("location.csv")) {
                                    //locationError = UploadDAO.demographicsImport(outputDirectory + File.separator + "location.csv");
                                } else if (fileExist != null && fileExist.contains("location-lookup.csv")) {
                                    //lookupError = UploadDAO.demographicsImport(outputDirectory + File.separator + "location-lookup.csv");
                                }

                                success = "Uploaded file: " + fileName + " (" + file.getFileSize() + " bytes)";
                                session.setAttribute("success", success); //send success messsage                                

                            } else if (UploadDAO.checkFileName(fileName) != null && UploadDAO.checkFileName(fileName).length() > 0) { //if location.csv or location-lookup.csv or demographics.csv
                                upBean.store(multipartRequest, "uploadfile"); //save to directory
                                switch (fileName) {
                                    case "demographics.csv":
                                        UploadDAO.readCSV(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport1(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport2(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport3(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport4(outputDirectory + File.separator + "demographics.csv");
                                        UploadDAO.demographicsImport5(outputDirectory + File.separator + "demographics.csv");
                                        demographicsError = UploadDAO.demographicsNilChecking();
                                        break;
                                //locationError = UploadDAO.demographicsImport(outputDirectory + File.separator + "location.csv");
                                    case "location.csv":
                                        break;
                                //lookupError = UploadDAO.demographicsImport(outputDirectory + File.separator + "location-lookup.csv");
                                    case "location-lookup.csv":
                                        break;
                                    default:
                                        break;
                                }

                                success = "Uploaded file: " + fileName + " (" + file.getFileSize() + " bytes)";
                                session.setAttribute("success", success); //send success messsage         

                            } else {
                                session.setAttribute("error", "Wrong file name or type"); //send error messsage                                  
                            }
                        } else {
                            session.setAttribute("error", "No uploaded files"); //send error messsage                     
                        }
                    } else {
                        session.setAttribute("error", "No uploaded files"); //send error messsage                   
                    }
                }
            }
        } catch (UploadException e) {
            session.setAttribute("error", "Unable to upload. Please try again later"); //send error messsage
        }
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
