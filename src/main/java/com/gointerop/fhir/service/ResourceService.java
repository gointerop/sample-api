package com.gointerop.fhir.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gointerop.fhir.repository.FileRepository;
import com.gointerop.fhir.util.FHIRPathParam;

@Service
public class ResourceService<T> {
	@Autowired
	FileRepository<T> fileRepository;
	
	public Parameters lookup(Class<T> classDefinition, HashMap<String, FHIRPathParam> params) throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException {
		return fileRepository.lookup(classDefinition, params);
	}
	
	public Parameters translate(Class<T> classDefinition, HashMap<String, FHIRPathParam> params) throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException {
		return fileRepository.translate(classDefinition, params);
	}
	
	public T read(Class<T> classDefinition, HashMap<String, FHIRPathParam> params) throws FileNotFoundException, IOException {
		return fileRepository.read(classDefinition, params);
	}

	public List<T> search(Class<T> classDefinition, HashMap<String, FHIRPathParam> params) throws FileNotFoundException, IOException {
		return fileRepository.search(classDefinition, params);
	}

	public ValueSet expand(Class<T> classDefinition, HashMap<String, FHIRPathParam> params) throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException {
		return fileRepository.expand(classDefinition, params);
	}
}
