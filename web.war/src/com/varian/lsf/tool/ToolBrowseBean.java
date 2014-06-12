package com.varian.lsf.tool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.domain.DomainServiceFactory;
import com.sap.me.frame.domain.DomainServiceInterface;
import com.sap.me.productdefinition.AttachedToContext;
import com.sap.me.productdefinition.AttachmentConfigurationServiceInterface;
import com.sap.me.productdefinition.AttachmentType;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextRequest;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextResponse;
import com.sap.me.productdefinition.FoundReferencesResponse;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.domain.OperationDO;
import com.sap.me.tooling.GetLoggedQuantityRequest;
import com.sap.me.tooling.ToolGroupAttachmentPoint;
import com.sap.me.tooling.ToolGroupFullConfiguration;
import com.sap.me.tooling.ToolLogServiceInterface;
import com.sap.me.tooling.ToolingConfigurationServiceInterface;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.util.FacesUtility;

public class ToolBrowseBean {

	private static final String TOOL_LOG_SERVICE = "ToolLogService";
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String TOOLING_CONFIGURATION_SERVICE = "ToolingConfigurationService";
	private static final String COM_SAP_ME_TOOLING = "com.sap.me.tooling";
	private static final String ATTACHMENT_CONFIGURATION_SERVICE = "AttachmentConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	DomainServiceInterface<OperationDO> operationDOservice = DomainServiceFactory.getServiceByClass(OperationDO.class);
	private List<ToolBrowseItem> toolBrowseList = new ArrayList<ToolBrowseItem>();
	private TableConfigurator configBean = null;
	String[] columnDefs = new String[] { "toolgroup;TOOL_DETAILS.toolgroup.LABEL", "requiredQty;TOOL_DETAILS.requiredQty.LABEL","loggedQty;TOOL_DETAILS.loggedQty.LABEL"};
	String[] listColumnNames = new String[] { "TOOL_GROUP", "REQUIRED_QTY","LOGGED_QTY"};
	private String sfc;
	private String sfcRef;
	private String opRef;
	private AttachmentConfigurationServiceInterface attachmentConfigurationService;
	private ToolingConfigurationServiceInterface toolingConfigurationService;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private ToolLogServiceInterface toolLogService;
	
	
	public ToolBrowseBean() {
		sfc = (String) FacesUtility.getSessionMapValue("toolBrowseBean_SFC");
		sfcRef = (String) FacesUtility.getSessionMapValue("toolBrowseBean_SFCREF");
		opRef = (String)FacesUtility.getSessionMapValue("toolBrowseBean_OPERREF");
	}

	public TableConfigurator getConfigBean() {
		return configBean;
	}

	
	public String getSfc() {
		sfc = (String) FacesUtility.getSessionMapValue("toolBrowseBean_SFC");
		return sfc;
	}

	public void setSfc(String sfc) {
		this.sfc = sfc;
	}

	public String getSfcRef() {
		sfcRef = (String) FacesUtility.getSessionMapValue("toolBrowseBean_SFCREF");
		return sfcRef;
	}

	public void setSfcRef(String sfcRef) {
		this.sfcRef = sfcRef;
	}

	public String getOpRef() {
		opRef = (String)FacesUtility.getSessionMapValue("toolBrowseBean_OPERREF");
		return opRef;
	}

	public void setOpRef(String opRef) {
		this.opRef = opRef;
	}

	public void setConfigBean(TableConfigurator configBean) {
		this.configBean = configBean;
		if (configBean.getColumnBindings() == null || configBean.getColumnBindings().size() < 1) {
			configBean.setListName(null);
			configBean.setColumnBindings(getColumnFieldMaping());
			configBean.setListColumnNames(listColumnNames);
			configBean.setAllowSelections(false);
			configBean.setMultiSelectType(false);
			configBean.configureTable();
		}
	}



	public List<ToolBrowseItem> getToolBrowseList() {
		getSfc();
		getSfcRef();
		getOpRef();
		initServices();
		toolBrowseList.clear();
		String toolGrp =null;
		String reqdQty = null;
		AttachedToContext attachedToContext = new AttachedToContext();
		attachedToContext.setOperationRef(opRef);
		FindAttachmentByAttachedToContextRequest attachreq = new FindAttachmentByAttachedToContextRequest();
		attachreq.setAttachedToContext(attachedToContext);
		attachreq.setSkipRevision(false);
		attachreq.setIgnoreExtraAttach(true);
		attachreq.setAttachmentType(AttachmentType.TOOLING);
		FindAttachmentByAttachedToContextResponse attachResp = new FindAttachmentByAttachedToContextResponse();
		try {
			attachResp = attachmentConfigurationService
					.findAttachmentByAttacheds(attachreq);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		List<FoundReferencesResponse> attachList = new ArrayList<FoundReferencesResponse>();
		attachList = attachResp.getFoundReferencesResponseList();
		if (attachList != null && attachList.size() > 0) {
			for (FoundReferencesResponse attachment : attachList) {
				List<String> toolGrprefList = new ArrayList<String>();
				toolGrprefList = attachment.getRefList();
				String toolGrpref = toolGrprefList.get(0);
				String attachmentRef = null;
				//get reqd qty
				ObjectReference objToolgrpRef = new ObjectReference(
						toolGrpref);
				reqdQty = null;
				toolGrp = null;
				attachmentRef = null;
				try {
					ToolGroupFullConfiguration toolGrpFullConfig = toolingConfigurationService
							.readToolGroup(objToolgrpRef);
					List<ToolGroupAttachmentPoint> attachmentPointList = toolGrpFullConfig
							.getAttachmentList();
					toolGrp = toolGrpFullConfig.getToolGroup();
					for (ToolGroupAttachmentPoint attachmentPoint : attachmentPointList) {
						ObjectReference objAttachOperRef = new ObjectReference(
								attachmentPoint.getOperationRef());
						ObjectReference resolveAttachOperReq = operationConfigurationService
								.resolveCurrentRevision(objAttachOperRef);
						if (opRef.equals(
								resolveAttachOperReq.getRef())) {
							reqdQty = attachmentPoint
									.getQuantityRequired().toString();
							attachmentRef = attachmentPoint
									.getAttachmentRef();
						}
					}
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//
				
				GetLoggedQuantityRequest logQtyReq = new GetLoggedQuantityRequest();
				logQtyReq.setAttachmentRef(attachmentRef);
				logQtyReq.setOperationRef(opRef);
				logQtyReq.setSfcRef(sfcRef);
				BigDecimal loggedQty;
				try {
					loggedQty = toolLogService.getLoggedQuantity(logQtyReq);
					ToolBrowseItem toolBrowseRow = new ToolBrowseItem();
					toolBrowseRow.setToolgroup(toolGrp);
					toolBrowseRow.setRequiredQty(reqdQty);
					toolBrowseRow.setLoggedQty(loggedQty.toString());
					toolBrowseList.add(toolBrowseRow);		
				} catch (BusinessException e) {
					e.printStackTrace();
				}
				
				
			}
	}
		return toolBrowseList;
	}

	public void setToolBrowseList(List<ToolBrowseItem> toolBrowseList) {		
		this.toolBrowseList = toolBrowseList;
	}

	protected HashMap<String, String> getColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = columnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(listColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public void close() {
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFC");
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFCREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean");
		opRef = null;
		sfc = null;
		sfcRef = null;
		FacesUtility.addScriptCommand("window.close();");
	}

	public void processWindowClosed() {
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFC");
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFCREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean");
		opRef = null;
		sfc = null;
		sfcRef = null;
		FacesUtility.addScriptCommand("window.close();");		
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		attachmentConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,ATTACHMENT_CONFIGURATION_SERVICE);
		toolingConfigurationService = Services.getService(COM_SAP_ME_TOOLING,TOOLING_CONFIGURATION_SERVICE);
		operationConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,OPERATION_CONFIGURATION_SERVICE);
		toolLogService = Services.getService(COM_SAP_ME_TOOLING,TOOL_LOG_SERVICE);
	}
	
}
