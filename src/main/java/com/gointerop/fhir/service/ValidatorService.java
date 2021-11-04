package com.gointerop.fhir.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.BaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gointerop.fhir.repository.FileRepository;
import com.gointerop.fhir.util.FHIRPathParam;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

@Service
public class ValidatorService {

    private FhirValidator fhirValidator;

    @Autowired
    private FhirContext context;

    @Autowired
    private FileRepository<StructureDefinition> fileRepositoryStructureDefinition;
    
    @Autowired
    private FileRepository<CodeSystem> fileRepositoryCodeSystem;
    
    @Autowired
    private FileRepository<ValueSet> fileRepositoryValueSet;
    
    @PostConstruct
    private void onInjected() {
        ValidationSupportChain supportChain = new ValidationSupportChain();
        PrePopulatedValidationSupport prePopulatedValidationSupport = new PrePopulatedValidationSupport(context);

        supportChain.addValidationSupport(new DefaultProfileValidationSupport(context));
        supportChain.addValidationSupport(new CommonCodeSystemsTerminologyService(context));
        supportChain.addValidationSupport(new InMemoryTerminologyServerValidationSupport(context));

        List<StructureDefinition> structureDefinitions = new ArrayList<StructureDefinition>();
        
		try {
			structureDefinitions = fileRepositoryStructureDefinition.search(StructureDefinition.class, new HashMap<String, FHIRPathParam>());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        for(StructureDefinition structureDefinition : structureDefinitions) {
            prePopulatedValidationSupport.addStructureDefinition(structureDefinition);
        }
        
        List<CodeSystem> codeSystems = new ArrayList<CodeSystem>();
        
        try {
        	codeSystems = fileRepositoryCodeSystem.search(CodeSystem.class, new HashMap<String, FHIRPathParam>());
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        for(CodeSystem codeSystem : codeSystems) {
            prePopulatedValidationSupport.addCodeSystem(codeSystem);
        }
        
        List<ValueSet> valueSets = new ArrayList<ValueSet>();
        
        try {
        	valueSets = fileRepositoryValueSet.search(ValueSet.class, new HashMap<String, FHIRPathParam>());
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        for(ValueSet valueSet : valueSets) {
            prePopulatedValidationSupport.addValueSet(valueSet);
        }

        supportChain.addValidationSupport(prePopulatedValidationSupport);

        CachingValidationSupport cache = new CachingValidationSupport(supportChain);
        FhirInstanceValidator validatorModule = new FhirInstanceValidator(cache);

        fhirValidator = context.newValidator().registerValidatorModule(validatorModule);
    }

    public MethodOutcome validate(BaseResource baseResource) {
        MethodOutcome retVal = new MethodOutcome();
        OperationOutcome outcome = new OperationOutcome();
        
        if(baseResource == null) {
        	throw new UnprocessableEntityException("No resource was found in the request body.");
        }
        
        List<Exception> exceptions = new ArrayList<Exception>();
        ValidationResult validationResult = validateWithResult(baseResource);

        boolean hasErrors = false;
        
        for (SingleValidationMessage next : validationResult.getMessages()) {
            ResultSeverityEnum severity = next.getSeverity();

            if(severity.equals(ResultSeverityEnum.ERROR)) {
            	addIssue(outcome, next, OperationOutcome.IssueSeverity.ERROR);
            } else if(severity.equals(ResultSeverityEnum.WARNING)) {
                addIssue(outcome, next, OperationOutcome.IssueSeverity.WARNING);
            } else if(severity.equals(ResultSeverityEnum.INFORMATION)) {
                addIssue(outcome, next, OperationOutcome.IssueSeverity.INFORMATION);
            }
        }
        
        if(hasErrors) {
        	String stacktrace = "";

        	for(Exception e : exceptions) {
        		stacktrace += e.getMessage()+". ";
        	}
        	
        	throw new UnprocessableEntityException(stacktrace);
        }

        retVal.setOperationOutcome(outcome);

        return retVal;
    }

    private void addIssue(OperationOutcome outcome, SingleValidationMessage next, OperationOutcome.IssueSeverity issueSeverity) {
        outcome.addIssue().setDiagnostics(next.getLocationString() + " " + next.getMessage()).setSeverity(issueSeverity);
    }

    private ValidationResult validateWithResult(BaseResource resource) {
        return this.fhirValidator.validateWithResult(resource);
    }
}
