package com.varian.production.web.podplugin;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sap.me.appconfig.FindSystemRuleSettingRequest;
import com.sap.me.appconfig.SystemRuleSetting;
import com.sap.me.common.ObjectReference;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.plant.ResourceKeyData;
import com.sap.me.productdefinition.FindItemGroupsForItemRefRequest;
import com.sap.me.productdefinition.ItemGroupBasicConfiguration;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.AcceptBuyoffSfcData;
import com.sap.me.production.AcceptBuyoffSfcStepData;
import com.sap.me.production.AcceptBuyoffsRequest;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.SfcStep;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.PodClientTableConfigurator;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.user.UserBasicConfiguration;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.me.wpmf.util.MessageHandler;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserAccountFactory;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.user.UserConfigurationServiceInterface;
import com.sap.me.production.BuyoffServiceInterface;
import com.varian.integration.connection.DataSourceConnection;
import com.varian.lsf.BuyoffDetailsItem;
import com.visiprise.globalization.DateGlobalizationServiceInterface;
import com.visiprise.globalization.GlobalizationService;
import com.visiprise.globalization.util.DateFormatStyle;
import com.visiprise.globalization.util.DateTimeInterface;
import com.visiprise.globalization.util.LocaleContextType;
import com.visiprise.globalization.util.TimeFormatStyle;
import com.sap.me.appconfig.SystemRuleServiceInterface;
import com.sap.me.productdefinition.ItemGroupConfigurationServiceInterface;

public class LogTqcBuyoffPlugin extends BasePodPlugin implements LoginModule {
	private static final String ITEM_GROUP_CONFIGURATION_SERVICE = "ItemGroupConfigurationService";
	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final String COM_SAP_ME_APPCONFIG = "com.sap.me.appconfig";
	private static final String SYSTEM_RULE_SERVICE = "SystemRuleService";
	private static final String BUYOFF_SERVICE = "BuyoffService";
	private static final String COM_SAP_ME_USER = "com.sap.me.user";
	private static final String USER_CONFIGURATION_SERVICE = "UserConfigurationService";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	DateGlobalizationServiceInterface dateTimeService = (DateGlobalizationServiceInterface) GlobalizationService
			.getService(LocaleContextType.USER,
					"com.visiprise.globalization.DateGlobalizationService");
	private static final long serialVersionUID = 5720994199428143027L;
	private String userId = null;
	private String password = null;
	private String sfc;
	private String sfcRef;
	private String currOpRef;
	private String currResRef;
	private SfcStateServiceInterface sfcStateService;
	private String userRef;
	private String userInwork;
	private UserConfigurationServiceInterface userConfigurationService;
	private TableConfigurator tableConfig;
	private List<BuyoffDetailsItem> buyoffDetailsList;
	private String buyoffAction = "ACCEPT";
	private String buyoffact = "Accept";
	private BuyoffServiceInterface buyoffService;
	private String buyoffRef;
	private String action = "Pending";
	private String comments = null;
	private String pastuser = null;
	private String buyoffState = null;
	private String routingRef = null;
	private String newcomments;
	private String currOp;
	private SystemRuleServiceInterface systemRuleService;
	private ItemGroupConfigurationServiceInterface itemGroupConfigurationService;

	public String getCurrOp() {
		return currOp;
	}

	public void setCurrOp(String currOp) {
		this.currOp = currOp;
	}

	public String getUserRef() {
		return userRef;
	}

	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}

	public String getNewcomments() {
		return newcomments;
	}

	public void setNewcomments(String newcomments) {
		this.newcomments = newcomments;
	}

	public String getRoutingRef() {
		return routingRef;
	}

	public void setRoutingRef(String routingRef) {
		this.routingRef = routingRef;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getBuyoffRef() {
		return buyoffRef;
	}

	public void setBuyoffRef(String buyoffRef) {
		this.buyoffRef = buyoffRef;
	}

	public String getCurrResRef() {
		return currResRef;
	}

	public void setCurrResRef(String currResRef) {
		this.currResRef = currResRef;
	}

	public String getBuyoffact() {
		return buyoffact;
	}

	public void setBuyoffact(String buyoffact) {
		this.buyoffact = buyoffact;
	}

	public String getBuyoffAction() {
		return buyoffAction;
	}

	public void setBuyoffAction(String buyoffAction) {
		this.buyoffAction = buyoffAction;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<BuyoffDetailsItem> getBuyoffDetailsList() {
		return buyoffDetailsList;
	}

	public void setBuyoffDetailsList(List<BuyoffDetailsItem> buyoffDetailsList) {
		this.buyoffDetailsList = buyoffDetailsList;
	}

	public String getSfc() {
		return sfc;
	}

	public void setSfc(String sfc) {
		this.sfc = sfc;
	}

	public LogTqcBuyoffPlugin() throws Exception {
		super();
		getPluginEventManager().addPluginListeners(this.getClass());
		populateTable();

	}

	@Override
	public void beforeLoad() throws Exception {
		FacesUtility.setSessionMapValue("logTqcBuyoffBean", null);
		TableConfigurator table = (TableConfigurator) FacesUtility
				.resolveExpression("#{tqcBuyoffTableBeanConfigurator}");
		setTableConfig(table);

	}

	public void populateTable() throws BusinessException {
		this.buyoffDetailsList = null;
		sfcStepMapper();
	}

	private void sfcStepMapper() throws BusinessException {
		if (buyoffDetailsList == null) {
			buyoffDetailsList = new ArrayList<BuyoffDetailsItem>();
		}
		OperationKeyData opKeydata1 = getPodSelectionModel()
		.getResolvedOperation();
	
		initServices();
		List<SfcSelection> sfcList1 = getPodSelectionModel().getResolvedSfcs();
		if (sfcList1 != null && sfcList1.size() > 0) {
			SfcKeyData sfcKeyData = sfcList1.get(0).getSfc();
			sfc = sfcKeyData.getSfc();
			sfcRef = sfcKeyData.getSfcRef();
			currOp = opKeydata1.getOperation();
			//
			ObjectReference objsfcRef = new ObjectReference();
			objsfcRef.setRef(sfcRef);
			SfcBasicData sfcbasicdata = sfcStateService.findSfcDataByRef(objsfcRef);
			String itemRef = sfcbasicdata.getItemRef();
			String rulename1 = "Z_TQC_OPERATIONS";
			String sysruleval1 = null;
			String sysruleval2 = null;
			FindSystemRuleSettingRequest findsysrulereq1 = new FindSystemRuleSettingRequest();
			findsysrulereq1.setRuleName(rulename1);
			SystemRuleSetting sysrulesetting1 = systemRuleService.findSystemRuleSetting(findsysrulereq1);				
			sysruleval1 = sysrulesetting1.getSetting().toString();
			//
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
						MessageHandler.handle("TQC buyoff is not applicable for this operation",null, MessageType.ERROR, this);
						return;
					}
					
				} else if (currOp.equals("BOX_UP") || currOp.equals("BOX_UP_1") || currOp.equals("BOX_UP_2") || currOp.equals("BOX_UP_CUSTOM")){
					buyoff = "TQC_BOX_UP";
				} else {
					buyoff = "TQC";
				}
			String currOpRefHash1 = opKeydata1.getRef().substring(0,
					opKeydata1.getRef().lastIndexOf(",") + 1)
					+ "#";
			
				BuyoffDetailsItem sampleBuyoffDetailItem = new BuyoffDetailsItem();
				Data queryData = null;
				SystemBase sysBase = SystemBase.createDefaultSystemBase();
				DynamicQuery selBuyoffDetail = DynamicQueryFactory
						.newInstance();
				selBuyoffDetail
						.append("select HANDLE,BUYOFF,DESCRIPTION from buyoff "
								+ "where BUYOFF = '"+buyoff+"' and CURRENT_REVISION = 'true'and SITE = '0536' ");
				queryData = sysBase.executeQuery(selBuyoffDetail);

				if (queryData.size() > 0) {
					buyoffRef = queryData.getString("HANDLE", "");
					// check if buyoff is closed
					Data queryData1 = null;
					DynamicQuery selLastAction = DynamicQueryFactory
							.newInstance();
					selLastAction
							.append("select COUNT(*) AS COUNT from BUYOFF_LOG where state = 'C' and BUYOFF_ACTION = 'A' "
									+ "and OPERATION_BO = '"
									+ currOpRefHash1
									+ "' and SFC_BO = '"
									+ sfcRef
									+ "' and BUYOFF_BO = '" + buyoffRef + "'");
					queryData1 = sysBase.executeQuery(selLastAction);
					int count = Integer.parseInt(queryData1.getString("COUNT",
							""));
					if (count > 0) {
						buyoffState = "Closed";
						action = "Accept";
						comments = "";
						Data queryData4 = null;
						DynamicQuery selCloseBuyoffUser = DynamicQueryFactory
								.newInstance();
						selCloseBuyoffUser
								.append("select USER_BO from BUYOFF_LOG where state = 'C' and BUYOFF_ACTION = 'A' "
										+ "and OPERATION_BO = '"
										+ currOpRefHash1
										+ "' and SFC_BO = '"
										+ sfcRef
										+ "' and BUYOFF_BO = '"
										+ buyoffRef + "'");
						queryData4 = sysBase.executeQuery(selCloseBuyoffUser);
						if (queryData4.size() > 0) {
							ObjectReference objUsrRef = new ObjectReference();
							objUsrRef.setRef(queryData4
									.getString("USER_BO", ""));
							UserBasicConfiguration userbconfig = userConfigurationService
									.findUserByRef(objUsrRef);
							pastuser = userbconfig.getUserId();
						}
						// throw new TQCBuyoffException(20127,sfc);
					} else {
						Data queryData2 = null;
						DynamicQuery selLastBuyoffCount = DynamicQueryFactory
								.newInstance();
						selLastBuyoffCount
								.append("select count(*) as COUNT1 from BUYOFF_LOG where "
										+ "BUYOFF_LOG_ID = (select MAX(BUYOFF_LOG_ID) from BUYOFF_LOG where "
										+ "OPERATION_BO = '"
										+ currOpRefHash1
										+ "' and SFC_BO =  '"
										+ sfcRef
										+ "' and BUYOFF_BO = '"
										+ buyoffRef
										+ "')");
						queryData2 = sysBase.executeQuery(selLastBuyoffCount);
						int count1 = 0;
						if (queryData2.size() > 0) {
							count1 = Integer.parseInt(queryData2.getString(
									"COUNT1", ""));
						}
						if (count1 == 0) {
							action = "Pending";
							comments = " ";
							buyoffState = "Open";
							pastuser = " ";
						} else {
							Data queryData3 = null;
							DynamicQuery selLastBuyoff = DynamicQueryFactory
									.newInstance();
							selLastBuyoff
									.append("select comments, user_bo,state,buyoff_action from BUYOFF_LOG where "
											+ "BUYOFF_LOG_ID = (select MAX(BUYOFF_LOG_ID) from BUYOFF_LOG where "
											+ "OPERATION_BO = '"
											+ currOpRefHash1
											+ "' and SFC_BO =  '"
											+ sfcRef
											+ "' and BUYOFF_BO = '"
											+ buyoffRef
											+ "')");
							queryData3 = sysBase.executeQuery(selLastBuyoff);

							if (queryData3.size() > 0) {

								String tempaction = queryData3.getString(
										"BUYOFF_ACTION", "");
								if (tempaction.equals("R")) {
									action = "Rejected";
								} else if (tempaction.equals("P")) {
									action = "Partial";
								}
								comments = queryData3.getString("COMMENTS", "");
								String tempstate = queryData3.getString(
										"STATE", "");
								if (tempstate.equals("O")) {
									buyoffState = "Open";
								}
								ObjectReference objUserRef = new ObjectReference();
								objUserRef.setRef(queryData3.getString(
										"USER_BO", ""));
								UserBasicConfiguration userbconfig = userConfigurationService
										.findUserByRef(objUserRef);
								pastuser = userbconfig.getUserId();
							}
						}
					}
					sampleBuyoffDetailItem.setBuyoff(queryData.getString(
							"BUYOFF", ""));
					sampleBuyoffDetailItem.setDescription(queryData.getString(
							"DESCRIPTION", ""));
					sampleBuyoffDetailItem.setSfc(sfc);
					sampleBuyoffDetailItem.setAction(action);
					sampleBuyoffDetailItem.setComments(comments);
					sampleBuyoffDetailItem.setPastUser(pastuser);
					sampleBuyoffDetailItem.setBuyoffState(buyoffState);

				}

				buyoffDetailsList.add(sampleBuyoffDetailItem);
			
		} else {
			MessageHandler.handle("TQC buyoff is not applicable for this operation",null, MessageType.ERROR, this);
		return;
		
		}
		} else {
			MessageHandler.handle("No SFC selected", null, MessageType.ERROR,
					this);
			return;
		}
		

	}

	/**
	 * Sets the TableConfigurator bean
	 * 
	 * @param configBean
	 */
	public void setTableConfig(TableConfigurator tableConfig) {
		if (tableConfig == null) {
			this.tableConfig = new PodClientTableConfigurator();
		} else {
			this.tableConfig = tableConfig;
		}
		tableConfig.setMultiSelectType(false);
		tableConfig.setAllowSelections(false);
		// tableConfig.setActivatePaging(true)
	}

	public TableConfigurator getTableConfig() {
		return tableConfig;
	}

	public int validateLoginData() throws UMException, BusinessException {
		initServices();
		MessageHandler.clear(this);
		List<SfcSelection> sfcList = getPodSelectionModel().getResolvedSfcs();
		if (sfcList != null && sfcList.size() > 0) {
			if (buyoffState.equals("Closed")) {
				MessageHandler.handle(
						"Buyoff has been closed for the selected SFC", null,
						MessageType.ERROR, this);
				return 0;
			}
			if (userId == null || password == null) {
				MessageHandler.handle("User ID/password cannot be null", null,
						MessageType.ERROR, this);
				return 0;
			}
			char[] pwdArray = password.toCharArray();
			IUserAccountFactory uaFactory = UMFactory.getUserAccountFactory();
			IUserAccount ua = null;
			try {
				ua = uaFactory.getUserAccountByLogonId(userId);
			} catch (Exception ex) {
				MessageHandler.handle("Invalid User Id entered", null,
						MessageType.ERROR, this);
				return 0;
			}
			int passwordStatus = ua.checkPasswordExtended(new String(pwdArray));
			if (passwordStatus != ILoginConstants.CHECKPWD_OK) {
				MessageHandler.handle(
						"Invalid Password entered for the user id", null,
						MessageType.ERROR, this);
				return 0;
			} else {

				SfcKeyData sfcKeyData = sfcList.get(0).getSfc();
				sfcRef = sfcKeyData.getSfcRef();

				PodSelectionModelInterface selectionModel = getPodSelectionModel();
				OperationKeyData opKeydata = selectionModel
						.getResolvedOperation();
				currOpRef = opKeydata.getRef();
				String currOpRefHash = currOpRef.substring(0, currOpRef
						.lastIndexOf(",") + 1)
						+ "#";
				ResourceKeyData reskeydata = getPodSelectionModel()
						.getResolvedResource();
				currResRef = reskeydata.getRef();
				SfcIdentifier sfcIdentifier = new SfcIdentifier(sfcRef);
				Collection<SfcStep> sfcStepColl = sfcStateService
						.findStepsSFCIsInWorkFor(sfcIdentifier);
				Iterator<SfcStep> sfcStepIterator = sfcStepColl.iterator();

				while (sfcStepIterator.hasNext()) {
					SfcStep sfcStepdetail = (SfcStep) sfcStepIterator.next();
					String inWorkOper = sfcStepdetail.getOperationRef();
					routingRef = sfcStepdetail.getRouterRef();
					if (inWorkOper.equals(currOpRefHash)) {
						userRef = sfcStepdetail.getUserRef();
						ObjectReference userObjRef = new ObjectReference(
								userRef);
						UserBasicConfiguration userbasic = userConfigurationService
								.findUserByRef(userObjRef);
						userInwork = userbasic.getUserId();
					}
					if (userId.equals(userInwork)) {
						MessageHandler.handle(
								"Ask a different user to log TQC buyoff", null,
								MessageType.ERROR, this);
						return 0;
					}
				}
			}
		} else {
			MessageHandler.handle("Select a SFC to log TQC Buyoff", null,
					MessageType.ERROR, this);
			return 0;
		}
		return 1;
	}

	public void logbuyoff() throws UMException, BusinessException {
		if (validateLoginData() == 0) {
			return;
		}
		initServices();
		String currOpRefHash = currOpRef.substring(0, currOpRef
				.lastIndexOf(",") + 1)
				+ "#";
		String newUserRef = "UserBO:0536," + userId;
		if (buyoffAction.equals("ACCEPT")) {
			List<AcceptBuyoffSfcStepData> acceptbuyoffList = new ArrayList<AcceptBuyoffSfcStepData>();
			AcceptBuyoffSfcStepData acceptBData = new AcceptBuyoffSfcStepData();
			acceptBData.setResourceRef(currResRef);
			acceptBData.setOperationRef(currOpRef);
			acceptbuyoffList.add(acceptBData);
			AcceptBuyoffSfcData acceptBSData = new AcceptBuyoffSfcData();			
			if (currOp.equals("FINAL_TEST")){
				acceptBSData.setBuyoff("TQC_FINAL_TEST");
			} else if (currOp.equals("BOX_UP") || currOp.equals("BOX_UP_1") || currOp.equals("BOX_UP_2") || currOp.equals("BOX_UP_CUSTOM")){
				acceptBSData.setBuyoff("TQC_BOX_UP");
			} else {
				acceptBSData.setBuyoff("TQC");
			}
			acceptBSData.setBuyOffRef(buyoffRef);
			acceptBSData.setSfcRef(sfcRef);
			acceptBSData.setSfcSteps(acceptbuyoffList);
			List<AcceptBuyoffSfcData> acceptbuyoffSfcList = new ArrayList<AcceptBuyoffSfcData>();
			acceptbuyoffSfcList.add(acceptBSData);
			AcceptBuyoffsRequest abRequest = new AcceptBuyoffsRequest();
			abRequest.setSfcs(acceptbuyoffSfcList);
			try {
				buyoffService.acceptBuyoffs(abRequest);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// update records with new user ref
			Connection connect = DataSourceConnection.getSQLConnection();
			String updateStmnt = "update BUYOFF_LOG set USER_BO = '"
					+ newUserRef
					+ "' where state = 'C' and BUYOFF_ACTION = 'A' "
					+ "and OPERATION_BO = '" + currOpRefHash
					+ "' and SFC_BO = '" + sfcRef + "' and BUYOFF_BO = '"
					+ buyoffRef + "'";
			try {
				PreparedStatement pstmt = connect.prepareStatement(updateStmnt);
				pstmt.executeUpdate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			action = "Accept";
			closePlugin();
		}
		if (buyoffAction.equals("REJECT")) {
			initServices();
			action = "Reject";
			ObjectReference objSfcRef = new ObjectReference();
			objSfcRef.setRef(sfcRef);
			SfcBasicData sfcData = sfcStateService.findSfcDataByRef(objSfcRef);
			String itemRef = sfcData.getItemRef().toString();
			String shopOrderRef = sfcData.getShopOrderRef().toString();
			String nextBuyoffID = null;

			Data queryData3 = null;
			DynamicQuery selNextBuyoffID = DynamicQueryFactory.newInstance();
			selNextBuyoffID
					.append("SELECT MAX(BUYOFF_LOG_ID)+1 AS NEXT_BUYOFF_ID FROM BUYOFF_LOG");
			SystemBase sysBase = SystemBase.createDefaultSystemBase();
			queryData3 = sysBase.executeQuery(selNextBuyoffID);
			if (queryData3.size() > 0) {
				nextBuyoffID = queryData3.getString("NEXT_BUYOFF_ID", "");
			}
			String logHandle = "BuyOffLogBO:0536," + nextBuyoffID + ","
					+ buyoffRef + "," + sfcRef;
			DateTimeInterface now = dateTimeService.createDateTime();
			Timestamp currentTime = new Timestamp(getJavaDate(now).getTime());
			if (newcomments == null) {
				MessageHandler.handle(
						"Comments are mandatory for Reject/Partial action",
						null, MessageType.ERROR, this);
				return;
			}

			Connection connect = DataSourceConnection.getSQLConnection();
			String insertStmnt = "INSERT INTO BUYOFF_LOG(HANDLE,BUYOFF_BO,SFC_BO,BUYOFF_LOG_ID,BUYOFF_ACTION,COMMENTS,STATE,OPERATION_BO,"
					+ "USER_BO,ITEM_BO,ROUTER_BO,SHOP_ORDER_BO,CUSTOMER_ORDER_BO,PROCESS_LOT_BO,RESOURCE_BO,DATE_TIME) "
					+ "VALUES ('"
					+ logHandle
					+ "','"
					+ buyoffRef
					+ "','"
					+ sfcRef
					+ "','"
					+ nextBuyoffID
					+ "','R','"
					+ newcomments
					+ "','O',"
					+ "'"
					+ currOpRefHash
					+ "','"
					+ newUserRef
					+ "','"
					+ itemRef
					+ "','"
					+ routingRef
					+ "','"
					+ shopOrderRef
					+ "',NULL,NULL,'" + currResRef + "','" + currentTime + "')";
			try {
				PreparedStatement pstmt = connect.prepareStatement(insertStmnt);
				pstmt.executeUpdate();
				closePlugin();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void closePlugin() {
		closeCurrentPlugin();
		FacesUtility.setSessionMapValue("logTqcBuyoffBean", null);
		FacesUtility.addScriptCommand("window.close();");
	}

	public void processWindowClosed() {
		FacesUtility.removeSessionMapValue("logTqcBuyoffBean");
	}

	public boolean abort() throws LoginException {
		return false;
	}

	public boolean commit() throws LoginException {
		return false;
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
	}

	public boolean login() throws LoginException {
		return false;
	}

	public boolean logout() throws LoginException {
		return false;
	}

	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,
				SFC_STATE_SERVICE);
		userConfigurationService = Services.getService(COM_SAP_ME_USER,
				USER_CONFIGURATION_SERVICE);
		buyoffService = Services.getService(COM_SAP_ME_PRODUCTION,
				BUYOFF_SERVICE);
		systemRuleService = Services.getService(COM_SAP_ME_APPCONFIG,SYSTEM_RULE_SERVICE);
		itemGroupConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,ITEM_GROUP_CONFIGURATION_SERVICE);
	}

	public Date getJavaDate(DateTimeInterface date) {

		java.sql.Date sqltDate = null;
		String val = dateTimeService.formatDateTime(DateFormatStyle.ISO,
				TimeFormatStyle.ISO_MEDIUM, date);
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date parsedUtilDate;
		try {
			parsedUtilDate = formater.parse(val);
			sqltDate = new java.sql.Date(parsedUtilDate.getTime());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sqltDate;

	}
}
