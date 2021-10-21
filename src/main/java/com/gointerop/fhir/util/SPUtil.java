package com.gointerop.fhir.util;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;

@Component
public class SPUtil<T> {

	public FHIRPathDefinition getFHIRPathDefinition(Class<T> classDefinition, String spName) throws NoSuchFieldException, SecurityException {
		FHIRPathDefinition retVal = null;
		
		for(Field field : classDefinition.getFields()) {
			SearchParamDefinition searchParamDefinition = field.getAnnotation(SearchParamDefinition.class);
			
			if(searchParamDefinition != null ) {
				String name = searchParamDefinition.name();
				String path = searchParamDefinition.path();		
				String type = searchParamDefinition.type();
				
				if(spName.equals(name)) {
					path = classDefinition.getSimpleName()+"."+path.substring(path.indexOf(".")+1, path.length());
					
					retVal = new FHIRPathDefinition(name, path, type);
				}
			}
		}

		return retVal;
	}
}
