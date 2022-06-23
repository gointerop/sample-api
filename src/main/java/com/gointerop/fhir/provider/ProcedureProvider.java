package com.gointerop.fhir.provider;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.gointerop.fhir.service.ValidatorService;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class ProcedureProvider implements IResourceProvider {

	@Autowired
	private ValidatorService validatorService;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Procedure.class;
	}

	@Operation(name = "$validate", idempotent = true)
	public MethodOutcome validate(@ResourceParam Procedure Procedure) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Procedure);
		retVal.setResource(Procedure);

		return retVal;
	}

	@Create
	public MethodOutcome create(@ResourceParam Procedure Procedure) {
		System.out.println("Requisição recebida!\n");

		MethodOutcome retVal = validatorService.validate(Procedure);
		retVal.setResource(Procedure);
		retVal.setCreated(true);

		return retVal;
	}
}