package com.varian.hook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sap.me.appconfig.FindSystemRuleSettingRequest;
import com.sap.me.appconfig.SystemRuleSetting;
import com.sap.me.common.ObjectReference;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.productdefinition.FindItemGroupsForItemRefRequest;
import com.sap.me.productdefinition.ItemGroupBasicConfiguration;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.BuyoffStateEnum;
import com.sap.me.production.FindBuyoffsBySfcData;
import com.sap.me.production.FindBuyoffsBySfcRequest;
import com.sap.me.production.FindBuyoffsBySfcResponse;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcStep;
import com.sap.me.production.StartHookDTO;
import com.varian.integration.connection.DataSourceConnection;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.BuyoffServiceInterface;
import com.sap.me.productdefinition.OperationConfigurationServiceInterface;
import com.sap.me.user.UserBasicConfiguration;
import com.sap.me.user.UserConfigurationServiceInterface;
import com.sap.me.appconfig.SystemRuleServiceInterface;
import com.sap.me.productdefinition.ItemGroupConfigurationServiceInterface;

public class EnableBuyoffatRework implements ActivityInterface<StartHookDTO> {

	private static final String ITEM_GROUP_CONFIGURATION_SERVICE = "ItemGroupConfigurationService";
	private static final String COM_SAP_ME_APPCONFIG = "com.sap.me.appconfig";
	private static final String SYSTEM_RULE_SERVICE = "SystemRuleService";
	private static final String COM_SAP_ME_USER = "com.sap.me.user";
	private static final String USER_CONFIGURATION_SERVICE = "UserConfigurationService";
	private static final String OPERATION_CONFIGURATION_SERVICE = "OperationConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final String BUYOFF_SERVICE = "BuyoffService";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService;
	private BuyoffServiceInterface buyoffService;
	private OperationConfigurationServiceInterface operationConfigurationService;
	private UserConfigurationServiceInterface userConfigurationService;
	private SystemRuleServiceInterface systemRuleService;
	private ItemGroupConfigurationServiceInterface itemGroupConfigurationService;

	public void execute(StartHookDTO dto) throws Exception {
		initServices();
		String userRef = null;
		int timesprocessed = 0;
		String sfcBO = dto.getSfcBO().getValue().toString();
		String operBO = dto.getOperationBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcBasicData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		ObjectReference objOperRef = new ObjectReference(operBO);
		OperationKeyData opKeyData = operationConfigurationService
				.findOperationKeyDataByRef(objOperRef);
		String sfcnumber = sfckeydata.getSfc();
		String itemRef = sfckeydata.getItemRef();
		String currOp = opKeyData.getOperation();
		String currOpRefHash = operBO.substring(0, operBO.lastIndexOf(",") + 1)
				+ "#";
		SfcIdentifier sfcIdentifier = new SfcIdentifier(sfcBO);
		Collection<SfcStep> sfcStepColl = sfcStateService
				.findStepsSFCIsInWorkFor(sfcIdentifier);
		Iterator<SfcStep> sfcStepIterator = sfcStepColl.iterator();
		while (sfcStepIterator.hasNext()) {
			SfcStep sfcStepdetail = (SfcStep) sfcStepIterator.next();
			String inWorkOper = sfcStepdetail.getOperationRef();
			if (inWorkOper.equals(currOpRefHash)) {
				userRef = sfcStepdetail.getUserRef();
				timesprocessed = sfcStepdetail.getTimesProcessed().intValue();
			}
		}
		int reworkCount = 0;
		int reworkflag = 0;
		Data parametricData1 = null;
		Data parametricData2 = null;
		Data parametricData3 = null;
		Data queryData1 = null;
		Data queryData2 = null;
		Data queryData3 = null;
		SystemBase sysBase = SystemBase.createDefaultSystemBase();
		DynamicQuery selreworkflag = DynamicQueryFactory.newInstance();
		selreworkflag
				.append("select COUNT(*) as COUNT1 from SFC_STEP where USE_AS_REWORK = 'true'"
						+ "and HANDLE like '%" + sfcBO + ",%'");
		queryData2 = sysBase.executeQuery(selreworkflag);
		if (queryData2 != null && queryData2.size() > 0) {
			Iterator<Data> dataIterator = queryData2.iterator();
			parametricData2 = dataIterator.next();
			reworkflag = parametricData2.getInteger("COUNT1");
		}
		DynamicQuery getReworkCount = DynamicQueryFactory.newInstance();
		getReworkCount
				.append("select COUNT(*) AS COUNT2 from production_log where SFC= '"
						+ sfcnumber + "' and REWORK = 'true'");
		queryData1 = sysBase.executeQuery(getReworkCount);
		if (queryData1.size() > 0) {
			Iterator<Data> dataIterator = queryData1.iterator();
			parametricData1 = dataIterator.next();
			reworkCount = parametricData1.getInteger("COUNT2");
		}
		FindBuyoffsBySfcData findbuyoffbysfcdata = new FindBuyoffsBySfcData();
		findbuyoffbysfcdata.setSfcRef(sfcBO);
		List<FindBuyoffsBySfcData> sfcReqList = new ArrayList<FindBuyoffsBySfcData>();
		sfcReqList.add(findbuyoffbysfcdata);
		FindBuyoffsBySfcRequest findbuyoffbysfcrequest = new FindBuyoffsBySfcRequest();
		findbuyoffbysfcrequest.setOperationRef(operBO);
		findbuyoffbysfcrequest.setSfcList(sfcReqList);
		findbuyoffbysfcrequest.setResourceRef(dto.getResourceBO().getValue()
				.toString());
		List<FindBuyoffsBySfcResponse> buyofflist = new ArrayList<FindBuyoffsBySfcResponse>();
		try {
			buyofflist = buyoffService
					.findBuyoffsForSfcs(findbuyoffbysfcrequest);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		if (buyofflist != null && buyofflist.size() > 0) {

			for (FindBuyoffsBySfcResponse buyoffRecord : buyofflist) {
				BuyoffStateEnum state = buyoffRecord.getState();
				String actualstate = state.value();

				String buyoff = buyoffRecord.getBuyoff();
				String buyoffRev = buyoffRecord.getRevision();
				String buyoffRef = "BuyoffBO:0536," + buyoff + "," + buyoffRev;
				String buyoffUser = buyoffRecord.getUserRef();
				int buyoffReworkHistory = 0;
				if (actualstate.equals("C")) {
					if (reworkCount > 0 || reworkflag > 0) {
						DynamicQuery selReworkHistory = DynamicQueryFactory
								.newInstance();
						selReworkHistory
								.append("select COUNT(*) as COUNT3 from BUYOFF_LOG WHERE SFC_BO='"
										+ sfcBO
										+ "' AND BUYOFF_BO='"
										+ buyoffRef
										+ "' AND OPERATION_BO='"
										+ currOpRefHash
										+ "' AND BUYOFF_ACTION = 'P' AND COMMENTS like '%("
										+ timesprocessed + ")'");
						queryData3 = sysBase.executeQuery(selReworkHistory);
						if (queryData3 != null && queryData3.size() > 0) {
							Iterator<Data> dataIterator = queryData3.iterator();
							parametricData3 = dataIterator.next();
							buyoffReworkHistory = parametricData3
									.getInteger("COUNT3");
						}
						if (buyoffReworkHistory == 0) {
							Connection connect = DataSourceConnection
									.getSQLConnection();
							PreparedStatement pstmt;
							String updateStmnt = ("UPDATE BUYOFF_LOG SET BUYOFF_ACTION = 'P',STATE='O', COMMENTS = 'Rework ("
									+ timesprocessed
									+ ")'"
									+ " WHERE SFC_BO='"
									+ sfcBO
									+ "' AND BUYOFF_BO='"
									+ buyoffRef
									+ "' AND OPERATION_BO='" + currOpRefHash + "' AND BUYOFF_ACTION = 'A'");
							pstmt = connect.prepareStatement(updateStmnt);
							pstmt.executeUpdate();
							connect.close();
						} else {
							if (!userRef.equals(buyoffUser)) {
								Connection connect = DataSourceConnection
										.getSQLConnection();
								PreparedStatement pstmt;
								String updateStmnt = ("UPDATE BUYOFF_LOG SET BUYOFF_ACTION = 'P',STATE='O', COMMENTS = 'User Change ("
										+ timesprocessed
										+ ")'"
										+ " WHERE SFC_BO='"
										+ sfcBO
										+ "' AND BUYOFF_BO='"
										+ buyoffRef
										+ "' AND OPERATION_BO='"
										+ currOpRefHash + "' AND BUYOFF_ACTION = 'A'");
								pstmt = connect.prepareStatement(updateStmnt);
								pstmt.executeUpdate();
								connect.close();
							}
						}
					} else {
						if (!userRef.equals(buyoffUser)) {
							Connection connect = DataSourceConnection
									.getSQLConnection();
							PreparedStatement pstmt;
							String updateStmnt = ("UPDATE BUYOFF_LOG SET BUYOFF_ACTION = 'P',STATE='O', COMMENTS = 'User Change (1)'"
									+ " WHERE SFC_BO='"
									+ sfcBO
									+ "' AND BUYOFF_BO='"
									+ buyoffRef
									+ "' AND OPERATION_BO='" + currOpRefHash + "' AND BUYOFF_ACTION = 'A'");
							pstmt = connect.prepareStatement(updateStmnt);
							pstmt.executeUpdate();
							connect.close();
						}
					}
				}

			}

		}
		
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
			Data queryData4 = null;
			DynamicQuery selBuyoffDetail = DynamicQueryFactory.newInstance();
			selBuyoffDetail.append("select HANDLE from buyoff "
					+ "where BUYOFF = '" + buyoff
					+ "' and CURRENT_REVISION = 'true'and SITE = '0536' ");
			queryData4 = sysBase.executeQuery(selBuyoffDetail);
			String buyoffReference = null;
			if (queryData4.size() > 0) {
				buyoffReference = queryData4.getString("HANDLE", "");
			}
			Data queryData5 = null;
			DynamicQuery selLastAction = DynamicQueryFactory.newInstance();
			selLastAction
					.append("select COUNT(*) AS COUNT from BUYOFF_LOG where state = 'C' and BUYOFF_ACTION = 'A' "
							+ "and OPERATION_BO = '"
							+ currOpRefHash
							+ "' and SFC_BO = '"
							+ sfcBO
							+ "' and BUYOFF_BO = '" + buyoffReference + "'");
			queryData5 = sysBase.executeQuery(selLastAction);
			int count = Integer.parseInt(queryData5.getString("COUNT", ""));
			if (count > 0) {
				// get the user who last completed the buyoff
				Data queryData7 = null;
				String pastuser = null;
				DynamicQuery selCloseBuyoffUser = DynamicQueryFactory
						.newInstance();
				selCloseBuyoffUser
						.append("select USER_BO from BUYOFF_LOG where state = 'C' and BUYOFF_ACTION = 'A' "
								+ "and OPERATION_BO = '"
								+ currOpRefHash
								+ "' and SFC_BO = '"
								+ sfcBO
								+ "' and BUYOFF_BO = '" + buyoffReference + "'");
				queryData7 = sysBase.executeQuery(selCloseBuyoffUser);
				if (queryData7.size() > 0) {
					ObjectReference objUsrRef = new ObjectReference();
					objUsrRef.setRef(queryData7.getString("USER_BO", ""));
					UserBasicConfiguration userbconfig = userConfigurationService
							.findUserByRef(objUsrRef);
					pastuser = userbconfig.getUserId();
				}

				//
				if (reworkCount > 0 || reworkflag > 0) {
					Data queryData6 = null;
					DynamicQuery selReworkHistory = DynamicQueryFactory
							.newInstance();
					selReworkHistory
							.append("select COUNT(*) as COUNT3 from BUYOFF_LOG WHERE SFC_BO='"
									+ sfcBO
									+ "' AND BUYOFF_BO='"
									+ buyoffReference
									+ "' AND OPERATION_BO='"
									+ currOpRefHash
									+ "' AND BUYOFF_ACTION = 'P' AND COMMENTS like '%("
									+ timesprocessed + ")'");
					queryData6 = sysBase.executeQuery(selReworkHistory);
					int buyoffReworkHistory = 0;
					if (queryData6 != null && queryData6.size() > 0) {
						Iterator<Data> dataIterator = queryData3.iterator();
						parametricData3 = dataIterator.next();
						buyoffReworkHistory = parametricData3
								.getInteger("COUNT3");
					}
					if (buyoffReworkHistory == 0) {
						Connection connect = DataSourceConnection
								.getSQLConnection();
						PreparedStatement pstmt;
						String updateStmnt = ("UPDATE BUYOFF_LOG SET BUYOFF_ACTION = 'P',STATE='O', COMMENTS = 'Rework ("
								+ timesprocessed
								+ ")'"
								+ " WHERE SFC_BO='"
								+ sfcBO
								+ "' AND BUYOFF_BO='"
								+ buyoffReference
								+ "' AND OPERATION_BO='" + currOpRefHash + "' AND BUYOFF_ACTION = 'A'");
						pstmt = connect.prepareStatement(updateStmnt);
						pstmt.executeUpdate();
						connect.close();
					} else {
						if (!userRef.equals(pastuser)) {
							Connection connect = DataSourceConnection
									.getSQLConnection();
							PreparedStatement pstmt;
							String updateStmnt = ("UPDATE BUYOFF_LOG SET BUYOFF_ACTION = 'P',STATE='O', COMMENTS = 'User Change ("
									+ timesprocessed
									+ ")'"
									+ " WHERE SFC_BO='"
									+ sfcBO
									+ "' AND BUYOFF_BO='"
									+ buyoffReference
									+ "' AND OPERATION_BO='"
									+ currOpRefHash + "' AND BUYOFF_ACTION = 'A'");
							pstmt = connect.prepareStatement(updateStmnt);
							pstmt.executeUpdate();
							connect.close();
						}
					}
				} else {
					if (!userRef.equals(pastuser)) {
						Connection connect = DataSourceConnection
								.getSQLConnection();
						PreparedStatement pstmt;
						String updateStmnt = ("UPDATE BUYOFF_LOG SET BUYOFF_ACTION = 'P',STATE='O', COMMENTS = 'User Change (1)'"
								+ " WHERE SFC_BO='"
								+ sfcBO
								+ "' AND BUYOFF_BO='"
								+ buyoffReference
								+ "' AND OPERATION_BO='" + currOpRefHash + "' AND BUYOFF_ACTION = 'A'");
						pstmt = connect.prepareStatement(updateStmnt);
						pstmt.executeUpdate();
						connect.close();
					}
				}

			}
		}
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,
				SFC_STATE_SERVICE);
		buyoffService = Services.getService(COM_SAP_ME_PRODUCTION,
				BUYOFF_SERVICE);
		operationConfigurationService = Services.getService(
				COM_SAP_ME_PRODUCTDEFINITION, OPERATION_CONFIGURATION_SERVICE);
		userConfigurationService = Services.getService(COM_SAP_ME_USER,
				USER_CONFIGURATION_SERVICE);
		systemRuleService = Services.getService(COM_SAP_ME_APPCONFIG,SYSTEM_RULE_SERVICE);
		itemGroupConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,ITEM_GROUP_CONFIGURATION_SERVICE);
	}
}