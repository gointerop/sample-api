package com.gointerop.fhir.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gointerop.fhir.util.FHIRPathDefinition;
import com.gointerop.fhir.util.FHIRPathParam;
import com.gointerop.fhir.util.FHIRPathParamUtil;
import com.gointerop.fhir.util.FileUtil;
import com.gointerop.fhir.util.SPUtil;
import com.gointerop.fhir.util.TypeUtil;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Repository
public class FileRepository<T> implements IRepository<T>, ISemanticRepository<T> {

	@Autowired
	FhirContext fhirContext;

	@Autowired
	IFhirPath iFhirPath;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	SPUtil<T> spUtil;

	@Autowired
	FHIRPathParamUtil fhirPathParamUtil;

	@Autowired
	TypeUtil typeUtil;

	List<Path> files;

	@PostConstruct
	private void onInjected() throws IOException {
		this.files = fileUtil.loadFiles("./definitions/");
	}

	@Override
	public T read(Class<T> classDefinition, HashMap<String, FHIRPathParam> params) throws IOException {
		T retVal = null;
		List<T> list = filterFiles(classDefinition, params);

		if (list.size() > 0)
			retVal = list.get(0);

		return retVal;
	}

	@Override
	public List<T> search(Class<T> classDefinition, HashMap<String, FHIRPathParam> params)
			throws ConfigurationException, DataFormatException, IOException {
		return filterFiles(classDefinition, params);
	}

	@Override
	public T post(Class<T> classDefinition, T resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T put(Class<T> classDefinition, T resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T delete(Class<T> classDefinition, T resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parameters lookup(Class<T> classDefinition, HashMap<String, FHIRPathParam> params)
			throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException {
		Parameters retVal = new Parameters();
		List<T> codeSystems = filterFiles(classDefinition, params);
		CodeSystem codeSystem = null;
		String systemParam = null;
		String codeParam = null;

		FHIRPathParam fhirPathParam = (FHIRPathParam) params.get(CodeSystem.SP_SYSTEM);
		systemParam = fhirPathParamUtil.paramToString(fhirPathParam);
		if (systemParam == null)
			throw new InvalidRequestException(
					"$lookup operation at the type level (no ID specified) requires a system and a code as a part of the request.");

		fhirPathParam = params.get(CodeSystem.SP_CODE);
		codeParam = fhirPathParamUtil.paramToString(fhirPathParam);
		if (codeParam == null)
			throw new InvalidRequestException(
					"$lookup operation at the type level (no ID specified) requires a system and a code as a part of the request.");

		if (codeSystems.size() > 0)
			codeSystem = (CodeSystem) codeSystems.get(0);

		if (codeSystem == null)
			return retVal;

		for (ConceptDefinitionComponent conceptDefinitionComponent : codeSystem.getConcept()) {
			String code = conceptDefinitionComponent.getCode();
			String display = conceptDefinitionComponent.getDisplay();

			if (codeParam != null && codeParam.equals(code)) {
				ParametersParameterComponent parameterName = new ParametersParameterComponent();
				parameterName.setName("name");
				parameterName.setValue(new StringType(codeSystem.getName()));
				retVal.getParameter().add(parameterName);

				ParametersParameterComponent parameterDisplay = new ParametersParameterComponent();
				parameterDisplay.setName("display");
				parameterDisplay.setValue(new StringType(display));
				retVal.getParameter().add(parameterDisplay);

				ParametersParameterComponent parameterAbstract = new ParametersParameterComponent();
				parameterAbstract.setName("abstract");
				parameterAbstract.setValue(new BooleanType(false));
				retVal.getParameter().add(parameterAbstract);
			}
		}

		return retVal;
	}

	@Override
	public Parameters translate(Class<T> classDefinition, HashMap<String, FHIRPathParam> params)
			throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException {
		boolean found = false;
		Parameters retVal = null;
		List<T> conceptMaps = filterFiles(classDefinition, params);
		ConceptMap conceptMap = null;
		String systemParam = null;
		String codeParam = null;

		FHIRPathParam fhirPathParam = (FHIRPathParam) params.get(ConceptMap.SP_SOURCE_SYSTEM);
		systemParam = fhirPathParamUtil.paramToString(fhirPathParam);
		if (systemParam == null)
			throw new InvalidRequestException(
					"$translate operation at the type level (no ID specified) requires a system and a code as a part of the request.");

		fhirPathParam = (FHIRPathParam) params.get(ConceptMap.SP_SOURCE_CODE);
		codeParam = fhirPathParamUtil.paramToString(fhirPathParam);
		if (codeParam == null)
			throw new InvalidRequestException(
					"$translate operation at the type level (no ID specified) requires a system and a code as a part of the request.");

		if (conceptMaps.size() > 0) {
			for (T tItem : conceptMaps) {
				ConceptMap conceptMapItem = (ConceptMap) tItem;

				for (ConceptMapGroupComponent conceptMapGroupComponent : conceptMapItem.getGroup()) {
					if (conceptMapGroupComponent.getSource().equals(systemParam)) {
						conceptMap = conceptMapItem;
					}
				}
			}
		}

		if (conceptMap == null) {
			return createParameters(found);
		}

		for (ConceptMapGroupComponent conceptMapGroupComponent : conceptMap.getGroup()) {
			String sourceSystem = conceptMapGroupComponent.getSource();
			String targetSystem = conceptMapGroupComponent.getTarget();

			if (systemParam != null && systemParam.equals(sourceSystem)) {
				for (SourceElementComponent sourceElementComponent : conceptMapGroupComponent.getElement()) {
					String sourceCode = sourceElementComponent.getCode();

					if (codeParam != null && codeParam.equals(sourceCode)) {
						for (TargetElementComponent targetElementComponent : sourceElementComponent.getTarget()) {
							if (!found)
								retVal = createParameters(!found);

							found = true;

							String targetCode = targetElementComponent.getCode();
							String targetDisplay = targetElementComponent.getDisplay();

							addTranslatedConcepts(found, retVal, conceptMap, targetSystem, targetCode, targetDisplay);
						}
					}
				}
			}
		}

		ParametersParameterComponent parameterMatch = new ParametersParameterComponent();
		parameterMatch.setName("match");

		return retVal;
	}

	public ValueSet expand(Class<T> classDefinition, HashMap<String, FHIRPathParam> params)
			throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException {
		boolean found = false;
		ValueSet retVal = null;
		List<T> valueSets = filterFiles(classDefinition, params);

		if (valueSets.size() > 0) {
			retVal = (ValueSet) valueSets.get(0);
		}

		FHIRPathParam fhirpathParam = (FHIRPathParam) params.get(ValueSet.SP_URL);
		String fhirpathValue = fhirPathParamUtil.paramToString(fhirpathParam);

		if (retVal == null) {
			throw new ResourceNotFoundException("Unknown ValueSet: " + fhirpathValue);
		}

		return retVal;
	}

	private void addTranslatedConcepts(boolean found, Parameters retVal, ConceptMap conceptMap, String targetSystem,
			String targetCode, String targetDisplay) {
		ParametersParameterComponent parameterMatch = new ParametersParameterComponent();
		parameterMatch.setName("match");

		ParametersParameterComponent equivalenceParametersParameterComponent = new ParametersParameterComponent();
		equivalenceParametersParameterComponent.setName("equivalence");
		equivalenceParametersParameterComponent.setValue(new CodeType("equal"));
		parameterMatch.addPart(equivalenceParametersParameterComponent);

		ParametersParameterComponent conceptParametersParameterComponent = new ParametersParameterComponent();
		conceptParametersParameterComponent.setName("concept");
		Coding codingTarget = new Coding();
		codingTarget.setSystem(targetSystem);
		codingTarget.setCode(targetCode);
		codingTarget.setDisplay(targetDisplay);
		conceptParametersParameterComponent.setValue(codingTarget);
		parameterMatch.addPart(conceptParametersParameterComponent);

		ParametersParameterComponent sourceParametersParameterComponent = new ParametersParameterComponent();
		sourceParametersParameterComponent.setName("source");
		sourceParametersParameterComponent.setValue(new UriType(conceptMap.getUrl()));

		retVal.addParameter(parameterMatch);
	}

	private Parameters createParameters(boolean found) {
		Parameters retVal = new Parameters();

		ParametersParameterComponent parameterResult = new ParametersParameterComponent();
		parameterResult.setName("result");
		parameterResult.setValue(new BooleanType(found));
		retVal.getParameter().add(parameterResult);

		ParametersParameterComponent parameterMessage = new ParametersParameterComponent();
		parameterMessage.setName("message");

		if (found) {
			parameterMessage.setValue(new StringType("Matches found!"));
		} else {
			parameterMessage.setValue(new StringType("No Matches found!"));
		}

		retVal.getParameter().add(parameterMessage);

		return retVal;
	}

	private List<T> filterFiles(Class<T> classDefinition, HashMap<String, FHIRPathParam> params)
			throws IOException, FileNotFoundException {
		List<T> retVal = new ArrayList<T>();
		String className = classDefinition.getSimpleName().toUpperCase();

		files.stream().forEach((Path file) -> {
			try {
				boolean filter = false;
				String filename = file.toAbsolutePath().toString().toUpperCase();

				if (filename.contains(className)) {
					String fileString = this.fileUtil.readFile(file.toFile());
					T t = (T) fhirContext.newJsonParser().parseResource(fileString);

					for (String key : params.keySet()) {
						FHIRPathParam fhirPathParam = params.get(key);
						String value = fhirPathParamUtil.paramToString(fhirPathParam);
						if (value == null)
							continue;

						List<Type> types = (List<Type>) iFhirPath.evaluate((Resource) t,
								fhirPathParam.getDefinition().getPath(),
								typeUtil.create(fhirPathParam.getDefinition()).getClass());

						if (types == null) {
							filter = true;
						} else {
							filter = true;

							for (Type type : types) {
								if (value.equals(typeUtil.typeToString(type))) {
									filter = false;
									break;
								}
							}

							if (filter)
								break;
						}
					}

					if (!filter)
						retVal.add(t);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return retVal;
	}
}
