<%-- (c)Copyright 2008 SAP AG. All rights reserved.  --%>

<%--  $Revision: #1 $  --%>

<%--
    Includes common report initialization scriptlet used by the main report views.  The primary purpose
    is to define and set page scope variables required by the JSP.  A secondary purpose is to hide
    framework names/constants and provide a central location where new initialization can be added
    later.
--%>

<%    
    final String REPORT_NAME = (String) request.getAttribute("reportName");
    com.sap.me.coral.core.model.Form model = com.sap.me.coral.core.util.WebUtils.getAppState(request).getFormForScreen();
    String resultView = model.getValue("RESULT_VIEW");
    if( resultView == null || resultView.length() == 0 ) {
        resultView = com.sap.me.coral.core.util.WebUtils.buildContextPath(request, response, "/com/sap/me/report/client/Empty.jsp");
    }
    String formAction = "/report/" + request.getAttribute("reportName");
%>
