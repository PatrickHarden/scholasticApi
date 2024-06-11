package com.scholastic.litplatformbaapi.controller;

import com.scholastic.litplatformbaapi.model.AssessmentAnswerVO;
import com.scholastic.litplatformbaapi.model.AssessmentAssignVO;
import com.scholastic.litplatformbaapi.model.AssessmentModifyVO;
import com.scholastic.litplatformbaapi.model.AssessmentResponse;
import com.scholastic.litplatformbaapi.model.AssessmentsQueryParams;
import com.scholastic.litplatformbaapi.model.StudentListResponse;
import com.scholastic.litplatformbaapi.model.StudentResponse;
import com.scholastic.litplatformbaapi.exception.AssessmentException;
import com.scholastic.litplatformbaapi.response.Assessment;
import com.scholastic.litplatformbaapi.response.SrmStatus;
import com.scholastic.litplatformbaapi.service.AssessmentServiceFactory;
import com.scholastic.litplatformbaapi.service.support.SrmServiceImpl;

import io.github.benas.jpopulator.impl.PopulatorBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AssessmentControllerTest {

    @Mock
    private SrmServiceImpl srmServiceImpl;

    @Mock
    private AssessmentServiceFactory assessmentServiceFactory;

    @InjectMocks
    private AssessmentController assessmentController;

    private final SrmStatus lexileAssessmentResponse = new PopulatorBuilder().build().populateBean(SrmStatus.class);

    private final List<StudentResponse> students = new ArrayList<>();

    private final StudentResponse studentResponse = new PopulatorBuilder().build().populateBean(StudentResponse.class);

    private final AssessmentResponse assessmentResponse = new PopulatorBuilder().build().populateBean(AssessmentResponse.class);

    @Test
    @DisplayName("Bad Request")
    void getAssessment_badRequest() {
        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentController.getAssessment("RANDOM", 10),
                "Wrong assessment type");

        assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
        assertEquals("Wrong assessment type", ae.getExceptionMessage());
    }

    @Test
    @DisplayName("Get student lexile status assessment")
    void getAssessment() {
        when(assessmentServiceFactory.getService("student-lexile-status")).thenReturn(srmServiceImpl);
        when(srmServiceImpl.getAssessment(10)).thenReturn(Assessment.builder().srmStatus(lexileAssessmentResponse).build());

        ResponseEntity<Assessment> responseEntity = assessmentController.getAssessment("student-lexile-status", 10);
        Assessment response = responseEntity.getBody();
        assertNotNull(response);

        var studentLexileStatusResponse = response.getSrmStatus();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(studentLexileStatusResponse);
        assertEquals(lexileAssessmentResponse.getAssessmentTest(), studentLexileStatusResponse.getAssessmentTest());
        assertEquals(lexileAssessmentResponse.getId(), studentLexileStatusResponse.getId());
        assertEquals(lexileAssessmentResponse.getStudentId(), studentLexileStatusResponse.getStudentId());
        assertEquals(lexileAssessmentResponse.getTeacherId(), studentLexileStatusResponse.getTeacherId());
        assertEquals(lexileAssessmentResponse.getBenchmark(), studentLexileStatusResponse.getBenchmark());
        assertEquals(lexileAssessmentResponse.getAssignedDate(), studentLexileStatusResponse.getAssignedDate());
        assertEquals(lexileAssessmentResponse.getCompletedDate(), studentLexileStatusResponse.getCompletedDate());
        assertEquals(lexileAssessmentResponse.getLexile(), studentLexileStatusResponse.getLexile());
        assertEquals(lexileAssessmentResponse.getStatus(), studentLexileStatusResponse.getStatus());
        assertEquals(lexileAssessmentResponse.getEndDate(), studentLexileStatusResponse.getEndDate());
        assertEquals(lexileAssessmentResponse.getAdminAssignmentId(), studentLexileStatusResponse.getAdminAssignmentId());
        assertEquals(lexileAssessmentResponse.getTeacherJudgement(), studentLexileStatusResponse.getTeacherJudgement());
        assertEquals(lexileAssessmentResponse.getCancelledByUserId(), studentLexileStatusResponse.getCancelledByUserId());
        assertEquals(lexileAssessmentResponse.getModifiedDate(), studentLexileStatusResponse.getModifiedDate());
        assertEquals(lexileAssessmentResponse.getCurrentQuestion(), studentLexileStatusResponse.getCurrentQuestion());
        assertEquals(lexileAssessmentResponse.getTimeSpent(), studentLexileStatusResponse.getTimeSpent());
    }

    @Test
    @DisplayName("Assign batch reading measure")
    void assignReadingMeasureAssessments() {
        List<AssessmentAssignVO> assessmentAssignVOList = new ArrayList<>();
        when(assessmentServiceFactory.getService("srm")).thenReturn(srmServiceImpl);

        ResponseEntity<Void> responseEntity = assessmentController.assignReadingMeasureAssessments(12345,
                assessmentAssignVOList);
        assertEquals(200, responseEntity.getStatusCode().value());
    }

    @Test
    @DisplayName("Edit batch reading measure")
    void editReadingMeasureAssessments() {
        List<AssessmentModifyVO> assessmentModifyVOList = new ArrayList<>();
        when(assessmentServiceFactory.getService("srm")).thenReturn(srmServiceImpl);

        ResponseEntity<Void> responseEntity = assessmentController.editReadingMeasureAssessments(12345, assessmentModifyVOList);
        assertEquals(200, responseEntity.getStatusCode().value());
    }

    @Test
    @DisplayName("Edit SRM for current question")
    void editSRMAnswer(){
        AssessmentAnswerVO assessmentAnswerVO  = new AssessmentAnswerVO();
        when(assessmentServiceFactory.getService("srm")).thenReturn(srmServiceImpl);

        ResponseEntity<Void> responseEntity = assessmentController
                .editSRMAnswer("srm", 100, assessmentAnswerVO);
        assertEquals(200, responseEntity.getStatusCode().value());
	}

    @Test
    @DisplayName("Edit SRM bad request")
    void editSRMAnswer_dadRequest() {
        AssessmentAnswerVO assessmentAnswerVO  = new AssessmentAnswerVO();
        AssessmentException ae = assertThrows(AssessmentException.class,
                () -> assessmentController.editSRMAnswer("RANDOM", 10,assessmentAnswerVO),
                "Wrong assessment type");

        assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
        assertEquals("Wrong assessment type", ae.getExceptionMessage());
    }

    @Test
    @DisplayName("Get assessments")
    void getAssessments() {
        List<Integer> classIds = Arrays.asList(45567, 64749, 95859);
        List<String> benchmark = Arrays.asList("END", "MIDDLE");
        List<String> status = Arrays.asList("ASSIGNED", "IN_PROGRESS");
        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DATE, 7);
        Date endDate = cal.getTime();
        students.add(studentResponse);
        List<AssessmentResponse> aResponse = new ArrayList<>();
        aResponse.add(assessmentResponse);
        studentResponse.setAssessments(aResponse);
        AssessmentsQueryParams taQueryParams = new AssessmentsQueryParams();

        when(assessmentServiceFactory.getService(AssessmentServiceFactory.STUDENT_LEXILE_STATUS)).thenReturn(srmServiceImpl);
		when(srmServiceImpl.populateAssessmentsQueryParams(2022, classIds, startDate, endDate, benchmark, status))
				.thenReturn(taQueryParams);
		when(srmServiceImpl.getAssessments(22790, 9374, taQueryParams))
                .thenReturn(StudentListResponse.builder().results(students).build());

        ResponseEntity<StudentListResponse> responseEntity = assessmentController.getAssessments(22790, 9374, 2022, classIds, startDate, endDate, benchmark, status);
        StudentListResponse studentsAndAssessmentsResponse = responseEntity.getBody();
        assertNotNull(studentsAndAssessmentsResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var studentResponse = studentsAndAssessmentsResponse.getResults();
        assertNotNull(studentResponse);
        var assessmentResponse = studentResponse.get(0).getAssessments();
        assertNotNull(assessmentResponse);
        assertEquals(this.studentResponse.getStudentId(), studentResponse.get(0).getStudentId());
        assertEquals(this.studentResponse.getFirstName(), studentResponse.get(0).getFirstName());
        assertEquals(this.studentResponse.getLastName(), studentResponse.get(0).getLastName());
        assertEquals(this.studentResponse.getDaysSinceLastAssessment(), studentResponse.get(0).getDaysSinceLastAssessment());
        assertEquals(this.studentResponse.getCompletedSrm(), studentResponse.get(0).getCompletedSrm());
        assertEquals(this.studentResponse.getAssessments(), studentResponse.get(0).getAssessments());
        assertEquals(this.assessmentResponse.getAssessmentId(), assessmentResponse.get(0).getAssessmentId());
        assertEquals(this.assessmentResponse.getBenchmark(), assessmentResponse.get(0).getBenchmark());
        assertEquals(this.assessmentResponse.getStartDate(), assessmentResponse.get(0).getStartDate());
        assertEquals(this.assessmentResponse.getEndDate(), assessmentResponse.get(0).getEndDate());
        assertEquals(this.assessmentResponse.getLexileValue(), assessmentResponse.get(0).getLexileValue());
        assertEquals(this.assessmentResponse.getStatus(), assessmentResponse.get(0).getStatus());
        assertEquals(this.assessmentResponse.getCurrentQuestion(), assessmentResponse.get(0).getCurrentQuestion());
        assertEquals(this.assessmentResponse.getProficiency(), assessmentResponse.get(0).getProficiency());
        assertEquals(this.assessmentResponse.getTimeSpent(), assessmentResponse.get(0).getTimeSpent());
        assertEquals(this.assessmentResponse.getAssignedByFirstName(), assessmentResponse.get(0).getAssignedByFirstName());
        assertEquals(this.assessmentResponse.getAssignedByLastName(), assessmentResponse.get(0).getAssignedByLastName());
        assertEquals(this.assessmentResponse.getAssignedById(), assessmentResponse.get(0).getAssignedById());
        assertEquals(this.assessmentResponse.getTeacherJudgment(), assessmentResponse.get(0).getTeacherJudgment());
    }

    @Test
    @DisplayName("Get assessments with only required params")
    void getAssessments_requiredParams() {

        List<AssessmentResponse> assessments = new ArrayList<>();
        assessments.add(assessmentResponse);
        studentResponse.setAssessments(assessments);
		students.add(studentResponse);
		AssessmentsQueryParams taQueryParams = new AssessmentsQueryParams();

        when(assessmentServiceFactory.getService("srm")).thenReturn(srmServiceImpl);
		when(srmServiceImpl.populateAssessmentsQueryParams(null, null, null, null, null, null))
				.thenReturn(taQueryParams);
		when(srmServiceImpl.getAssessments(22790, 9374, taQueryParams))
				.thenReturn(StudentListResponse.builder().results(students).build());

		ResponseEntity<StudentListResponse> responseEntity = assessmentController
				.getAssessments(22790, 9374, null, null, null, null, null, null);
        StudentListResponse studentsAndAssessmentsResponse = responseEntity.getBody();
        assertNotNull(studentsAndAssessmentsResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var studentResponse = studentsAndAssessmentsResponse.getResults();
        assertNotNull(studentResponse);
        var assessmentResponse = studentResponse.get(0).getAssessments();
        assertNotNull(assessmentResponse);
        assertEquals(this.studentResponse.getStudentId(), studentResponse.get(0).getStudentId());
        assertEquals(this.studentResponse.getFirstName(), studentResponse.get(0).getFirstName());
        assertEquals(this.studentResponse.getLastName(), studentResponse.get(0).getLastName());
        assertEquals(this.studentResponse.getDaysSinceLastAssessment(), studentResponse.get(0).getDaysSinceLastAssessment());
        assertEquals(this.studentResponse.getCompletedSrm(), studentResponse.get(0).getCompletedSrm());
        assertEquals(this.studentResponse.getAssessments(), studentResponse.get(0).getAssessments());
        assertEquals(this.assessmentResponse.getAssessmentId(), assessmentResponse.get(0).getAssessmentId());
        assertEquals(this.assessmentResponse.getBenchmark(), assessmentResponse.get(0).getBenchmark());
        assertEquals(this.assessmentResponse.getStartDate(), assessmentResponse.get(0).getStartDate());
        assertEquals(this.assessmentResponse.getEndDate(), assessmentResponse.get(0).getEndDate());
        assertEquals(this.assessmentResponse.getLexileValue(), assessmentResponse.get(0).getLexileValue());
        assertEquals(this.assessmentResponse.getStatus(), assessmentResponse.get(0).getStatus());
        assertEquals(this.assessmentResponse.getCurrentQuestion(), assessmentResponse.get(0).getCurrentQuestion());
        assertEquals(this.assessmentResponse.getProficiency(), assessmentResponse.get(0).getProficiency());
        assertEquals(this.assessmentResponse.getTimeSpent(), assessmentResponse.get(0).getTimeSpent());
        assertEquals(this.assessmentResponse.getAssignedByFirstName(), assessmentResponse.get(0).getAssignedByFirstName());
        assertEquals(this.assessmentResponse.getAssignedByLastName(), assessmentResponse.get(0).getAssignedByLastName());
        assertEquals(this.assessmentResponse.getAssignedById(), assessmentResponse.get(0).getAssignedById());
        assertEquals(this.assessmentResponse.getTeacherJudgment(), assessmentResponse.get(0).getTeacherJudgment());
    }

    @Test
    @DisplayName("cancel outdated assessments")
    void cancelAssessments() {
        Set<Long> assessmentTestIds = new HashSet<>();
        assessmentTestIds.add(2l);
        assessmentTestIds.add(5l);
        when(assessmentServiceFactory.getService("srm")).thenReturn(srmServiceImpl);

        ResponseEntity<Void> responseEntity = assessmentController.cancelAssessments(assessmentTestIds);
        assertEquals(200, responseEntity.getStatusCode().value());
    }
}