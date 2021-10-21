package com.gointerop.fhir.provider;

import java.util.HashMap;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ConceptMap;
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
public class ConceptMapProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Autowired
	private ResourceService<ConceptMap> conceptMapService;
	
	@Autowired
	private SPUtil<ConceptMap> spUtil;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ConceptMap.class;
	}

	@Operation(name = "$translate", idempotent = true)
	public Parameters translate(@OperationParam(name = "system") UriParam system,
			@OperationParam(name = "code") StringParam code) {
		HashMap<String, FHIRPathParam> params = new HashMap<String, FHIRPathParam>();

		try {
			FHIRPathParam systemParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(ConceptMap.class, ConceptMap.SP_SOURCE_SYSTEM), system);
			params.put(systemParam.getKey(), systemParam);

			FHIRPathParam codeParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(ConceptMap.class, ConceptMap.SP_SOURCE_CODE), code);
			params.put(codeParam.getKey(), codeParam);
			
			return conceptMapService.translate(ConceptMap.class, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException(e.getLocalizedMessage());
		}
	}

	@Search
	public IBundleProvider search(@OptionalParam(name = ConceptMap.SP_URL) UriParam system,
			@OptionalParam(name = ConceptMap.SP_SOURCE_CODE) StringParam code) {
		HashMap<String, FHIRPathParam> params = new HashMap<String, FHIRPathParam>();

		try {
			FHIRPathParam systemParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(ConceptMap.class, ConceptMap.SP_SOURCE_SYSTEM), system);
			params.put(systemParam.getKey(), systemParam);

			FHIRPathParam codeParam = new FHIRPathParam(spUtil.getFHIRPathDefinition(ConceptMap.class, ConceptMap.SP_SOURCE_CODE), code);
			params.put(codeParam.getKey(), codeParam);
			
			return new PageableBundleProvider(conceptMapService.search(ConceptMap.class, params));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException(e.getLocalizedMessage());
		}
	}
}