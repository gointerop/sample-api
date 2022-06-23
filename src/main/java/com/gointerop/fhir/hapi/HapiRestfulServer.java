package com.gointerop.fhir.hapi;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gointerop.fhir.config.CorsConfig;
import com.gointerop.fhir.provider.BundleProvider;
import com.gointerop.fhir.provider.CodeSystemProvider;
import com.gointerop.fhir.provider.ConceptMapProvider;
import com.gointerop.fhir.provider.ConditionProvider;
import com.gointerop.fhir.provider.CoverageProvider;
import com.gointerop.fhir.provider.EncounterProvider;
import com.gointerop.fhir.provider.FlagProvider;
import com.gointerop.fhir.provider.MedicationStatementProvider;
import com.gointerop.fhir.provider.OrganizationProvider;
import com.gointerop.fhir.provider.PatientProvider;
import com.gointerop.fhir.provider.PractitionerProvider;
import com.gointerop.fhir.provider.ProcedureProvider;
import com.gointerop.fhir.provider.ServiceRequestProvider;
import com.gointerop.fhir.provider.ValueSetProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

@WebServlet("/*")
@Component
public class HapiRestfulServer extends RestfulServer {

    private static final long serialVersionUID = 2467468320139726878L;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private FhirContext fhirContext;

    @Autowired
    private CodeSystemProvider codeSystemProvider;
    
    @Autowired
    private ValueSetProvider valueSetProvider;
    
    @Autowired
    private ConceptMapProvider conceptMapProvider;
    
    @Autowired
    private PatientProvider patientProvider;
    
    @Autowired
    private FlagProvider flagProvider;
    
    @Autowired
    private EncounterProvider encounterProvider;
    
    @Autowired
    private CoverageProvider coverageProvider;
    
    @Autowired
    private ConditionProvider conditionProvider;
    
    @Autowired
    private PractitionerProvider practitionerProvider;
    
    @Autowired
    private OrganizationProvider organizationProvider;

    @Autowired
    private ServiceRequestProvider serviceRequestProvider;

    @Autowired
    private MedicationStatementProvider medicationStatementProvider;

    @Autowired
    private ProcedureProvider procedureProvider;
   
    @Autowired
    private BundleProvider bundleProvider;
    
    @Override
    protected void initialize() {
        //context
        setFhirContext(fhirContext);

        //semantic providers
        registerProvider(codeSystemProvider);
        registerProvider(valueSetProvider);
        registerProvider(conceptMapProvider);
        registerProvider(patientProvider);
        registerProvider(flagProvider);
        registerProvider(encounterProvider);
        registerProvider(coverageProvider);
        registerProvider(conditionProvider);
        registerProvider(practitionerProvider);
        registerProvider(organizationProvider);
        registerProvider(bundleProvider);
        registerProvider(serviceRequestProvider);
        registerProvider(medicationStatementProvider);
        registerProvider(procedureProvider);
        
        //sintatic providers
        

        //cors
        registerInterceptor(new CorsInterceptor(CorsConfig.getCorsConfig()));
        
        //ui
        registerInterceptor(new ResponseHighlighterInterceptor());
    }
}