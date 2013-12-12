// Hook to force serialization at specific operations at pre complete

package com.varian.hook;

import java.util.Iterator;

import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.production.CompleteHookDTO;
import com.sap.me.production.SfcKeyData;
import com.varian.hook.exception.SerializationMissingException;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.SfcStateServiceInterface;

// author vmurthy 12/12/13 HP QC defect 56

public class CheckSerializationHook implements
		ActivityInterface<CompleteHookDTO> {
 
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final long serialVersionUID = 1L;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private SfcStateServiceInterface sfcStateService;

	public void execute(CompleteHookDTO dto) throws Exception {
		initServices();
		String sfcBO =dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcnumber =  sfckeydata.getSfc();
		String operBo = dto.getOperationBO().getValue().toString();
		ObjectReference operRef = new ObjectReference(operBo);
		OperationKeyData operKeyData = operationConfigurationService.findOperationKeyDataByRef(operRef);
		String operation = operKeyData.getOperation();
		int count = 0;
		Data parametricData = null;
		Data queryData = null;
		SystemBase sysBase = SystemBase.createDefaultSystemBase();
		DynamicQuery getSerialCount = DynamicQueryFactory.newInstance();
		getSerialCount.append("select COUNT(*) AS COUNT from sfc_id_history where REASON= 'S' and SFC_BO = '"+sfcBO+"'");
		queryData = sysBase.executeQuery(getSerialCount);
		if(queryData.size() > 0){
			Iterator<Data> dataIterator = queryData.iterator();					
			parametricData = dataIterator.next();
			count = parametricData.getInteger("COUNT");
			}
		if (count == 0){
			if (operation.equals("GLASS_PREP")){
				throw new SerializationMissingException(20104,sfcnumber);
				} 
			if (operation.equals("BOX_UP")){
				throw new SerializationMissingException(20105,sfcnumber);
				}
			if (operation.equals("HI_POT")){
				throw new SerializationMissingException(20106,sfcnumber);
				}
			if (operation.equals("LIGHT_PIPE_TEST")){
				throw new SerializationMissingException(20106,sfcnumber);
				}
			if (operation.equals("FINAL_TEST_NO_HIPOT")){
				throw new SerializationMissingException(20107,sfcnumber);
				}
			else {
				throw new SerializationMissingException(20108,sfcnumber);	
			}
		}
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		operationConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,OPERATION_CONFIGURATION_SERVICE);
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
	}

}
