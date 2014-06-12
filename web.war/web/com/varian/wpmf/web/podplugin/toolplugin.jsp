<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:subview id="ToolLogPage">
	<ls:script facet="headScripts" type="CUSTOM"
		content="
            UCF_DomUtil.attachEvent(window, 'load', loadLightSpeed);
            UCF_DomUtil.attachEvent(window, 'unload', windowUnload);

            function loadLightSpeed() {
                window.oLS = new UCF_LS();
               }

            function unloadLightSpeed() {
                if (window.oLS) {
                    window.oLS.destroy();
                }
            }
             function windowUnload() {
                if (window.parent == undefined) {
                 //   window.alert('windowLoaded: window.parent is undefined');
                    return;
                }
                var btnControl = document.getElementById('materialCustomDataForm:windowCloseButton');
                if (btnControl) {
                    btnControl.click();
                } else {
                  //  window.alert('windowLoaded: hidden button not found');
                }

                unloadLightSpeed();
            }
           
            function openBrowse() {
                var url = '/manufacturing/com/varian/lsf/tool/OperationBrowseLSF.jsf';
                window.open(url, 'Browse', 'menubar=no height=400 width=600  scrollbars=yes status=no location=no resizable=yes');  
           }
           
            function sfcDetails() {
                var url = '/manufacturing/com/varian/lsf/tool/ToolSFCdetailsLSF.jsf';
                window.open(url, 'Details', 'menubar=no height=400 width=600  scrollbars=yes status=no location=no resizable=yes');  
           }
           
        " />
        <f:attribute name="height" value="100%" />
        <f:attribute name="sap-delta-id"
				value="#{sap:toClientId('toolLogForm')}" />
		<ls:panel facet="content" id="fieldButtonPanel"
				title="Varian Log Tools for Multiple SFCs"
				width="100%" height="15%" hasEditableTitle="false"
				isCollapsible="false" collapsed="false" enabled="true"
				headerDesign="STANDARD" areaDesign="TRANSPARENT" borderDesign="NONE"
				scrollingMode="NONE" isDragHandle="false" contentPadding="NONE">
			<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('fieldButtonPanel')}" />
				<ls:matrixLayout facet="content" id="selectionPanelLayout"
					width="100%" height="100%">
					<ls:matrixLayoutRow facet="rows" id="row1">
						<ls:matrixLayoutCell facet="cells" id="cell1"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="LEFT" width="100%" height="3%">
							<ls:messageBar id="messageBar"
								text="#{logToolBean.message}" rendered="false" />
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
					<ls:matrixLayoutRow facet="rows" id="row2">
						<ls:matrixLayoutCell facet="cells" id="cell2"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="LEFT" width="100%" height="12%">
							<ls:label text=" " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED" text="User"
								doubleClickInfoEnabled="false" hasIcon="false"
								width="6em" />
							<ls:inputField facet="content" id="UserName" name="UserName"
								upperCase="true"
								changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}"
								fieldHelpPressInfoEnabled="false" width="10em"
								enterInfoEnabled="true" showHelpButton="false"
								value="#{logToolBean.userId}" readonly="true"
								changeInfoEnabled="true" changeInfoResponseData="delta"
								changeInfoClientAction="submit" hideFieldHelp="true" />							
							<ls:label text=" " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED" text="Operation"
								doubleClickInfoEnabled="false" hasIcon="false" 
								width="6em" />
							<ls:inputField facet="content" id="Operation" name="Operation"
								upperCase="true" width="15em" changeInfoEnabled="true"
								changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}"
								enterInfoEnabled="true"	value="#{logToolBean.filterOperation}"							
								changeInfoResponseData="delta"/>
							<ls:button facet="content" id="OperationClick" text=".."
								action="#{logToolBean.operBrowse}" pressInfoEnabled="true"
								width="1em" pressInfoResponseData="delta"
								pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}" />
							<ls:label text=" " visibility="BLANK" />
							<ls:button facet="content" id="RETRIEVE" text="Retrieve"
								action="#{logToolBean.readSfcSelectionData}"
								pressInfoEnabled="true" width="6em"
								pressInfoResponseData="delta" pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}" />
							<ls:label text="   " visibility="BLANK" />
							<ls:button facet="content" id="CLEAR" text="Clear"
								action="#{logToolBean.clear}" pressInfoEnabled="true"
								width="6em" pressInfoResponseData="delta"
								pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}" />
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
				</ls:matrixLayout>
			</ls:panel>
				<ls:matrixLayout facet="content" id="displayPanelLayout" width="100%"
				height="45%">
				<ls:matrixLayoutRow facet="rows" id="row4">
					<ls:matrixLayoutCell facet="cells" id="cell4"
						cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
						HAlign="CENTER" width="100%" height="90%">
						<ls:panel facet="content" id="displayPanel"
							title="Active SFCs for the User" width="100%" height="100%"
							hasEditableTitle="false" isCollapsible="false" collapsed="false"
							enabled="true" headerDesign="STANDARD" areaDesign="TRANSPARENT"
							borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
							contentPadding="NONE">
                            <f:attribute name="sap-delta-id"
								value="#{sap:toClientId('displayPanel')}" />
							<ls:scrollContainer id="tableScroller" facet="content"
								width="100%" height="99%" scrollingMode="AUTO"
								scrollInfoEnabled="true" visibility="VISIBLE" isLayout="true"
								scrollTop="0" scrollLeft="0">
								<sap:dataTable
									binding="#{logtoolSfcBeanConfigurator.table}"
									value="#{logToolBean.sfcSelectionItemList}"
									first="0" var="rows" id="toolLogSfcTable" width="100%"
									height="100%" columnReorderingEnabled="true" rendered="true">
								</sap:dataTable>
							</ls:scrollContainer>
						</ls:panel>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="100%" height="5%" VAlign="BOTTOM" HAlign="CENTER" hasBorder="false">
						<ls:button facet="content" text="Retrieve Tool Groups" id="RETRIEVETOOLGROUPS"
								action="#{logToolBean.retrieveToolGroups}"
								pressInfoEnabled="true" width="6em"
								pressInfoResponseData="delta" pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}" />
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" VAlign="BOTTOM" HAlign="CENTER" hasBorder="false" cellBackgroundDesign="TRANSPARENT" height="5%">
						<ls:label text=" " width="2px" designBar="LIGHT"/>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				</ls:matrixLayout>
				
				<ls:matrixLayout facet="content" width="100%" height="40%">
				<ls:matrixLayoutRow facet="rows">
				<ls:matrixLayoutCell facet="cells" VAlign="TOP" hasBorder="false" cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS" height="89%">	
				<ls:matrixLayout facet="content" fixedLayout="false" width="100%" height="94%">
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" height="99%" width="100%" VAlign="TOP" HAlign="FORCEDLEFT" hasBorder="false">
						<ls:matrixLayout facet="content" fixedLayout="false" height="99%" width="100%" debugMode="false">
							<ls:matrixLayoutRow facet="rows">
								<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="40%" VAlign="TOP" HAlign="FORCEDLEFT" rendered="true" hasBorder="false">
                                   	<ls:panel facet="content" id="toolgroupdisplayPanel"
									title="ToolGroups" width="100%" height="100%"
									hasEditableTitle="false" isCollapsible="false" collapsed="false"
									enabled="true" headerDesign="STANDARD" areaDesign="TRANSPARENT"
									borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
									contentPadding="NONE">
                            		<f:attribute name="sap-delta-id"
									value="#{sap:toClientId('toolgroupdisplayPanel')}" />
                                    <ls:scrollContainer id="toolGroupListScroller" facet="content" width="100%" height="99%"
                                            scrollingMode="AUTO" scrollInfoEnabled="true"
                                            visibility="VISIBLE" isLayout="true" scrollTop="0" scrollLeft="0">
                                    <sap:dataTable binding="#{logtoolGroupBeanConfigurator.table}" value="#{logToolBean.toolGroupList}"
                                                       width="100%" height="99%" rows="25"
                                                       var="row" first="0" id="ncGroupList_table" />
                                    </ls:scrollContainer>
                                    </ls:panel>
                                </ls:matrixLayoutCell>
								<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="2%" VAlign="TOP" HAlign="FORCEDLEFT" rendered="true" hasBorder="false"/>
								<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="50%" VAlign="TOP" HAlign="FORCEDLEFT" hasBorder="false">
                                			<ls:panel facet="content" id="toolnumberdisplayPanel"
											title="ToolNumbers" width="100%" height="100%"
											hasEditableTitle="false" isCollapsible="false" collapsed="false"
											enabled="true" headerDesign="STANDARD" areaDesign="TRANSPARENT"
											borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
											contentPadding="NONE">                                  
                                    		<ls:scrollContainer id="ncCodeListScroller" facet="content" width="100%" height="99%"
                                            scrollingMode="AUTO" scrollInfoEnabled="true"
                                            visibility="VISIBLE" isLayout="true" scrollTop="0" scrollLeft="0">											
											<f:attribute name="sap-delta-id"
											value="#{sap:toClientId('toolnumberdisplayPanel')}" />
                                       	 	<sap:dataTable binding="#{logtoolNumberBeanConfigurator.table}"
                                                       width="100%" height="99%" rows="25"
                                                       value="#{logToolBean.toolNumberList}" var="row" first="0" 
                                                       id="ncCodeList_table"/>
                                  			</ls:scrollContainer>
                                         	</ls:panel>
								</ls:matrixLayoutCell>
							</ls:matrixLayoutRow>
						</ls:matrixLayout>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
			</ls:matrixLayout>		
			</ls:matrixLayoutCell>
			</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="100%" height="5%" VAlign="BOTTOM" HAlign="CENTER"  hasBorder="false">
						<ls:button text="Retrieve Tool Numbers"
							action="#{logToolBean.retrieveToolNumbers}"
							pressInfoEnabled="true"
							enabled="true"
							rendered="true"
							pressInfoClientAction="submit"
							pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toolGroupList'))}" />
						<ls:label text="" width="2px" designBar="LIGHT"/>
						<ls:label text="" width="2px" designBar="LIGHT"/>
						<ls:label text="" width="2px" designBar="LIGHT"/>
						<ls:button text="Log Tool Numbers"
							action="#{logToolBean.logToolNumbers}"
							pressInfoEnabled="true"
							pressInfoClientAction="submit"
							rendered="true"
							pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toolNumberList'))}" />
						<ls:label text=" " width="2px" designBar="LIGHT"/>
						<ls:label text="" width="2px" designBar="LIGHT"/>
						<ls:label text="" width="2px" designBar="LIGHT"/>
						<ls:button text="Close"
							action="#{logToolBean.closePlugin}"
							pressInfoEnabled="true"
							pressInfoClientAction="submit"
							rendered="true"
							pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('toolNumberList'))}" />
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" VAlign="BOTTOM" HAlign="CENTER" hasBorder="false" cellBackgroundDesign="TRANSPARENT" height="5%">
						<ls:label text=" " width="2px" designBar="LIGHT"/>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
			</ls:matrixLayout>
</f:subview>