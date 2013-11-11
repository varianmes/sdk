/**
 * 
 */
package com.varian.hook;

import java.util.ArrayList;
import java.util.List;

import com.sap.me.activity.HookContextInterface;
import com.sap.me.activity.HookContextSetter;
import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.BOHandle;
import com.sap.me.nonconformance.FindNCsBySFCRequest;
import com.sap.me.nonconformance.FindNCsBySFCResponse;
import com.sap.me.nonconformance.NCProductionServiceInterface;
import com.sap.me.nonconformance.NCStatus;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.production.CompleteHookDTO;
import com.varian.hook.exception.CheckOpenNCsException;
import com.visiprise.frame.service.ext.ActivityInterface;

/**
 * @author hgunda
 *
 */
public class CheckOpenNCsHook implements ActivityInterface<CompleteHookDTO>,
		HookContextSetter {

	private static final long serialVersionUID = 1L;
	private static final String NCPRODUCTION_SERVICE = "NCProductionService";
	private static final String COM_SAP_ME_NONCONFORMANCE = "com.sap.me.nonconformance";
	private NCProductionServiceInterface nCProductionService;
	private HookContextInterface hookContext;

	/*
	 * (non-Javadoc)
	 * @see com.visiprise.frame.service.ext.ActivityInterface#execute(java.lang.Object)
	 */
	public void execute(CompleteHookDTO dto) throws Exception {
		initServices();
		 FindNCsBySFCRequest request = new FindNCsBySFCRequest();
	      request.setSfcRef(dto.getSfcBO().getValue());
	      
	      List<String> ncOperationFilter = new ArrayList <String> ();
	      ncOperationFilter.add(dto.getOperationBO().getValue());
	      request.setOperations(ncOperationFilter);
	
	      List<NCStatus> ncStatusFilter = new ArrayList <NCStatus> ();
		  ncStatusFilter.add(NCStatus.OPEN);
		  ncStatusFilter.add(NCStatus.CLOSE_PENDING);
		  request.setNcStatusFilter(ncStatusFilter );

		  FindNCsBySFCResponse response = nCProductionService.findNCsBySFC(request);
		  
		  if (response.getNcs() != null && response.getNcs().size() > 0) {
			SFCBOHandle sfcHandle = SFCBOHandle.convert(new BOHandle (dto.getSfcBO().getValue()));
			OperationBOHandle operationHandle = OperationBOHandle.convert(new BOHandle(dto.getOperationBO().getValue()));
			 throw new CheckOpenNCsException (sfcHandle.getSFC(),operationHandle.getOperation());
	
		  }
		  
		 
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices() {
		nCProductionService = Services.getService(COM_SAP_ME_NONCONFORMANCE,
				NCPRODUCTION_SERVICE);

	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.me.activity.HookContextSetter#setHookContext(com.sap.me.activity.HookContextInterface)
	 */
	public void setHookContext(final HookContextInterface hookContext) {
		this.hookContext = hookContext;
	}

}
