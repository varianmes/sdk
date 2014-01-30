// Hook to force serialization at specific operations at pre complete

package com.varian.hook;

import java.util.Iterator;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.StartHookDTO;
import com.varian.hook.exception.InvalidSerialFormatException;
import com.varian.hook.exception.SerializationMissingException;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;

// author vmurthy 12/12/13 HP QC defect 56

public class CheckFMatStart implements
		ActivityInterface<StartHookDTO> {
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService;

	
	public void execute(StartHookDTO dto) throws Exception {
		initServices();
		String sfcBO = dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcnumber =  sfckeydata.getSfc();
		
		int reworkCount =0;
		int reworkflag = 0;	
		int serialCount = 0;
		Data parametricData1 = null;
		Data parametricData2 = null;
		Data parametricData3 = null;
		Data queryData1 = null;
		Data queryData2 = null;
		Data queryData3 = null;
		SystemBase sysBase = SystemBase.createDefaultSystemBase();
		DynamicQuery selreworkflag = DynamicQueryFactory.newInstance();	
		selreworkflag.append("select COUNT(*) as COUNT1 from SFC_STEP where USE_AS_REWORK = 'true'"+
							"and HANDLE like '%"+sfcBO+",%'");
				
		queryData2 = sysBase.executeQuery(selreworkflag);
		if(queryData2 != null && queryData2.size() > 0){
			Iterator<Data> dataIterator = queryData2.iterator();					
			parametricData2 = dataIterator.next();
			reworkflag = parametricData2.getInteger("COUNT1");				
		}

		
		DynamicQuery getReworkCount = DynamicQueryFactory.newInstance();
		getReworkCount.append("select COUNT(*) AS COUNT2 from production_log where SFC= '"+sfcnumber+"' and REWORK = 'true'");
		queryData1 = sysBase.executeQuery(getReworkCount);
		if(queryData1.size() > 0){
			Iterator<Data> dataIterator = queryData1.iterator();					
			parametricData1 = dataIterator.next();
			reworkCount = parametricData1.getInteger("COUNT2");
			}
		DynamicQuery getSerialCount = DynamicQueryFactory.newInstance();
		getSerialCount.append("select COUNT(*) AS COUNT from sfc_id_history where REASON= 'S' and SFC_BO = '"+sfcBO+"'");
		queryData3 = sysBase.executeQuery(getSerialCount);
		if(queryData3.size() > 0){
			Iterator<Data> dataIterator = queryData3.iterator();					
			parametricData3 = dataIterator.next();
			serialCount = parametricData3.getInteger("COUNT");
			}
		if(reworkCount> 0 || reworkflag >0) {
				if (serialCount == 0){
					throw new SerializationMissingException(20107,sfcnumber);
				}	else {
					 String threechar = String.valueOf(sfcnumber.charAt(2));	
					 if (!threechar.equals("-") || sfcnumber.length()<4){
						 throw new SerializationMissingException(20114,sfcnumber);
					 }
				}
		} else {
			if (serialCount == 0){
			throw new SerializationMissingException(20104,sfcnumber);
			} else {
			String prefix = sfcnumber.substring(0, Math.min(sfcnumber.length(), 3));
			if (!prefix.equals("FM-") || sfcnumber.length()<4){			
			throw new InvalidSerialFormatException(20113,sfcnumber);		
			}					
			}
		}
		}

	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
	}

}
