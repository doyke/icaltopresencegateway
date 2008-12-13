/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.columbia.voip.server.conf.ServerParameters;
import edu.columbia.voip.user.CalendarAccount;
import edu.columbia.voip.user.GatewayUser;
import java.io.*;
import java.net.Socket;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

/**
 *
 * @author milind
 */
public class addServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("check3");
        try {
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet addServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet addServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("check1");
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("check2");

        //Extract Data

        String uni = request.getParameter("uni");
        String icalid = request.getParameter("icalid");
        String icalpass = request.getParameter("icalpass");
        String icalurl = request.getParameter("icalurl");
        String icaluri = request.getParameter("icaluri");
        String icalport = request.getParameter("icalport");
        String ssl = request.getParameter("ssl");
        boolean sslVal = false;
        
        if(ssl.equals("Yes")){
            sslVal = true;
        }else{
            sslVal = false;
        }        

        addUser(uni, icalid, icalpass, icalurl, icaluri, icalport, ssl);
        
      
        CalendarAccount calAccount =  new CalendarAccount(icalid, icalpass.toCharArray(),icalurl, icaluri, Integer.parseInt(icalport), sslVal);        
        GatewayUser newUser = GatewayUser.createUser(uni, calAccount, null);        
        Socket socket = new Socket("127.0.0.1", ServerParameters.REGISTRATION_PORT);        
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());        
        oos.writeObject(newUser);
        oos.close(); 
        
       

        response.sendRedirect("success.jsp");





    }

    /** 
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void addUser(String uni, String icalid, String icalpass, String icalurl, String icaluri, String icalport, String ssl) {
        
        int sslVal;
        
        if(ssl.equals("Yes")){
            sslVal = 1;            
        }
        else{
            sslVal = 0;
        }
        
        Connection conn = null;

        try {

            String userName = "btuser";
            String passwd = "passwd";
            String url = "jdbc:mysql://localhost:3306/icalgateway";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, passwd);
            System.out.println("Database connection established");
            Statement s = conn.createStatement();
            String sqlquery=null;
            
            sqlquery = "INSERT INTO icalgateway.registrations (cuid, ical_userid, ical_passwd, ical_host, ical_uri, ical_port, ical_ssl) VALUES ('"+uni+"','"+icalid+"','"+icalpass+"','"+icalurl+"','"+icaluri+"','"+icalport+"','"+sslVal+"');";
            s.executeUpdate(sqlquery);
            
            s.close();
            conn.close();
            
            String url2 = "jdbc:mysql://128.59.18.182:3306/Presence";
            conn = DriverManager.getConnection(url2, userName, passwd);
            s = conn.createStatement();
            sqlquery = "INSERT INTO presence.login(user,password) VALUES ('"+uni+"','"+icalpass+"')";
            s.executeUpdate(sqlquery);
            s.close();
            
            

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection terminated");

                } catch (Exception e) { /* ignore close errors */ }
            }
        }




    }
}
