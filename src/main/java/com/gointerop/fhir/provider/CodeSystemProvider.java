package com.gointerop.fhir.provider;

import java.util.HashMap;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.gointerop.fhir.service.ResourceService;
import com.gointerop.fhir.service.ValidatorService;
import com.gointerop.fhir.util.FHIRPathParam;
import com.gointerop.fhir.util.SPUtil;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Controller
public class CodeSystemProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Autowired
	private ResourceService<CodeSystem> codeSystemService;
	
	@Autowired
	private SPUtil<CodeSystem> spUtil;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return CodeSystem.class;
	}

	@Operation(name = "$lookup", idempotent = true)
	public Parameters lookup(@OperationParam(name = CodeSystem.SP_SYSTEM) UriParam system,
			@OperationParam(name = CodeSystem.SP_CODE) StringParam code) {
		HashMap<String, FHIRPathParam> params = new HashMap<String, FHIRPathParam>();

		try {
			FHIRPathParam systemParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(CodeSystem.class, CodeSystem.SP_SYSTEM),system);
			params.put(systemParam.getKey(), systemParam);
			
			FHIRPathParam codeParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(CodeSystem.class, CodeSystem.SP_CODE), code);
			params.put(codeParam.getKey(), codeParam);
			
			return codeSystemService.lookup(CodeSystem.class, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException(e.getLocalizedMessage());
		}
	}

	@Search
	public IBundleProvider search(@OptionalParam(name = CodeSystem.SP_SYSTEM) UriParam system,
			@OptionalParam(name = CodeSystem.SP_CODE) StringParam code) {
		HashMap<String, FHIRPathParam> params = new HashMap<String, FHIRPathParam>();

		try {
			FHIRPathParam systemParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(CodeSystem.class, CodeSystem.SP_SYSTEM),system);
			params.put(systemParam.getKey(), systemParam);
			
			FHIRPathParam codeParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(CodeSystem.class, CodeSystem.SP_CODE), code);
			params.put(codeParam.getKey(), codeParam);
			
			return new PageableBundleProvider(codeSystemService.search(CodeSystem.class, params));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException(e.getLocalizedMessage());
		}
	}
}