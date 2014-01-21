
package com.varian.hook;
import java.util.Iterator;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
//import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
//import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.CompleteHookDTO;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.SfcStateServiceInterface;
import com.varian.hook.exception.InvalidSerialFormatException;
import com.visiprise.frame.service.ext.ActivityInterface;

/**
 * @author vmurthy
 *
 */
public class ValidateSfcForTR implements ActivityInterface<CompleteHookDTO> {

	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	/*
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";	
	private OperationConfigurationServiceInterface operationConfigurationService;
	*/
	private SfcStateServiceInterface sfcStateService;

	public void execute(CompleteHookDTO dto) throws Exception {
		initServices();
		/*
		String operRef = dto.getOperationBO().getValue();
		ObjectReference operReference = new ObjectReference(operRef);
		OperationKeyData operKeyData = operationConfigurationService.findOperationKeyDataByRef(operReference);		
		String operation =  operKeyData.getOperation();
		*/
		
		String sfcRef = dto.getSfcBO().getValue().toString();	
		ObjectReference sfcreference = new ObjectReference(sfcRef);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcNumber =  sfckeydata.getSfc();	
		String prefix = sfcNumber.substring(0, Math.min(sfcNumber.length(), 3));
		
		int count = 0;
		Data parametricData = null;
		Data queryData = null;
		SystemBase sysBase = SystemBase.createDefaultSystemBase();
		DynamicQuery getSerialCount = DynamicQueryFactory.newInstance();
		getSerialCount.append("select COUNT(*) AS COUNT from sfc_id_history where REASON= 'S' and SFC_BO = '"+sfcRef+"'");
		queryData = sysBase.executeQuery(getSerialCount);
		if(queryData.size() > 0){
			Iterator<Data> dataIterator = queryData.iterator();					
			parametricData = dataIterator.next();
			count = parametricData.getInteger("COUNT");
			}
		if (count == 0) {						
			throw new InvalidSerialFormatException(20107,sfcNumber);		
		} else {
			if (prefix.equals("TR-")){	
				throw new InvalidSerialFormatException(20109,sfcNumber);	
			}
		}
		
	}
	
	private void initServices(){
	//	operationConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,OPERATION_CONFIGURATION_SERVICE);
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
		
	}

}
