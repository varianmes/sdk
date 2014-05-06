/**
 * 
 */
package com.varian.hook;

import java.util.Collection;

import com.sap.me.appconfig.FindSystemRuleSettingRequest;
import com.sap.me.appconfig.SystemRuleSetting;
import com.sap.me.common.ObjectReference;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.productdefinition.FindItemGroupsForItemRefRequest;
import com.sap.me.productdefinition.ItemGroupBasicConfiguration;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.CompleteHookDTO;
import com.sap.me.production.SfcBasicData;
import com.varian.hook.exception.TQCBuyoffException;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.appconfig.SystemRuleServiceInterface;
import com.sap.me.productdefinition.ItemGroupConfigurationServiceInterface;

/**
 * @author vmurthy
 *
 */
public class CheckTQCbuyoff implements ActivityInterface<CompleteHookDTO> {

	private static final String ITEM_GROUP_CONFIGURATION_SERVICE = "ItemGroupConfigurationService";
	private static final String COM_SAP_ME_APPCONFIG = "com.sap.me.appconfig";
	private static final String SYSTEM_RULE_SERVICE = "SystemRuleService";
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private SystemRuleServiceInterface systemRuleService;
	private ItemGroupConfigurationServiceInterface itemGroupConfigurationService;

	/*
	 * (non-Javadoc)
	 * @see com.visiprise.frame.service.ext.ActivityInterface#execute(java.lang.Object)
	 */
	public void execute(CompleteHookDTO dto) throws Exception {
		initServices();
		String currOpRef = dto.getOperationBO().getValue().toString();
		String sfcRef = dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcRef);
		SfcBasicData sfcBasicData = sfcStateService.findSfcDataByRef(sfcreference);
		String itemRef = sfcBasicData.getItemRef();
		String sfcNumber =  sfcBasicData.getSfc();
		ObjectReference operreference = new ObjectReference(currOpRef);
		OperationKeyData operKeyData = operationConfigurationService.findOperationKeyDataByRef(operreference);
		String currOp =  operKeyData.getOperation();
		//
		String rulename1 = "Z_TQC_OPERATIONS";
		String sysruleval1 = null;
		String sysruleval2 = null;
		FindSystemRuleSettingRequest findsysrulereq1 = new FindSystemRuleSettingRequest();
		findsysrulereq1.setRuleName(rulename1);
		SystemRuleSetting sysrulesetting1 = systemRuleService.findSystemRuleSetting(findsysrulereq1);				
		sysruleval1 = sysrulesetting1.getSetting().toString();
		if(sysruleval1.contains(currOp+";")){
			String buyoff = null;
			if (currOp.equals("FINAL_TEST")){
				String rulename2 = "Z_TQC_MATERIAL_GROUPS";
				FindSystemRuleSettingRequest findsysrulereq2 = new FindSystemRuleSettingRequest();
				findsysrulereq2.setRuleName(rulename2);
				SystemRuleSetting sysrulesetting2 = systemRuleService.findSystemRuleSetting(findsysrulereq2);				
				sysruleval2 = sysrulesetting2.getSetting().toString();
				FindItemGroupsForItemRefRequest itemGrpReq = new FindItemGroupsForItemRefRequest();
				itemGrpReq.setItemRef(itemRef);
				Collection<ItemGroupBasicConfiguration> itemGroupConfig = itemGroupConfigurationService.findItemGroupsForItemRef(itemGrpReq);
				for (ItemGroupBasicConfiguration attrValue : itemGroupConfig) {
					if (sysruleval2.contains(attrValue.getItemGroup()+";")){
						buyoff = "TQC_FINAL_TEST";
					}
				}
				if (buyoff== null){
					return;
				}
				
			} else if (currOp.equals("BOX_UP") || currOp.equals("BOX_UP_1") || currOp.equals("BOX_UP_2") || currOp.equals("BOX_UP_CUSTOM")){
				buyoff = "TQC_BOX_UP";
			} else {
				buyoff = "TQC";
			}
			Data queryData = null;
			SystemBase sysBase = SystemBase.createDefaultSystemBase();
			DynamicQuery selBuyoffDetail = DynamicQueryFactory.newInstance();
			selBuyoffDetail
					.append("select HANDLE from buyoff "
							+ "where BUYOFF = '"+buyoff+"' and CURRENT_REVISION = 'true'and SITE = '0536' ");
			queryData = sysBase.executeQuery(selBuyoffDetail);
			String buyoffRef = null;
			if (queryData.size() > 0) {
			buyoffRef = queryData.getString("HANDLE", "");
			}
			String currOpRefHash = currOpRef.substring(0, currOpRef
					.lastIndexOf(",") + 1)
					+ "#";
			Data queryData1 = null;
			DynamicQuery selLastAction = DynamicQueryFactory.newInstance();
			selLastAction
					.append("select COUNT(*) AS COUNT from BUYOFF_LOG where state = 'C' and BUYOFF_ACTION = 'A' "
							+ "and OPERATION_BO = '"
							+ currOpRefHash
							+ "' and SFC_BO = '"
							+ sfcRef
							+ "' and BUYOFF_BO = '" + buyoffRef + "'");
			queryData1 = sysBase.executeQuery(selLastAction);
			int count = Integer.parseInt(queryData1.getString("COUNT", ""));
			if (count == 0) {
				throw new TQCBuyoffException(20127,sfcNumber);
			} 
			} 
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
		operationConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,OPERATION_CONFIGURATION_SERVICE);
		systemRuleService = Services.getService(COM_SAP_ME_APPCONFIG,SYSTEM_RULE_SERVICE);
		itemGroupConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,ITEM_GROUP_CONFIGURATION_SERVICE);
	}

}
