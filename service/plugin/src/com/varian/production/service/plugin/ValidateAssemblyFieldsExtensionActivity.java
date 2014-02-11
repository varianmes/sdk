package com.varian.production.service.plugin;
import java.util.List;

import com.sap.me.appconfig.FindSystemRuleSettingRequest;
import com.sap.me.appconfig.SystemRuleSetting;
import com.sap.me.common.ObjectReference;
import com.sap.me.production.AssemblyDataField;
import com.sap.me.production.AssemblyDataValidationRequest;
import com.sap.me.production.SfcKeyData;
import com.varian.hook.exception.InvalidGlassSerialNumberException;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.appconfig.SystemRuleServiceInterface;

public class ValidateAssemblyFieldsExtensionActivity implements
		ActivityInterface<AssemblyDataValidationRequest> {

	private static final String COM_SAP_ME_APPCONFIG = "com.sap.me.appconfig";
	private static final String SYSTEM_RULE_SERVICE = "SystemRuleService";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService;
	private SystemRuleServiceInterface systemRuleService;
	
	public void execute(com.sap.me.production.AssemblyDataValidationRequest dto)
			throws Exception {
		initServices();
		boolean sysruleval = false;
		String ruleName = "Z_ASSEMBLY_DATA_VALIDATION";
		FindSystemRuleSettingRequest findsysrulereq = new FindSystemRuleSettingRequest();
		findsysrulereq.setRuleName(ruleName);
		SystemRuleSetting sysrulesetting = systemRuleService.findSystemRuleSetting(findsysrulereq);
		sysruleval =  Boolean.valueOf(sysrulesetting.getSetting().toString());	
		if (sysruleval){
		String assemblyData = null;
		ObjectReference objsfcRef = new ObjectReference(dto.getSfcRef().toString());
		SfcKeyData sfcKeyData = sfcStateService.findSfcKeyDataByRef(objsfcRef);
		String sfcNumber = sfcKeyData.getSfc();
		String[] mySfcStrings = sfcNumber.split("-");
		String sfcSerialNumber = null;
		String sfcPrefix = mySfcStrings[0];
		try {
		sfcSerialNumber = mySfcStrings[1];
		} catch (Exception e) {
			throw new InvalidGlassSerialNumberException(20116, sfcNumber);	
		}
		//String sfcSerialNumber = sfcNumber.substring(3,sfcNumber.length());
		List<AssemblyDataField> assemblyDataFields = dto.getAssemblyDataFields();
		for (AssemblyDataField assemblyDataField : assemblyDataFields) {
			if ("GLASS_SERIAL_NUMBER".equals(assemblyDataField.getAttribute())) {
				//assemblyData = assemblyDataField.getValue();
				String[] myAssyStrings = assemblyDataField.getValue().split("-");
				assemblyData = myAssyStrings[0];
					if (assemblyData == null || sfcSerialNumber == null){
						throw new InvalidGlassSerialNumberException(20116, sfcNumber);
					}
					if (!assemblyData.equals(sfcSerialNumber)){
					throw new InvalidGlassSerialNumberException(20117, sfcNumber);
					}				
			}
		if ("SFC".equals(assemblyDataField.getAttribute())) {
			//assemblyData = assemblyDataField.getValue();
			//glassSerial = assemblyData.substring(3,assemblyData.length());
			String[] myAssyStrings = assemblyDataField.getValue().split("-");
			String assemblyDataPrefix = myAssyStrings[0];
			if (sfcPrefix.length()==2 && assemblyDataPrefix.length()==2){
				if (sfcPrefix.equals("UR")){
					if (!assemblyDataPrefix.equals("FM")){
						throw new InvalidGlassSerialNumberException(20120,sfcNumber);
					}
				} else if (sfcPrefix.equals("TR")){
					if (!assemblyDataPrefix.equals("UR")){
						throw new InvalidGlassSerialNumberException(20121,sfcNumber);
					}
				} else {
					throw new InvalidGlassSerialNumberException(20119,sfcNumber);
				}
				
			} else {
				throw new InvalidGlassSerialNumberException(20119, sfcNumber);
			}
			try{
			assemblyData = myAssyStrings[1];
			} catch (Exception e){
				throw new InvalidGlassSerialNumberException(20116, sfcNumber);
			}
			if (assemblyData == null || sfcSerialNumber == null){
				throw new InvalidGlassSerialNumberException(20116, sfcNumber);	
			}
			if (!assemblyData.equals(sfcSerialNumber)){
				throw new InvalidGlassSerialNumberException(20118, sfcNumber);
				}		
			}
		}
		}
		}

	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
		systemRuleService = Services.getService(COM_SAP_ME_APPCONFIG,SYSTEM_RULE_SERVICE);
	}
	}
