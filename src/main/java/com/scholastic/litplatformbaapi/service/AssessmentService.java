package com.scholastic.litplatformbaapi.service;

import com.scholastic.litplatformbaapi.model.AssessmentAnswerVO;
import com.scholastic.litplatformbaapi.model.AssessmentAssignVO;
import com.scholastic.litplatformbaapi.model.AssessmentModifyVO;
import com.scholastic.litplatformbaapi.model.AssessmentsQueryParams;
import com.scholastic.litplatformbaapi.model.StudentListResponse;
import com.scholastic.litplatformbaapi.response.Assessment;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AssessmentService {
    Assessment getAssessment(Integer studentId);

	void updateReadingMeasureAssignment(List<AssessmentAssignVO> assessmentAssignVOList,
			Integer teacher);

	void modifyReadingMeasureBatchAssignment(List<AssessmentModifyVO> assessmentModifyVOList, Integer teacherId);

    void updateSRMForCurrentQuestion(AssessmentAnswerVO assessmentAnswerVO, Integer studentId);

	StudentListResponse getAssessments(Integer staffId, Integer orgId,
			AssessmentsQueryParams queryParams);

	AssessmentsQueryParams populateAssessmentsQueryParams(Integer schoolYear, List<Integer> classIds,
											Date startDate, Date endDate, List<String> benchmark, List<String> status);

    void cancelAssessments(Set<Long> assessmentTestIds);
}
