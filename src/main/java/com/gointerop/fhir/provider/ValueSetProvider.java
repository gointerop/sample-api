package com.gointerop.fhir.provider;

import java.util.HashMap;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent;
import org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent;
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
public class ValueSetProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Autowired
	private ResourceService<CodeSystem> codeSystemService;
	
	@Autowired
	private ResourceService<ValueSet> valueSetService;
	
	@Autowired
	private SPUtil<CodeSystem> spUtilCodeSystem;
	
	@Autowired
	private SPUtil<ValueSet> spUtilValueSet;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ValueSet.class;
	}

	@Operation(name = "$expand", idempotent = true)
	public ValueSet expand(@OperationParam(name = ValueSet.SP_URL) UriParam url) {
		ValueSet retVal = null;
		HashMap<String, FHIRPathParam> params = new HashMap<String, FHIRPathParam>();

		try {
			FHIRPathParam urlParam = new FHIRPathParam(spUtilValueSet.getFHIRPathDefinition(ValueSet.class, ValueSet.SP_URL), url);
			params.put(urlParam.getKey(), urlParam);

			retVal = valueSetService.expand(ValueSet.class, params);			
			String codeSystemUrl = retVal.getCompose().getIncludeFirstRep().getSystem();
			
			params = new HashMap<String, FHIRPathParam>();			
			urlParam = new FHIRPathParam(spUtilCodeSystem.getFHIRPathDefinition(CodeSystem.class, CodeSystem.SP_URL), new UriParam(codeSystemUrl));
			params.put(urlParam.getKey(), urlParam);
			
			CodeSystem codeSystem = codeSystemService.read(CodeSystem.class, params);
			
			ValueSetExpansionComponent expansion = retVal.getExpansion();
						
			for(ConceptDefinitionComponent concept : codeSystem.getConcept()) {
				ValueSetExpansionContainsComponent contains = new ValueSetExpansionContainsComponent();
				
				contains.setSystem(codeSystemUrl);
				contains.setCode(concept.getCode());
				contains.setDisplay(concept.getDisplay());
				
				expansion.addContains(contains);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException(e.getLocalizedMessage());
		}
		
		return retVal;
	}

	@Search
	public IBundleProvider search(@OptionalParam(name = ValueSet.SP_URL) UriParam url,
			@OptionalParam(name = ValueSet.SP_CODE) StringParam code) {
		HashMap<String, FHIRPathParam> params = new HashMap<String, FHIRPathParam>();
		

		try {
			FHIRPathParam urlParam = new FHIRPathParam(spUtilValueSet.getFHIRPathDefinition(ValueSet.class, ValueSet.SP_URL), url);
			params.put(urlParam.getKey(), urlParam);
			
			FHIRPathParam codeParam = new FHIRPathParam(spUtilValueSet.getFHIRPathDefinition(ValueSet.class, ValueSet.SP_CODE), code);
			params.put(codeParam.getKey(), codeParam);
			
			return new PageableBundleProvider(valueSetService.search(ValueSet.class, params));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException(e.getLocalizedMessage());
		}
	}
}