package com.gointerop.fhir.provider;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Flag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.gointerop.fhir.service.ValidatorService;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class FlagProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Flag.class;
	}

	@Operation(name = "$validate", idempotent = true)
	public MethodOutcome validate(@ResourceParam Flag Flag) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Flag);
		retVal.setResource(Flag);

		return retVal;
	}

	@Create
	public MethodOutcome create(@ResourceParam Flag Flag) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Flag);
		retVal.setResource(Flag);
		retVal.setCreated(true);

		return retVal;
	}
}