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
    <script language="javascript" type="text/javascript">
    
        function showLoading(divId){
    
            var displayElement = document.getElementById(divId)
            displayElement.innerHTML =""            
            displayElement.innerHTML = "<img src=\"loading_animation_small.gif\">";

        }
        
        var inputError = true;

        
        function reteriveFormValue(divId){
          
            var formVal;

            switch(divId)
            {
                case 2: formVal = document.myForm.icalid.value;break;
                case 3: formVal = document.myForm.icalpass.value;break;
                case 4: formVal = document.myForm.icalurl.value;break;
                case 5: formVal = document.myForm.icaluri.value;break;
                case 6: formVal = document.myForm.icalport.value;break;
                case 7: formVal = document.myForm.ssl.value;break;                
                
            }
        
            return formVal;
            
        }
        
        function showErrorMsg(divId){
            
            var errorMsg;
            
            switch(divId)
            {
                case 2: errorMsg = "Enter iCalendar User ID";break;
                case 3: errorMsg = "Enter iCalendar Password";break;
                case 4: errorMsg = "Enter iCalendar Host URL";break;
                case 5: errorMsg = "Enter iCalendar URI";break;
                case 6: errorMsg = "Enter iCalendar Port";break;
                case 7: errorMsg = "Please select one option";break;                            
            }
            
            return errorMsg;
        }
        
        function validation(){
            
            var inputErr1;
            var inputErr2;var inputErr3;var inputErr4;var inputErr5;var inputErr6;
            var inputErr7;
            
           
            if(document.getElementById(1).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr1 = false;
            }else{
                inputErr1 = true;
            }
            if(document.getElementById(2).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr2 = false;
            }else{
                inputErr2 = true;
            }
          
            if(document.getElementById(3).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr3 = false;
            }else{
                inputErr3 = true;
            }
           
            if(document.getElementById(4).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr4 = false;
            }else{
                inputErr4 = true;
            }
         
            if(document.getElementById(5).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr5 = false;
            }else{
                inputErr5 = true;
            }
         
            if(document.getElementById(6).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr6 = false;
            }else{
                inputErr6 = true;
            }
            
            if(document.getElementById(7).innerHTML == "<img src=\"green_tick.jpg\">"){
                inputErr7 = false;
            }else{
                inputErr7 = true;
            }
            
            if(!inputErr1 && !inputErr2 && !inputErr3 && !inputErr4 && !inputErr5 && !inputErr6 && !inputErr7 )
            {
                document.getElementById("button").innerHTML = "<INPUT TYPE=SUBMIT VALUE=\"Submit\">";
            }else{
                document.getElementById("button").innerHTML = "<INPUT TYPE=SUBMIT VALUE=\"Submit\" DISABLED=\"disabled\">";
            }
           
            
            
            
        }
    
    
        function ajaxFunction(divId){
            
            
        
            var displayElement = document.getElementById(divId) 
            displayElement.innerHTML = "";
        
            if(divId == 1){
                
               if(document.myForm.uni.value == ""){
                   displayElement.innerHTML = "Enter Columbia UNI";
               }else{
            
                var ajaxRequest
                try{
                    // Opera 8.0+, Firefox, Safari
                    ajaxRequest = new XMLHttpRequest();
                } catch (e){
                    // Internet Explorer Browsers
                    try{
                        ajaxRequest = new ActiveXObject("Msxml2.XMLHTTP");
                    } catch (e) {
                        try{
                            ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
                        } catch (e){
                            // Something went wrong
                            alert("Browser Not Supported");
                            return false;
                        }
                    }
                } 

                // Create a function that will receive data sent from the server
                ajaxRequest.onreadystatechange = function(){
                
                    var response;
                
                    if(ajaxRequest.readyState == 4){
                        
                        response = ajaxRequest.responseText;
                        
                        if(response == 1){
                            displayElement.innerHTML = "User already exists"; 
                        }
                        
                        if(response == 2){
                            displayElement.innerHTML = "<img src=\"green_tick.jpg\">"; 
                        }
                        
                     
                       validation();  
                   
                    }                  
                                
                }
            
                var uniVal = document.myForm.uni.value;
           
                var queryString = "?enteredUni="+uniVal;
                ajaxRequest.open("GET", "/GatewaySite/addServlet"+queryString, true);
                ajaxRequest.send(null);
                
               } 
                
                
                
               
            }
            else{
          
                var formVal = reteriveFormValue(divId);
                
                if(formVal=="")
                {
                    displayElement.innerHTML = showErrorMsg(divId); 
                    inputError = true;
                }
                else
                {
                    displayElement.innerHTML = "<img src=\"green_tick.jpg\">";
                    inputError = false;    
                }
                if((divId == 6) && (isNaN(formVal)))
                {
                    displayElement.innerHTML = "Please enter a numeric value"; 
                }
                            
                           
              
            }
            
          validation();
           
        }
             
                  
   
    
    
    
    
    
    
    </script>
    
    
    
    <body>
        <style>
            #page {
                position: relative; left: 450px; top: 80px;  width: 725px;
            } 
        </style>
        
        
        <div id="page">
            
            <h2>Gateway user registration form</h2>
            <form name="myForm" action="addServlet" method="POST">
                <TABLE>
                    <TR>    
                        <TD>Columbia UNI</TD><TD> <INPUT TYPE=TEXT NAME="uni"  SIZE=25 ONCLICK=showLoading(1) ONBLUR=ajaxFunction(1)></TD><TD><DIV ID="1"></DIV></TD>
                    </TR>
                    <TR>
                        <TD>iCal User ID</TD><TD> <INPUT TYPE=TEXT NAME="icalid"  SIZE=25 ONCLICK=showLoading(2) ONBLUR=ajaxFunction(2)></TD><TD><DIV ID="2"></DIV></TD>
                    </TR>
                    <TR>
                        <TD>iCal Password</TD><TD> <INPUT TYPE=PASSWORD NAME="icalpass"  SIZE=25 ONCLICK=showLoading(3) ONBLUR=ajaxFunction(3)></TD><TD><DIV ID="3"></DIV></TD>
                    </TR>
                    <TR>
                        <TD>iCal Host URL </TD><TD><INPUT TYPE=TEXT NAME="icalurl"  SIZE=25 ONCLICK=showLoading(4) ONBLUR=ajaxFunction(4)></TD><TD><DIV ID="4"></DIV></TD>
                    </TR>
                    <TR>
                        <TD>iCal URI</TD><TD> <INPUT TYPE=TEXT NAME="icaluri"  SIZE=25 ONCLICK=showLoading(5) ONBLUR=ajaxFunction(5)></TD><TD><DIV ID="5"></DIV></TD>
                    </TR>
                    <TR>
                        <TD>iCal Port</TD><TD> <INPUT TYPE=TEXT NAME="icalport"  SIZE=25 ONCLICK=showLoading(6) ONBLUR=ajaxFunction(6)></TD><TD><DIV ID="6"></DIV></TD>
                    </TR>
                    <TR>
                        <TD>iCal SSL Enabled?</TD><TD> <INPUT TYPE=RADIO NAME="ssl" VALUE="Yes" ONCLICK=ajaxFunction(7)> Yes 
                            
                            
                        <INPUT TYPE=RADIO NAME="ssl" VALUE="No"  ONCLICK=ajaxFunction(7)> No</TD><TD><DIV ID="7"></DIV></TD>
                    </TR>
                    <TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR><TR></TR>
                    <TR>
                        <TD></TD><TD><DIV ID="button"><INPUT TYPE=SUBMIT VALUE="Submit" DISABLED="disabled"></DIV></TD>
                    </TR>
                </TABLE>
                
            </form>
        </div>
        
        
        
    </body>
</html>
