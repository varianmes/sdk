<%-- (c)Copyright 2008 SAP AG. All rights reserved.  --%>

<%--  $Revision: #1 $  --%>
<%--
    Includes common report initialization scriptlet used by the main report results.  The primary purpose
    is to define and set page scope variables required by the JSP.  A secondary purpose is to hide
    framework names/constants and provide a central location where new initialization can be added
    later.
--%>

<%
    String formAction = "/report/" + request.getAttribute( "reportName" );
    String isIncluded = "true".equals( request.getParameter( "includeFlag" ) ) ? "true" : "false";
%>