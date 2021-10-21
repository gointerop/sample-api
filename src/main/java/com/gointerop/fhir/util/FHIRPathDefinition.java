package com.gointerop.fhir.util;

public class FHIRPathDefinition {
	private String name;
	private String path;
	private String type;

	public FHIRPathDefinition(String name, String path, String type) {
		super();
		this.name = name;
		this.path = path;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
