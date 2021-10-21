package com.gointerop.fhir.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.gointerop.fhir.util.FHIRPathParam;

public interface IRepository<T> {
	public T read(Class<T> classDefinition, HashMap<String,FHIRPathParam> params) throws IOException;
	
	public List<T> search(Class<T> classDefinition, HashMap<String,FHIRPathParam> params) throws FileNotFoundException, IOException;
	
	public T post(Class<T> classDefinition, T resource);
	
	public T put(Class<T> classDefinition, T resource);
	
	public T delete(Class<T> classDefinition, T resource);
}
