/**
 * 
 */
package com.varian.nonconformance.service.plugin;

import com.sap.me.appconfig.FindSystemRuleSettingRequest;
import com.sap.me.appconfig.SystemRuleServiceInterface;
import com.sap.me.appconfig.SystemRuleSetting;
import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.nonconformance.CreateNCRequest;
import com.sap.me.nonconformance.NcCodeBasicConfiguration;
import com.sap.me.nonconformance.NcCodeConfigurationServiceInterface;
import com.visiprise.frame.service.ext.ActivityInterface;

/**
 * @author vmurthy
 * 
 *
 */
public class ValidateReworkStatusExtensionActivity implements
		ActivityInterface<CreateNCRequest> {

	private static final String COM_SAP_ME_APPCONFIG = "com.sap.me.appconfig";
	private static final String SYSTEM_RULE_SERVICE = "SystemRuleService";
	private static final String NC_CODE_CONFIGURATION_SERVICE = "NcCodeConfigurationService";
	private static final String COM_SAP_ME_NONCONFORMANCE = "com.sap.me.nonconformance";
	private static final long serialVersionUID = 1L;
	private NcCodeConfigurationServiceInterface ncCodeConfigurationService;
	private SystemRuleServiceInterface systemRuleService;
	public void execute(com.sap.me.nonconformance.CreateNCRequest dto)
			throws Exception {
		// TODO Auto-generated method stub
		initServices();
		boolean sysruleval = false;
		String ruleName = "Z_REC_HIST_VALIDATE_ON_OFF";
		FindSystemRuleSettingRequest findsysrulereq = new FindSystemRuleSettingRequest();
		findsysrulereq.setRuleName(ruleName);
		SystemRuleSetting sysrulesetting1 = systemRuleService.findSystemRuleSetting(findsysrulereq);
		sysruleval =  Boolean.valueOf(sysrulesetting1.getSetting().toString());
		if (sysruleval == true){

	    String ncRef = dto.getNcCodeRef().toString(); 
	    String ncCategory = null;
	    String failureId = null;

	    ObjectReference objRef = new ObjectReference();
	    objRef.setRef(ncRef);
	    NcCodeBasicConfiguration ncCodeconfig= ncCodeConfigurationService.findNcCodeByRef(objRef);
	    ncCategory = ncCodeconfig.getNcCategory().value();
	    String ncCode = ncCodeconfig.getNcCode().toString();

	    if(ncCategory.equals("REPAIR") && ncCode.equals("LOG_REWORK_STATE")){ 		
			 failureId =  dto.getFailureId();
			 	if(failureId == null){
			 			throw new BusinessException(20220);	
			 	} else if ( ! ( failureId.equalsIgnoreCase("PASS") || failureId.equalsIgnoreCase("FAIL")) ){
			 			
			 			throw new BusinessException(20220);
	    		
			 	}
		 	}
		}
		
		}


		private void initServices(){
			ncCodeConfigurationService = Services.getService(COM_SAP_ME_NONCONFORMANCE,NC_CODE_CONFIGURATION_SERVICE);
			systemRuleService = Services.getService(COM_SAP_ME_APPCONFIG,SYSTEM_RULE_SERVICE);
		}

	}
