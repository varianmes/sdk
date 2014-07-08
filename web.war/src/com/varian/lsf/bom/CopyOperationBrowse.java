package com.varian.lsf.bom;

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

public class CopyOperationBrowse implements InternalTableSelectionEventListener {

	DomainServiceInterface<OperationDO> operationDOservice = DomainServiceFactory.getServiceByClass(OperationDO.class);
	private List<CopyTableOperationItem> operationList;
	private TableConfigurator copyOperconfigBean = null;
	String[] columnDefs = new String[] { "operation;OPERATION_BROWSE.operation.LABEL", "version;OPERATION_BROWSE.version.LABEL","description;OPERATION_BROWSE.description.LABEL" ,"currentversion;OPERATION_BROWSE.currentversion.LABEL"};
	String[] listColumnNames = new String[] { "OPERATION", "VERSION","DESCRIPTION","CURRENT_VERSION" };
	String copyOpFilter = null;
	
	public CopyOperationBrowse() {
		copyOpFilter = (String) FacesUtility.getSessionMapValue("copyOperBrowseBean_OPERATION");
	}
	
	
	public TableConfigurator getCopyOperconfigBean() {
		return copyOperconfigBean;
	}


	public String getCopyOpFilter() {
		copyOpFilter = (String) FacesUtility.getSessionMapValue("copyOperBrowseBean_OPERATION");
		return copyOpFilter;
	}


	public void setCopyOpFilter(String copyOpFilter) {
		
		this.copyOpFilter = copyOpFilter;
	}


	public void setCopyOperconfigBean(TableConfigurator copyOperconfigBean) {

		this.copyOperconfigBean = copyOperconfigBean;
		if (copyOperconfigBean.getColumnBindings() == null || copyOperconfigBean.getColumnBindings().size() < 1) {
			copyOperconfigBean.setListName(null);
			copyOperconfigBean.setColumnBindings(getColumnFieldMaping());
			copyOperconfigBean.setListColumnNames(listColumnNames);
			copyOperconfigBean.setAllowSelections(true);
			copyOperconfigBean.setMultiSelectType(false);
			// configBean.setSelectionBehavior("server");
			// enable row double click
			copyOperconfigBean.setDoubleClick(true);
			copyOperconfigBean.addInternalTableSelectionEventListener(this);
			copyOperconfigBean.configureTable();
		}
	}


	public List<CopyTableOperationItem> getOperationList() {
		getCopyOpFilter();
		operationList = new ArrayList<CopyTableOperationItem>();
		String site = SecurityContext.instance().getSite();
		OperationDO operationDO = new OperationDO();
		operationDO.setSite(site);
		Collection<OperationDO> operationDOList = operationDOservice.readByExample(operationDO);
		for (OperationDO operDO : operationDOList) {
			if (!(copyOpFilter==null)){
			if (operDO.getCurrentRevision() && operDO.getOperation().startsWith(copyOpFilter)){ 	
			CopyTableOperationItem operationItem = new CopyTableOperationItem();
			operationItem.setOperation(operDO.getOperation());
			operationItem.setVersion(operDO.getRevision());
			operationItem.setDescription(operDO.getDescription());
			operationItem.setCurrentversion(operDO.getCurrentRevision());
			operationList.add(operationItem);
			}
			} else {
				if (operDO.getCurrentRevision()){
					CopyTableOperationItem operationItem = new CopyTableOperationItem();
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

	public void setOperationList(List<CopyTableOperationItem> operationList) {
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
		if (!copyOperconfigBean.equals(conf)) {
			return;
		}
		Object selectedObject = event.getCurrentSelection();
		if (selectedObject instanceof CopyTableOperationItem) {			
			CopyTableOperationItem operItem = (CopyTableOperationItem) selectedObject;
			FacesUtility.setSessionMapValue("copyOperBrowseBean_OPERATION",operItem.getOperation());
			StringBuffer buf = new StringBuffer("window.setCopyOperationField('");
			buf.append(operItem.getOperation());
			buf.append("');");
			FacesUtility.addScriptCommand(buf.toString());
		}
	}

	public void copyOkAction(){
		CopyTableOperationItem selectedOper = (CopyTableOperationItem) copyOperconfigBean.getSelectedItems();
		if (selectedOper == null)
			FacesUtility.addScriptCommand("window.close();");
		FacesUtility.setSessionMapValue("copyOperBrowseBean_OPERATION",selectedOper.getOperation());
		StringBuffer buf = new StringBuffer("window.setCopyOperationField('");
		buf.append(selectedOper.getOperation());
		buf.append("');");
		FacesUtility.addScriptCommand(buf.toString());
	}

	public void cancelAction() {
		FacesUtility.removeSessionMapValue("copyOperBrowseBean_OPERATION");
		copyOpFilter = null;
		FacesUtility.addScriptCommand("window.close();");
	}

}
