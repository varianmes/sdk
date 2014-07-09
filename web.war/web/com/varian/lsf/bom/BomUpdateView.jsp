<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:view>
	<ls:page id="BomUpdatePage" initBaseLibrary="false"
		title="Update/Delete New and Not used BOMs" hasMargin="false"
		scrollingMode="HIDE" verticalSizing="FILL" formId="bomUpdateForm"
		hasEventQueue="false" browserHistory="DISABLED">
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
            function openCopyOperationBrowse() {
                var url = '/manufacturing/com/varian/lsf/bom/CopyOperationBrowseLSF.jsf';
                window.open(url, 'Browse', 'menubar=no height=400 width=600  scrollbars=yes status=no location=no resizable=yes');  
           }
           
             function opentableOperationBrowse() {
                var url = '/manufacturing/com/varian/lsf/bom/TableOperationBrowseLSF.jsf';
                window.open(url, 'Browse', 'menubar=no height=400 width=600  scrollbars=yes status=no location=no resizable=yes');  
           }
           
           
        " />

		<h:form id="bomUpdateForm">
			<f:attribute name="height" value="100%" />
			<f:attribute name="sap-delta-id"
				value="#{sap:toClientId('bomUpdateForm')}" />
			<ls:panel facet="content" id="fieldButtonPanel"
				title="Update/Delete New and Not used BOMs" width="100%"
				height="20%" hasEditableTitle="false" isCollapsible="false"
				collapsed="false" enabled="true" headerDesign="STANDARD"
				areaDesign="TRANSPARENT" borderDesign="NONE" scrollingMode="NONE"
				isDragHandle="false" contentPadding="NONE">
				<f:attribute name="sap-delta-id"
					value="#{sap:toClientId('fieldButtonPanel')}" />
				<ls:matrixLayout facet="content" id="messageLayout" width="100%"
					height="20%">
					<ls:matrixLayoutRow facet="rows" id="row178">
						<ls:matrixLayoutCell facet="cells" id="celle1"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="LEFT" width="100%">
							<ls:messageBar id="messageBar" text="#{bomUpdateBean.message}"
								rendered="false" />
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
				</ls:matrixLayout>
				<ls:matrixLayout facet="content" id="selectionPanelLayout"
					width="65%" height="80%">
					<ls:matrixLayoutRow facet="rows" id="row2">
						<ls:matrixLayoutCell facet="cells" id="cell62"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="3%" height="80%" VAlign="MIDDLE">
							<ls:label text="   " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED"
								text="Bom Retrieve Criteria" />
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell23"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="22%" height="80%" VAlign="MIDDLE">
							<sap:commandOneMenu id="BUYOFF_ACTION"
								value="#{bomUpdateBean.newCheckBoxSelection}">
								<f:selectItem itemValue="NEW" itemLabel="New" />
								<f:selectItem itemValue="NOTUSED" itemLabel="NotUsed" />
								<f:selectItem itemValue="BOTH" itemLabel="Both" />
							</sap:commandOneMenu>
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell69"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="3%" height="80%" VAlign="MIDDLE">
							<ls:label text="   " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED"
								text="Bom Number Filter" />
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell29"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="22%" height="80%" VAlign="MIDDLE">
							<ls:inputField facet="content" id="SearchbyBOM" name="SearchbyBOM"
								upperCase="true" width="10em" changeInfoEnabled="true"
								changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}"
								enterInfoEnabled="true"	value="#{bomUpdateBean.bomFilter}"							
								changeInfoResponseData="delta"/>
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell1221"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="15%" height="80%" VAlign="MIDDLE">
							<ls:label text="   " visibility="BLANK" />
							<ls:button facet="content" id="Retrieve" text="Retrieve"
								action="#{bomUpdateBean.retrieveBomData}"
								pressInfoEnabled="true"
								pressInfoResponseData="delta" pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('updatePanel'))}" />
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
				</ls:matrixLayout>
			</ls:panel>
			<ls:matrixLayout facet="content" id="displayPanelLayout" width="100%"
				height="30%">
				<ls:matrixLayoutRow facet="rows" id="row4">
					<ls:matrixLayoutCell facet="cells" id="cell4"
						cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
						HAlign="CENTER" width="100%" height="90%">
						<ls:panel facet="content" id="displayPanel"
							title="BOM details for Selected criteria" width="100%"
							height="100%" hasEditableTitle="false" isCollapsible="false"
							collapsed="false" enabled="true" headerDesign="STANDARD"
							areaDesign="TRANSPARENT" borderDesign="NONE" scrollingMode="NONE"
							isDragHandle="false" contentPadding="NONE">
							<f:attribute name="sap-delta-id"
								value="#{sap:toClientId('displayPanel')}" />
							<ls:scrollContainer id="tableScroller" facet="content"
								width="100%" height="99%" scrollingMode="AUTO"
								scrollInfoEnabled="true" visibility="VISIBLE" isLayout="true"
								scrollTop="0" scrollLeft="0">
								<sap:dataTable binding="#{bomDisplayConfigurator.table}"
									value="#{bomUpdateBean.bomDisplayList}" first="0" var="rows"
									id="toolLogSfcTable" width="100%" height="100%"
									columnReorderingEnabled="true" rendered="true">
								</sap:dataTable>
							</ls:scrollContainer>
						</ls:panel>
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS"
						width="100%" height="5%" VAlign="BOTTOM" HAlign="CENTER"
						hasBorder="false">
						<ls:button text="Delete selected BOM" id="DELETEBOM"
							pressInfoEnabled="true" pressInfoClientAction="submit"
							action="#{bomUpdateBean.deleteBOM}" rendered="true"
							pressInfoResponseData="delta"
							pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('fieldButtonPanel'))}" />
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" VAlign="BOTTOM" HAlign="CENTER"
						hasBorder="false" cellBackgroundDesign="TRANSPARENT" height="5%">
						<ls:label text=" " width="2px" designBar="LIGHT" />
					</ls:matrixLayoutCell>
				</ls:matrixLayoutRow>
			</ls:matrixLayout>
			<ls:matrixLayout facet="content" width="100%" height="25%">
				<ls:matrixLayoutRow facet="rows">
					<ls:matrixLayoutCell facet="cells" VAlign="TOP" hasBorder="false"
						cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
						height="100%">
						<ls:matrixLayout facet="content" fixedLayout="false" width="100%"
							height="94%">
							<ls:matrixLayoutRow facet="rows">
								<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS"
									height="99%" width="100%" VAlign="TOP" HAlign="FORCEDLEFT"
									hasBorder="false">
									<ls:matrixLayout facet="content" fixedLayout="false"
										height="99%" width="100%" debugMode="false">
										<ls:matrixLayoutRow facet="rows">
											<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS"
												width="100%" VAlign="TOP" HAlign="FORCEDLEFT"
												rendered="true" hasBorder="false">
												<ls:panel facet="content" id="bomComponentDisplayPanel"
													title="Components for the selected BOM" width="100%"
													height="100%" hasEditableTitle="false"
													isCollapsible="false" collapsed="false" enabled="true"
													headerDesign="STANDARD" areaDesign="TRANSPARENT"
													borderDesign="NONE" scrollingMode="NONE"
													isDragHandle="false" contentPadding="NONE">
													<f:attribute name="sap-delta-id"
														value="#{sap:toClientId('bomComponentDisplayPanel')}" />
													<ls:scrollContainer id="toolGroupListScroller"
														facet="content" width="100%" height="99%"
														scrollingMode="AUTO" scrollInfoEnabled="true"
														visibility="VISIBLE" isLayout="true" scrollTop="0"
														scrollLeft="0">
														<sap:dataTable binding="#{bomComponentConfigurator.table}"
															value="#{bomUpdateBean.bomComponentList}" width="100%"
															height="99%" rows="25" var="row" first="0"
															id="ncGroupList_table" />
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
			</ls:matrixLayout>
			<ls:panel facet="content" id="updatePanel"
				title="New values for Update" width="100%" height="25%"
				hasEditableTitle="false" isCollapsible="false" collapsed="false"
				enabled="true" headerDesign="STANDARD" areaDesign="TRANSPARENT"
				borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
				contentPadding="NONE">
				<ls:matrixLayout facet="content" width="100%" height="100%">
					<ls:matrixLayoutRow facet="rows" id="row40">
						<ls:matrixLayoutCell facet="cells" id="cell40"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="LEFT" width="55%" height="65%">
							<ls:label text="   " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED"
								text="New Assembly Operation" doubleClickInfoEnabled="false"
								hasIcon="false"/>
							<ls:inputField id="Operationentry" upperCase="true"
								name="Operationentry" value="#{bomUpdateBean.updateOperation}"
								changeInfoEnabled="true" changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('updatePanel'))}"
								enterInfoEnabled="true"	changeInfoResponseData="delta" />
							<ls:label text="" visibility="BLANK" />
							<ls:button facet="content" id="OperationClick" text=".."
								action="#{bomUpdateBean.operBrowse}" pressInfoEnabled="true"
								pressInfoResponseData="delta"
								pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('updatePanel'))}" />
							<ls:label text="   		" visibility="BLANK" />
							<ls:button facet="content" id="CopyToAll" text="Copy to All"
								action="#{bomUpdateBean.copyToAll}" pressInfoEnabled="true"
								pressInfoResponseData="delta" pressInfoClientAction="submit"
								pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('updatePanel'))}" />
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell405"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="10%" height="65%">
							<ls:label facet="content" design="EMPHASIZED" text="New Status"
								doubleClickInfoEnabled="false" hasIcon="false" />
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell406"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="FORCEDLEFT" width="15%" height="65%">
							<sap:commandOneMenu id="BOM_STATUS"
								value="#{bomUpdateBean.newBomStatus}">
								<f:selectItem itemValue="SELECT" itemLabel="Select" />
								<f:selectItem itemValue="NEW" itemLabel="New" />
								<f:selectItem itemValue="RELEASABLE" itemLabel="Releasable" />
								<f:selectItem itemValue="OBSOLETE" itemLabel="Obsolete" />
							</sap:commandOneMenu>
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell407"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="LEFT" width="10%" height="65%">
							<ls:label text="   " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED"
								text="Current Version" doubleClickInfoEnabled="false"
								hasIcon="false" visibility="BLANK"/>
						</ls:matrixLayoutCell>
						<ls:matrixLayoutCell facet="cells" id="cell408"
							cellBackgroundDesign="TRANSPARENT" cellDesign="PADLESS"
							HAlign="LEFT" width="10%" height="65%">
						<ls:label text="   " visibility="BLANK" />
							<ls:label facet="content" design="EMPHASIZED"
								text="Current Version" doubleClickInfoEnabled="false"
								hasIcon="false" visibility="BLANK"/>	
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
					<ls:matrixLayoutRow facet="rows">
						<ls:matrixLayoutCell facet="cells" width="100%" height="25%" VAlign="BOTTOM" HAlign="FORCEDRIGHT" hasBorder="false">
							<ls:button text="Update BOM" pressInfoEnabled="true" width="10em"
								enabled="true" rendered="true" pressInfoClientAction="submit"
								action="#{bomUpdateBean.updateBOM}"
								pressInfoResponseData="delta" />
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
					<ls:matrixLayoutRow facet="rows">
						<ls:matrixLayoutCell facet="cells" VAlign="BOTTOM" HAlign="CENTER"
							hasBorder="false" cellBackgroundDesign="TRANSPARENT" width = "100%" height="10%">
							<ls:label text=" "  designBar="LIGHT" />
						</ls:matrixLayoutCell>
					</ls:matrixLayoutRow>
				</ls:matrixLayout>
			</ls:panel>
		</h:form>
	</ls:page>
</f:view>