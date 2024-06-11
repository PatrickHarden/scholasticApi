package com.scholastic.litplatformbaapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AssessmentServiceFactory {
    public static final String STUDENT_LEXILE_STATUS = "srm";
    private final AssessmentService srmAssessmentService;

    @Autowired
    public AssessmentServiceFactory(@Qualifier("srmService") AssessmentService srmAssessmentService) {
        this.srmAssessmentService = srmAssessmentService;
    }

    public AssessmentService getService(String assessmentType) {
        if(STUDENT_LEXILE_STATUS.equalsIgnoreCase(assessmentType)) {
            return this.srmAssessmentService;
        }
        return null;
    }
}
