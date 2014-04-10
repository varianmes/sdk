package com.varian.production.web.podplugin;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.status.StatusBasicConfiguration;
import com.sap.me.status.StatusServiceInterface;
import com.varian.hook.exception.DCInvalidforSFCException;

public class RestrictDCforInactiveSFC extends BasePodPlugin {
	private static final String COM_SAP_ME_STATUS = "com.sap.me.status";
	private static final String STATUS_SERVICE = "StatusService";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private int flag = 0;
	private String sfcStatus = null;
	private String sfc = null;
	private static final long serialVersionUID = 627834305298819546L;
	private SfcStateServiceInterface sfcStateService;
	private StatusServiceInterface statusService;

	public void execute() throws Exception {
		initServices();	
		isBlockingEnabled();
		List<SfcSelection> sfcList;			
		sfcList = getPodSelectionModel().getResolvedSfcs();
		if(sfcList!= null && sfcList.size() > 0) {
			ObjectReference sfcRef = new ObjectReference(sfcList.get(0).getSfc().getSfcRef());	
			SfcBasicData sfcBasicData = sfcStateService.findSfcDataByRef(sfcRef);
			sfc = sfcBasicData.getSfc();
			ObjectReference statusRef = new ObjectReference(sfcBasicData.getStatusRef());
			StatusBasicConfiguration statusBasicConfiguration = statusService.findStatusByRef(statusRef);
			sfcStatus = statusBasicConfiguration.getStatusDescription();
			if (sfcStatus.equals("Active") || sfcStatus.equals("Hold")){
				flag = 1;
				isBlockingEnabled();
				complete();
				return;			
			} else {	
				throw new DCInvalidforSFCException(sfc,20129);
			}
		}
		flag = 1;
		isBlockingEnabled();
		complete();
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
		statusService = Services.getService(COM_SAP_ME_STATUS,STATUS_SERVICE);
	}

	//@Override	xcvc
	protected void complete() {
		if (!isBlockingEnabled()) {
			return;
		}
	}

	/**
     * 
     */
	//@Overrideddf
	public boolean isBlockingEnabled() {
		if (flag ==0 ){
		return true;
	   } else {
		return false;
	}
	}

		
	}


