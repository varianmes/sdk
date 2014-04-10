package com.varian.hook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.production.BuyoffStateEnum;
import com.sap.me.production.FindBuyoffsBySfcData;
import com.sap.me.production.FindBuyoffsBySfcRequest;
import com.sap.me.production.FindBuyoffsBySfcResponse;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcKeyData;
import com.sap.me.production.SfcStep;
import com.sap.me.production.StartHookDTO;
import com.varian.integration.connection.DataSourceConnection;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.BuyoffServiceInterface;

/**
 * @author vmurthy
 * 
 */
public class EnableBuyoffatRework implements ActivityInterface<StartHookDTO> {

	private static final String BUYOFF_SERVICE = "BuyoffService";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService;
	private BuyoffServiceInterface buyoffService;

	// private static final String updateStmnt =
	// "UPDATE BUYOFF_LOG SET BUYOFF_ACTION = ?,STATE=?, COMMENTS = ? " +
	// "WHERE SFC_BO=? AND BUYOFF_BO=? AND OPERATION_BO=? AND BUYOFF_ACTION = 'A'";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiprise.frame.service.ext.ActivityInterface#execute(java.lang.Object
	 * )
	 */
	public void execute(StartHookDTO dto) throws Exception {
		initServices();
		String userRef = null;
		int timesprocessed = 0;
		String sfcBO = dto.getSfcBO().getValue().toString();
		String operBO = dto.getOperationBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcBO);
		SfcKeyData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcnumber = sfckeydata.getSfc();
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
	}

	/**
	 * Initialization of services that are represented as fields.
	 */
	private void initServices() {
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,
				SFC_STATE_SERVICE);
		buyoffService = Services.getService(COM_SAP_ME_PRODUCTION,
				BUYOFF_SERVICE);
	}
}