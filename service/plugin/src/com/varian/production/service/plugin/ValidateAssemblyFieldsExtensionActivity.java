package com.varian.production.service.plugin;
import java.util.List;
import com.sap.me.common.ObjectReference;
import com.sap.me.production.AssemblyDataField;
import com.sap.me.production.AssemblyDataValidationRequest;
import com.sap.me.production.SfcKeyData;
import com.varian.hook.exception.InvalidGlassSerialNumberException;
import com.visiprise.frame.service.ext.ActivityInterface;
import com.sap.me.extension.Services;
import com.sap.me.production.SfcStateServiceInterface;

public class ValidateAssemblyFieldsExtensionActivity implements
		ActivityInterface<AssemblyDataValidationRequest> {

	private static final String SFC_STATE_SERVICE = "SfcStateService";
	private static final String COM_SAP_ME_PRODUCTION = "com.sap.me.production";
	private static final long serialVersionUID = 1L;
	private SfcStateServiceInterface sfcStateService;
	
	public void execute(com.sap.me.production.AssemblyDataValidationRequest dto)
			throws Exception {
		initServices();
		String assemblyData = null;
		String glassSerial = null;
		ObjectReference objsfcRef = new ObjectReference(dto.getSfcRef().toString());
		SfcKeyData sfcKeyData = sfcStateService.findSfcKeyDataByRef(objsfcRef);
		String sfcNumber = sfcKeyData.getSfc();
		String sfcSerialNumber = sfcNumber.substring(3,sfcNumber.length());
		List<AssemblyDataField> assemblyDataFields = dto.getAssemblyDataFields();
		for (AssemblyDataField assemblyDataField : assemblyDataFields) {
			if ("GLASS_SERIAL_NUMBER".equals(assemblyDataField.getAttribute())) {
				assemblyData = assemblyDataField.getValue();
					if (assemblyData == null || sfcSerialNumber == null){
						throw new InvalidGlassSerialNumberException(20116, sfcNumber);
					}
					if (!assemblyData.equals(sfcSerialNumber)){
					throw new InvalidGlassSerialNumberException(20117, sfcNumber);
					}				
			}
		if ("SFC".equals(assemblyDataField.getAttribute())) {
			assemblyData = assemblyDataField.getValue();
			glassSerial = assemblyData.substring(3,assemblyData.length());
			if (glassSerial == null || sfcSerialNumber == null){
				throw new InvalidGlassSerialNumberException(20116, sfcNumber);	
			}
			if (!glassSerial.equals(sfcSerialNumber)){
				throw new InvalidGlassSerialNumberException(20118, sfcNumber);
				}		
			}
		}
		}
	
	private void initServices(){
		sfcStateService = Services.getService(COM_SAP_ME_PRODUCTION,SFC_STATE_SERVICE);
	}
	}
