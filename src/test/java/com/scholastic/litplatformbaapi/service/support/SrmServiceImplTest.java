package com.scholastic.litplatformbaapi.service.support;

import com.scholastic.litplatformbaapi.model.Assessment;
import com.scholastic.litplatformbaapi.model.AssessmentAnswerVO;
import com.scholastic.litplatformbaapi.model.AssessmentAssignVO;
import com.scholastic.litplatformbaapi.model.AssessmentModifyVO;
import com.scholastic.litplatformbaapi.model.AssessmentResponse;
import com.scholastic.litplatformbaapi.model.AssessmentsQueryParams;
import com.scholastic.litplatformbaapi.model.LexileStatus;
import com.scholastic.litplatformbaapi.model.Student;
import com.scholastic.litplatformbaapi.model.StudentLexileStatus;
import com.scholastic.litplatformbaapi.model.StudentListResponse;
import com.scholastic.litplatformbaapi.exception.AssessmentException;
import com.scholastic.litplatformbaapi.repository.SrmStatusRepository;
import com.scholastic.litplatformbaapi.repository.AssessmentRepository;
import com.scholastic.litplatformbaapi.repository.StudentRepository;

import com.scholastic.litplatformbaapi.util.Constants;
import static com.scholastic.litplatformbaapi.util.Constants.CANCELED;
import static com.scholastic.litplatformbaapi.util.Constants.IN_PROGRESS;
import static com.scholastic.litplatformbaapi.util.Constants.SCHEDULED;
import static com.scholastic.litplatformbaapi.util.Constants.NO_TEST_SCHEDULED;
import static com.scholastic.litplatformbaapi.util.Constants.NOT_STARTED;
import static com.scholastic.litplatformbaapi.util.Constants.COMPLETED;
import static com.scholastic.litplatformbaapi.util.Constants.ASSIGNED;

import io.github.benas.jpopulator.impl.PopulatorBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SrmServiceImplTest {

    @Mock
    private SrmStatusRepository studentLexileStatusRepository;

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private AssessmentRepository assessmentRepository;

    private final StudentLexileStatus randomLexileStatus = new PopulatorBuilder().build().populateBean(StudentLexileStatus.class);

	private final Student randomStudent = new PopulatorBuilder().build().populateBean(Student.class);

	private final Assessment randomAssessment1 = new PopulatorBuilder().build()
			.populateBean(Assessment.class);

	private final Assessment randomAssessment2 = new PopulatorBuilder().build()
			.populateBean(Assessment.class);

    private SrmServiceImpl srmService;

    @BeforeEach
    public void setUp() {
		srmService = new SrmServiceImpl(studentLexileStatusRepository, studentRepository,
				assessmentRepository);
    }

    @Test
    @DisplayName("Lexile for Student not present")
    void getLexileAssessment_lexileStatusNotPresent() {
        when(studentLexileStatusRepository.findStudentLexileStatusByStudentId(10)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> srmService.getAssessment(10));
    }

    @Test
    @DisplayName("Lexile present for Student")
    void getLexileAssessment_lexileStatusPresent() {
        when(studentLexileStatusRepository.findStudentLexileStatusByStudentId(10)).thenReturn(Optional.ofNullable(randomLexileStatus));

        com.scholastic.litplatformbaapi.response.Assessment response = srmService.getAssessment(10);
        assertNotNull(response);

        var lexileAssessment = response.getSrmStatus();

        assertNotNull(lexileAssessment);
        assertNotNull(randomLexileStatus);
        assertEquals(randomLexileStatus.getAssessmentTest(), lexileAssessment.getAssessmentTest());
        assertEquals(randomLexileStatus.getId(), lexileAssessment.getId());
        assertEquals(randomLexileStatus.getStudentId(), lexileAssessment.getStudentId());
        assertEquals(randomLexileStatus.getTeacherId(), lexileAssessment.getTeacherId());
        assertEquals(randomLexileStatus.getBenchmark(), lexileAssessment.getBenchmark());
        assertEquals(randomLexileStatus.getAssignedDate(), lexileAssessment.getAssignedDate());
        assertEquals(randomLexileStatus.getCompletedDate(), lexileAssessment.getCompletedDate());
        assertEquals(randomLexileStatus.getLexile(), lexileAssessment.getLexile());
        assertEquals(randomLexileStatus.getStatus(), lexileAssessment.getStatus());
        assertEquals(randomLexileStatus.getEndDate(), lexileAssessment.getEndDate());
        assertEquals(randomLexileStatus.getAdminAssignmentId(), lexileAssessment.getAdminAssignmentId());
        assertEquals(randomLexileStatus.getTeacherJudgement(), lexileAssessment.getTeacherJudgement());
        assertEquals(randomLexileStatus.getCancelledByUserId(), lexileAssessment.getCancelledByUserId());
        assertEquals(randomLexileStatus.getModifiedDate(), lexileAssessment.getModifiedDate());
        assertEquals(randomLexileStatus.getCurrentQuestion(), lexileAssessment.getCurrentQuestion());
        assertEquals(randomLexileStatus.getTimeSpent(), lexileAssessment.getTimeSpent());
    }

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Assign SRM with valid input")
	void updateReadingMeasureAssignment_assessmentAssignVOListIsPresent() {
		Date startDate = Date.from(Instant.now());
		AssessmentAssignVO ssa1 = new AssessmentAssignVO();
		ssa1.setBenchmark("End");
		ssa1.setStudentId("227865");
		ssa1.setStartDate(startDate);
		ssa1.setTeacherAppraisal("Below Grade Level");

		AssessmentAssignVO ssa2 = new AssessmentAssignVO();
		ssa2.setBenchmark("Start");
		ssa2.setStudentId("227866");
		ssa2.setStartDate(startDate);
		ssa2.setTeacherAppraisal("Above Grade Level");

		AssessmentAssignVO ssa3 = new AssessmentAssignVO();
		ssa3.setStudentId("227867");
		ssa3.setStartDate(startDate);
		ssa3.setTeacherAppraisal("On Grade Level");

		AssessmentAssignVO ssa4 = new AssessmentAssignVO();
		ssa4.setBenchmark("Start");
		ssa4.setStudentId("227868");
		ssa4.setStartDate(startDate);

		List<AssessmentAssignVO> assessmentAssignVOList = new ArrayList<>();
		assessmentAssignVOList.add(ssa1);
		assessmentAssignVOList.add(ssa2);
		assessmentAssignVOList.add(ssa3);
		assessmentAssignVOList.add(ssa4);
		
		List<StudentLexileStatus> studentLexileStatuses = new ArrayList<>();
		studentLexileStatuses.add(randomLexileStatus);
		studentLexileStatuses.add(randomLexileStatus);
		studentLexileStatuses.add(randomLexileStatus);
		studentLexileStatuses.add(randomLexileStatus);

		doReturn(studentLexileStatuses).when(studentLexileStatusRepository).saveAll(any(List.class));

		srmService.updateReadingMeasureAssignment(assessmentAssignVOList, 12345);

		verify(studentLexileStatusRepository, times(1)).saveAll(any(List.class));
	}

	@Test
	@DisplayName("Assign SRM assessmentAssignVOList is empty")
	void updateReadingMeasureAssignment_assessmentAssignVOListIsEmpty() {
		List<AssessmentAssignVO> assessmentAssignVOList = new ArrayList<>();

		AssessmentException ae = assertThrows(AssessmentException.class,
				() -> srmService.updateReadingMeasureAssignment(assessmentAssignVOList, 12345),
				Constants.BAD_REQUEST_ERROR);

		assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
	}

	@Test
	@DisplayName("Assign SRM assessmentAssignVOList startDate is missing")
	void updateReadingMeasureAssignment_assessmentAssignVOTeacherAppraisalIsUnknown() {
		Date startDate = Date.from(Instant.now());

		AssessmentAssignVO sl = new AssessmentAssignVO();
		sl.setStudentId("227867");
		sl.setBenchmark("start");
		sl.setStartDate(startDate);
		sl.setTeacherAppraisal("Unknown");

		List<AssessmentAssignVO> assessmentAssignVOList = new ArrayList<>();
		assessmentAssignVOList.add(sl);

		AssessmentException ae = assertThrows(AssessmentException.class,
				() -> srmService.updateReadingMeasureAssignment(assessmentAssignVOList, 12345),
				Constants.INVALID_TEACHER_JUDGEMENT_ERROR);

		assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
	}

	@Test
	@DisplayName("Edit/Cancel SRM with valid input")
	void modifyReadingMeasureBatchAssignment_AssessmentModifyVOListIsPresent(){
		Calendar cal = Calendar.getInstance();
		Date startDate = cal.getTime();
		cal.add(Calendar.DATE,7);
		Date endDate = cal.getTime();

		AssessmentModifyVO am1 = new AssessmentModifyVO();
		am1.setId("1");
		am1.setStartDate(startDate);
		am1.setBenchmark("Start");
		am1.setStudentId("56791");
		am1.setTeacherAppraisal("Below Grade Level");
		am1.setEndDate(endDate);
		am1.setAction("cancel");

		AssessmentModifyVO am2 = new AssessmentModifyVO();
		am2.setId("2");
		am2.setStartDate(startDate);
		am2.setBenchmark("End");
		am2.setStudentId("56792");
		am2.setTeacherAppraisal("Above Grade Level");
		am2.setEndDate(endDate);
		am2.setAction("update");

		AssessmentModifyVO am3 = new AssessmentModifyVO();
		am3.setId("3");
		am3.setStudentId("56793");
		am3.setAction("cancel");

		AssessmentModifyVO am4 = new AssessmentModifyVO();
		am4.setId("4");
		am4.setStudentId("56794");
		am4.setAction("update");

		List<AssessmentModifyVO> assessmentModifyVOList = new ArrayList<>();
		assessmentModifyVOList.add(am1);
		assessmentModifyVOList.add(am2);
		assessmentModifyVOList.add(am3);
		assessmentModifyVOList.add(am4);

		List<StudentLexileStatus> studentLexileStatuses = new ArrayList<>();

		studentLexileStatuses.add(mockStudentLexileStatus(am1));
		studentLexileStatuses.add(mockStudentLexileStatus(am2));
		studentLexileStatuses.add(mockStudentLexileStatus(am3));
		studentLexileStatuses.add(mockStudentLexileStatus(am4));

		when(studentLexileStatusRepository.findAllById(anySet())).thenReturn(studentLexileStatuses);
		doReturn(studentLexileStatuses).when(studentLexileStatusRepository).saveAll(any(List.class));

		srmService.modifyReadingMeasureBatchAssignment(assessmentModifyVOList, 12345);

		verify(studentLexileStatusRepository,times (1)).findAllById(anySet());
		verify(studentLexileStatusRepository,times (1)).saveAll(any(List.class));
	}

	@Test
	@DisplayName("Edit/Cancel SRM assessmentModifyVOList is empty")
	void modifyReadingMeasureBatchAssignment_assessmentModifyVOListIsEmpty(){
		List<AssessmentModifyVO> assessmentModifyVOList = new ArrayList<>();

		AssessmentException ae = assertThrows(AssessmentException.class,
				() -> srmService.modifyReadingMeasureBatchAssignment(assessmentModifyVOList, 12345)
				);

		assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
	}

	@Test
	@DisplayName("Edit/Cancel SRM assessmentModifyVOList action is unknown")
	void modifyReadingMeasureBatchAssignment_assessmentModifyVOListActionIsUnknown(){
		AssessmentModifyVO am = new AssessmentModifyVO();
		am.setId("1");
		am.setStudentId("56791");
		am.setAction("Unknown");

		List<AssessmentModifyVO> assessmentModifyVOList = new ArrayList<>();
		assessmentModifyVOList.add(am);

		AssessmentException ae = assertThrows(AssessmentException.class,
				() -> srmService.modifyReadingMeasureBatchAssignment(assessmentModifyVOList, 12345),
				Constants.INVALID_ACTION_ERROR);

		assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
	}

	@Test
	@DisplayName("Edit/Cancel SRM assessmentModifyVOList status is not assigned")
	void modifyReadingMeasureBatchAssignment_assessmentModifyVOListStatusIsNotAssigned(){
		AssessmentModifyVO am = new AssessmentModifyVO();
		am.setId("1");
		am.setStudentId("56791");
		am.setAction("update");

		List<AssessmentModifyVO> assessmentModifyVOList = new ArrayList<>();
		assessmentModifyVOList.add(am);

		StudentLexileStatus sls = mockStudentLexileStatus(am);
		sls.setStatus(LexileStatus.CANCELED.getStatus());

		when(studentLexileStatusRepository.findAllById(anySet())).thenReturn(Collections.singletonList(sls));

		AssessmentException ae = assertThrows(AssessmentException.class,
				() -> srmService.modifyReadingMeasureBatchAssignment(assessmentModifyVOList, 12345),
				Constants.INVALID_STATUS_ERROR);

		verify(studentLexileStatusRepository,times (1)).findAllById(anySet());
		assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
	}

    @Test
    @DisplayName("Assessment doesn't exist")
    void updateSRMForCurrentQuestion_noAssessment() {
        AssessmentAnswerVO assessmentAnswerVO = new AssessmentAnswerVO();
        assessmentAnswerVO.setId("10");
        assessmentAnswerVO.setTimeSpent(25L);
        assessmentAnswerVO.setCurrentQuestion(16);
        assessmentAnswerVO.setStatus("IN_PROGRESS");
        assessmentAnswerVO.setLexile(BigDecimal.valueOf(107));

        when(studentLexileStatusRepository.findByIdAndStudentId(10, 12345)).thenReturn(Optional.empty());

		AssessmentException ae = assertThrows(AssessmentException.class,
                () -> srmService.updateSRMForCurrentQuestion(assessmentAnswerVO, 12345),
                Constants.BAD_REQUEST_ERROR);

        verify(studentLexileStatusRepository,times (1))
                .findByIdAndStudentId(10, 12345);
        assertEquals(HttpStatus.BAD_REQUEST, ae.getStatus());
    }

    @Test
    @DisplayName("Update assessment for current question")
    void updateSRMForCurrentQuestion() {
        AssessmentAnswerVO assessmentAnswerVO = new AssessmentAnswerVO();
        assessmentAnswerVO.setId("10");
        assessmentAnswerVO.setTimeSpent(25L);
        assessmentAnswerVO.setCurrentQuestion(16);
        assessmentAnswerVO.setStatus("IN_PROGRESS");
        assessmentAnswerVO.setLexile(BigDecimal.valueOf(107));

        StudentLexileStatus mockSls = new StudentLexileStatus();

        when(studentLexileStatusRepository.findByIdAndStudentId(10, 12345))
                .thenReturn(Optional.of(mockSls));

        srmService.updateSRMForCurrentQuestion(assessmentAnswerVO, 12345);

        verify(studentLexileStatusRepository,times (1))
                .findByIdAndStudentId(10, 12345);
        verify(studentLexileStatusRepository,times (1))
                .save(mockSls);

    }

    private StudentLexileStatus mockStudentLexileStatus(AssessmentModifyVO assessmentModifyVOList){
		StudentLexileStatus sls = new StudentLexileStatus();
		sls.setId(assessmentModifyVOList.getId());
		sls.setStudentId(assessmentModifyVOList.getStudentId());
		sls.setStatus(LexileStatus.ASSIGNED.getStatus());

		return sls;
	}

	@Test
	@DisplayName("Students not present")
	void getAssessments_studentsNotPresent() {
		AssessmentsQueryParams taParams = new AssessmentsQueryParams();
		List<Student> stuList = new ArrayList<>();
		when(studentRepository.findStudents(null, null, null, null)).thenReturn(stuList);

		StudentListResponse response = srmService.getAssessments(null, null, taParams);

		assertNotNull(response);

		var assessments = response.getResults();

		assertNotNull(assessments);
		assertEquals(new ArrayList<>(), assessments);

	}

	@Test
	@DisplayName("Student with assessment present")
	void getAssessments_studentWithAssessmentPresent() {
		List<Integer> classIds = new ArrayList<>();

		List<String> statuses = new ArrayList<>();
		statuses.add(NO_TEST_SCHEDULED);
		statuses.add(NOT_STARTED);
		statuses.add(SCHEDULED);

		AssessmentsQueryParams taNullParams = new AssessmentsQueryParams();
		taNullParams.setClassIds(classIds);
		taNullParams.setStatus(statuses);

		List<Student> randomStudents = new ArrayList<>();
		randomStudents.add(randomStudent);
		randomStudents.add(null);

		List<StudentLexileStatus> randomLexileStatuses = new ArrayList<>();
		randomLexileStatus.setStudentId(randomStudent.getStudentId());
		randomLexileStatuses.add(randomLexileStatus);

		List<Assessment> randomAssessments = new ArrayList<>();
		randomAssessment1.setStudentId(randomStudent.getStudentId());
		randomAssessments.add(randomAssessment1);

		when(studentRepository.findStudents(null, null, null, classIds)).thenReturn(randomStudents);
		List<Integer> studentIds = randomStudents.stream().filter(Objects::nonNull).map(Student::getStudentId)
				.toList();
		List<String> queryStatuses = new ArrayList<>();
		queryStatuses.add(ASSIGNED);

		when(studentLexileStatusRepository.findCompletedByStudentId(studentIds)).thenReturn(randomLexileStatuses);
		when(assessmentRepository.findAssessments(studentIds, null, null, null, queryStatuses))
				.thenReturn(randomAssessments);

		StudentListResponse response = srmService.getAssessments(null, null, taNullParams);

		assertNotNull(response);

		var assessments = response.getResults();

		var daysSinceAssessment1 = (int) TimeUnit.DAYS.convert(
				Math.abs(Date.from(Instant.now()).getTime() - randomAssessment1.getEndDate().getTime()),
				TimeUnit.MILLISECONDS);

		assertNotNull(assessments);
		assertEquals(randomAssessment1.getAssessmentId(),
				assessments.get(0).getAssessments().get(0).getAssessmentId());
		assertEquals(randomAssessment1.getAssignedByFirstName(),
				assessments.get(0).getAssessments().get(0).getAssignedByFirstName());
		assertEquals(randomAssessment1.getAssignedById(),
				assessments.get(0).getAssessments().get(0).getAssignedById());
		assertEquals(randomAssessment1.getAssignedByLastName(),
				assessments.get(0).getAssessments().get(0).getAssignedByLastName());
		assertEquals(randomAssessment1.getBenchmark(), assessments.get(0).getAssessments().get(0).getBenchmark());
		assertEquals(randomAssessment1.getCurrentQuestion(),
				assessments.get(0).getAssessments().get(0).getCurrentQuestion());
		assertEquals(randomAssessment1.getEndDate(), assessments.get(0).getAssessments().get(0).getEndDate());
		assertEquals(randomAssessment1.getLexile(), assessments.get(0).getAssessments().get(0).getLexileValue());
		assertEquals(randomAssessment1.getStartDate(), assessments.get(0).getAssessments().get(0).getStartDate());
		assertEquals(randomAssessment1.getStatus(), assessments.get(0).getAssessments().get(0).getStatus());
		assertEquals(randomAssessment1.getTeacherJudgement(),
				assessments.get(0).getAssessments().get(0).getTeacherJudgment());
		assertEquals(randomAssessment1.getTimeSpent(), assessments.get(0).getAssessments().get(0).getTimeSpent());

		assertEquals(true, assessments.get(0).getCompletedSrm());
		assertEquals(daysSinceAssessment1, assessments.get(0).getDaysSinceLastAssessment());
		assertEquals(randomStudent.getFirstName(), assessments.get(0).getFirstName());
		assertEquals(randomStudent.getLastName(), assessments.get(0).getLastName());
		assertEquals(randomStudent.getStudentId(), assessments.get(0).getStudentId());
	}

	@Test
	@DisplayName("Student with assessment present and no test scheduled")
	void getAssessments_studentWithAssessmentPresent_noTestScheduled() {
		List<Integer> classIds = new ArrayList<>();

		List<String> statuses = new ArrayList<>();
		statuses.add(NO_TEST_SCHEDULED);

		AssessmentsQueryParams taNullParams = new AssessmentsQueryParams();
		taNullParams.setClassIds(classIds);
		taNullParams.setStatus(statuses);

		List<Student> randomStudents = new ArrayList<>();
		randomStudents.add(randomStudent);
		randomStudents.add(null);

		List<StudentLexileStatus> randomLexileStatuses = new ArrayList<>();
		randomLexileStatus.setStudentId(randomStudent.getStudentId());
		randomLexileStatuses.add(randomLexileStatus);

		List<Assessment> randomAssessments = new ArrayList<>();
		randomAssessment1.setStudentId(randomStudent.getStudentId());
		randomAssessments.add(randomAssessment1);

		when(studentRepository.findStudents(null, null, null, classIds)).thenReturn(randomStudents);
		List<Integer> studentIds = randomStudents.stream().filter(Objects::nonNull).map(Student::getStudentId).toList();
		List<String> queryStatuses = new ArrayList<>();
		queryStatuses.add(ASSIGNED);

		when(studentLexileStatusRepository.findCompletedByStudentId(studentIds)).thenReturn(randomLexileStatuses);
		when(assessmentRepository.findAssessments(studentIds, null, null, null, queryStatuses))
				.thenReturn(randomAssessments);

		StudentListResponse response = srmService.getAssessments(null, null, taNullParams);

		assertNotNull(response);

		var assessments = response.getResults();

		var daysSinceAssessment1 = (int) TimeUnit.DAYS.convert(
				Math.abs(Date.from(Instant.now()).getTime() - randomAssessment1.getEndDate().getTime()),
				TimeUnit.MILLISECONDS);

		assertNotNull(assessments);
		assertEquals(randomAssessment1.getAssessmentId(), assessments.get(0).getAssessments().get(0).getAssessmentId());
		assertEquals(randomAssessment1.getAssignedByFirstName(),
				assessments.get(0).getAssessments().get(0).getAssignedByFirstName());
		assertEquals(randomAssessment1.getAssignedById(), assessments.get(0).getAssessments().get(0).getAssignedById());
		assertEquals(randomAssessment1.getAssignedByLastName(),
				assessments.get(0).getAssessments().get(0).getAssignedByLastName());
		assertEquals(randomAssessment1.getBenchmark(), assessments.get(0).getAssessments().get(0).getBenchmark());
		assertEquals(randomAssessment1.getCurrentQuestion(),
				assessments.get(0).getAssessments().get(0).getCurrentQuestion());
		assertEquals(randomAssessment1.getEndDate(), assessments.get(0).getAssessments().get(0).getEndDate());
		assertEquals(randomAssessment1.getLexile(), assessments.get(0).getAssessments().get(0).getLexileValue());
		assertEquals(randomAssessment1.getStartDate(), assessments.get(0).getAssessments().get(0).getStartDate());
		assertEquals(randomAssessment1.getStatus(), assessments.get(0).getAssessments().get(0).getStatus());
		assertEquals(randomAssessment1.getTeacherJudgement(),
				assessments.get(0).getAssessments().get(0).getTeacherJudgment());
		assertEquals(randomAssessment1.getTimeSpent(), assessments.get(0).getAssessments().get(0).getTimeSpent());

		assertEquals(true, assessments.get(0).getCompletedSrm());
		assertEquals(daysSinceAssessment1, assessments.get(0).getDaysSinceLastAssessment());
		assertEquals(randomStudent.getFirstName(), assessments.get(0).getFirstName());
		assertEquals(randomStudent.getLastName(), assessments.get(0).getLastName());
		assertEquals(randomStudent.getStudentId(), assessments.get(0).getStudentId());
	}

	@Test
	@DisplayName("Student with assessments present")
	void getAssessments_studentWithMultipleAssessmentsPresent() {
		List<Integer> classIds = new ArrayList<>();

		AssessmentsQueryParams taNullParams = new AssessmentsQueryParams();
		taNullParams.setClassIds(classIds);

		List<Student> randomStudents = new ArrayList<>();
		randomStudents.add(randomStudent);
		randomStudents.add(null);

		List<StudentLexileStatus> randomLexileStatuses = new ArrayList<>();
		randomLexileStatus.setStudentId(randomStudent.getStudentId());
		randomLexileStatuses.add(randomLexileStatus);

		List<Assessment> randomAssessments = new ArrayList<>();
		randomAssessment1.setStudentId(randomStudent.getStudentId());
		randomAssessments.add(randomAssessment1);
		randomAssessment2.setStudentId(randomStudent.getStudentId());
		randomAssessments.add(randomAssessment2);

		when(studentRepository.findStudents(null, null, null, classIds)).thenReturn(randomStudents);
		List<Integer> studentIds = randomStudents.stream().filter(Objects::nonNull).map(Student::getStudentId)
				.toList();

		when(studentLexileStatusRepository.findCompletedByStudentId(studentIds)).thenReturn(randomLexileStatuses);
		when(assessmentRepository.findAssessments(studentIds, null, null, null, null))
				.thenReturn(randomAssessments);

		StudentListResponse response = srmService.getAssessments(null, null, taNullParams);

		assertNotNull(response);

		var assessments = response.getResults();

		AssessmentResponse assessment1 = assessments.get(0).getAssessments().get(0);

		AssessmentResponse assessment2 = assessments.get(0).getAssessments().get(1);

		var daysSinceAssessment1 = (int) TimeUnit.DAYS.convert(
				Math.abs(Date.from(Instant.now()).getTime() - assessment1.getEndDate().getTime()),
				TimeUnit.MILLISECONDS);

		var daysSinceAssessment2 =  (int) TimeUnit.DAYS.convert(
				Math.abs(Date.from(Instant.now()).getTime() - assessment2.getEndDate().getTime()),
				TimeUnit.MILLISECONDS);

		var daysSinceLastAssessment = (daysSinceAssessment1 > daysSinceAssessment2) ? daysSinceAssessment2
				: daysSinceAssessment1;

		var daysSinceRandomAssessment1 = (int) TimeUnit.DAYS.convert(
				Math.abs(Date.from(Instant.now()).getTime() - randomAssessment1.getEndDate().getTime()),
				TimeUnit.MILLISECONDS);

		var daysSinceRandomAssessment2 = (int) TimeUnit.DAYS.convert(
				Math.abs(Date.from(Instant.now()).getTime() - randomAssessment2.getEndDate().getTime()),
				TimeUnit.MILLISECONDS);

		var daysSinceLastRandomAssessment = (daysSinceRandomAssessment1 > daysSinceRandomAssessment2) ? daysSinceRandomAssessment2
				: daysSinceRandomAssessment1;

		assertNotNull(assessments);

		assertEquals(randomAssessment2.getAssessmentId(),
				assessments.get(0).getAssessments().get(1).getAssessmentId());
		assertEquals(randomAssessment2.getAssignedByFirstName(),
				assessments.get(0).getAssessments().get(1).getAssignedByFirstName());
		assertEquals(randomAssessment2.getAssignedById(),
				assessments.get(0).getAssessments().get(1).getAssignedById());
		assertEquals(randomAssessment2.getAssignedByLastName(),
				assessments.get(0).getAssessments().get(1).getAssignedByLastName());
		assertEquals(randomAssessment2.getBenchmark(), assessments.get(0).getAssessments().get(1).getBenchmark());
		assertEquals(randomAssessment2.getCurrentQuestion(),
				assessments.get(0).getAssessments().get(1).getCurrentQuestion());
		assertEquals(randomAssessment2.getEndDate(), assessments.get(0).getAssessments().get(1).getEndDate());
		assertEquals(randomAssessment2.getLexile(), assessments.get(0).getAssessments().get(1).getLexileValue());
		assertEquals(randomAssessment2.getStartDate(), assessments.get(0).getAssessments().get(1).getStartDate());
		assertEquals(randomAssessment2.getStatus(), assessments.get(0).getAssessments().get(1).getStatus());
		assertEquals(randomAssessment2.getTeacherJudgement(),
				assessments.get(0).getAssessments().get(1).getTeacherJudgment());
		assertEquals(randomAssessment2.getTimeSpent(), assessments.get(0).getAssessments().get(1).getTimeSpent());
		assertEquals(true, assessments.get(0).getCompletedSrm());
		assertEquals(daysSinceLastRandomAssessment, daysSinceLastAssessment);
		assertEquals(randomStudent.getFirstName(), assessments.get(0).getFirstName());
		assertEquals(randomStudent.getLastName(), assessments.get(0).getLastName());
		assertEquals(randomStudent.getStudentId(), assessments.get(0).getStudentId());
	}


	@Test
	@DisplayName("Student without assessment present with canceled, in progress and completed status")
	void getAssessments_studentWithoutAssessmentPresent_canceledInProgressCompleted() {
		List<Integer> classIds = new ArrayList<>();

		List<String> statuses = new ArrayList<>();
		statuses.add(CANCELED);
		statuses.add(IN_PROGRESS);
		statuses.add(COMPLETED);

		AssessmentsQueryParams taNullParams = new AssessmentsQueryParams();
		taNullParams.setClassIds(classIds);
		taNullParams.setStatus(statuses);

		List<Student> randomStudents = new ArrayList<>();
		randomStudents.add(randomStudent);

		List<StudentLexileStatus> randomLexileStatuses = new ArrayList<>();

		List<Assessment> randomAssessments = new ArrayList<>();

		when(studentRepository.findStudents(null, null, null, classIds)).thenReturn(randomStudents);
		List<Integer> studentIds = randomStudents.stream().filter(Objects::nonNull).map(Student::getStudentId)
				.toList();

		List<String> queryStatuses = new ArrayList<>();
		queryStatuses.add(COMPLETED);
		queryStatuses.add(IN_PROGRESS);
		queryStatuses.add(CANCELED);

		when(studentLexileStatusRepository.findCompletedByStudentId(studentIds)).thenReturn(randomLexileStatuses);
		when(assessmentRepository.findAssessments(studentIds, null, null, null, queryStatuses))
				.thenReturn(randomAssessments);

		StudentListResponse response = srmService.getAssessments(null, null, taNullParams);

		assertNotNull(response);

		var assessments = response.getResults();

		assertNotNull(assessments);
		assertEquals(new ArrayList<>(), assessments);

	}

	@Test
	@DisplayName("Student without assessment present and no query params")
	void getAssessments_studentWithoutAssessmentPresent_noQueryParams() {
		List<Integer> classIds = new ArrayList<>();

		AssessmentsQueryParams taNullParams = new AssessmentsQueryParams();
		taNullParams.setClassIds(classIds);

		List<Student> randomStudents = new ArrayList<>();
		randomStudents.add(randomStudent);

		List<StudentLexileStatus> randomLexileStatuses = new ArrayList<>();

		List<Assessment> randomAssessments = new ArrayList<>();

		when(studentRepository.findStudents(null, null, null, classIds)).thenReturn(randomStudents);
		List<Integer> studentIds = randomStudents.stream().filter(Objects::nonNull).map(Student::getStudentId).toList();

		when(studentLexileStatusRepository.findCompletedByStudentId(studentIds)).thenReturn(randomLexileStatuses);
		when(assessmentRepository.findAssessments(studentIds, null, null, null, null)).thenReturn(randomAssessments);

		StudentListResponse response = srmService.getAssessments(null, null, taNullParams);

		assertNotNull(response);

		var assessments = response.getResults();

		assertNotNull(assessments);
		assertEquals(new ArrayList<>(), assessments.get(0).getAssessments());
		assertEquals(false, assessments.get(0).getCompletedSrm());
		assertNull(assessments.get(0).getDaysSinceLastAssessment());
		assertEquals(randomStudent.getFirstName(), assessments.get(0).getFirstName());
		assertEquals(randomStudent.getLastName(), assessments.get(0).getLastName());
		assertEquals(randomStudent.getStudentId(), assessments.get(0).getStudentId());

	}

	@Test
	@DisplayName("Get assessment query params")
	void getAssessmentsQueryParams() {
		List<Integer> classIds = Arrays.asList(45567, 64749, 95859);
		List<String> benchmark = Arrays.asList("END", "MIDDLE");
		List<String> status = Arrays.asList("ASSIGNED", "IN_PROGRESS");
		Calendar cal = Calendar.getInstance();
		Date startDate = cal.getTime();
		cal.add(Calendar.DATE, 7);
		Date endDate = cal.getTime();

		AssessmentsQueryParams assessmentsQueryParams = srmService.populateAssessmentsQueryParams(2022, classIds,
				startDate, endDate, benchmark, status);
		assertNotNull(assessmentsQueryParams);
	}

    @Test
    @DisplayName("Cancel outdated assessments")
    void cancelAssessments() {
        Set<Long> assessmentTestIds = new HashSet<>();
        assessmentTestIds.add(2l);
        assessmentTestIds.add(4l);

        srmService.cancelAssessments(assessmentTestIds);

        verify(studentLexileStatusRepository, times(1))
                .cancelAssessmentsInProgress(assessmentTestIds);
        verify(studentLexileStatusRepository, times(1))
                .cancelAssessmentsNotStarted();
    }
}
