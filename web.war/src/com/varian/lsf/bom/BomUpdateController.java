package com.varian.lsf.bom;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationException;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemKeyData;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationExtendedConfiguration;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.productdefinition.OperationSearchRequest;
import com.sap.me.productdefinition.OperationSearchResult;
import com.sap.me.wpmf.BaseManagedBean;
import com.sap.me.wpmf.InternalTableSelectionEventListener;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.TableSelectionEvent;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.tc.ls.internal.faces.component.UIMessageBar;
import com.sap.ui.faces.component.sap.UICommandInputText;
import com.varian.integration.connection.DataSourceConnection;

/**
 * @author a553425
 * 
 */
public class BomUpdateController extends BaseManagedBean implements
		ActionListener, InternalTableSelectionEventListener {

	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String ITEM_CONFIGURATION_SERVICE = "ItemConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final String BOMCONFIGURATION_SERVICE = "BOMConfigurationService";
	/**
	 * 
	 */
	private static final long serialVersionUID = 8294674406606393199L;
	private List<BomDisplayItem> bomDisplayList = new ArrayList<BomDisplayItem>();
	private List<BomComponentItem> bomComponentList = new ArrayList<BomComponentItem>();
	private TableConfigurator tableBomConfigBean = null;
	private TableConfigurator tableBomComponentBean = null;
	String[] bomDisplayListColumnNames = new String[] { "BOM", "REVISION",
			"BOM_STATUS", "CURRENT_VERSION" };
	String[] bomDisplaycolumnDefs = new String[] { "bom;BOM_UPDATE.bom.LABEL",
			"revision;BOM_UPDATE.revision.LABEL",
			"bomStatus;BOM_UPDATE.bomStatus.LABEL",
			"isCurrVersion;BOM_UPDATE.isCurrVersion.LABEL" };
	String[] bomComponentListColumnNames = new String[] { "SEQUENCE",
			"COMPONENT_VERSION", "OPERATION", "NEW_OPERATION" };
	String[] bomComponentcolumnDefs = new String[] {
			"assySequence;BOM_UPDATE.assySequence.LABEL",
			"component;BOM_UPDATE.component.LABEL",
			"assyoperation;BOM_UPDATE.assyoperation.LABEL",
			"newoperation;BOM_UPDATE.newoperation.LABEL" };
	private String message = null;
	private String updateOperation = null;
	private String newCheckBoxSelection = null;
	private String newBomStatus = null;
	private String newCurrStatus = null;
	private BOMConfigurationServiceInterface bOMConfigurationService;
	private ItemConfigurationServiceInterface itemConfigurationService;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private String bomFilter = null;

	public BomUpdateController() {

	}

	public void setMessageBar(boolean render, LSMessageType messageType) {
		UIMessageBar messageBar = (UIMessageBar) findComponent(FacesUtility
				.getFacesContext().getViewRoot(), "bomUpdateForm:messageBar");
		messageBar.setRendered(render);
		messageBar.setType(messageType);
		UIComponent fieldButtonPanel = findComponent(FacesUtility
				.getFacesContext().getViewRoot(),
				"bomUpdateForm:fieldButtonPanel");
		if (fieldButtonPanel != null) {
			FacesUtility.addControlUpdate(fieldButtonPanel);
		}
	}

	public void clear() {

	}

	public void retrieveBomData() throws SQLException {
		setMessageBar(false, null);
		Connection connect = DataSourceConnection.getSQLConnection();
		PreparedStatement pstmt;
		String selBoms = null;
		if (!(bomFilter==null)){
			if (newCheckBoxSelection.equals("NEW")) {
				selBoms = ("select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,205' and BOM_TYPE = 'U' and has_been_released = 'false' and bom like '"
						+ bomFilter + "%' ORDER BY BOM");
			} else if (newCheckBoxSelection.equals("NOTUSED")) {
				selBoms = ("select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,201' and BOM_TYPE = 'U' and has_been_released = 'false' and bom like '"
						+ bomFilter + "%' ORDER BY BOM");
			} else {
				selBoms = ("select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,205'and has_been_released = 'false' and BOM_TYPE = 'U' and bom like '"
						+ bomFilter + "%' union"
						+ " select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,201' and BOM_TYPE = 'U' and has_been_released = 'false' and bom like '"
						+ bomFilter + "%' ORDER BY BOM");
			}
		} else {
			if (newCheckBoxSelection.equals("NEW")) {
				selBoms = ("select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,205' and BOM_TYPE = 'U' and has_been_released = 'false' ORDER BY BOM");
			} else if (newCheckBoxSelection.equals("NOTUSED")) {
				selBoms = ("select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,201' and BOM_TYPE = 'U' and has_been_released = 'false' ORDER BY BOM");
			} else {
				selBoms = ("select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,205'and has_been_released = 'false' and BOM_TYPE = 'U' union"
						+ " select HANDLE, BOM, REVISION,STATUS_BO,CURRENT_REVISION from BOM where status_bo = 'StatusBO:0536,201' and BOM_TYPE = 'U' and has_been_released = 'false' ORDER BY BOM");
			}
		} 
		pstmt = connect.prepareStatement(selBoms);
		pstmt.executeQuery();
		ResultSet rs = pstmt.getResultSet();
		bomDisplayList.clear();
		while (rs.next()) {
			BomDisplayItem bomDisplayitem = new BomDisplayItem();
			bomDisplayitem.setBom(rs.getString("BOM"));
			bomDisplayitem.setRevision(rs.getString("REVISION"));
			bomDisplayitem.setBomRef(rs.getString("HANDLE"));
			if (rs.getString("STATUS_BO").equals("StatusBO:0536,201")) {
				bomDisplayitem.setBomStatus("Releasable");
			} else if (rs.getString("STATUS_BO").equals("StatusBO:0536,205")) {
				bomDisplayitem.setBomStatus("New");
			}
			bomDisplayitem.setIsCurrVersion(rs.getString("CURRENT_REVISION"));
			bomDisplayList.add(bomDisplayitem);
		}
		if (bomDisplayList.size() == 0) {
			message = FacesUtility.getLocaleSpecificText("noRecords.MESSAGE");
			setMessageBar(true, LSMessageType.INFO);
		}
		connect.close();
		// newCheckBoxSelection = null;
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "bomUpdateForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		// add now
		tableBomConfigBean.setSelectedRows(null);
		// add now
		bomComponentList.clear();
		UIComponent tablePanel1 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "bomUpdateForm:bomComponentDisplayPanel");
		if (tablePanel1 != null) {
			FacesUtility.addControlUpdate(tablePanel1);
		}

		updateOperation = null;
		newCurrStatus = null;
		newBomStatus = null;
		FacesUtility
				.removeSessionMapValue("tableOperBrowseBean_currentBrowseComponentId");
		FacesUtility.removeSessionMapValue("copyOperBrowseBean_OPERATION");
		UIComponent tablePanel2 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "bomUpdateForm:updatePanel");
		if (tablePanel2 != null) {
			FacesUtility.addControlUpdate(tablePanel2);
		}
	}

	public void operBrowse() {
		FacesUtility.setSessionMapValue("copyOperBrowseBean_OPERATION",
				updateOperation);
		FacesUtility.addScriptCommand("window.openCopyOperationBrowse();");
	}

	public void copyToAll() {
		// String operation = null;
		// operation= (String) FacesUtility
		// .getSessionMapValue("copyOperBrowseBean_OPERATION");
		getUpdateOperation();
		if (bomComponentList.size() > 0) {
			for (BomComponentItem row : bomComponentList) {
				// if (operation!=null){
				if (!(updateOperation == null)) {
					row.setNewoperation(updateOperation);
					UIComponent tablePanel1 = findComponent(FacesUtility
							.getFacesContext().getViewRoot(),
							"bomUpdateForm:bomComponentDisplayPanel");
					if (tablePanel1 != null) {
						FacesUtility.addControlUpdate(tablePanel1);
					}
				} else {
					// row.setNewoperation(this.updateOperation);
					message = FacesUtility
							.getLocaleSpecificText("Enter a operation in the new operation field");
					setMessageBar(true, LSMessageType.ERROR);
					return;
				}
			}

		}
	}

	protected HashMap<String, String> getbomDisplayColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = bomDisplaycolumnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(bomDisplayListColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public TableConfigurator getTableBomConfigBean() {
		return tableBomConfigBean;
	}

	public void setTableBomConfigBean(TableConfigurator tableBomConfigBean) {
		this.tableBomConfigBean = tableBomConfigBean;
		if (tableBomConfigBean.getColumnBindings() == null
				|| tableBomConfigBean.getColumnBindings().size() < 1) {
			tableBomConfigBean.setListName(null);
			tableBomConfigBean
					.setColumnBindings(getbomDisplayColumnFieldMaping());
			tableBomConfigBean.setListColumnNames(bomDisplayListColumnNames);
			tableBomConfigBean.setAllowSelections(true);
			tableBomConfigBean.setMultiSelectType(false);
			tableBomConfigBean.setSelectionBehavior("server");
			// enable row double click
			// tableBomConfigBean.setDoubleClick(true);
			tableBomConfigBean.addInternalTableSelectionEventListener(this);
			tableBomConfigBean.configureTable();
		}
	}

	protected HashMap<String, String> getbomComponentColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = bomComponentcolumnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(bomComponentListColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public TableConfigurator getTableBomComponentBean() {
		return tableBomComponentBean;
	}

	public void setTableBomComponentBean(TableConfigurator tableBomComponentBean) {
		this.tableBomComponentBean = tableBomComponentBean;
		if (tableBomComponentBean.getColumnBindings() == null
				|| tableBomComponentBean.getColumnBindings().size() < 1) {
			tableBomComponentBean.setListName(null);
			tableBomComponentBean
					.setColumnBindings(getbomComponentColumnFieldMaping());
			tableBomComponentBean
					.setListColumnNames(bomComponentListColumnNames);
			tableBomComponentBean.setAllowSelections(false);
			tableBomComponentBean.setMultiSelectType(false);
			//
			ArrayList<String> editCols = new ArrayList<String>();
			// make these columns editable
			editCols.add("NEW_OPERATION");
			tableBomComponentBean.setEditableColumns(editCols);
			UICommandInputText inputTextCell = new UICommandInputText();
			inputTextCell.setSubmitOnChange(false);
			inputTextCell.setSubmitOnFieldHelp(true);
			inputTextCell.addActionListener(this);
			tableBomComponentBean.setCellEditor("NEW_OPERATION", inputTextCell);
			//
			tableBomComponentBean.configureTable();
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteBOM() throws BusinessException, SQLException {
		initServices();
		setMessageBar(false, null);
		List<BomDisplayItem> selList = (List<BomDisplayItem>) tableBomConfigBean
				.getSelectedItems();
		String bomRef = null;
		String bom = null;
		String rev = null;
		if (selList.size() == 0) {
			message = FacesUtility
					.getLocaleSpecificText("Please select a BOM to delete");
			setMessageBar(true, LSMessageType.INFO);
			return;
		}
		for (BomDisplayItem bomitemrow : selList) {
			bom = bomitemrow.getBom();
			rev = bomitemrow.getRevision();
			bomRef = bomitemrow.getBomRef();
		}
		ObjectReference objBomRef = new ObjectReference(bomRef);
		bOMConfigurationService.deleteBOM(objBomRef);
		bomDisplayList.clear();
		retrieveBomData();
		message = FacesUtility.getLocaleSpecificText("Selected BOM " + bom
				+ "," + rev + " is deleted");
		setMessageBar(true, LSMessageType.INFO);
		selList.clear();
	}

	@SuppressWarnings("unchecked")
	public void retrieveComponents() throws BOMConfigurationException,
			BusinessException {
		message = null;
		setMessageBar(false, null);
		initServices();
		List<BomDisplayItem> selList = (List<BomDisplayItem>) tableBomConfigBean
				.getSelectedItems();
		String bomRef = null;
		String bom = null;
		String rev = null;
		for (BomDisplayItem bomitemrow : selList) {
			bom = bomitemrow.getBom();
			rev = bomitemrow.getRevision();
			bomRef = bomitemrow.getBomRef();
		}
		BOMComponentRequest bomCompReq = new BOMComponentRequest();
		BigDecimal one = new BigDecimal(1);
		bomCompReq.setBomRef(bomRef);
		bomCompReq.setSfcQty(one);
		try {
			Collection<BomComponentProductionConfiguration> BomCompColl = bOMConfigurationService
					.findAllBOMComponents(bomCompReq);
			bomComponentList.clear();
			for (BomComponentProductionConfiguration collRow : BomCompColl) {
				BomComponentItem bomCompItemRow = new BomComponentItem();
				ObjectReference itemObjRef = new ObjectReference(collRow
						.getComponentRef());
				ItemKeyData itemKeyData = itemConfigurationService
						.findItemKeyDataByRef(itemObjRef);
				String component = itemKeyData.getItem();
				String version = itemKeyData.getRevision();
				String combined = component + "/" + version;
				bomCompItemRow.setComponent(combined);
				if (collRow.getOperationRef() != null) {
					ObjectReference itemOprRef = new ObjectReference(collRow
							.getOperationRef());
					ObjectReference objCurrOpRef = operationConfigurationService
							.resolveCurrentRevision(itemOprRef);
					OperationKeyData opKeyData = operationConfigurationService
							.findOperationKeyDataByRef(objCurrOpRef);
					bomCompItemRow.setAssyoperation(opKeyData.getOperation());
				} else {
					bomCompItemRow.setAssyoperation("");
				}

				bomCompItemRow.setNewoperation("");
				bomCompItemRow.setAssySequence(collRow.getSequence());
				bomCompItemRow.setAssyQty(collRow.getQuantity().toString());
				bomComponentList.add(bomCompItemRow);

			}
		} catch (Exception ex) {
			System.out.print(ex);
		}

		if (bomComponentList.size() == 0) {
			message = FacesUtility
					.getLocaleSpecificText("There are no components for the selected BOM "
							+ bom + "," + rev);
			setMessageBar(true, LSMessageType.INFO);
		}
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "bomUpdateForm:bomComponentDisplayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
	}

	@SuppressWarnings("unchecked")
	public void updateBOM() throws BusinessException, SQLException {
		initServices();
		setMessageBar(false, null);
		Connection connect = DataSourceConnection.getSQLConnection();

		List<BomDisplayItem> selList = (List<BomDisplayItem>) tableBomConfigBean
				.getSelectedItems();
		String bomRef = null;
		String bom = null;
		String rev = null;
		if (selList.size() == 0 || bomComponentList.size() == 0) {
			message = FacesUtility
					.getLocaleSpecificText("Please select a BOM to update");
			setMessageBar(true, LSMessageType.INFO);
			return;
		}
		for (BomDisplayItem bomitemrow : selList) {
			bom = bomitemrow.getBom();
			rev = bomitemrow.getRevision();
			bomRef = bomitemrow.getBomRef();
		}

		for (BomComponentItem bomCompRow : bomComponentList) {
			String sequence = bomCompRow.getAssySequence().toString();
			String newOperation = bomCompRow.getNewoperation();
			if (!newOperation.equals("")) {
				OperationSearchRequest opSearchReq = new OperationSearchRequest();
				opSearchReq.setOperation(newOperation);
				OperationSearchResult opSearchResult = operationConfigurationService
						.findOperationConfiguration(opSearchReq);
				List<OperationExtendedConfiguration> opSearchList = new ArrayList<OperationExtendedConfiguration>();
				opSearchList = opSearchResult.getOperationList();
				if (opSearchList.size() == 0) {
					message = FacesUtility.getLocaleSpecificText("Operation "
							+ newOperation + "is invalid");
					setMessageBar(true, LSMessageType.ERROR);
					return;
				}
				String newOperRef = null;
				for (OperationExtendedConfiguration opRow : opSearchList) {
					if (opRow.isCurrentRevision() == true
							&& opRow.getOperation().equals(
									bomCompRow.getNewoperation())) {
						newOperRef = opRow.getRef().toString();
					}
				}

				String currOpRefHash = newOperRef.substring(0, newOperRef
						.lastIndexOf(",") + 1)
						+ "#";
				if (!bomCompRow.getAssyoperation().equals("")) {
					// update operation for each component
					PreparedStatement pOpstmt;
					String updateOpStmt = "update BOM_OPERATION set OPERATION_BO = '"
							+ currOpRefHash
							+ "' where BOM_COMPONENT_BO = (select HANDLE from BOM_COMPONENT where BOM_BO = '"
							+ bomRef + "' and SEQUENCE = '" + sequence + "')";
					pOpstmt = connect.prepareStatement(updateOpStmt);
					pOpstmt.executeUpdate();
				} else {
					PreparedStatement pstmt1;
					String bomComRef = null;
					String selBomComRef = null;
					String quantity = bomCompRow.getAssyQty();
					selBomComRef = ("select HANDLE from BOM_COMPONENT where BOM_BO = '"
							+ bomRef + "' and SEQUENCE = '" + sequence + "'");
					pstmt1 = connect.prepareStatement(selBomComRef);
					pstmt1.executeQuery();
					ResultSet rs = pstmt1.getResultSet();
					while (rs.next()) {
						bomComRef = rs.getString("HANDLE");
					}
					if (bomComRef != null) {
						String insertHandle = "BOMOperationBO:" + bomComRef
								+ "," + currOpRefHash;
						try {
							String insertStmnt = "INSERT into BOM_OPERATION values ('"
									+ insertHandle
									+ "','"
									+ bomComRef
									+ "','"
									+ currOpRefHash + "','" + quantity + "')";
							PreparedStatement pstmt2 = connect
									.prepareStatement(insertStmnt);
							pstmt2.executeUpdate();
						} catch (Exception ex) {
							message = FacesUtility
									.getLocaleSpecificText("Error while insert assemebly operations.Contact Developer");
							setMessageBar(true, LSMessageType.ERROR);
							return;
						}
					} else {
						message = FacesUtility
								.getLocaleSpecificText("Bom Component Reference is not valid.Contact Developer");
						setMessageBar(true, LSMessageType.ERROR);
						return;
					}
				}
			}
		}

		PreparedStatement pstmt;
		PreparedStatement pUpstmt;
		String updateStatus = null;

		if (newBomStatus.equals("RELEASABLE")) {
			// if status is releasable , first set current version as untrue for
			// previous version
			PreparedStatement pstmt3;
			String selCurrVerCount = ("select COUNT(*) as COUNT from BOM where BOM = '"
					+ bom + "' and CURRENT_REVISION = 'true'");
			pstmt3 = connect.prepareStatement(selCurrVerCount);
			pstmt3.executeQuery();
			ResultSet rs = pstmt3.getResultSet();
			String currVerCount = null;
			while (rs.next()) {
				currVerCount = rs.getString("COUNT");
			}
			if (!currVerCount.equals("0")) {
				String updateStmnt = "UPDATE BOM set CURRENT_REVISION = 'false' where handle = (select HANDLE from BOM where BOM = '"
						+ bom + "' and CURRENT_REVISION = 'true')";
				pstmt = connect.prepareStatement(updateStmnt);
				pstmt.executeUpdate();
			}
			updateStatus = "Update BOM set STATUS_BO = 'StatusBO:0536,201',CURRENT_REVISION = 'true' where HANDLE =  '"
					+ bomRef + "'";
			// now update the status
			pUpstmt = connect.prepareStatement(updateStatus);
			pUpstmt.executeUpdate();
			connect.close();
		} else if (newBomStatus.equals("OBSOLETE")) {
			updateStatus = "Update BOM set STATUS_BO = 'StatusBO:0536,203' where HANDLE =  '"
					+ bomRef + "'";
			// now update the status
			pUpstmt = connect.prepareStatement(updateStatus);
			pUpstmt.executeUpdate();
			connect.close();
		} else if (newBomStatus.equals("NEW")) {
			updateStatus = "Update BOM set STATUS_BO = 'StatusBO:0536,205' where HANDLE =  '"
					+ bomRef + "'";
			// now update the status
			pUpstmt = connect.prepareStatement(updateStatus);
			pUpstmt.executeUpdate();
			connect.close();
		}

		/*
		 * for (BomComponentItem bomCompRow:bomComponentList){
		 * bomCompRow.setNewoperation(""); }
		 */
		retrieveBomData();

		updateOperation = null;
		newCurrStatus = null;
		newBomStatus = null;

		FacesUtility.removeSessionMapValue("copyOperBrowseBean_OPERATION");
		UIComponent tablePanel2 = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "bomUpdateForm:updatePanel");
		if (tablePanel2 != null) {
			FacesUtility.addControlUpdate(tablePanel2);
		}
		message = FacesUtility.getLocaleSpecificText("Selected BOM " + bom
				+ "/" + rev + " is updated");
		setMessageBar(true, LSMessageType.INFO);
		selList.clear();

	}

	public List<BomDisplayItem> getBomDisplayList() {
		return bomDisplayList;
	}

	public void setBomDisplayList(List<BomDisplayItem> bomDisplayList) {
		this.bomDisplayList = bomDisplayList;
	}

	public List<BomComponentItem> getBomComponentList() {
		return bomComponentList;
	}

	public void setBomComponentList(List<BomComponentItem> bomComponentList) {
		this.bomComponentList = bomComponentList;
	}

	public String getUpdateOperation() {
		return updateOperation;
	}

	public void setUpdateOperation(String updateOperation) {
		this.updateOperation = updateOperation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNewCheckBoxSelection() {
		return newCheckBoxSelection;
	}

	public void setNewCheckBoxSelection(String newCheckBoxSelection) {
		this.newCheckBoxSelection = newCheckBoxSelection;
	}

	/**
	 * Initialization of services that are represented as fields.
	 */
	private void initServices() {
		bOMConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, BOMCONFIGURATION_SERVICE);
		itemConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, ITEM_CONFIGURATION_SERVICE);
		operationConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, OPERATION_CONFIGURATION_SERVICE);
	}

	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		// TODO Auto-generated method stub
		UICommandInputText currentBrowseComponent = (UICommandInputText) event
				.getSource();
		String currentBrowseComponentId = currentBrowseComponent
				.getClientId(FacesContext.getCurrentInstance());
		FacesUtility.removeSessionMapValue("tableOperBrowseBean");
		FacesUtility.setSessionMapValue(
				"tableOperBrowseBean_currentBrowseComponentId",
				currentBrowseComponentId);
		FacesUtility.addScriptCommand("window.opentableOperationBrowse();");

	}

	public String getBomFilter() {
		return bomFilter;
	}

	public void setBomFilter(String bomFilter) {
		this.bomFilter = bomFilter;
	}

	public String getNewBomStatus() {
		return newBomStatus;
	}

	public void setNewBomStatus(String newBomStatus) {
		this.newBomStatus = newBomStatus;
	}

	public String getNewCurrStatus() {
		return newCurrStatus;
	}

	public void setNewCurrStatus(String newCurrStatus) {
		this.newCurrStatus = newCurrStatus;
	}

	public void processTableSelectionEvent(TableSelectionEvent event) {
		// TODO Auto-generated method stub
		TableConfigurator conf = (TableConfigurator) event.getSource();
		if (!tableBomConfigBean.equals(conf)) {
			return;
		}
		Object selectedObject = event.getCurrentSelection();
		if (selectedObject instanceof BomDisplayItem) {
			try {
				retrieveComponents();
			} catch (BOMConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
