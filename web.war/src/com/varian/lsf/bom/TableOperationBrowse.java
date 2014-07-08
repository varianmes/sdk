package com.varian.lsf.bom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.component.UIComponent;

import com.sap.me.frame.domain.DomainServiceFactory;
import com.sap.me.frame.domain.DomainServiceInterface;
import com.sap.me.productdefinition.domain.OperationDO;
import com.sap.me.wpmf.BaseManagedBean;
import com.sap.me.wpmf.InternalTableSelectionEventListener;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.TableSelectionEvent;
import com.sap.me.wpmf.util.FacesUtility;
import com.visiprise.frame.service.ext.SecurityContext;

public class TableOperationBrowse extends BaseManagedBean implements InternalTableSelectionEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	DomainServiceInterface<OperationDO> operationDOservice = DomainServiceFactory.getServiceByClass(OperationDO.class);
	private List<TableOperationItem> operationList;
	private TableConfigurator tableOperconfigBean = null;
	String[] columnDefs = new String[] { "operation;OPERATION_BROWSE.operation.LABEL", "version;OPERATION_BROWSE.version.LABEL","description;OPERATION_BROWSE.description.LABEL" ,"currentversion;OPERATION_BROWSE.currentversion.LABEL"};
	String[] listColumnNames = new String[] { "OPERATION", "VERSION","DESCRIPTION","CURRENT_VERSION" };
	private String tableOpFilter;
	
	public TableOperationBrowse() {

	}
	
	public String getTableOpFilter() {
		return tableOpFilter;
	}

	public void setTableOpFilter(String tableOpFilter) {
		this.tableOpFilter = tableOpFilter;
	}
	public TableConfigurator getTableOperconfigBean() {
		return tableOperconfigBean;
	}
	
	public void retOperation(){
		String operation = tableOpFilter;
		List<TableOperationItem> newlist = getOperationList();
		UIComponent tablePanel = findComponent(FacesUtility.getFacesContext()
				.getViewRoot(), "toperationBrowseForm:displayPanel");
		if (tablePanel != null) {
			FacesUtility.addControlUpdate(tablePanel);
		}
		newlist.clear();
	}

	public void setTableOperconfigBean(TableConfigurator tableOperconfigBean) {
		this.tableOperconfigBean = tableOperconfigBean;
		if (tableOperconfigBean.getColumnBindings() == null || tableOperconfigBean.getColumnBindings().size() < 1) {
			tableOperconfigBean.setListName(null);
			tableOperconfigBean.setColumnBindings(getColumnFieldMaping());
			tableOperconfigBean.setListColumnNames(listColumnNames);
			tableOperconfigBean.setAllowSelections(true);
			tableOperconfigBean.setMultiSelectType(false);
			// configBean.setSelectionBehavior("server");
			// enable row double click
			tableOperconfigBean.setDoubleClick(true);
			tableOperconfigBean.addInternalTableSelectionEventListener(this);
			tableOperconfigBean.configureTable();
		}
	}

	public List<TableOperationItem> getOperationList() {
		//getTableOpFilter();
		operationList = new ArrayList<TableOperationItem>();
		String site = SecurityContext.instance().getSite();
		OperationDO operationDO = new OperationDO();
		operationDO.setSite(site);
		Collection<OperationDO> operationDOList = operationDOservice.readByExample(operationDO);
		for (OperationDO operDO : operationDOList) {
			if (!(this.tableOpFilter==null)){
				if (operDO.getCurrentRevision() && operDO.getOperation().startsWith(this.tableOpFilter)){ 	
			TableOperationItem operationItem = new TableOperationItem();
			operationItem.setOperation(operDO.getOperation());
			operationItem.setVersion(operDO.getRevision());
			operationItem.setDescription(operDO.getDescription());
			operationItem.setCurrentversion(operDO.getCurrentRevision());
			operationList.add(operationItem);
			}
			} else {	
				if (operDO.getCurrentRevision()){
					TableOperationItem operationItem = new TableOperationItem();
					operationItem.setOperation(operDO.getOperation());
					operationItem.setVersion(operDO.getRevision());
					operationItem.setDescription(operDO.getDescription());
					operationItem.setCurrentversion(operDO.getCurrentRevision());
					operationList.add(operationItem);
				}
			}
			}
		return operationList;
	}

	public void setOperationList(List<TableOperationItem> operationList) {
		this.operationList = operationList;
	}

	protected HashMap<String, String> getColumnFieldMaping() {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		String[] columns = columnDefs;
		for (int i = 0; i < columns.length; i++) {
			columnFieldMap.put(listColumnNames[i], columns[i]);
		}
		return columnFieldMap;
	}

	public void processTableSelectionEvent(TableSelectionEvent event) {
		TableConfigurator conf = (TableConfigurator) event.getSource();
		if (!tableOperconfigBean.equals(conf)) {
			return;
		}
		Object selectedObject = event.getCurrentSelection();
		if (selectedObject instanceof TableOperationItem) {
			String tableComponentID = (String) FacesUtility.getSessionMapValue("tableOperBrowseBean_currentBrowseComponentId");
			TableOperationItem operItem = (TableOperationItem) selectedObject;
			StringBuffer buf = new StringBuffer("window.setTableOperField('");
			buf.append(operItem.getOperation());
			buf.append("','");
			buf.append(tableComponentID);
			buf.append("');");
			FacesUtility.addScriptCommand(buf.toString());
		}
	}

	public void okAction() {
		TableOperationItem selectedOper = (TableOperationItem) tableOperconfigBean.getSelectedItems();
		if (selectedOper == null)
			FacesUtility.addScriptCommand("window.close();");
		String tableComponentID = (String) FacesUtility.getSessionMapValue("tableOperBrowseBean_currentBrowseComponentId");
		StringBuffer buf = new StringBuffer("window.setTableOperField('");
		buf.append(selectedOper.getOperation());
		buf.append("','");
		buf.append(tableComponentID);
		buf.append("');");
		FacesUtility.addScriptCommand(buf.toString());
	}
	

	public void cancelAction() {
		FacesUtility.removeSessionMapValue("tableOperBrowseBean_currentBrowseComponentId");
		//tableOpFilter = null;
		FacesUtility.addScriptCommand("window.close();");
	}

}
