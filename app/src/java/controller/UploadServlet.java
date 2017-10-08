package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javazoom.upload.*;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import model.UploadDAO;


public class UploadServlet extends HttpServlet implements java.io.Serializable{

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {      
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();        
        HttpSession session = request.getSession();
        UploadBean upBean = new UploadBean();
        String success = "";
        String uploadType = "";     
        try{
            //DiskFileItemFactory factory = new DiskFileItemFactory();   
            //ServletContext servletContext = this.getServletConfig().getServletContext();
            //File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir"); 
            String outputDirectory = "d:/testt/dontupload"; //repository
            
            upBean.setFolderstore(outputDirectory); //the location of where documents will be stored
            Long size = Long.parseLong("8589934592"); //the size limit of the file uploads
            upBean.setFilesizelimit(size);
        
            if (MultipartFormDataRequest.isMultipartFormData(request)){
                //Uses MultipartFormDataRequest to parse the HTTP request.
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request); //specialized version of request object to interpret the data
                uploadType = multipartRequest.getParameter("uploadType"); //either intialize or update              
                String todo = multipartRequest.getParameter("todo");
                
                if(todo.equalsIgnoreCase("upload")){
                    Hashtable files = multipartRequest.getFiles(); //get the files sent over, hastable is the older version of hashmap
                    if ((files != null) && (!files.isEmpty())){
                        UploadFile file = (UploadFile) files.get("uploadfile"); //get the files from bootstrapinitialize or bootstrapupdate
                        if (file != null && file.getFileSize()>0 && file.getFileName()!=null) {
                            String contentType = file.getContentType(); //Get the file type
                            String filePath = outputDirectory + File.separator + file.getFileName(); //get the zip file directory 
                            
                            if(contentType.equals("application/x-zip-compressed")){ //if it is a zip file
                                upBean.store(multipartRequest, "uploadfile"); //uses the bean now to store specified by the properties                                      
                                out.print(UploadDAO.unzip(filePath, outputDirectory)); //unzip the files in the zip and save into the directory    
                                
                                success = "Uploaded file: " + file.getFileName()+ " (" + file.getFileSize() + " bytes)";
                                session.setAttribute("success", success); //send success messsage                                

                                upBean.store(multipartRequest, "uploadfile"); //uses the bean now to store specified by the properties                                       
                            } else if(UploadDAO.checkFileName(file.getFileName())){ //if location.csv or location-lookup.csv or demographics.csv
                                upBean.store(multipartRequest, "uploadfile"); //uses the bean now to store specified by the properties                                  
                                success = "Uploaded file: " + file.getFileName()+ " (" + file.getFileSize() + " bytes)";
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
        } catch (UploadException e){
            session.setAttribute("error", "Unable to upload. Please try again later"); //send error messsage
        }    
        
        if(uploadType.equals("update")){ //check where did the request come from and send back to the respective place
            //response.sendRedirect("BootstrapUpdate.jsp");                  
        } else {
            //response.sendRedirect("BootstrapInitialize.jsp");     
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
        return "This is a Upload Servlet that processes uploading of data by admin";
    }// </editor-fold>

}
