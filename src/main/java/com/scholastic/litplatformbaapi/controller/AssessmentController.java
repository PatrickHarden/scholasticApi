package com.scholastic.litplatformbaapi.controller;

import com.scholastic.litplatformbaapi.exception.AssessmentException;
import com.scholastic.litplatformbaapi.model.AssessmentAnswerVO;
import com.scholastic.litplatformbaapi.model.AssessmentAssignVO;
import com.scholastic.litplatformbaapi.model.AssessmentModifyVO;
import com.scholastic.litplatformbaapi.model.StudentListResponse;
import com.scholastic.litplatformbaapi.response.Assessment;
import com.scholastic.litplatformbaapi.service.AssessmentService;
import com.scholastic.litplatformbaapi.service.AssessmentServiceFactory;
import com.scholastic.litplatformbaapi.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {
    private final AssessmentServiceFactory assessmentServiceFactory;

    @Autowired
    public AssessmentController(AssessmentServiceFactory assessmentServiceFactory) {
        this.assessmentServiceFactory = assessmentServiceFactory;
    }

    @GetMapping("/{assessmentType}/{studentId}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable  String assessmentType,
                                                    @PathVariable Integer studentId) {

        var service = this.assessmentServiceFactory.getService(assessmentType);
        if(service == null) {
			throw new AssessmentException(HttpStatus.BAD_REQUEST, Constants.WRONG_ASSESSMENT_TYPE_ERROR);
        }

        return ResponseEntity.ok(service.getAssessment(studentId));
    }

    @Operation(summary = "Assign batch reading measure", description = "Assign batch reading measure")
	@PostMapping("/{teacherId}/assign")
	public ResponseEntity<Void> assignReadingMeasureAssessments(@PathVariable Integer teacherId,
			@RequestBody @Valid List<AssessmentAssignVO> assessmentAssignVOList) {

		var service = this.assessmentServiceFactory.getService(Constants.ASSESSMENT_TYPE_SRM);

		service.updateReadingMeasureAssignment(assessmentAssignVOList, teacherId);
		return ResponseEntity.ok().build();
	}

    @Operation(summary = "Teacher editing batch reading measure", description = "Editing batch reading measure")
    @PutMapping("/{teacherId}/assign")
    public ResponseEntity<Void> editReadingMeasureAssessments(@PathVariable Integer teacherId, @RequestBody @Valid List<AssessmentModifyVO> assessmentModifyVOList){

        var service = this.assessmentServiceFactory.getService(Constants.ASSESSMENT_TYPE_SRM);
        service.modifyReadingMeasureBatchAssignment(assessmentModifyVOList, teacherId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Student updating the details of their current question", description = "Editing SRM assessment")
    @PutMapping("/{assessmentType}/{studentId}/answer")
    public ResponseEntity<Void> editSRMAnswer(@PathVariable String assessmentType,
                                              @PathVariable Integer studentId,
                                              @RequestBody AssessmentAnswerVO assessmentAnswerVO) {

        var service = getAssessmentService(assessmentType);
        service.updateSRMForCurrentQuestion(assessmentAnswerVO, studentId);
        return ResponseEntity.ok().build();
    }

    private AssessmentService getAssessmentService(String assessmentType) {
        var service = this.assessmentServiceFactory.getService(assessmentType);
        if(service == null) {
			throw new AssessmentException(HttpStatus.BAD_REQUEST, Constants.WRONG_ASSESSMENT_TYPE_ERROR);
        }
        return service;
	}

    @Operation(summary = "Teacher getting table results", description = "Get tables results")
    @GetMapping("/staff/{staffId}/organization/{orgId}")
	public ResponseEntity<StudentListResponse> getAssessments(@PathVariable Integer staffId,
                                                              @PathVariable Integer orgId, @RequestParam(value = "schoolYear", required = false) Integer schoolYear,
                                                              @RequestParam(value = "classIds", required = false) List<Integer> classIds,
                                                              @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                              @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                              @RequestParam(value = "benchmark", required = false) List<String> benchmark,
                                                              @RequestParam(value = "status", required = false) List<String> status) {

		var service = this.assessmentServiceFactory.getService(Constants.ASSESSMENT_TYPE_SRM);
		var queryParams = service.populateAssessmentsQueryParams(schoolYear, classIds,
				startDate, endDate,
				benchmark, status);

		return ResponseEntity.ok(service.getAssessments(staffId, orgId, queryParams));
	}

    @PutMapping(value = "/cancelAssessments")
    public ResponseEntity<Void> cancelAssessments(@RequestBody Set<Long> assessmentTestIds) {
        var service = this.assessmentServiceFactory.getService(Constants.ASSESSMENT_TYPE_SRM);

        service.cancelAssessments(assessmentTestIds);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
