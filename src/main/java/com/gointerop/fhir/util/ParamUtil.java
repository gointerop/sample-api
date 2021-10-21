package com.gointerop.fhir.util;

import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.param.BaseParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.UriParam;

@Component
public class ParamUtil {
	public String paramToString(BaseParam param) {
		String retVal = null;
		
		if (param == null) return retVal;
		
		if (param instanceof UriParam)
			retVal = ((UriParam) param).getValue();
		if (param instanceof StringParam)
			retVal = ((StringParam) param).getValue();

		return retVal;
	}
}
