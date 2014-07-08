<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:view>

	<ls:page id="tableoperationBrowseForm" initBaseLibrary="false"
		title="#{gapiI18nTransformer['OPERATION_BROWSE.title.DESC']}"
		hasMargin="false" scrollingMode="HIDE" verticalSizing="FILL"
		formId="toperationBrowseForm" hasEventQueue="false">


		<ls:script facet="headScripts" type="CUSTOM"
			content="

            UCF_DomUtil.attachEvent(window, 'load', loadLightSpeed);
            UCF_DomUtil.attachEvent(window, 'unload', unloadLightSpeed);

            function loadLightSpeed() {
                window.oLS = new UCF_LS();
                
            }

            function unloadLightSpeed() {
                window.oLS.destroy();
            }
             
            
            
        " />

		<ls:script facet="headScripts" type="CUSTOM"
			content="
		function setTableOperField (customField , tableComponentID){
                 var customFieldTableCompID = window.opener.document.getElementById(tableComponentID);
                 customFieldTableCompID.value = customField;
                 customFieldTableCompID.focus();
                 window.close();
        }
        
        function setCopyOperationField (operation){
                 var operationField = window.opener.document.getElementById('OPERATIONENTRY');
                 operationField.value = operation;
                 operationField.focus();
                 window.close();
        }
        
" />
		<h:form id="toperationBrowseForm">
			<f:attribute name="height" value="100%" />
			<f:attribute name="sap-delta-id"
				value="#{sap:toClientId('toperationBrowseForm')}" />
			<ls:matrixLayout facet="content" id="displayPanelLayout" width="100%"
				height="100%">
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS"
						width="100%" height="15%" HAlign="CENTER">
						<ls:label facet="content" design="EMPHASIZED" text="Operation"
								doubleClickInfoEnabled="false" hasIcon="false" 
								width="4em" />
						<ls:inputField facet="content" id="SearchOperation" name="SearchOperation"
								upperCase="true" width="10em" changeInfoEnabled="true"
								changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toperationBrowseForm'))}"
								enterInfoEnabled="true"	value="#{tableOperBrowseBean.tableOpFilter}"							
								changeInfoResponseData="delta"/>
						<ls:label text=" " visibility="BLANK" />
						<ls:label text=" " visibility="BLANK" />
						<ls:button facet="content" id="search" text="Retrieve"
							action="#{tableOperBrowseBean.retOperation}" pressInfoEnabled="true"
							pressInfoClientAction="submit" pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toperationBrowseForm'))}" />
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows" id="row4">
					<ls:matrixLayoutCell facet="cells" id="cell4"
						cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
						HAlign="CENTER" width="100%" height="75%">
						<ls:panel facet="content" id="displayPanel"
							title="#{gapiI18nTransformer['OPERATION_BROWSE.title.DESC']}"
							width="100%" height="100%" hasEditableTitle="false"
							isCollapsible="false" collapsed="false" enabled="true"
							headerDesign="STANDARD" areaDesign="TRANSPARENT"
							borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
							contentPadding="NONE">
							<f:attribute name="sap-delta-id"
								value="#{sap:toClientId('displayPanel')}" />
							<ls:scrollContainer id="tableScroller" facet="content"
								width="100%" height="99%" scrollingMode="AUTO"
								scrollInfoEnabled="true" visibility="VISIBLE" isLayout="true"
								scrollTop="0" scrollLeft="0">
								<sap:dataTable binding="#{tableOperBrowseBeanConfigurator.table}"
									value="#{tableOperBrowseBean.operationList}" first="0" var="rows"
									id="operationTable" width="100%" height="100%"
									columnReorderingEnabled="true" rendered="true">
								</sap:dataTable>
							</ls:scrollContainer>
						</ls:panel>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS"
						width="100%" height="15%" HAlign="CENTER">
						<ls:button facet="content" id="submit" text="OK"
							action="#{tableOperBrowseBean.okAction}" pressInfoEnabled="true"
							pressInfoClientAction="submit" pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toperationBrowseForm'))}" />
						<ls:label text=" " visibility="BLANK" />

						<ls:button facet="content" id="cancel" text="Cancel"
							action="#{tableOperBrowseBean.cancelAction}"
							pressInfoEnabled="true" pressInfoClientAction="submit"
							pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toperationBrowseForm'))}" />
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
			</ls:matrixLayout>
		</h:form>
	</ls:page>
</f:view>