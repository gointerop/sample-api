package com.gointerop.fhir.provider;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Coverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.gointerop.fhir.service.ValidatorService;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class CoverageProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Coverage.class;
	}

	@Operation(name = "$validate", idempotent = true)
	public MethodOutcome validate(@ResourceParam Coverage Coverage) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Coverage);
		retVal.setResource(Coverage);

		return retVal;
	}

	@Create
	public MethodOutcome create(@ResourceParam Coverage Coverage) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Coverage);
		retVal.setResource(Coverage);
		retVal.setCreated(true);

		return retVal;
	}
}