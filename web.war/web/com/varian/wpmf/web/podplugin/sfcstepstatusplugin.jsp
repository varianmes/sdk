
<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:subview id="sfcStepStatusView">
	<ls:script facet="headScripts" type="CUSTOM"
		content="
			function windowUnload() {
                var btnControl = document.getElementById('popupWindowForm:POPUP_WINDOW:poupwindowInclude:sfcStepStatusView:Close:');
                if (btnControl) {
                    btnControl.click();
                }
                }
         " />
	<ls:panel facet="content" id="sfcStepStatusView"
		title="Varian Log TQC Buyoff" width="100%" height="100%"
		hasEditableTitle="false" isCollapsible="false" collapsed="false"
		enabled="true" headerDesign="STANDARD" areaDesign="TRANSPARENT"
		borderDesign="NONE" scrollingMode="NONE" isDragHandle="false"
		contentPadding="NONE">
		<f:attribute name="sap-delta-id"
			value="#{sap:toClientId('sfcStepStatus')}" />
		<ls:matrixLayout facet="content" width="100%" height="100%">
			<ls:matrixLayoutRow facet="rows">
				<ls:matrixLayoutCell facet="cells" cellDesign="PADLESS" width="100%"
					VAlign="TOP" HAlign="FORCEDLEFT">
					<ls:matrixLayout facet="content"
						binding="#{logTqcBuyoffBean.pluginMessageMatrixLayout}"
						width="100%" />
				</ls:matrixLayoutCell>
			</ls:matrixLayoutRow>
		</ls:matrixLayout>
		<ls:gridLayout facet="content" width="100%" fixedLayout="true"
			height="80%">
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="labelCell30" facet="cells" colSpan="4"
					HAlign="FORCED LEFT" height="10%">
					<ls:label text="		" visibility="BLANK" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="labelCell3" facet="cells" colSpan="4"
					HAlign="FORCED LEFT" height="5%">
					<ls:label text="		" visibility="BLANK" />
					<ls:label facet="content" design="EMPHASIZED"
						text="TQC Buyoff Details for the Selected SFC"
						doubleClickInfoEnabled="false" width="10em" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell facet="cells" cellBackgroundDesign="TRANSPARENT"
					colSpan="4" HAlign="CENTER" width="95%" height="2%">
					<sap:dataTable binding="#{logTqcBuyoffBean.tableConfig.table}"
						value="#{logTqcBuyoffBean.buyoffDetailsList}" width="100%"
						rows="1" var="row" first="0" id="buyoffDetailsTable">
					</sap:dataTable>
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="labelCell32" facet="cells" colSpan="4"
					HAlign="FORCED LEFT" height="5%">
					<ls:label text="		" visibility="BLANK" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="fieldCell4" facet="cells" colSpan="4"
					width="100%" height="5%" HAlign="FORCEDLEFT">
					<ls:label facet="content" design="EMPHASIZED" text="	Comments"
						doubleClickInfoEnabled="false"/>
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="fieldCell9" facet="cells" colSpan="4"
					width="100%" height="5%" HAlign="FORCEDLEFT">
					<ls:inputField facet="content" id="Comments" name="Comments"
						changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('sfcStepStatusView'))}"
						value="#{logTqcBuyoffBean.newcomments}"
						fieldHelpPressInfoEnabled="false" width="20em"
						enterInfoEnabled="true" changeInfoEnabled="true"
						changeInfoResponseData="delta" changeInfoClientAction="submit"
						hideFieldHelp="true" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="labelCell33" facet="cells" colSpan="4"
					HAlign="FORCEDLEFT" height="5%">
					<ls:label text="		" visibility="BLANK" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="fieldCell10" facet="cells" width="10%"
					height="5%" HAlign="FORCEDLEFT" VAlign="MIDDLE">
					<ls:label design="EMPHASIZED" text="	Action"/>
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="fieldCell19" facet="cells" width="100%"
					height="5%" HAlign="FORCEDLEFT" VAlign="MIDDLE">
					<sap:commandOneMenu id="BUYOFF_ACTION" 
						value="#{logTqcBuyoffBean.buyoffAction}">
						<f:selectItem itemValue="ACCEPT" itemLabel="Accept" />
						<f:selectItem itemValue="REJECT" itemLabel="Reject" />
					</sap:commandOneMenu>
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="labelCell6" facet="cells" colSpan="4"
					HAlign="CENTER" height="10%">
					<ls:label facet="content" design="EMPHASIZED"
						text="Provide a login to log TQC Buyoff"
						doubleClickInfoEnabled="false" width="10em" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell id="fieldCell5" facet="cells" colSpan="4"
					width="100%" height="10%" HAlign="CENTER">
					<ls:label facet="content" design="EMPHASIZED" text="User Name"
						doubleClickInfoEnabled="false" required="true"
						requiredIndicatorAtFront="true" />
					<ls:inputField facet="content" id="UserId" name="UserId"
						upperCase="true"
						changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('sfcStepStatusView'))}"
						value="#{logTqcBuyoffBean.userId}"
						fieldHelpPressInfoEnabled="false" width="10em"
						enterInfoEnabled="true" changeInfoEnabled="true"
						changeInfoResponseData="delta" changeInfoClientAction="submit"
						hideFieldHelp="true" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell facet="cells" colSpan="4" VAlign="TOP"
					HAlign="CENTER" width="100%" height="10%">
					<ls:label facet="content" design="EMPHASIZED" text="Password"
						doubleClickInfoEnabled="false" required="true"
						requiredIndicatorAtFront="true" />
					<ls:label text="  " visibility="BLANK" />
					<ls:inputField facet="content" id="Password" name="Password"
						changeInfoParameters="#{sap:deltaUpdateId(sap:toClientId('sfcStepStatusView'))}"
						passwordField="true" value="#{logTqcBuyoffBean.password}"
						fieldHelpPressInfoEnabled="false" width="10em"
						enterInfoEnabled="true" changeInfoEnabled="true"
						changeInfoResponseData="delta" changeInfoClientAction="submit"
						hideFieldHelp="true" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell facet="cells" colSpan="4" HAlign="CENTER"
					width="100%" height="10%">
					<ls:largeButton facet="content" id="windowCloseButton"
						visibility="NONE" text=""
						action="#{logTqcBuyoffBean.processWindowClosed}"
						pressInfoEnabled="true" pressInfoClientAction="submit"
						pressInfoResponseData="delta"
						pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('sfcStepStatusView'))}" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>

			<ls:gridLayoutRow facet="rows">
				<ls:gridLayoutCell facet="cells" colSpan="4" HAlign="CENTER"
					width="100%" height="10%">
					<ls:largeButton facet="content" id="LogBuyoff" text="Log Buyoff"
						action="#{logTqcBuyoffBean.logbuyoff}" pressInfoEnabled="true"
						width="8em" pressInfoResponseData="delta"
						pressInfoClientAction="submit"
						pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('sfcStepStatusView'))}" />
					<ls:label text="   " visibility="BLANK" />
					<ls:largeButton facet="content" id="Close" text="Close"
						action="#{logTqcBuyoffBean.closePlugin}" pressInfoEnabled="true"
						width="8em" pressInfoResponseData="delta"
						pressInfoClientAction="submit"
						pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('sfcStepStatusView'))}" />
				</ls:gridLayoutCell>
			</ls:gridLayoutRow>
		</ls:gridLayout>
	</ls:panel>
</f:subview>