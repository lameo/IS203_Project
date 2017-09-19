package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javazoom.upload.*;
import java.util.*;
import javax.servlet.RequestDispatcher;

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
        UploadBean upBean = new UploadBean();
        RequestDispatcher view = null;   
        String success = null;
        
        try{
            upBean.setFolderstore("d:/testt/dontupload"); //the location of where documents will be stored, changing to database later
            Long size = Long.parseLong("8589934592"); //the size limit of the file uploads
            upBean.setFilesizelimit(size);

            if (MultipartFormDataRequest.isMultipartFormData(request)){
                //Uses MultipartFormDataRequest to parse the HTTP request.
                MultipartFormDataRequest multipartRequest = new MultipartFormDataRequest(request);
                String todo = multipartRequest.getParameter("todo");
                
                if(todo.equalsIgnoreCase("upload")){
                    Hashtable files = multipartRequest.getFiles();
                    if ((files != null) && (!files.isEmpty())){
                        UploadFile file = (UploadFile) files.get("uploadfile");
                        if (file != null && file.getFileSize()>0 && file.getFileName()!=null) {
                            success = "Uploaded file: " + file.getFileName()+ " (" + file.getFileSize() + " bytes)" + "<br>Content Type : " + file.getContentType();
                            request.setAttribute("success", success); //send error messsage to adminPage.jsp                                  
                            
                            // Uses the bean now to store specified by the properties
                            upBean.store(multipartRequest, "uploadfile");
                        } else {
                            request.setAttribute("error", "No uploaded files"); //send error messsage to adminPage.jsp                      
                        }
                    } else {
                        request.setAttribute("error", "No uploaded files"); //send error messsage to adminPage.jsp                      
                    }
                }
                view = request.getRequestDispatcher("adminPage.jsp"); //send back to adminPage but same URL
                view.forward(request, response);                 
            }
        } catch (UploadException e){
            request.setAttribute("error", "Unable to upload. Please try again later"); //send error messsage to adminPage.jsp
            view = request.getRequestDispatcher("adminPage.jsp"); //send back to adminPage but same URL
            view.forward(request, response);                     
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
