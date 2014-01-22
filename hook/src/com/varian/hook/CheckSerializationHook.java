// Hook to force serialization at specific operations at pre complete

package com.varian.hook;
import java.util.Iterator;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.production.CompleteHookDTO;
import com.sap.me.production.PostCompleteHookDTO;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.StartHookDTO;
import com.varian.hook.exception.InvalidSerialFormatException;
import com.varian.hook.exception.SerializationMissingException;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.SfcStateServiceInterface;

// author vmurthy 12/12/13 HP QC defect 56

public class CheckSerializationHook implements
		ActivityInterface<Object> {
 
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final long serialVersionUID = 1L;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private SfcStateServiceInterface sfcStateService;

	public void execute(Object dto) throws Exception {
		if (dto instanceof PostCompleteHookDTO) {
			execute((PostCompleteHookDTO) dto);
		} else if (dto instanceof CompleteHookDTO) {
			execute((CompleteHookDTO) dto);
		} else if (dto instanceof StartHookDTO) {
			execute((StartHookDTO) dto);
		}
	}
	
	
	public void execute(StartHookDTO dto) throws Exception {
		initServices();
		String sfcBO = dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcnumber =  sfckeydata.getSfc();
		String operBo = dto.getOperationBO().getValue().toString();
		ObjectReference operRef = new ObjectReference(operBo);
		OperationKeyData operKeyData = operationConfigurationService.findOperationKeyDataByRef(operRef);
		String operation = operKeyData.getOperation();
		checkforSerial(sfcBO, operation, sfcnumber,"start");		
	}

	/*
	 * (non-Javadoc)
	 * @see com.visiprise.frame.service.ext.ActivityInterface#execute(java.lang.Object)
	 */
	public void execute(CompleteHookDTO dto) throws Exception {
		initServices();
		String sfcBO = dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcnumber =  sfckeydata.getSfc();
		String operBo = dto.getOperationBO().getValue().toString();
		ObjectReference operRef = new ObjectReference(operBo);
		OperationKeyData operKeyData = operationConfigurationService.findOperationKeyDataByRef(operRef);
		String operation = operKeyData.getOperation();
		checkforSerial(sfcBO, operation, sfcnumber, "precomplete");
	}
	
	public void execute(PostCompleteHookDTO dto) throws Exception {
		initServices();
		String sfcBO = dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcnumber =  sfckeydata.getSfc();
		String operBo = dto.getOperationBO().getValue().toString();
		ObjectReference operRef = new ObjectReference(operBo);
		OperationKeyData operKeyData = operationConfigurationService.findOperationKeyDataByRef(operRef);
		String operation = operKeyData.getOperation();
		checkforSerial(sfcBO, operation, sfcnumber,"postcomplete");
	}
	
	public void checkforSerial(String sfcBO,String operation,String sfcnumber,String trigger) throws Exception {
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
			if (operation.equals("BOX_UP") || operation.equals("BOX_UP_1") || operation.equals("BOX_UP_CUSTOM")){
				throw new SerializationMissingException(20105,sfcnumber);
				}
			if (operation.equals("HI_POT") || operation.equals("LIGHT_PIPE_TEST") || operation.equals("FINAL_TEST_NO_HIPOT") || operation.equals("FINAL_TEST")){
				throw new SerializationMissingException(20106,sfcnumber);
				}
			else {
				throw new SerializationMissingException(20107,sfcnumber);	
			}
		} else {
			if (trigger.equals("start") &&(operation.equals("FINAL_TEST") || operation.equals("FINAL_TEST_NO_HIPOT"))){
				String prefix = sfcnumber.substring(0, Math.min(sfcnumber.length(), 3));
				if (!prefix.equals("TR-")){
					throw new InvalidSerialFormatException(20108,sfcnumber);
				}
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
