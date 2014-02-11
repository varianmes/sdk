<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sap.com/jsf/core"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>

<f:subview id="podSelectView">

    <ls:panel facet="content" id="podSelection" binding="#{podSelectionHVPlugin.backingBean.container}"
        title="#{gapiI18nTransformer['podselection.title.TEXT']}" width="100%" height="100%" hasEditableTitle="false"
        isCollapsible="false" collapsed="false" enabled="true" headerDesign="STANDARD"
        areaDesign="" borderDesign="NONE" scrollingMode="NONE" isDragHandle="false" contentPadding="STANDARD">

        <f:attribute name="sap-delta-id" value="#{sap:toClientId('podSelection')}" />

        <ls:button facet="headerFunctions" type="HELP" tooltip="#{gapiI18nTransformer['toolTip.help.TEXT']}"
                   action="#{podSelectionHVPlugin.showPluginHelp}" pressInfoEnabled="true" pressInfoClientAction="submit"
                   pressInfoResponseData="delta" pressInfoParameters="#{sap:deltaUpdateId(sap:toClientId('podSelection'))}"/>

        <ls:gridLayout facet="content" width="100%" height="100%"  fixedLayout="true">
            <ls:gridLayoutRow facet="rows" >
                <ls:gridLayoutCell facet="cells" colSpan="4"  VAlign="TOP" >
                    <ls:gridLayout facet="content"  fixedLayout="true" width="100%" textOverflow="true">
                        <ls:gridLayoutRow facet="rows" >
                          <ls:gridLayoutCell id="labelCell1" binding="#{podSelectionHVPlugin.backingBean.labelCell1}" facet="cells"  height="1.5em" colSpan="1" HAlign="ENDOFLINE">
                          </ls:gridLayoutCell>
                          <ls:gridLayoutCell id="fieldCell1" binding="#{podSelectionHVPlugin.backingBean.fieldCell1}" facet="cells" height="1.5em" colSpan="1">
                          </ls:gridLayoutCell>
                          <ls:gridLayoutCell id="labelCell2" binding="#{podSelectionHVPlugin.backingBean.labelCell2}" facet="cells" height="1.5em" colSpan="1" HAlign="ENDOFLINE">
                          </ls:gridLayoutCell>
                          <ls:gridLayoutCell id="fieldCell2" binding="#{podSelectionHVPlugin.backingBean.fieldCell2}" facet="cells" colSpan="1">
                          </ls:gridLayoutCell>
                        </ls:gridLayoutRow>
                        <ls:gridLayoutRow facet="rows" >
                          <ls:gridLayoutCell id="labelCell3" binding="#{podSelectionHVPlugin.backingBean.labelCell3}" facet="cells" height="1.5em" colSpan="1" HAlign="ENDOFLINE">
                          </ls:gridLayoutCell>
                          <ls:gridLayoutCell id="fieldCell3" binding="#{podSelectionHVPlugin.backingBean.fieldCell3}" facet="cells" height="1.5em" colSpan="1">
                          </ls:gridLayoutCell>
                          <ls:gridLayoutCell id="labelCell4" binding="#{podSelectionHVPlugin.backingBean.labelCell4}" facet="cells" height="1.5em" colSpan="1" HAlign="ENDOFLINE">
                          </ls:gridLayoutCell>
                          <ls:gridLayoutCell id="fieldCell4" binding="#{podSelectionHVPlugin.backingBean.fieldCell4}" facet="cells" height="1.5em" colSpan="1">
                          </ls:gridLayoutCell>
                        </ls:gridLayoutRow>
                         <ls:gridLayoutRow facet="rows" >
                          <ls:gridLayoutCell id="fieldCell5" facet="cells" colSpan="4" width="100%" height="3.5em">
                          <sap:dataTable binding="#{sfcStatusTableBeanConfigurator.table}" 
                          value = "#{visualStatusIndicatorBean.sfcStatusList}"> 
                          <h:column>
                          <f:facet name="header">
            			  <h:outputText value="Start" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.startStatus}.png"/>
						  </h:column>	
                          <h:column>
                          <f:facet name="header">
            			  <h:outputText value="WorkInstructions" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.workInststatus}.png"/>
						  </h:column>
                          <h:column>
                          <f:facet name="header">
            			  <h:outputText value="Assembly" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.assemblystatus}.png"/>
						  </h:column>	
						     <h:column>					 
                          <f:facet name="header">
            			  <h:outputText value="DataCollection" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.dcStatus}.png"/>
						  </h:column>
						     <h:column>
                          <f:facet name="header">
            			  <h:outputText value="Tooling" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.toolStatus}.png"/>
						  </h:column>
						      <h:column>
                          <f:facet name="header">
            			  <h:outputText value="Buyoff" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.buyoffStatus}.png"/>
						  </h:column>		
						     <h:column>
                          <f:facet name="header">
            			  <h:outputText value="NonConformances" />
                          </f:facet>
						  <h:graphicImage url="/com/varian/wpmf/web/podplugin/#{visualStatusIndicatorBean.ncStatus}.png"/>
						  </h:column>				
						   </sap:dataTable>	
						        </ls:gridLayoutCell>                         
                        </ls:gridLayoutRow>
                         <ls:gridLayoutRow facet="rows" >
                         <ls:gridLayoutCell id="Refreshcell" facet="cells"  height="1.25em" colSpan="2" HAlign="ENDOFLINE">
						   <ls:button facet="content" id="REFRESH" text="Refresh" 
							action="#{visualStatusIndicatorBean.statusRefresh}" pressInfoEnabled="true"
							pressInfoClientAction="submit" enabled= "true" />
							</ls:gridLayoutCell>
                         </ls:gridLayoutRow>
                     </ls:gridLayout>
                </ls:gridLayoutCell>
                <ls:gridLayoutCell facet="cells" binding="#{podSelectionHVPlugin.backingBean.sessionInfoCell}" colSpan="1"  VAlign="TOP" HAlign="RIGHT" paddingRight="1em">
                    <table border="1"><tr><td><table border="0"><tr><td align="right">
                    <ls:textView id="sessionInfo1TextView" facet="content" text="#{podSelectionHVPlugin.sessionInfo1Text}"/></td></tr><tr><td align="right">
                    <ls:textView id="sessionInfo2TextView" facet="content" text="#{podSelectionHVPlugin.sessionInfo2Text}"/></td></tr></table></td></tr></table>
                </ls:gridLayoutCell>
            </ls:gridLayoutRow>
            <ls:gridLayoutRow facet="rows" >
                <ls:gridLayoutCell facet="cells" height="1px" >
                    <ls:inputField facet="content" id="OPERATION_CHANGED" name="OPERATION_CHANGED" visibility="NONE" width="1%" binding="#{podSelectionHVPlugin.backingBean.operationChangedField}"/>
                    <ls:inputField facet="content" id="RESOURCE_CHANGED" name="RESOURCE_CHANGED" visibility="NONE" width="1%" binding="#{podSelectionHVPlugin.backingBean.resourceChangedField}"/>
                </ls:gridLayoutCell>
            </ls:gridLayoutRow>
        </ls:gridLayout>
    </ls:panel>
</f:subview>