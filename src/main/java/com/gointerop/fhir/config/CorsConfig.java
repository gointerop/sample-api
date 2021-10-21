package com.gointerop.fhir.config;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;

public class CorsConfig extends CorsInterceptor {

    public static CorsConfiguration getCorsConfig() {
        CorsConfiguration retVal = new CorsConfiguration();

        ArrayList<String> allowedHeaders = new ArrayList<>(Constants.CORS_ALLOWED_HEADERS);
        allowedHeaders.add(Constants.HEADER_AUTHORIZATION);
        retVal.setAllowedHeaders(allowedHeaders);
        retVal.setAllowedMethods(new ArrayList<>(Constants.CORS_ALLWED_METHODS));

        retVal.addExposedHeader(Constants.HEADER_CONTENT_LOCATION);
        retVal.addExposedHeader(Constants.HEADER_LOCATION);

        retVal.addAllowedOrigin("*");

        return retVal;
    }
}
