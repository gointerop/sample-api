package com.gointerop.fhir.provider;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.gointerop.fhir.service.ValidatorService;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class EncounterProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Encounter.class;
	}

	@Operation(name = "$validate", idempotent = true)
	public MethodOutcome validate(@ResourceParam Encounter Encounter) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Encounter);
		retVal.setResource(Encounter);

		return retVal;
	}

	@Create
	public MethodOutcome create(@ResourceParam Encounter Encounter) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Encounter);
		retVal.setResource(Encounter);
		retVal.setCreated(true);

		return retVal;
	}
}