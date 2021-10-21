package com.gointerop.fhir.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FHIRPathParamUtil {
	@Autowired
	ParamUtil paramUtil;

	public String paramToString(FHIRPathParam param) {
		return paramUtil.paramToString(param.getParam());
	}
}
