package com.gointerop.fhir.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.ValueSet;

import com.gointerop.fhir.util.FHIRPathParam;

public interface ISemanticRepository<T> {
	public Parameters lookup(Class<T> classType, HashMap<String, FHIRPathParam> params)
			throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException;
	
	public ValueSet expand(Class<T> classType, HashMap<String, FHIRPathParam> params)
			throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException;

	Parameters translate(Class<T> classDefinition, HashMap<String, FHIRPathParam> params)
			throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException;
}
