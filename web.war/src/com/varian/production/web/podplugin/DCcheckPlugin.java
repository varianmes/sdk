package com.varian.production.web.podplugin;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.datacollection.CollectDataAt;
import com.sap.me.datacollection.DCGroupToCollectResponse;
import com.sap.me.datacollection.DCGroupsToCollectResponse;
import com.sap.me.datacollection.DCParameterResponse;
import com.sap.me.datacollection.DataCollectionServiceInterface;
import com.sap.me.datacollection.OperationRequest;
import com.sap.me.datacollection.OperationSfc;
import com.sap.me.extension.Services;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.plant.ResourceBOHandle;
import com.sap.me.plant.ResourceKeyData;
import com.sap.me.plant.WorkCenterBOHandle;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStep;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.varian.hook.exception.CheckOpenDCsException;
import com.visiprise.frame.service.ext.SecurityContext;
import com.sap.me.production.podclient.SfcSelection;

public class DCcheckPlugin extends BasePodPlugin {
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final String DATA_COLLECTION_SERVICE = "DataCollectionService";
	private static final String COM_SAP_ME_DATACOLLECTION = "com.sap.me.datacollection";
	private int flag =0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 627834305298819546L;
	private DataCollectionServiceInterface dataCollectionService;
	private SfcStateServiceInterface sfcStateService;

	public void execute() throws Exception {
		initServices();	
		isBlockingEnabled();
		List<SfcSelection> sfcList;			
		sfcList = getPodSelectionModel().getResolvedSfcs();
		if(sfcList!= null && sfcList.size() > 0) {
			//
			ObjectReference sfcRef1 = new ObjectReference(sfcList.get(0).getSfc().getSfcRef());	
			SfcBasicData sfcBasicData = sfcStateService.findSfcDataByRef(sfcRef1);
			String sfcRef = sfcBasicData.getSfcRef();
			PodSelectionModelInterface selectionModel = getPodSelectionModel();
				OperationKeyData opKeydata = selectionModel.getResolvedOperation();
				ResourceKeyData resKeyData = selectionModel.getResolvedResource();			
				String CurrentResRef = resKeyData.getRef();
				String currentOpRef  = opKeydata.getRef();
			//
		//String CurrentResRef = dto.getResourceBO().getValue();
		//String sfcRef = dto.getSfcBO().getValue();
		//String currentOpRef = dto.getOperationBO().getValue();
		String site = SecurityContext.instance().getSite();
		WorkCenterBOHandle wcRef = new WorkCenterBOHandle(site,"DUMMY");
		ResourceBOHandle resRef = new ResourceBOHandle(site,CurrentResRef);
		OperationSfc opSfc = new OperationSfc();
		opSfc.setSfcRef(sfcRef);
		List<OperationSfc> opSfcList = new ArrayList <OperationSfc> ();
		opSfcList.add(opSfc);
		OperationRequest opReq = new OperationRequest();
		opReq.setOperationRef(currentOpRef);
		opReq.setResourceRef(resRef.toString());
		opReq.setSfcList(opSfcList);
		opReq.setCollectDataAt(CollectDataAt.ANYTIME);
		opReq.setWorkCenterRef(wcRef.toString());
		ObjectReference objref = new ObjectReference();
		objref.setRef(sfcRef);			
		String routerRef = null;	
		StringBuffer dclist = new StringBuffer(); 
		DCGroupsToCollectResponse response = new DCGroupsToCollectResponse();
		SfcKeyData sfcKeyData = new SfcKeyData();
		int pendingCount = 0;
		try {
			response = dataCollectionService.findDcGroupsToCollect(opReq);	
			sfcKeyData = sfcStateService.findSfcKeyDataByRef(objref);
			Collection<SfcStep> sfcStepColl = sfcStateService.findCurrentRouterSfcStepsBySfcRef(objref);
			for (SfcStep attrValue : sfcStepColl) {
				if (!(attrValue.getStatus()== null) && ("In_Work".equals(attrValue.getStatus().value()))) {
					routerRef = attrValue.getRouterRef();
			  }
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		if(response != null){
		List<DCGroupToCollectResponse> dcgrplist = new ArrayList <DCGroupToCollectResponse> ();
		dcgrplist = response.getDcGroupList();
			for (DCGroupToCollectResponse dcval:dcgrplist){			
					List<DCParameterResponse> dcParamList = new ArrayList <DCParameterResponse> ();	
					dcParamList = dcval.getDcParameterList();
			for (DCParameterResponse dcParamVal:dcParamList){
				String dcParamName = dcParamVal.getParameterName();
				int count = 0;				
				DynamicQuery sqlString = DynamicQueryFactory.newInstance();		
				Data queryData2 = null;
				Data parametricData1 = null;
				BigDecimal timesProcessed = dcval.getTimesProcessed();
				sqlString.append("SELECT COUNT(*) AS COUNT  FROM PARAMETRIC, PARAMETRIC_MEASURE WHERE " +
						"PARAMETRIC_MEASURE.EDITED<>'C'  AND PARAMETRIC.PARA_CONTEXT_GBO = '"+sfcRef+"' "+
						"AND PARAMETRIC_MEASURE.PARAMETRIC_BO = PARAMETRIC.HANDLE AND "+
						"PARAMETRIC_MEASURE.MEASURE_NAME = '"+dcParamName+"' AND "+
						"PARAMETRIC.times_processed = '"+timesProcessed+"' "+
			            "AND PARAMETRIC.ROUTER_BO = '"+routerRef+"'");
				SystemBase sysBase = SystemBase.createDefaultSystemBase();
				queryData2 = sysBase.executeQuery(sqlString);		
								
				if(queryData2.size() > 0){
					Iterator<Data> dataIterator = queryData2.iterator();					
					parametricData1 = dataIterator.next();
					count = parametricData1.getInteger("COUNT");
					if(count == 0){	
						pendingCount++;
						dclist.append(dcParamVal.getParameterName());
						dclist.append(";");	
					}					
				}				
			}
			}		
		}
		if (dclist.length() > 0) {			
			  throw new CheckOpenDCsException(sfcKeyData.getSfc(),dclist.toString(),pendingCount);	
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
		dataCollectionService = Services.getService(COM_SAP_ME_DATACOLLECTION,DATA_COLLECTION_SERVICE);
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
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


