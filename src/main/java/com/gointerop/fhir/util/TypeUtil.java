package com.gointerop.fhir.util;

import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.stereotype.Component;

@Component
public class TypeUtil {
	private static final String URI = "uri";
	private static final String TOKEN = "token";
	
	public Type create(FHIRPathDefinition fhirPathDefinition) {
		Type retVal = null;
		
		if(fhirPathDefinition.getType().equalsIgnoreCase(URI)) retVal = new UriType();
		if(fhirPathDefinition.getType().equalsIgnoreCase(TOKEN)) retVal = new CodeType();
		
		return retVal;
	}
	
	public String typeToString(Type type) {
		String retVal = null;
		
		if(type instanceof UriType) retVal = ((UriType) type).getValue();
		if(type instanceof CodeType) retVal = ((CodeType) type).getCode();
		
		return retVal;
	}
}
