package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javazoom.upload.*;
import java.util.*;
import javax.servlet.http.HttpSession;

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
        HttpSession session = request.getSession();
        UploadBean upBean = new UploadBean();
        String success = "";
        String uploadType = "";     
        try{
            upBean.setFolderstore("d:/testt/dontupload"); //the location of where documents will be stored, changing to database later
            Long size = Long.parseLong("8589934592"); //the size limit of the file uploads
            upBean.setFilesizelimit(size);

            if (MultipartFormDataRequest.isMultipartFormData(request)){
                //Uses MultipartFormDataRequest to parse the HTTP request.
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request);
                uploadType = multipartRequest.getParameter("uploadType"); //either intialize or update              
                String todo = multipartRequest.getParameter("todo");
                
                if(todo.equalsIgnoreCase("upload")){
                    Hashtable files = multipartRequest.getFiles();
                    if ((files != null) && (!files.isEmpty())){
                        UploadFile file = (UploadFile) files.get("uploadfile");
                        if (file != null && file.getFileSize()>0 && file.getFileName()!=null) {
                            success = "Uploaded file: " + file.getFileName()+ " (" + file.getFileSize() + " bytes)" + "<br>Content Type : " + file.getContentType();
                            session.setAttribute("success", success); //send error messsage to BootstrapInitialize.jsp                                  
                            
                            // Uses the bean now to store specified by the properties
                            upBean.store(multipartRequest, "uploadfile");
                        } else {
                            session.setAttribute("error", "No uploaded files"); //send error messsage to BootstrapInitialize.jsp                      
                        }
                    } else {
                        session.setAttribute("error", "No uploaded files"); //send error messsage to BootstrapInitialize.jsp                      
                    }
                }            
            }
        } catch (UploadException e){
            session.setAttribute("error", "Unable to upload. Please try again later"); //send error messsage to BootstrapInitialize.jsp
        }    
        if(uploadType.equals("update")){
            response.sendRedirect("BootstrapUpdate.jsp");                  
        } else {
            response.sendRedirect("BootstrapInitialize.jsp");     
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
