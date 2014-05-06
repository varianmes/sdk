package com.varian.hook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sap.me.common.ObjectReference;
import com.sap.me.extension.Services;
import com.sap.me.frame.Data;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.jdbc.DynamicQuery;
import com.sap.me.frame.jdbc.DynamicQueryFactory;
import com.sap.me.productdefinition.BOMComponentRequest;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.BomComponentProductionConfiguration;
import com.sap.me.production.AssembledComponent;
import com.sap.me.production.AssembledComponentsResponse;
import com.sap.me.production.AssemblyDataField;
import com.sap.me.production.CompleteHookDTO;
import com.sap.me.production.GroupAssembledComponentsRequest;
import com.sap.me.production.RetrieveComponentsServiceInterface;
import com.sap.me.production.SfcBasicData;
import com.sap.me.production.SfcIdentifier;
import com.sap.me.production.SfcStateServiceInterface;
import com.varian.hook.exception.InvalidGlassSerialNumberException;
import com.varian.hook.exception.InvalidSerialFormatException;
import com.visiprise.frame.service.ext.ActivityInterface;

/**
 * @author vmurthy
 * 
 */
public class CheckTRatComplete implements ActivityInterface<CompleteHookDTO> {

	private static final String COM_SAP_ME_PRODUCTDEFINITION = "com.sap.me.productdefinition";
	private static final String BOMCONFIGURATION_SERVICE = "BOMConfigurationService";
	private static final String RETRIEVE_COMPONENTS_SERVICE = "RetrieveComponentsService";
	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;

	private SfcStateServiceInterface sfcStateService;
	private RetrieveComponentsServiceInterface retrieveComponentsService;
	private BOMConfigurationServiceInterface bOMConfigurationService;

	public void execute(CompleteHookDTO dto) throws Exception {
		initServices();

		String sfcRef = dto.getSfcBO().getValue().toString();
		ObjectReference sfcreference = new ObjectReference(sfcRef);
		SfcBasicData sfckeydata = sfcStateService.findSfcDataByRef(sfcreference);
		String sfcNumber = sfckeydata.getSfc();
		BigDecimal sfcqty = sfckeydata.getQty();
		String prefix = sfcNumber.substring(0, Math.min(sfcNumber.length(), 3));

		int count = 0;
		Data parametricData = null;
		Data queryData = null;
		SystemBase sysBase = SystemBase.createDefaultSystemBase();
		DynamicQuery getSerialCount = DynamicQueryFactory.newInstance();
		getSerialCount
				.append("select COUNT(*) AS COUNT from sfc_id_history where REASON= 'S' and SFC_BO = '"
						+ sfcRef + "'");
		queryData = sysBase.executeQuery(getSerialCount);
		if (queryData.size() > 0) {
			Iterator<Data> dataIterator = queryData.iterator();
			parametricData = dataIterator.next();
			count = parametricData.getInteger("COUNT");
		}
		if (count == 0) {
			throw new InvalidSerialFormatException(20107, sfcNumber);
		} else {
			if (prefix.equals("TR-") || sfcNumber.length() < 4) {
				throw new InvalidSerialFormatException(20109, sfcNumber);
			} else {
				String onetwo = prefix.substring(0, 2);
				boolean hasNonAlpha = onetwo.matches("^.*[^a-zA-Z0-9 ].*$");

				char first = prefix.charAt(0);
				char second = prefix.charAt(1);
				String threechar = String.valueOf(prefix.charAt(2));

				String firsttype = null;
				String secondtype = null;
				String serialFormat = null;

				if (first >= 48 && first <= 57) {
					firsttype = "digit";
				} else {
					firsttype = "alphabet";
				}
				if (second >= 48 && second <= 57) {
					secondtype = "digit";
				} else {
					secondtype = "alphabet";
				}

				if ((firsttype.equals("alphabet") && secondtype.equals("digit"))
						|| (firsttype.equals("digit") && secondtype
								.equals("alphabet"))) {
					serialFormat = "Valid";
				} else if ((firsttype.equals("alphabet") && secondtype
						.equals("alphabet"))
						|| (firsttype.equals("digit") && secondtype
								.equals("digit"))) {
					serialFormat = "FormatError";
				}
				if (serialFormat.equals("FormatError")) {
					throw new InvalidSerialFormatException(20110, sfcNumber);
				}
				if (hasNonAlpha == true) {
					throw new InvalidSerialFormatException(20111, sfcNumber);
				}
				if (!threechar.equals("-")) {
					throw new InvalidSerialFormatException(20112, sfcNumber);
				}
			}

		}

		// validate the second part that is serial number
		ObjectReference objBomRef = sfcStateService.findBomBySfcRef(sfcreference);
		String bomRef = objBomRef.getRef();
		BOMComponentRequest bomCompReq = new BOMComponentRequest();
		bomCompReq.setBomRef(bomRef);
		bomCompReq.setSfcQty(sfcqty);
		Collection<BomComponentProductionConfiguration> bomCompColl = bOMConfigurationService.findAllBOMComponents(bomCompReq);
		String assyOpRef=null;
		
		for (BomComponentProductionConfiguration bomcomp : bomCompColl) {
			if (bomcomp.getAssemblyDataTypeRef().equals("DataTypeBO:0536,ASSEMBLY,SFC")){
				assyOpRef = bomcomp.getOperationRef();
			}
		}
		String childSfc = null;
		List<SfcIdentifier> sfcIdentifierList = new ArrayList<SfcIdentifier>();
		SfcIdentifier sfcIdentifier = new SfcIdentifier();
		sfcIdentifier.setSfcRef(sfcRef);
		sfcIdentifierList.add(sfcIdentifier);
		GroupAssembledComponentsRequest grpAssCompreq = new GroupAssembledComponentsRequest();
		grpAssCompreq.setEnforceAssyStates(false);
		grpAssCompreq.setIncludeNonBomComponents(true);
		grpAssCompreq.setIncludeRemoved(true);
		grpAssCompreq.setOperationRef(assyOpRef);
		grpAssCompreq.setOperationRequired(true);
		grpAssCompreq.setSfcList(sfcIdentifierList);
		AssembledComponentsResponse response = new AssembledComponentsResponse();
		response = retrieveComponentsService
		.findAssembledComponents(grpAssCompreq);
		List<AssembledComponent> assCompList = new ArrayList<AssembledComponent>();
		assCompList = response.getAssembledComponentsList();
		for (AssembledComponent asscomp : assCompList) {
			List<AssemblyDataField> assDatafieldlist = new ArrayList<AssemblyDataField>();
			assDatafieldlist =  asscomp.getAssemblyDataFields();
			for (AssemblyDataField assDataField : assDatafieldlist) {
				if (assDataField.getAttribute().equals("SFC")){
					childSfc = assDataField.getValue();
				}
			}
			
		}
		// get the serial number from the sfc

		String sfcSerial = sfcNumber.substring(3, sfcNumber.length());
		String csfc = null;
		String childprefix = null;
		String lastthree = null;
		String split[] = null;
		String secondhalf = null;
		String clastthree = null;
		String csplit[] = null;
		String csecondhalf = null;
		String assemblySerial = null;
				csfc = childSfc;
				childprefix = csfc
						.substring(0, Math.min(sfcNumber.length(), 3));
				if (childprefix.equals("UR-") || csfc.length() < 4) {
					try {
						lastthree = sfcNumber.substring(
								(sfcNumber.length() - 3), sfcNumber.length());
						split = sfcNumber.split("-");
						secondhalf = sfcNumber.substring(
								sfcNumber.indexOf("-") + 1, sfcNumber.length());
					} catch (Exception e) {
						throw new InvalidGlassSerialNumberException(20119,
								sfcNumber);
					}
					if (split.length > 1) {
						if (lastthree.startsWith("-")
								|| !lastthree.contains("-")) {
							sfcSerial = sfcNumber.substring(3, sfcNumber
									.length());
						} else {
							if (secondhalf.length() == 1) {
								sfcSerial = secondhalf;
							} else {
								sfcSerial = sfcNumber.substring(3, sfcNumber
										.lastIndexOf("-"));
							}
						}
					} else {
						throw new InvalidGlassSerialNumberException(20122,
								sfcNumber);
					}
					// get child sfc serial number

					try {
						clastthree = csfc.substring((csfc.length() - 3), csfc
								.length());
						csplit = csfc.split("-");
						csecondhalf = csfc.substring(csfc.indexOf("-") + 1,
								csfc.length());
					} catch (Exception e) {
						throw new InvalidGlassSerialNumberException(20119,
								sfcNumber);
					}
					if (csplit.length > 1) {
						if (clastthree.startsWith("-")
								|| !clastthree.contains("-")) {
							assemblySerial = csfc.substring(3, csfc.length());
						} else {
							if (csecondhalf.length() == 1) {
								assemblySerial = csecondhalf;
							} else {
								assemblySerial = csfc.substring(3, csfc
										.lastIndexOf("-"));
							}
						}
					} else {
						throw new InvalidGlassSerialNumberException(20123, csfc);
					}
					// check if actual serial on the sfc and the serial on the
					// child sfc are equal

					if (assemblySerial == null || sfcSerial == null) {
						throw new InvalidGlassSerialNumberException(20116,
								sfcNumber);
					}
					if (!assemblySerial.equals(sfcSerial)) {
						throw new InvalidGlassSerialNumberException(20130,
								sfcNumber);
					}
				}
			}
	/**
	 *	Initialization of services that are represented as fields.
	 */
	private void initServices(){
		// operationConfigurationService =
		// Services.getService(COM_SAP_ME_PRODUCTDEFINITION,OPERATION_CONFIGURATION_SERVICE);
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,
				SFC_STATE_SERVICE);
	
		retrieveComponentsService = Services.getService(COM_SAP_ME_PRODUCTION,RETRIEVE_COMPONENTS_SERVICE);
		bOMConfigurationService = Services.getService(COM_SAP_ME_PRODUCTDEFINITION,BOMCONFIGURATION_SERVICE);
	}

}
