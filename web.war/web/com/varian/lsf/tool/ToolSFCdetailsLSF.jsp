<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:view>

	<ls:page id="toolBrowseId" initBaseLibrary="false"
		title="#{gapiI18nTransformer['TOOL_DETAILS.title.DESC']}"
		hasMargin="false" scrollingMode="HIDE" verticalSizing="FILL"
		formId="ToolDetailsBrowseForm" hasEventQueue="false">


		<ls:script facet="headScripts" type="CUSTOM"
			content="

            UCF_DomUtil.attachEvent(window, 'load', loadLightSpeed);
            UCF_DomUtil.attachEvent(window, 'unload', unloadLightSpeed);

            function loadLightSpeed() {
             //  window.alert('windowLoaded: I am in loadlight speed');
                window.oLS = new UCF_LS();
                
            }

            function unloadLightSpeed() {    
             //  window.alert('windowLoaded: I am in unloadlight speed');
				window.oLS.destroy();
            }
            
            function windowUnload() {
                if (window.parent == undefined) {
               //  window.alert('windowLoaded: window.parent is undefined');
                    return;
                }
                var btnControl = document.getElementById('toolDetailsBrowseForm:windowCloseButton');
                if (btnControl) {
               // window.alert('windowLoaded: clicking close button');
                    btnControl.click();
                } else {
               //window.alert('windowLoaded: hidden button not found');
                }
                unloadLightSpeed();
            }            
        " />


		<h:form id="toolDetailsBrowseForm">
			<f:attribute name="height" value="100%" />
			<f:attribute name="sap-delta-id"
				value="#{sap:toClientId('toolDetailsBrowseForm')}" />
			<ls:matrixLayout facet="content" id="displayPanelLayout" width="100%"
				height="100%">
				<ls:matrixLayoutRow facet="rows" id="row4">
					<ls:matrixLayoutCell facet="cells" id="cell4"
						cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
						HAlign="CENTER" width="100%" height="80%">
						<ls:panel facet="content" id="displayPanel"
							title="#{gapiI18nTransformer['TOOL_DETAILS.title.DESC']}"
							width="100%" height="100%" hasEditableTitle="false"
							isCollapsible="false" collapsed="false" enabled="true"
							headerDesign="STANDARD" areaDesign="TRANSPARENT"
							borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
							contentPadding="NONE">
							<ls:scrollContainer id="tableScroller" facet="content"
								width="100%" height="99%" scrollingMode="AUTO"
								scrollInfoEnabled="true" visibility="VISIBLE" isLayout="true"
								scrollTop="0" scrollLeft="0">
								<sap:dataTable binding="#{toolBrowseTableBeanConfigurator.table}"
									value="#{toolBrowseBean.toolBrowseList}" first="0" var="rows"
									id="toolBrowseTable" width="100%" height="100%"
									columnReorderingEnabled="true" rendered="true">
								</sap:dataTable>
							</ls:scrollContainer>
						</ls:panel>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS"
						width="100%" height="20%" HAlign="CENTER">
						<ls:button facet="content" id="cancel" text="Close"
							action="#{toolBrowseBean.close}"
							pressInfoEnabled="true" pressInfoClientAction="submit"
							pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toolDetailsBrowseForm'))}" />
						<ls:largeButton facet="content" id="windowCloseButton"
								visibility="NONE" text=""
								action="#{toolBrowseBean.processWindowClosed}"
								pressInfoEnabled="true" pressInfoClientAction="submit"
								pressInfoResponseData="delta" 
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toolDetailsBrowseForm'))}"/>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
			</ls:matrixLayout>
		</h:form>
	</ls:page>
</f:view>