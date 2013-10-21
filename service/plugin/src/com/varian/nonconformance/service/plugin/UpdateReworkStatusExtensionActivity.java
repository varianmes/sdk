/**
 * 
 */
package com.varian.nonconformance.service.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.varian.integration.connection.DataSourceConnection;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.nonconformance.NCProductionServiceInterface;
import com.sap.me.nonconformance.NcCodeBasicConfiguration;
import com.sap.me.nonconformance.ReadNCRequest;
import com.sap.me.nonconformance.ReadNCResponse;
import com.sap.me.nonconformance.UpdateNCStatusRequest;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.appconfig.FindSystemRuleSettingRequest;
import com.sap.me.appconfig.SystemRuleServiceInterface;
import com.sap.me.appconfig.SystemRuleSetting;
import com.sap.me.nonconformance.NcCodeConfigurationServiceInterface;
/**
 * @author vmurthy
 *
 */
public class UpdateReworkStatusExtensionActivity implements
		ActivityInterface<UpdateNCStatusRequest> {

	private static final String NC_CODE_CONFIGURATION_SERVICE = "NcCodeConfigurationService";
	private static final String COM_SAP_ME_APPCONFIG = "com.sap.me.appconfig";
	private static final String SYSTEM_RULE_SERVICE = "SystemRuleService";
	private static final String NCPRODUCTION_SERVICE = "NCProductionService";
	private static final String COM_SAP_ME_NONCONFORMANCE = "com.sap.me.nonconformance";

	private static final long serialVersionUID = 1L;

	private NCProductionServiceInterface nCProductionService;
	private SystemRuleServiceInterface systemRuleService;
	private NcCodeConfigurationServiceInterface ncCodeConfigurationService;

	/*
	 * (non-Javadoc)
	 * @see com.visiprise.frame.service.ext.ActivityInterface#execute(java.lang.Object)
	 */
	public void execute(com.sap.me.nonconformance.UpdateNCStatusRequest dto)
			throws Exception {
		// TODO Auto-generated method stub
		initServices();
		boolean sysruleval = false;
		String ruleName = "Z_FAILURE_ID_VALIDATE_ON_OFF";
		FindSystemRuleSettingRequest findsysrulereq = new FindSystemRuleSettingRequest();
		findsysrulereq.setRuleName(ruleName);
		SystemRuleSetting sysrulesetting1 = systemRuleService.findSystemRuleSetting(findsysrulereq);
		sysruleval =  Boolean.valueOf(sysrulesetting1.getSetting().toString());
		if (sysruleval == true){
		
		//process this logic when user tries to close a NC code 
		if(dto.getNcStatus().name().equals("CLOSED")){	
	  	
		String ncref = dto.getNcRef();
		//get information from nc ref
		String parentNCref = null;
		String ncCategory = null;
	    String failureId = null;
	   
		ReadNCRequest ncreq = new ReadNCRequest(ncref);
		ReadNCResponse ncResponse = nCProductionService.readNC(ncreq);
		parentNCref = ncResponse.getParentNCRef();
		ncCategory = ncResponse.getNcCodeCategory().value();

		//get the exact nc code
		ObjectReference objref = new ObjectReference();
		objref.setRef(ncResponse.getNcCodeRef());
		NcCodeBasicConfiguration ncCodeResp = ncCodeConfigurationService.findNcCodeByRef(objref);
		String ncCode = ncCodeResp.getNcCode();
		//validate if this code is a tertiary NC code
		
	    if(parentNCref != null && !ncCategory.equals("FAILURE") && !ncCategory.equals("DEFECT") && ncCode.equals("LOG_REWORK_STATE")){   
	        	failureId = ncResponse.getFailureId();         
				Connection connect = DataSourceConnection.getSQLConnection();
	    		PreparedStatement pstmt;
	    		String setFailureId= ("UPDATE NC_DATA SET FAILURE_ID = '"+failureId+"' WHERE HANDLE = ?");
				pstmt = connect.prepareStatement(setFailureId);
				pstmt.setString(1, parentNCref);
			try
			{
				pstmt.executeUpdate();
				connect.close();
			} 
			catch (Exception updatefailed){
	           	throw new BusinessException(20222);		
			}
	    			
		}
		}
		}
	    }
		//}

		/**
		 *	Initialization of services that are represented as fields.
		 */
		private void initServices(){
			nCProductionService = Services.getService(COM_SAP_ME_NONCONFORMANCE,NCPRODUCTION_SERVICE);
			systemRuleService = Services.getService(COM_SAP_ME_APPCONFIG,SYSTEM_RULE_SERVICE);
			ncCodeConfigurationService = Services.getService(COM_SAP_ME_NONCONFORMANCE,NC_CODE_CONFIGURATION_SERVICE);
		}


	}


