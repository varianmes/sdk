package com.varian.lsf.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.sap.me.frame.domain.DomainServiceFactory;
import com.sap.me.frame.domain.DomainServiceInterface;
import com.sap.me.productdefinition.domain.OperationDO;
import com.sap.me.wpmf.InternalTableSelectionEventListener;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.TableSelectionEvent;
import com.sap.me.wpmf.util.FacesUtility;
import com.visiprise.frame.service.ext.SecurityContext;

public class OperationBrowse implements InternalTableSelectionEventListener {

	DomainServiceInterface<OperationDO> operationDOservice = DomainServiceFactory.getServiceByClass(OperationDO.class);
	private List<OperationItem> operationList;
	private TableConfigurator configBean = null;
	String[] columnDefs = new String[] { "operation;OPERATION_BROWSE.operation.LABEL", "version;OPERATION_BROWSE.version.LABEL","description;OPERATION_BROWSE.description.LABEL" ,"currentversion;OPERATION_BROWSE.currentversion.LABEL"};
	String[] listColumnNames = new String[] { "OPERATION", "VERSION","DESCRIPTION","CURRENT_VERSION" };
	String operationFilter;

	public OperationBrowse() {
		operationFilter = (String) FacesUtility.getSessionMapValue("toolBrowseBean_OPERATION");
	}

	public TableConfigurator getConfigBean() {
		return configBean;
	}
	
	public String getOperationFilter() {
		operationFilter = (String) FacesUtility.getSessionMapValue("toolBrowseBean_OPERATION");
		return operationFilter;
	}

	public void setOperationFilter(String operationFilter) {
		this.operationFilter = operationFilter;
	}

	public void setConfigBean(TableConfigurator configBean) {
		this.configBean = configBean;
		if (configBean.getColumnBindings() == null || configBean.getColumnBindings().size() < 1) {
			configBean.setListName(null);
			configBean.setColumnBindings(getColumnFieldMaping());
			configBean.setListColumnNames(listColumnNames);
			configBean.setAllowSelections(true);
			configBean.setMultiSelectType(false);
			// configBean.setSelectionBehavior("server");
			// enable row double click
			configBean.setDoubleClick(true);
			configBean.addInternalTableSelectionEventListener(this);
			configBean.configureTable();
		}
	}



	public List<OperationItem> getOperationList() {
		getOperationFilter();
		operationList = new ArrayList<OperationItem>();
		String site = SecurityContext.instance().getSite();
		OperationDO operationDO = new OperationDO();
		operationDO.setSite(site);
		Collection<OperationDO> operationDOList = operationDOservice.readByExample(operationDO);
		for (OperationDO operDO : operationDOList) {
		if (!(operationFilter==null)){
		if (operDO.getCurrentRevision() && operDO.getOperation().startsWith(operationFilter)){ 	
			OperationItem operationItem = new OperationItem();
			operationItem.setOperation(operDO.getOperation());
			operationItem.setVersion(operDO.getRevision());
			operationItem.setDescription(operDO.getDescription());
			operationItem.setCurrentversion(operDO.getCurrentRevision());
			operationList.add(operationItem);
			}
		} else {
		if (operDO.getCurrentRevision()){
			OperationItem operationItem = new OperationItem();
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

	public void setOperationList(List<OperationItem> operationList) {
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
		if (!configBean.equals(conf)) {
			return;
		}
		Object selectedObject = event.getCurrentSelection();
		if (selectedObject instanceof OperationItem) {
			OperationItem operItem = (OperationItem) selectedObject;
			StringBuffer buf = new StringBuffer("window.setOperationVersionFields('");
			buf.append(operItem.getOperation());
			buf.append("');");
			FacesUtility.addScriptCommand(buf.toString());
		}
	}

	public void okAction() {
		OperationItem selectedOper = (OperationItem) configBean.getSelectedItems();
		if (selectedOper == null)
			FacesUtility.addScriptCommand("window.close();");

		StringBuffer buf = new StringBuffer("window.setOperationVersionFields('");
		buf.append(selectedOper.getOperation());
		buf.append("');");
		FacesUtility.addScriptCommand(buf.toString());
	}

	public void cancelAction() {
		FacesUtility.removeSessionMapValue("toolBrowseBean_OPERATION");
		operationFilter = null;
		FacesUtility.addScriptCommand("window.close();");
	}

}
