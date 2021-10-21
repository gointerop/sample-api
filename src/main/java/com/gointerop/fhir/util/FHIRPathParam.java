package com.gointerop.fhir.util;

import ca.uhn.fhir.rest.param.BaseParam;

public class FHIRPathParam {
	String key;
	FHIRPathDefinition definition;
	BaseParam param;

	public FHIRPathParam(FHIRPathDefinition definition, BaseParam param) {
		super();
		this.key = definition.getName();
		this.definition = definition;
		this.param = param;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FHIRPathDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(FHIRPathDefinition definition) {
		this.definition = definition;
	}

	public BaseParam getParam() {
		return param;
	}

	public void setParam(BaseParam param) {
		this.param = param;
	}

}
