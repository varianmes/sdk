package com.varian.production.web.podplugin;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.convert.BooleanConverter;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import com.sap.me.common.ObjectReference;
import com.sap.me.demand.ShopOrderBasicConfiguration;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.extension.Services;
import com.sap.me.frame.Utils;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.utils.I18nUtility;
import com.sap.me.plant.ResourceConfigurationServiceInterface;
import com.sap.me.plant.ResourceKeyData;
import com.sap.me.productdefinition.AttachedToContext;
import com.sap.me.productdefinition.AttachmentConfigurationServiceInterface;
import com.sap.me.productdefinition.AttachmentType;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextRequest;
import com.sap.me.productdefinition.FindAttachmentByAttachedToContextResponse;
import com.sap.me.productdefinition.FoundReferencesResponse;
import com.sap.me.productdefinition.ItemBasicConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStep;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.status.StatusBasicConfiguration;
import com.sap.me.status.StatusServiceInterface;
import com.sap.me.tooling.FindToolNumbersByToolGroupAndFilterRequest;
import com.sap.me.tooling.GetLoggedQuantityRequest;
import com.sap.me.tooling.InvalidLoggedQuantityException;
import com.sap.me.tooling.InvalidToolGroupStatusException;
import com.sap.me.tooling.InvalidToolNumberStatusException;
import com.sap.me.tooling.LogToolRequest;
import com.sap.me.tooling.ResourceStatusEnum;
import com.sap.me.tooling.ToolGroupAttachmentPoint;
import com.sap.me.tooling.ToolGroupFullConfiguration;
import com.sap.me.tooling.ToolLogServiceInterface;
import com.sap.me.tooling.ToolNumberBasicConfiguration;
import com.sap.me.tooling.ToolNumberCalibrationPeriodExpiredException;
import com.sap.me.tooling.ToolQuantity;
import com.sap.me.tooling.ToolingConfigurationServiceInterface;
import com.sap.me.user.FindUserBySiteAndUserIdRequest;
import com.sap.me.user.UserBasicConfiguration;
import com.sap.me.user.UserConfigurationServiceInterface;
import com.sap.me.wpmf.InternalTableSelectionEventListener;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.TableSelectionEvent;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.tc.ls.internal.faces.component.UIMessageBar;
import com.sap.ui.faces.component.html.HtmlCommandButton;
import com.sap.ui.faces.component.sap.HtmlCommandBooleanCheckbox;
import com.varian.integration.connection.DataSourceConnection;
import com.varian.lsf.tool.SfcSelectionItem;
import com.varian.lsf.tool.ToolGroupItem;
import com.varian.lsf.tool.ToolNumberItem;
import com.visiprise.frame.service.ext.SecurityContext;

public class LogToolPlugin extends BasePodPlugin implements InternalTableSelectionEventListener{

	private static final String TOOL_LOG_SERVICE = "ToolLogService";
	private static final String TOOLING_CONFIGURATION_SERVICE = "ToolingConfigurationService";
	private static final String COM_SAP_ME_TOOLING = "com.sap.me.tooling";
	private static final String ATTACHMENT_CONFIGURATION_SERVICE = "AttachmentConfigurationService";
	private static final String COM_SAP_ME_DEMAND = "com.sap.me.demand";
	private static final String SHOP_ORDER_SERVICE = "ShopOrderService";
	private static final String ITEM_CONFIGURATION_SERVICE = "ItemConfigurationService";
	private static final String RESOURCE_CONFIGURATION_SERVICE = "ResourceConfigurationService";
	private static final String COM_SAP_ME_PLANT = "com.sap.me.plant";
	private static final String COM_SAP_ME_STATUS = "com.sap.me.status";
	private static final String STATUS_SERVICE = "StatusService";
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final String COM_SAP_ME_USER = "com.sap.me.user";
	private static final String USER_CONFIGURATION_SERVICE = "UserConfigurationService";
	/**
	 * 
	 */
	private static final long serialVersionUID = -6605985431400614513L;
	private List<SfcSelectionItem> sfcSelectionItemList = new ArrayList<SfcSelectionItem>();
	private List<ToolGroupItem> toolGroupList = new ArrayList<ToolGroupItem>();
	private List<ToolNumberItem> toolNumberList = new ArrayList<ToolNumberItem>();
	private TableConfigurator tableConfigBean = null;
	private TableConfigurator tabletoolGroupBean = null;
	private TableConfigurator tabletoolNumberBean = null;
	String[] listColumnNames = new String[] { "SELECT", "SFC_NUMBER",
			"SHOP_ORDER", "MATERIAL", "OPERATION", "RESOURCE", "QTY",
			"TOOL_STATUS", "DETAILS" };
	String[] columnDefs = new String[] { "select;TOOL_LOG.select.LABEL",
			"sfcNumber;TOOL_LOG.sfc.LABEL",
			"shopOrder;TOOL_LOG.shopOrder.LABEL",
			"material;TOOL_LOG.material.LABEL",
			"operation;TOOL_LOG.operation.LABEL",
			"resource;TOOL_LOG.resource.LABEL", "qty;TOOL_LOG.qty.LABEL",
			"toolStatus;TOOL_LOG.toolStatus.LABEL",
			"details;TOOL_LOG.details.LABEL" };
	String[] toolgrouplistColumnNames = new String[] {"TOOL_GROUP",
			"DESCRIPTION", "STATUS", "REQUIRED_QTY" };
	String[] toolgroupcolumnDefs = new String[] {
			"toolGroup;TOOL_LOG.toolGroup.LABEL",
			"toolgrpDesc;TOOL_LOG.toolgrpDesc.LABEL",
			"status;TOOL_LOG.status.LABEL",
			"toolReqdQty;TOOL_LOG.toolReqdQty.LABEL" };
	String[] toolnumberlistColumnNames = new String[] { "SELECT",
			"TOOL_NUMBER", "DESCRIPTION", "STATUS", "AVAILABLE_QTY",
			"TOOL_GROUP" };
	String[] toolnumbercolumnDefs = new String[] {
			"select;TOOL_LOG.select.LABEL",
			"toolNumber;TOOL_LOG.toolNumber.LABEL",
			"toolNumDesc;TOOL_LOG.toolNumDesc.LABEL",
			"toolNumStatus;TOOL_LOG.toolNumStatus.LABEL",
			"availableQty;TOOL_LOG.availableQty.LABEL",
			"toolGroup;TOOL_LOG.toolGroup.LABEL" };
	private String message;
	private String userId = SecurityContext.instance().getUserId();
	private UserConfigurationServiceInterface userConfigurationService;
	private SfcStateServiceInterface sfcStateService;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private StatusServiceInterface statusService;
	private ResourceConfigurationServiceInterface resourceConfigurationService;
	private String filterOperation = getCurrOperation();
	private ItemConfigurationServiceInterface itemConfigurationService;
	private ShopOrderServiceInterface shopOrderService;
	private AttachmentConfigurationServiceInterface attachmentConfigurationService;
	private ToolingConfigurationServiceInterface toolingConfigurationService;
	private ToolLogServiceInterface toolLogService;

	public String getCurrOperation() {
		try {
			this.filterOperation = getPodSelectionModel()
					.getResolvedOperation().getOperation();
		} catch (Exception e) {
			this.filterOperation = null;
		}
		return filterOperation;
	}

	@Override
	public void beforeLoad() throws Exception {
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFC");
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFCREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERATION");
		FacesUtility.removeSessionMapValue("operationBrowseBean");
		sfcSelectionItemList.clear();
		toolGroupList.clear();
		toolNumberList.clear();
		filterOperation = null;
		getCurrOperation();
	}

	public void closePlugin() {
		clear();
		FacesUtility.addScriptCommand("window.close();");
	}

	public LogToolPlugin() {
	
	}

	public void clear() {
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFC");
		FacesUtility.removeSessionMapValue("toolBrowseBean_SFCREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERREF");
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERATION");
		FacesUtility.removeSessionMapValue("operationBrowseBean");
		sfcSelectionItemList.clear();
		toolGroupList.clear();
		toolNumberList.clear();
		tabletoolGroupBean.setSelectedRows(null);
		filterOperation= null;
		setMessageBar(false, null);
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		UIComponent tablePanel1 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:toolgroupdisplayPanel");
		if (tablePanel1 != null) {
			FacesUtility.addControlUpdate(tablePanel1);
		}
		UIComponent tablePanel2 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:toolnumberdisplayPanel");
		if (tablePanel2 != null) {
			FacesUtility.addControlUpdate(tablePanel2);
		}
	}

	protected HashMap<String, String> getColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = columnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(listColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public List<SfcSelectionItem> getSfcSelectionItemList() {
		return sfcSelectionItemList;
	}

	public void setSfcSelectionItemList(
			List<SfcSelectionItem> sfcSelectionItemList) {
		this.sfcSelectionItemList = sfcSelectionItemList;
	}

	public TableConfigurator getTableConfigBean() {
		return tableConfigBean;
	}

	public void setTableConfigBean(TableConfigurator config) {
		this.tableConfigBean = config;
		if (tableConfigBean.getColumnBindings() == null
				|| tableConfigBean.getColumnBindings().size() < 1) {
			tableConfigBean.setListName(null);
			tableConfigBean.setColumnBindings(getColumnFieldMaping());
			tableConfigBean.setListColumnNames(listColumnNames);
			tableConfigBean.setAllowSelections(false);
			tableConfigBean.setMultiSelectType(false);
			ArrayList<String> editCols = new ArrayList<String>();
			// make these columns editable
			editCols.add("SELECT");
			tableConfigBean.setEditableColumns(editCols);
			// Lets try a check box for the boolean column
			tableConfigBean.setCellEditor("SELECT",
					new HtmlCommandBooleanCheckbox());
			configureCellEditor("SELECT");
			tableConfigBean
					.setColumnConverter(new BooleanConverter(), "SELECT");
			tableConfigBean.setColumnWidth("SELECT", "6em");
			// Lets add a details column
			tableConfigBean.setCellRenderer("DETAILS", new HtmlCommandButton());
			configureCellRenderer("DETAILS");
			tableConfigBean.configureTable();
		}
	}

	private void configureCellRenderer(String colName) {
		if (colName.equals("DETAILS")) {
			HtmlCommandButton detailComp = (HtmlCommandButton) tableConfigBean
					.getCellRenderer(colName);
			String url = "/com/varian/icons/icon_detail.gif";
			detailComp.setImage(url);
			detailComp.setAlt("Details");
			String binding = "#{logToolBean.processDetailsAction}";
			MethodExpression lstnr = FacesUtility.createMethodExpression(
					binding, null, new Class[0]);
			detailComp.setActionExpression(lstnr);

		}
	}

	public void processDetailsAction() {
		SfcSelectionItem rowData = (SfcSelectionItem) tableConfigBean
				.getTable().getRowData();
		FacesUtility.setSessionMapValue("toolBrowseBean_SFC", rowData
				.getSfcNumber());
		FacesUtility.setSessionMapValue("toolBrowseBean_SFCREF", rowData
				.getSfcRef());
		FacesUtility.setSessionMapValue("toolBrowseBean_OPERREF", rowData
				.getOpRef());
		FacesUtility.addScriptCommand("window.sfcDetails();");
	}

	@SuppressWarnings("unchecked")
	private void configureCellEditor(String colName) {
		UIComponent editCell = tableConfigBean.getCellEditor(colName);
		if (editCell instanceof javax.faces.component.EditableValueHolder) {
			Class[] clazz = new Class[1];
			clazz[0] = ValueChangeEvent.class;
			String editCellBinding = "#{logToolBean.processEditCellChange}";
			MethodExpression lstnr = FacesUtility.createMethodExpression(
					editCellBinding, null, clazz);
			MethodExpressionValueChangeListener changeListener = new MethodExpressionValueChangeListener(
					lstnr);
			((javax.faces.component.EditableValueHolder) editCell)
					.addValueChangeListener(changeListener);
		}

	}

	public void processEditCellChange(ValueChangeEvent event) {
		String oldVal = (event.getOldValue() != null) ? event.getOldValue()
				.toString() : "";
		String newVal = (event.getNewValue() != null) ? event.getNewValue()
				.toString() : "";
		if (oldVal.equals(newVal))
			return;
		setMessageBar(false, null);
	}

	public void setMessageBar(boolean render, LSMessageType messageType) {
		UIMessageBar messageBar = (UIMessageBar) findComponent(FacesUtility
				.getFacesContext().getViewRoot(), "ToolLogPage:messageBar");
		messageBar.setRendered(render);
		messageBar.setType(messageType);
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"ToolLogPage:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void operBrowse() {
		FacesUtility.setSessionMapValue("toolBrowseBean_OPERATION",
				filterOperation);
		FacesUtility.addScriptCommand("window.openBrowse();");
	}

	public void readSfcSelectionData() {
		// StackTraceElement[] stackTraceElements =
		// Thread.currentThread().getStackTrace();
		initServices();
		message = null;
		setMessageBar(false, null);
		String reqdQty = null;

		try {
			FindUserBySiteAndUserIdRequest request = new FindUserBySiteAndUserIdRequest(
					"0536", userId);
			UserBasicConfiguration usrbasicconfig = userConfigurationService
					.findUserBySiteAndUserId(request);
			String userRef = usrbasicconfig.getRef();

			// Data queryData = null;
			// SystemBase sysBase = SystemBase.createDefaultSystemBase();
			Connection connect = DataSourceConnection.getSQLConnection();
			PreparedStatement pstmt;
			String selActiveSFCs = null;
			if (filterOperation == null || filterOperation.equals("")) {
				selActiveSFCs = ("select b.OPERATION_BO,a.RESOURCE_BO,d.SFC_BO from SFC_IN_WORK a "
						+ "JOIN SFC_STEP b on a.SFC_STEP_BO = b.handle "
						+ "JOIN SFC_ROUTER c on b.SFC_ROUTER_BO = c.HANDLE "
						+ "JOIN SFC_ROUTING d on c.SFC_ROUTING_BO = d.handle "
						+ "where a.USER_BO = '" + userRef + "'");
			} else {
				String filterOperationRef = "OperationBO:0536,"
						+ filterOperation + ",#";
				selActiveSFCs = ("select b.OPERATION_BO,a.RESOURCE_BO,d.SFC_BO from SFC_IN_WORK a "
						+ "JOIN SFC_STEP b on a.SFC_STEP_BO = b.handle "
						+ "JOIN SFC_ROUTER c on b.SFC_ROUTER_BO = c.HANDLE "
						+ "JOIN SFC_ROUTING d on c.SFC_ROUTING_BO = d.handle "
						+ "where a.USER_BO = '"
						+ userRef
						+ "' and b.OPERATION_BO = '" + filterOperationRef + "'");
			}
			pstmt = connect.prepareStatement(selActiveSFCs);
			pstmt.executeQuery();
			ResultSet rs = pstmt.getResultSet();
			sfcSelectionItemList.clear();
			while (rs.next()) {
				SfcSelectionItem sfcselectionitem = new SfcSelectionItem();
				ObjectReference objSfcRef = new ObjectReference();
				objSfcRef.setRef(rs.getString("SFC_BO"));
				SfcBasicData sfcBasicData = sfcStateService
						.findSfcDataByRef(objSfcRef);
				ObjectReference objOperRef = new ObjectReference();
				objOperRef.setRef(rs.getString("OPERATION_BO"));
				OperationKeyData operKeyData = operationConfigurationService
						.findOperationKeyDataByRef(objOperRef);
				ObjectReference objStatusRef = new ObjectReference();
				objStatusRef.setRef(sfcBasicData.getStatusRef());
				ObjectReference objResRef = new ObjectReference(rs
						.getString("RESOURCE_BO"));
				ObjectReference objMatRef = new ObjectReference(sfcBasicData
						.getItemRef());
				ObjectReference objShopRef = new ObjectReference(sfcBasicData
						.getShopOrderRef());
				ShopOrderBasicConfiguration soBasicConfig = shopOrderService
						.findShopOrder(objShopRef);
				ItemBasicConfiguration itemBasicConfig = itemConfigurationService
						.findItemConfigurationByRef(objMatRef);
				ResourceKeyData resKeyData = resourceConfigurationService
						.findResourceKeyDataByRef(objResRef);
				StatusBasicConfiguration statusBasicConfig = statusService
						.findStatusByRef(objStatusRef);

				sfcselectionitem.setSfcNumber(sfcBasicData.getSfc());
				sfcselectionitem.setShopOrder(soBasicConfig.getShopOrder());
				sfcselectionitem.setMaterial(itemBasicConfig.getItem());
				sfcselectionitem.setOperation(operKeyData.getOperation());
				sfcselectionitem.setOpRevision(operKeyData.getRevision());
				sfcselectionitem.setResource(resKeyData.getResource());
				sfcselectionitem.setQty(sfcBasicData.getQty().toString());
				sfcselectionitem.setStatus(statusBasicConfig
						.getStatusDescription());
				sfcselectionitem.setItemRef(sfcBasicData.getItemRef());
				sfcselectionitem.setResRef(resKeyData.getRef().toString());
				sfcselectionitem.setSfcRef(sfcBasicData.getSfcRef());
				sfcselectionitem
						.setShopOrderRef(sfcBasicData.getShopOrderRef());
				Collection<SfcStep> sfcStepColl = sfcStateService
						.findCurrentRouterSfcStepsBySfcRef(objSfcRef);
				for (SfcStep sfcStepRow : sfcStepColl) {
					if (sfcStepRow.getOperationRef().equals(
							operKeyData.getRef().toString())) {
						sfcselectionitem
								.setRouterRef(sfcStepRow.getRouterRef());
					}
				}
				ObjectReference resolveOperReq = operationConfigurationService
						.resolveCurrentRevision(objOperRef);
				sfcselectionitem.setOpRef(resolveOperReq.getRef());
				// get current tooling status
				AttachedToContext attachedToContext = new AttachedToContext();
				attachedToContext.setOperationRef(resolveOperReq.getRef());
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
					int pendingCount = 0;
					for (FoundReferencesResponse attachment : attachList) {
						List<String> toolGrprefList = new ArrayList<String>();
						toolGrprefList = attachment.getRefList();
						String toolGrpref = toolGrprefList.get(0);
						String attachmentRef = null;
						// String currOpRefHash =
						// currentOpRef.substring(0,currentOpRef.length()-1)+
						// "#";
						// get reqd qty

						ObjectReference objToolgrpRef = new ObjectReference(
								toolGrpref);
						reqdQty = null;
						attachmentRef = null;
						try {
							ToolGroupFullConfiguration toolGrpFullConfig = toolingConfigurationService
									.readToolGroup(objToolgrpRef);
							List<ToolGroupAttachmentPoint> attachmentPointList = toolGrpFullConfig
									.getAttachmentList();
							for (ToolGroupAttachmentPoint attachmentPoint : attachmentPointList) {
								ObjectReference objAttachOperRef = new ObjectReference(
										attachmentPoint.getOperationRef());
								ObjectReference resolveAttachOperReq = operationConfigurationService
										.resolveCurrentRevision(objAttachOperRef);
								if (resolveOperReq.getRef().equals(
										resolveAttachOperReq.getRef())) {
									reqdQty = attachmentPoint
											.getQuantityRequired().toString();
									attachmentRef = attachmentPoint
											.getAttachmentRef();
								}
							}
						} catch (BusinessException e) {
							e.printStackTrace();
						}


						GetLoggedQuantityRequest logQtyReq = new GetLoggedQuantityRequest();
						logQtyReq.setAttachmentRef(attachmentRef);
						logQtyReq.setOperationRef(resolveOperReq.getRef());
						logQtyReq.setSfcRef(sfcBasicData.getSfcRef());
						BigDecimal loggedQty;
						try {
							loggedQty = toolLogService
									.getLoggedQuantity(logQtyReq);
							if (loggedQty.intValueExact() < Integer
									.parseInt(reqdQty)) {
								pendingCount = pendingCount + 1;
							}
						} catch (BusinessException e) {
							e.printStackTrace();
						}
					}
					if (pendingCount != 0) {
						sfcselectionitem.setToolStatus("Pending");
						sfcselectionitem.setSelect(true);
					} else {
						sfcselectionitem.setToolStatus("Closed");
						sfcselectionitem.setSelect(false);
					}
				} else {
					sfcselectionitem.setToolStatus("N/A");
					sfcselectionitem.setSelect(false);
				}
				sfcSelectionItemList.add(sfcselectionitem);
			}

			if (sfcSelectionItemList.size() == 0) {
				message = FacesUtility
						.getLocaleSpecificText("noRecords.MESSAGE");
				setMessageBar(true, LSMessageType.INFO);
				return;
			}

		} catch (BusinessException e) {
			message = I18nUtility.getMessageForException(e);
			setMessageBar(true, LSMessageType.ERROR);

		} catch (Exception e) {
			message = "Runtime Exception: Look at details in Log Viwer";
			Utils.errorMsg(e);
			setMessageBar(true, LSMessageType.ERROR);
		}
		// Make sure you add the table to the list of control updates so that
		// the new model value will be shown on the UI.
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		tabletoolGroupBean.setSelectedRows(null);
	}

	protected HashMap<String, String> gettoolGroupColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = toolgroupcolumnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(toolgrouplistColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public TableConfigurator getTabletoolGroupBean() {
		return tabletoolGroupBean;
	}

	public void setTabletoolGroupBean(TableConfigurator tabletoolGroupBean) {
		this.tabletoolGroupBean = tabletoolGroupBean;
		if (tabletoolGroupBean.getColumnBindings() == null
				|| tabletoolGroupBean.getColumnBindings().size() < 1) {
			tabletoolGroupBean.setListName(null);
			tabletoolGroupBean
					.setColumnBindings(gettoolGroupColumnFieldMaping());
			tabletoolGroupBean.setListColumnNames(toolgrouplistColumnNames);
			tabletoolGroupBean.setAllowSelections(true);
			tabletoolGroupBean.setMultiSelectType(true);
			tabletoolGroupBean.setSelectionBehavior("server");
			// enable row double click
			//copyOperconfigBean.setDoubleClick(true);
			tabletoolGroupBean.addInternalTableSelectionEventListener(this);
			tabletoolGroupBean.configureTable();
		}
	}

	protected HashMap<String, String> gettoolNumberColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = toolnumbercolumnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(toolnumberlistColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public TableConfigurator getTabletoolNumberBean() {
		return tabletoolNumberBean;
	}

	public void setTabletoolNumberBean(TableConfigurator tabletoolNumberBean) {
		this.tabletoolNumberBean = tabletoolNumberBean;
		if (tabletoolNumberBean.getColumnBindings() == null
				|| tabletoolNumberBean.getColumnBindings().size() < 1) {
			tabletoolNumberBean.setListName(null);
			tabletoolNumberBean
					.setColumnBindings(gettoolNumberColumnFieldMaping());
			tabletoolNumberBean.setListColumnNames(toolnumberlistColumnNames);
			tabletoolNumberBean.setAllowSelections(false);
			tabletoolNumberBean.setMultiSelectType(false);
			ArrayList<String> editCols = new ArrayList<String>();
			// make these columns editable
			editCols.add("SELECT");
			tabletoolNumberBean.setEditableColumns(editCols);
			// Lets try a check box for the boolean column
			tabletoolNumberBean.setCellEditor("SELECT",
					new HtmlCommandBooleanCheckbox());
			// let's add an event handler to this.
			configureCellEditor("SELECT");
			tabletoolNumberBean.setColumnConverter(new BooleanConverter(),
					"SELECT");
			tabletoolNumberBean.setColumnWidth("SELECT", "5em");
			tabletoolNumberBean.configureTable();
		}
	}

	public void retrieveToolGroups() {
		message = null;
		setMessageBar(false, null);
		String toolgroup = null;
		String toolgrpDesc = null;
		String reqdQty = null;
		String attachmentRef = null;
		String toolgroupStatus = null;
		String expirydate = null;

		int sflag = 0;
		toolGroupList.clear();
		initServices();
		if (sfcSelectionItemList.size() == 0) {
			message = FacesUtility.getLocaleSpecificText("noRecords.MESSAGE");
			setMessageBar(true, LSMessageType.ERROR);
			return;
		} else {
			for (SfcSelectionItem sfcRow : sfcSelectionItemList) {
				if (sfcRow.isSelect()) {
					sflag = 1;
				}
			}
			if (sflag == 0) {
				message = FacesUtility
						.getLocaleSpecificText("noSFCsSelected.MESSAGE");
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
		}

		if (filterOperation == null || filterOperation.equals("")) {
			String selectedOp = null;
			for (SfcSelectionItem sfcrow : sfcSelectionItemList) {
				if (sfcrow.isSelect()) {
					if (selectedOp == null) {
						selectedOp = sfcrow.getOperation();
					} else if (!selectedOp.equals(sfcrow.getOperation())) {
						message = FacesUtility
								.getLocaleSpecificText("multipleOperations.MESSAGE");
						setMessageBar(true, LSMessageType.ERROR);
						return;
					}
				}
			}
		}

		for (SfcSelectionItem sfcrow : sfcSelectionItemList) {
			if (sfcrow.isSelect()) {
				AttachedToContext attachedToContext = new AttachedToContext();
				attachedToContext.setOperationRef(sfcrow.getOpRef());
				FindAttachmentByAttachedToContextRequest attachreq = new FindAttachmentByAttachedToContextRequest();
				attachreq.setAttachedToContext(attachedToContext);
				attachreq.setSkipRevision(true);
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
						ObjectReference objToolgrpRef = new ObjectReference(
								toolGrpref);
						toolgroup = null;
						toolgrpDesc = null;
						reqdQty = null;
						attachmentRef = null;
						toolgroupStatus = null;
						expirydate = null;
						try {
							ToolGroupFullConfiguration toolGrpFullConfig = toolingConfigurationService
									.readToolGroup(objToolgrpRef);
							toolgroup = toolGrpFullConfig.getToolGroup();
							toolgrpDesc = toolGrpFullConfig.getDescription();
							ObjectReference objStatusRef = new ObjectReference(
									toolGrpFullConfig.getStatusRef());
							StatusBasicConfiguration statusBasicConfig = statusService
									.findStatusByRef(objStatusRef);
							toolgroupStatus = statusBasicConfig
									.getStatusDescription();
							if (!(toolGrpFullConfig.getExpirationDate() == null)) {
								String date = toolGrpFullConfig
										.getExpirationDate().toString();
								expirydate = date.substring(0, date
										.lastIndexOf("T"));
							}
							List<ToolGroupAttachmentPoint> attachmentPointList = toolGrpFullConfig
									.getAttachmentList();

							for (ToolGroupAttachmentPoint attachmentPoint : attachmentPointList) {
								ObjectReference objAttachOperRef = new ObjectReference(
										attachmentPoint.getOperationRef());
								ObjectReference resolveOperReq = operationConfigurationService
										.resolveCurrentRevision(objAttachOperRef);
								if (sfcrow.getOpRef().equals(
										resolveOperReq.getRef())) {
									reqdQty = attachmentPoint
											.getQuantityRequired().toString();
									attachmentRef = attachmentPoint
											.getAttachmentRef();
								}
							}
						} catch (BusinessException e) {
							e.printStackTrace();
						}
						if (toolGroupList.size() > 0) {
							int flag = 0;
							for (ToolGroupItem row : toolGroupList) {
								if (row.getToolGroup().equals(toolgroup)) {
									flag = 1;
								}
							}
							if (flag == 0) {
								ToolGroupItem newitem = new ToolGroupItem();
								newitem.setSelect(false);
								newitem.setToolGroup(toolgroup);
								newitem.setToolGroupRef(toolGrpref);
								newitem.setToolgrpDesc(toolgrpDesc);
								newitem.setToolReqdQty(reqdQty);
								newitem.setAttachmentRef(attachmentRef);
								newitem.setStatus(toolgroupStatus);
								newitem.setToolGrpExpDate(expirydate);
								toolGroupList.add(newitem);
							}

						} else {
							ToolGroupItem newitem = new ToolGroupItem();
							newitem.setSelect(false);
							newitem.setToolGroup(toolgroup);
							newitem.setToolGroupRef(toolGrpref);
							newitem.setToolgrpDesc(toolgrpDesc);
							newitem.setToolReqdQty(reqdQty);
							newitem.setAttachmentRef(attachmentRef);
							newitem.setStatus(toolgroupStatus);
							newitem.setToolGrpExpDate(expirydate);
							toolGroupList.add(newitem);
						}
					}
				}
			}
		}
		if (toolGroupList.size() == 0) {
			message = FacesUtility
					.getLocaleSpecificText("noToolGroups.MESSAGE");
			setMessageBar(true, LSMessageType.INFO);
			return;
		} else {
			setToolGroupList(toolGroupList);
		}
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:toolgroupdisplayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		tabletoolGroupBean.setSelectedRows(null);
	}

	@SuppressWarnings("unchecked")
	public void retrieveToolNumbers() {
		message = null;
		setMessageBar(false, null);
		String toolnumber = null;
		String toolNumberStatus = null;
		String expirydate = null;
		int sflag = 0;
		toolNumberList.clear();
		initServices();
		List<ToolGroupItem> selToolGrpList = (List<ToolGroupItem>) tabletoolGroupBean.getSelectedItems();
		if (sfcSelectionItemList.size() == 0) {
			message = FacesUtility.getLocaleSpecificText("noRecords.MESSAGE");
			setMessageBar(true, LSMessageType.ERROR);
			return;
		} else {
			for (SfcSelectionItem sfcRow : sfcSelectionItemList) {
				if (sfcRow.isSelect()) {
					sflag = 1;
				}
			}
			if (sflag == 0) {
				message = FacesUtility
						.getLocaleSpecificText("noSFCsSelected.MESSAGE");
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
		}
	
		for (ToolGroupItem toolGrprow : selToolGrpList) {
				FindToolNumbersByToolGroupAndFilterRequest toolNumReq = new FindToolNumbersByToolGroupAndFilterRequest();
				toolNumReq.setToolGroupRef(toolGrprow.getToolGroupRef());
				List<ToolNumberBasicConfiguration> toolNumList = null;
				try {
					toolNumList = toolingConfigurationService
							.findToolNumbersByToolGroupAndFilterByName(toolNumReq);
				} catch (BusinessException e) {
					e.printStackTrace();
				}
				for (ToolNumberBasicConfiguration toolNumConfig : toolNumList) {
					toolnumber = toolNumConfig.getToolNumber();
					if (!(toolNumConfig.getExpirationDate() == null)) {
						String date = toolNumConfig.getExpirationDate()
								.toString();
						expirydate = date.substring(0, date.lastIndexOf("T"));
					}
					ResourceStatusEnum toolStatus = toolNumConfig.getStatus();
					toolNumberStatus = toolStatus.toString();

					if (toolNumList.size() > 0) {
						int flag = 0;
						for (ToolNumberItem row : toolNumberList) {
							if (row.getToolNumber().equals(toolnumber)) {
								flag = 1;
							}
						}
						if (flag == 0) {
							ToolNumberItem newitem = new ToolNumberItem();
							newitem.setSelect(false);
							newitem.setToolNumber(toolnumber);
							newitem.setToolNumDesc(toolNumConfig
									.getDescription());
							newitem.setToolNumStatus(toolNumberStatus);
							newitem.setAvailableQty(toolNumConfig
									.getAvailableQuantity().toString());
							newitem.setToolNumRef(toolNumConfig.getRef()
									.toString());
							newitem.setToolGroup(toolGrprow.getToolGroup());
							newitem.setToolNumExpDate(expirydate);
							toolNumberList.add(newitem);
						}

					} else {
						ToolNumberItem newitem = new ToolNumberItem();
						newitem.setSelect(false);
						newitem.setToolNumber(toolnumber);
						newitem.setToolNumDesc(toolNumConfig.getDescription());
						newitem.setToolNumStatus(toolNumberStatus);
						newitem.setAvailableQty(toolNumConfig
								.getAvailableQuantity().toString());
						newitem
								.setToolNumRef(toolNumConfig.getRef()
										.toString());
						newitem.setToolGroup(toolGrprow.getToolGroup());
						newitem.setToolNumExpDate(expirydate);
						toolNumberList.add(newitem);
					}
				}

			
		}
		if (toolNumberList.size() == 0) {
			message = FacesUtility
					.getLocaleSpecificText("noToolNumbers.MESSAGE");
			setMessageBar(true, LSMessageType.INFO);
			return;
		}
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:toolnumberdisplayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);

		}
		selToolGrpList.clear();
	}

	@SuppressWarnings("unchecked")
	public void logToolNumbers() throws SQLException {
		message = null;
		setMessageBar(false, null);
		initServices();
		int sflag = 0;
		int tnflag = 0;
		if (sfcSelectionItemList.size() == 0) {
			message = FacesUtility.getLocaleSpecificText("noRecords.MESSAGE");
			setMessageBar(true, LSMessageType.ERROR);
			return;
		} else {
			for (SfcSelectionItem sfcRow : sfcSelectionItemList) {
				if (sfcRow.isSelect()) {
					sflag = 1;
				}
			}
			if (sflag == 0) {
				message = FacesUtility
						.getLocaleSpecificText("noSFCsSelected.MESSAGE");
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
		}
		if (toolNumberList.size() == 0) {
			message = FacesUtility
					.getLocaleSpecificText("noToolNumbers.MESSAGE");
			setMessageBar(true, LSMessageType.ERROR);
			return;
		} else {
			for (ToolNumberItem toolNumRow : toolNumberList) {
				if (toolNumRow.isSelect()) {
					tnflag = 1;
				}
			}
			if (tnflag == 0) {
				message = FacesUtility
						.getLocaleSpecificText("noToolNumbersSelected.MESSAGE");
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
		}
		List<ToolGroupItem> selToolGrpList = (List<ToolGroupItem>) tabletoolGroupBean.getSelectedItems();
		// check if only one tool number form each tool group is selected
		ArrayList<String> myList = new ArrayList<String>();
		for (ToolNumberItem toolNumRow : toolNumberList) {
			if (toolNumRow.isSelect()) {
				if (myList.contains(toolNumRow.getToolGroup())) {
					message = FacesUtility
							.getLocaleSpecificText("There are multiple tool numbers selected for Tool Group "
									+ toolNumRow.getToolGroup()
									+ ".Please select one tool number only for each tool group");
					setMessageBar(true, LSMessageType.ERROR);
					return;
				} else {
					myList.add(toolNumRow.getToolGroup());
				}
			}
		}
		// check if all selected tool numbers have status enabled
		// check for tool group and tool number expiry

		for (ToolNumberItem toolNumRow : toolNumberList) {
			if (toolNumRow.isSelect()) {
				if (!toolNumRow.getToolNumStatus().equals("ENABLED")) {
					message = FacesUtility.getLocaleSpecificText("Tool Number "
							+ toolNumRow.getToolNumber() + " is not enabled");
					setMessageBar(true, LSMessageType.ERROR);
					return;
				}

				if (!(toolNumRow.getToolNumExpDate() == null)) {
					Date currentDate = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date expiryDate = sdf.parse(toolNumRow
								.getToolNumExpDate());
						if (currentDate.after(expiryDate)) {
							message = FacesUtility
									.getLocaleSpecificText("Tool Number "
											+ toolNumRow.getToolNumber()
											+ " Calibration has expired");
							setMessageBar(true, LSMessageType.ERROR);
							return;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// check for tool group status

		for (ToolGroupItem toolGrpRow : selToolGrpList) {
				if (toolGrpRow.getStatus().equals("Disabled")) {
					message = FacesUtility.getLocaleSpecificText("Tool Group "
							+ toolGrpRow.getToolGroup() + " is not enabled");
					setMessageBar(true, LSMessageType.ERROR);
					return;
				}

				if (!(toolGrpRow.getToolGrpExpDate() == null)) {
					Date currentDate = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date expiryDate = sdf.parse(toolGrpRow
								.getToolGrpExpDate());
						if (currentDate.after(expiryDate)) {
							message = FacesUtility
									.getLocaleSpecificText("Tool Group "
											+ toolGrpRow.getToolGroup()
											+ " Calibration has expired");
							setMessageBar(true, LSMessageType.ERROR);
							return;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
		}
	
		for (SfcSelectionItem sfcRow : sfcSelectionItemList) {
			if (sfcRow.isSelect() && sfcRow.getToolStatus().equals("Closed")) {
				message = FacesUtility
						.getLocaleSpecificText("Tool logging for SFC "
								+ sfcRow.getSfcNumber()
								+ " is already Closed. Please unselect that Sfc and try again");
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
			if (sfcRow.isSelect() && sfcRow.getToolStatus().equals("N/A")) {
				message = FacesUtility
						.getLocaleSpecificText("Tool logging for SFC "
								+ sfcRow.getSfcNumber()
								+ " is not applicable. Please unselect that Sfc and try again");
				setMessageBar(true, LSMessageType.ERROR);
				return;
			}
		}

		for (SfcSelectionItem sfcRow : sfcSelectionItemList) {
			// get logged quantity
			// if tool num is not enabled , throw error
			// calculate tool qty = reqd qty - logged qty
			// if available qty is less than qty, throw error
			// log tool
			// publish message
			// clear all tables
			
			if (sfcRow.isSelect()) {

				for (ToolGroupItem toolgroupRow : selToolGrpList) {
						GetLoggedQuantityRequest logQtyReq = new GetLoggedQuantityRequest();
						logQtyReq.setAttachmentRef(toolgroupRow
								.getAttachmentRef());
						logQtyReq.setOperationRef(sfcRow.getOpRef());
						logQtyReq.setSfcRef(sfcRow.getSfcRef());
						BigDecimal loggedQty = null;

						for (ToolNumberItem toolNumRow : toolNumberList) {
							if (toolNumRow.isSelect()
									&& toolNumRow.getToolGroup().equals(
											toolgroupRow.getToolGroup())) {
								try {
									loggedQty = toolLogService
											.getLoggedQuantity(logQtyReq);
								} catch (BusinessException e) {
									e.printStackTrace();
								}
								int requiredQty = Integer.parseInt(toolgroupRow
										.getToolReqdQty())
										- loggedQty.intValueExact();

								if (requiredQty <= 0) {
									message = FacesUtility
											.getLocaleSpecificText("Tool from Tool group "
													+ toolgroupRow
															.getToolGroup()
													+ " has already been logged for SFC "
													+ sfcRow.getSfcNumber());
									setMessageBar(true, LSMessageType.ERROR);
									return;
								}

								int availableQty = Integer.parseInt(toolNumRow
										.getAvailableQty());

								if (requiredQty > availableQty) {
									message = FacesUtility
											.getLocaleSpecificText("Available Qty of Tool number "
													+ toolNumRow
															.getToolNumber()
													+ " is less than the required qty.Rolling back transaction ");
									setMessageBar(true, LSMessageType.ERROR);
									return;
								}

								LogToolRequest logToolReq = new LogToolRequest();
								logToolReq.setAttachmentRef(toolgroupRow
										.getAttachmentRef());
								logToolReq
										.setComments("Logged Via Tooling Plugin");
								logToolReq.setItemRef(sfcRow.getItemRef());
								logToolReq.setOperationRef(sfcRow.getOpRef());
								logToolReq.setResourceRef(sfcRow.getResRef());
								logToolReq.setRouterRef(sfcRow.getRouterRef());
								logToolReq.setSfcRef(sfcRow.getSfcRef());
								logToolReq.setToolGroupRef(toolgroupRow
										.getToolGroupRef());
								logToolReq.setShopOrderRef(sfcRow
										.getShopOrderRef());
								List<ToolQuantity> toolQtyList = new ArrayList<ToolQuantity>();
								ToolQuantity toolQtyItem = new ToolQuantity();
								BigDecimal reqdQtyBd = new BigDecimal(
										requiredQty);
								toolQtyItem.setQuantity(reqdQtyBd);
								ObjectReference objtoolNumRef = new ObjectReference(
										toolNumRow.getToolNumRef());
								toolQtyItem.setToolNumber(objtoolNumRef);
								toolQtyList.add(toolQtyItem);
								logToolReq.setToolNumberList(toolQtyList);
								try {
									toolLogService.logTool(logToolReq);
									// tool number is now logged, so tool number
									// status needs to be reset to enabled,
									// so that this tool can be logged for the
									// next sfc
									Connection connect = DataSourceConnection
											.getSQLConnection();
									PreparedStatement pstmt;
									String updateStmnt = ("update TOOL_NUMBER set STATUS_BO = 'StatusBO:0536,301'"
											+ " where HANDLE = '"
											+ toolNumRow.getToolNumRef() + "'");
									pstmt = connect
											.prepareStatement(updateStmnt);
									pstmt.executeUpdate();
									connect.close();
								} catch (ToolNumberCalibrationPeriodExpiredException e) {
									message = "Calibration period for tool number "
											+ toolNumRow.getToolNumber()
											+ " or tool group "
											+ toolNumRow.getToolGroup()
											+ " has expired.";
									setMessageBar(true, LSMessageType.ERROR);
									e.printStackTrace();
									return;
								} catch (InvalidLoggedQuantityException e) {
									message = "Logged qty for tool number "
											+ toolNumRow.getToolNumber()
											+ " is Invalid.";
									setMessageBar(true, LSMessageType.ERROR);
									e.printStackTrace();
									return;
								} catch (InvalidToolGroupStatusException e) {
									message = "Status of tool group "
											+ toolNumRow.getToolGroup()
											+ " is Invalid.";
									setMessageBar(true, LSMessageType.ERROR);
									e.printStackTrace();
									return;
								} catch (InvalidToolNumberStatusException e) {
									message = "Status of tool number "
											+ toolNumRow.getToolNumber()
											+ " is Invalid.";
									setMessageBar(true, LSMessageType.ERROR);
									e.printStackTrace();
									return;

								} catch (BusinessException e) {
									message = "Unknown Business Exception. Please report to System Adminstrator or Developer";
									setMessageBar(true, LSMessageType.ERROR);
									e.printStackTrace();
									return;

								} catch (Exception ex) {
									ex.printStackTrace();
									message = "Unknown Exception. Please report to System Adminstrator or Developer";
									setMessageBar(true, LSMessageType.ERROR);
									return;
								}

							}
						}
				}
			}

		}
		// filterOperation = null;
		toolGroupList.clear();
		UIComponent tablePanel1 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:toolgroupdisplayPanel");
		if (tablePanel1 != null) {
			FacesUtility.addControlUpdate(tablePanel1);
		}
		toolNumberList.clear();
		UIComponent tablePanel2 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "ToolLogPage:toolnumberdisplayPanel");
		if (tablePanel2 != null) {
			FacesUtility.addControlUpdate(tablePanel2);
		}
		readSfcSelectionData();
		message = "Tools are logged for all the selected SFCs Successfully";
		setMessageBar(true, LSMessageType.OK);
		selToolGrpList.clear();
	}

	/**
	 * Initialization of services that are represented as fields.
	 */
	private void initServices() {
		userConfigurationService = Services.getService(COM_SAP_ME_USER,
				USER_CONFIGURATION_SERVICE);
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,
				SFC_STATE_SERVICE);
		operationConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, OPERATION_CONFIGURATION_SERVICE);
		statusService = Services.getService(COM_SAP_ME_STATUS, STATUS_SERVICE);
		resourceConfigurationService = Services.getService(COM_SAP_ME_PLANT,
				RESOURCE_CONFIGURATION_SERVICE);
		itemConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, ITEM_CONFIGURATION_SERVICE);
		shopOrderService = Services.getService(COM_SAP_ME_DEMAND,
				SHOP_ORDER_SERVICE);
		attachmentConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, ATTACHMENT_CONFIGURATION_SERVICE);
		toolingConfigurationService = Services.getService(COM_SAP_ME_TOOLING,
				TOOLING_CONFIGURATION_SERVICE);
		toolLogService = Services.getService(COM_SAP_ME_TOOLING,
				TOOL_LOG_SERVICE);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFilterOperation() {
		return filterOperation;
	}

	public void setFilterOperation(String filterOperation) {
		this.filterOperation = filterOperation;
	}

	public List<ToolGroupItem> getToolGroupList() {
		return toolGroupList;
	}

	public void setToolGroupList(List<ToolGroupItem> toolGroupList) {
		this.toolGroupList = toolGroupList;
	}

	public List<ToolNumberItem> getToolNumberList() {
		return toolNumberList;
	}

	public void setToolNumberList(List<ToolNumberItem> toolNumberList) {
		this.toolNumberList = toolNumberList;
	}

	public void processDialogClosed() {
	}

	public void processTableSelectionEvent(TableSelectionEvent event) {
		TableConfigurator conf = (TableConfigurator) event.getSource();
		if (!tabletoolGroupBean.equals(conf)) {
			return;
		}
	//	Object selectedObject = event.getCurrentSelection();
	//	if (selectedObject instanceof ToolGroupItem) {			
		retrieveToolNumbers();
		//}
	}

}
