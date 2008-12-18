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
import javax.servlet.http.HttpSession;

/**
 *
 * @author milind
 */
public class addServlet extends HttpServlet 
{
    private static final int CODE_OK = -1;
    private static final int CODE_DB = 0;
    private static final int CODE_GATEWAY_THREAD = 1;
    private static final int CODE_TOMCAT = 2;
    
    private static final String[] ERROR_MESSAGES = { "Could not connect to either the registration or presence DB. Are both of the DBs running? Does the servlet have the correct network address / credentials?",
                                                     "Could not connect to gateway thread. Is there iCal to Presence gateway running?",
                                                     "There was a Java container error. Is your web server and container configured correctly?",
                                                     };

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String enteredUni = request.getParameter("enteredUni");
        
        Connection conn = null;
        int returnCode = -1;
        try {

            String userName = "btuser";
            String passwd = "passwd";
            String url = "jdbc:mysql://localhost:3306/icalgateway";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, passwd);
            System.out.println("Database connection established");
            Statement s = conn.createStatement();
            String sqlquery = null;

            sqlquery = "SELECT * FROM icalgateway.registrations where cuid='" + enteredUni + "';";
            s.executeQuery(sqlquery);
            ResultSet rs = s.getResultSet();
            if (rs.next())
                returnCode = 1;
            else
                returnCode = 2;
                    
            rs.close();
            s.close();
        } catch (IllegalAccessException e) {
            returnCode = -4;
        } catch (InstantiationException e) {
            returnCode = -3;
        } catch (ClassNotFoundException e) {
            returnCode = -2;
        } catch (SQLException e) {
            returnCode = -1;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
        }

        out.println(returnCode);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException
    {
        //Extract Data
        int ret = CODE_OK;
        String uni = request.getParameter("uni");
        String icalid = request.getParameter("icalid");
        String icalpass = request.getParameter("icalpass");
        String icalurl = request.getParameter("icalurl");
        String icaluri = request.getParameter("icaluri");
        String icalport = request.getParameter("icalport");
        String ssl = request.getParameter("ssl");
        boolean sslVal = ssl.equals("Yes");
        
        String userName = "btuser";
        String passwd = "passwd";
        String urlRegistration = "jdbc:mysql://localhost:3306/icalgateway";
        String urlPresence = "jdbc:mysql://128.59.18.182:3306/Presence";
        
        Connection connRegistration = null;
        Connection connPresence = null;
        try {
            // make sure we can connect to both databases before doing the INSERTs
            connRegistration = createDBConn(userName, passwd, urlRegistration);
            connPresence = createDBConn(userName, passwd, urlPresence);
            
            addUserRegistrationDB(connRegistration, uni, icalid, icalpass, icalurl, icaluri, icalport, ssl);
            addUserToPresenceDB(connPresence, uni, icalpass);
            pipeRegistrationToGateway(uni, icalid, icalpass, icalurl, icaluri, icalport, ssl);
        } 
        catch (IllegalAccessException e)    { ret = CODE_TOMCAT; } 
        catch (ClassNotFoundException e)    { ret = CODE_TOMCAT; } 
        catch (InstantiationException e)    { ret = CODE_TOMCAT; }
        catch (SQLException e)              { ret = CODE_DB; }
        catch (IOException e)               { ret = CODE_GATEWAY_THREAD; }
        finally {
            if (connRegistration != null) {
                try { connRegistration.close(); System.out.println("connRegistration database connection terminated"); } 
                catch (Exception e) {}
            }
            if (connPresence != null) {
                try { connPresence.close(); System.out.println("connPresence database connection terminated"); } 
                catch (Exception e) {}
            }
        }

        try {
            if (ret != CODE_OK)
            {
                HttpSession session = request.getSession(true);
                session.setAttribute("emessage", ERROR_MESSAGES[ret]);
                response.sendRedirect("error.jsp");
            }
            else
                response.sendRedirect("success.jsp");
        } catch (IOException e) { throw new ServletException(e); }
    }

    private Connection createDBConn(String userName, String passwd, String url) 
            throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException 
    {
        Connection conn = null;
        System.out.println("Database connection established");
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection(url, userName, passwd);
        return conn;
    }

    /** 
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void pipeRegistrationToGateway( String uni, 
                                            String icalid, 
                                            String icalpass, 
                                            String icalurl, 
                                            String icaluri, 
                                            String icalport, 
                                            String ssl) throws IOException
    {   
        Socket socket = new Socket("127.0.0.1", ServerParameters.REGISTRATION_PORT);
        DataOutputStream oos = new DataOutputStream(socket.getOutputStream());

        oos.writeUTF(uni);
        oos.writeUTF(icalid);
        oos.writeUTF(icalpass);
        oos.writeUTF(icalurl);
        oos.writeUTF(icaluri);
        oos.writeUTF(icalport);
        oos.writeUTF(ssl);

        oos.close();
        socket.close();
    }
    
    public void addUserRegistrationDB(Connection conn,  String uni, 
                                                        String icalid, 
                                                        String icalpass, 
                                                        String icalurl, 
                                                        String icaluri, 
                                                        String icalport, 
                                                        String ssl) throws SQLException 
    {
        int sslVal = ssl.equals("Yes") ? 1 : 0;

        Statement s = conn.createStatement();
        String sqlquery = null;

        sqlquery = "INSERT INTO icalgateway.registrations (cuid, ical_userid, ical_passwd, ical_host, ical_uri, ical_port, ical_ssl) VALUES ('" + uni + "','" + icalid + "','" + icalpass + "','" + icalurl + "','" + icaluri + "','" + icalport + "','" + sslVal + "');";
        s.executeUpdate(sqlquery);
        s.close();
    }
    
    public void addUserToPresenceDB(Connection conn,String uni, 
                                                    String icalpass) throws SQLException 
    {
        Statement s = conn.createStatement();
        String sqlquery = null;

        sqlquery = "INSERT INTO presence.login(user,password) VALUES ('" + uni + "','" + icalpass + "')";
        s.executeUpdate(sqlquery);
        s.close();
    }
}
