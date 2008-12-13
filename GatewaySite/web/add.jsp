<%-- 
    Document   : add
    Created on : Dec 13, 2008, 1:07:28 PM
    Author     : milind
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>iCal to Presence Gateway >> User Registration</title>
    </head>
    <body>
        
        <CENTER>
        <h2>Gateway user registration form</h2>
        <form action="addServlet" method="POST">
            <TABLE>
                <TR>    
                    <TD>Columbia UNI</TD><TD> <INPUT TYPE=TEXT NAME="uni"  SIZE=10></TD>
                </TR>
                <TR>
                    <TD>iCal User ID</TD><TD> <INPUT TYPE=TEXT NAME="icalid"  SIZE=10></TD>
                </TR>
                <TR>
                    <TD>iCal Password</TD><TD> <INPUT TYPE=PASSWORD NAME="icalpass"  SIZE=10</TD>
                </TR>
                <TR>
                    <TD>iCal Host URL </TD><TD><INPUT TYPE=TEXT NAME="icalurl"  SIZE=10><TD>
                </TR>
                <TR>
                    <TD>iCal URI</TD><TD> <INPUT TYPE=TEXT NAME="icaluri"  SIZE=10>  </TD>
                </TR>
                <TR>
                    <TD>iCal Port</TD><TD> <INPUT TYPE=TEXT NAME="icalport"  SIZE=10></TD>
                </TR>
                <TR>
                    <TD>iCal SSL Enabled?</TD><TD> <INPUT TYPE=RADIO NAME="ssl" VALUE="Yes"> Yes </TD>
                    
                    
                    <TD><INPUT TYPE=RADIO NAME="ssl" VALUE="No"> No  </TD>
                </TR>
                <TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR>
                <TR>
                    <TD></TD><TD> <INPUT TYPE=SUBMIT VALUE="Submit"></TD>
                </TR>
            </TABLE>
            
        </form>
    </CENTER>
        
        
        
    </body>
</html>
