package com.scholastic.litplatformbaapi.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentResponse {
    private Integer studentId;
    private String firstName;
    private String lastName;
    private Integer daysSinceLastAssessment;
    private Boolean completedSrm;
	private List<AssessmentResponse> assessments;
}
