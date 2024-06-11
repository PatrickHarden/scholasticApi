package com.scholastic.litplatformbaapi.service.support;

import com.scholastic.litplatformbaapi.exception.AssessmentException;
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
import com.scholastic.litplatformbaapi.model.StudentResponse;
import com.scholastic.litplatformbaapi.repository.AssessmentRepository;
import com.scholastic.litplatformbaapi.repository.SrmStatusRepository;
import com.scholastic.litplatformbaapi.repository.StudentRepository;
import com.scholastic.litplatformbaapi.response.SrmStatus;
import com.scholastic.litplatformbaapi.service.AssessmentService;
import com.scholastic.litplatformbaapi.util.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.scholastic.litplatformbaapi.util.Constants.ASSIGNED;
import static com.scholastic.litplatformbaapi.util.Constants.BAD_REQUEST_ERROR;
import static com.scholastic.litplatformbaapi.util.Constants.CANCELED;
import static com.scholastic.litplatformbaapi.util.Constants.COMPLETED;
import static com.scholastic.litplatformbaapi.util.Constants.INVALID_ACTION_ERROR;
import static com.scholastic.litplatformbaapi.util.Constants.INVALID_STATUS_ERROR;
import static com.scholastic.litplatformbaapi.util.Constants.INVALID_TEACHER_JUDGEMENT_ERROR;
import static com.scholastic.litplatformbaapi.util.Constants.IN_PROGRESS;
import static com.scholastic.litplatformbaapi.util.Constants.NOT_STARTED;
import static com.scholastic.litplatformbaapi.util.Constants.NO_TEST_SCHEDULED;
import static com.scholastic.litplatformbaapi.util.Constants.SCHEDULED;

@Service("srmService")
public class SrmServiceImpl implements AssessmentService {

    private final SrmStatusRepository repository;
	private final StudentRepository studentRepository;
	private final AssessmentRepository assessmentRepository;

    @Autowired
	public SrmServiceImpl(SrmStatusRepository repository, StudentRepository studentRepository,
			AssessmentRepository assessmentRepository) {
        this.repository = repository;
		this.studentRepository = studentRepository;
		this.assessmentRepository = assessmentRepository;
    }

    @Override
    public com.scholastic.litplatformbaapi.response.Assessment getAssessment(Integer studentId) {
        var studentLexileData = this.repository.findStudentLexileStatusByStudentId(studentId)
                .orElseThrow(() -> new AssessmentException(HttpStatus.NOT_FOUND, "No srm assessment found for the student"));

        return com.scholastic.litplatformbaapi.response.Assessment.builder()
                .srmStatus(mapStudentLexileStatus(studentLexileData))
                .build();
    }

    private SrmStatus mapStudentLexileStatus(StudentLexileStatus studentLexileData) {
        var studentLexileStatusResponse = new SrmStatus();
        BeanUtils.copyProperties(studentLexileData, studentLexileStatusResponse);
        return studentLexileStatusResponse;
    }

	@Override
	@Transactional
	public void updateReadingMeasureAssignment(List<AssessmentAssignVO> assessmentAssignVOList, Integer teacher) {
		if (assessmentAssignVOList.isEmpty()) {
			throw new AssessmentException(HttpStatus.BAD_REQUEST, BAD_REQUEST_ERROR);
		}
		assessmentAssignVOList.forEach(this::validateTeacherAppraisalForBatchAssign);

		Map<String, AssessmentAssignVO> studentLexilePerStudent = assessmentAssignVOList.stream()
				.collect(Collectors.toMap(AssessmentAssignVO::getStudentId, Function.identity()));

		List<StudentLexileStatus> studentLexileStatuses = new ArrayList<>();
		for (Entry<String, AssessmentAssignVO> entry : studentLexilePerStudent.entrySet()) {
			var sls = new StudentLexileStatus();
			populateStudentLexileStatus(teacher, entry.getKey(), entry.getValue(), sls);

			studentLexileStatuses.add(sls);
		}

		repository.saveAll(studentLexileStatuses);
	}

	private void validateTeacherAppraisalForBatchAssign(AssessmentAssignVO assessmentAssignVOList) {
		if (assessmentAssignVOList.getTeacherAppraisal() != null
				&& Constants.UNKNOWN.equals(getTeacherJudgement(assessmentAssignVOList.getTeacherAppraisal()))) {
			throw new AssessmentException(HttpStatus.BAD_REQUEST, INVALID_TEACHER_JUDGEMENT_ERROR);
		}
	}

	private String getTeacherJudgement(String teacherJudgement) {
		if (teacherJudgement == null)
			return null;
		switch (teacherJudgement.toLowerCase()) {
		case "below grade level":
			return Constants.BELOW_LEVEL;
		case "above grade level":
			return Constants.ABOVE_LEVEL;
		case "on grade level":
			return Constants.ON_LEVEL;
		default:
			return Constants.UNKNOWN;
		}
	}

	private void populateStudentLexileStatus(Integer teacher, String student, AssessmentAssignVO assessmentAssignVOList,
			StudentLexileStatus sls) {
		sls.setAssignedDate(assessmentAssignVOList.getStartDate());
		sls.setBenchmark(assessmentAssignVOList.getBenchmark());
		sls.setStatus(LexileStatus.ASSIGNED.getStatus());
		sls.setStudentId(Integer.parseInt(student));
		sls.setTeacherId(teacher);
		sls.setModifiedDate(Date.from(Instant.now()));
		sls.setCurrentQuestion(0);
		sls.setTimeSpent(0L);
		sls.setTeacherJudgement(getTeacherJudgement(assessmentAssignVOList.getTeacherAppraisal()));
	}

	@Override
	@Transactional
	public void modifyReadingMeasureBatchAssignment(List<AssessmentModifyVO> assessmentModifyVOList, Integer teacher){
		if (assessmentModifyVOList.isEmpty())
			throw new AssessmentException(HttpStatus.BAD_REQUEST, BAD_REQUEST_ERROR);

		assessmentModifyVOList.forEach(this::validateAction);

		Map<Integer, AssessmentModifyVO> map = assessmentModifyVOList.stream()
				.collect(Collectors.toMap(AssessmentModifyVO::getId, Function.identity()));

		List<StudentLexileStatus> studentLexileStatuses = this.repository.findAllById(map.keySet());

		for (StudentLexileStatus sls: studentLexileStatuses) {
			int id = studentLexileStatuses.indexOf(sls);

			if (!sls.getStatus().equals(LexileStatus.ASSIGNED.getStatus()))
				throw new AssessmentException(HttpStatus.BAD_REQUEST, INVALID_STATUS_ERROR);

			modifyStudentLexileStatus(teacher, assessmentModifyVOList.get(id), sls);
		}

		repository.saveAll(studentLexileStatuses);
	}

    @Override
    @Transactional
    public void updateSRMForCurrentQuestion(AssessmentAnswerVO assessmentAnswerVO, Integer studentId) {
        var sls = this.repository
                .findByIdAndStudentId(Integer.parseInt(assessmentAnswerVO.getId()), studentId);

        if(sls.isEmpty()) {
			throw new AssessmentException(HttpStatus.BAD_REQUEST, BAD_REQUEST_ERROR);
        }

        repository.save(modifyAssessmentForCurrentQuestion(sls.get(), assessmentAnswerVO));
    }

    private StudentLexileStatus modifyAssessmentForCurrentQuestion(StudentLexileStatus sls,
                                                                   AssessmentAnswerVO assessmentAnswerVO) {
        sls.setTimeSpent(assessmentAnswerVO.getTimeSpent());
        sls.setCurrentQuestion(assessmentAnswerVO.getCurrentQuestion());
        sls.setStatus(assessmentAnswerVO.getStatus());
        sls.setLexile(assessmentAnswerVO.getLexile());
		sls.setModifiedDate(Date.from(Instant.now()));
        return sls;
    }

    private void modifyStudentLexileStatus(Integer teacher, AssessmentModifyVO assessmentModifyVOList,
										   StudentLexileStatus sls){
		sls.setModifiedDate(Date.from(Instant.now()));

		if (assessmentModifyVOList.getAction().equalsIgnoreCase("cancel")) {
			sls.setStatus(LexileStatus.CANCELED.getStatus());
			sls.setCancelledByUserId(teacher);
		}
		else {
			if (assessmentModifyVOList.getStartDate()!=null)
				sls.setAssignedDate(assessmentModifyVOList.getStartDate());
			if(assessmentModifyVOList.getEndDate() != null)
				sls.setEndDate(assessmentModifyVOList.getEndDate());
			if (assessmentModifyVOList.getBenchmark() != null)
				sls.setBenchmark(assessmentModifyVOList.getBenchmark());
			if (assessmentModifyVOList.getTeacherAppraisal() != null)
				sls.setTeacherJudgement(getTeacherJudgement(assessmentModifyVOList.getTeacherAppraisal()));
		}
	}

	private void validateAction(AssessmentModifyVO assessmentModifyVOList){
		if (Constants.UNKNOWN.equals(getAssessmentAction(assessmentModifyVOList.getAction())))
			throw new AssessmentException(HttpStatus.BAD_REQUEST, INVALID_ACTION_ERROR);
	}

	private String getAssessmentAction(String action){
		switch (action){
			case "update":
				return Constants.UPDATE;
			case "cancel":
				return Constants.CANCEL;
			default:
				return Constants.UNKNOWN;
		}
	}

	@Override
	public StudentListResponse getAssessments(Integer staffId, Integer orgId,
											  AssessmentsQueryParams queryParams) {

		// find students filtered by given params
		var studentData = this.studentRepository.findStudents(staffId, orgId, queryParams.getSchoolYear(),
				queryParams.getClassIds());

		List<Integer> studentIds = studentData.stream().filter(Objects::nonNull).map(Student::getStudentId)
				.collect(Collectors.toList());

		// find students who have completed an assessment
		List<Integer> completedStudentIds = getStudentsWithCompletedAssessments(studentIds);

		// find assessments filtered by given params

		List<String> queryStatuses = getQueryStatuses(queryParams.getStatus());

		var studentAssessmentsData = this.assessmentRepository.findAssessments(studentIds, queryParams.getStartDate(),
				queryParams.getEndDate(),
				queryParams.getBenchmark(), queryStatuses);

		Map<Integer, List<Assessment>> studentAssessmentsMap = studentAssessmentsData.stream()
				.collect(Collectors.groupingBy(Assessment::getStudentId,
						Collectors.mapping(Function.identity(), Collectors.toList())));

		return StudentListResponse.builder()
				.results(
						populateStudents(studentData, completedStudentIds, studentAssessmentsMap, queryParams.getStatus()))
				.build();
	}

	private List<Integer> getStudentsWithCompletedAssessments(List<Integer> studentIds) {
		List<StudentLexileStatus> studentLexileStatuses = this.repository.findCompletedByStudentId(studentIds);

		return studentLexileStatuses.stream().distinct()
				.map(StudentLexileStatus::getStudentId).collect(Collectors.toList());
	}
	
	private List<String> getQueryStatuses(List<String> statuses) {
		if (statuses == null)
			return statuses;

		List<String> queryStatuses = new ArrayList<>();
		if (statuses.contains(COMPLETED))
			queryStatuses.add(COMPLETED);
		if (statuses.contains(IN_PROGRESS))
			queryStatuses.add(IN_PROGRESS);
		if (statuses.contains(CANCELED))
			queryStatuses.add(CANCELED);
		
		if ((statuses.contains(NOT_STARTED) || statuses.contains(SCHEDULED))
				|| statuses.contains(NO_TEST_SCHEDULED)) {
			queryStatuses.add(ASSIGNED);
		}

		return queryStatuses;
	}

	private ArrayList<StudentResponse> populateStudents(List<Student> students,
			List<Integer> completedStudentIds,
			Map<Integer, List<Assessment>> studentAssessmentsMap, List<String> statuses) {
		var studentResponseList = new ArrayList<StudentResponse>();
		
		if (!students.isEmpty()) {
			for (Student student : students) {
				if (student == null) {
					continue;
				}
				var response = new StudentResponse();

				response.setStudentId(student.getStudentId());
				response.setFirstName(student.getFirstName());
				response.setLastName(student.getLastName());
				response.setCompletedSrm(completedStudentIds.contains(student.getStudentId()));

				var assessmentResponseList = new ArrayList<AssessmentResponse>();
				var assignedAssessmentPresent = false;

				if (studentAssessmentsMap.containsKey(student.getStudentId())) {
					assignedAssessmentPresent = populateAssessments(studentAssessmentsMap, student, response,
							assessmentResponseList, statuses);
					response.setAssessments(assessmentResponseList);
				} else {
					response.setAssessments(assessmentResponseList);
					response.setDaysSinceLastAssessment(null);
				}

				if (statuses == null || !skipStudent(response, statuses, assignedAssessmentPresent))
					studentResponseList.add(response);
			}
		}

		return studentResponseList;
	}

	private boolean skipStudent(StudentResponse response, List<String> statuses, boolean assignedAssessmentPresent) {
		boolean skipStudentsWithAssessmentAssigned = response.getAssessments().isEmpty()
				&& statuses.contains(NO_TEST_SCHEDULED)
				&& assignedAssessmentPresent;

		boolean skipStudentsWithoutAssessments = !statuses.contains(NO_TEST_SCHEDULED)
				&& response.getAssessments().isEmpty();

		return skipStudentsWithAssessmentAssigned || skipStudentsWithoutAssessments;
	}
	
	private boolean populateAssessments(Map<Integer, List<Assessment>> studentAssessmentsMap,
			 Student student, StudentResponse response, ArrayList<AssessmentResponse> assessmentResponseList, List<String> statuses) {
		var assignedAssessmentPresent = false;
		for (Assessment assessment : studentAssessmentsMap.get(student.getStudentId())) {
			if (assessment.getStatus().equals(ASSIGNED))
				assignedAssessmentPresent = true;
			if (statuses != null && skipAssessment(assessment, statuses))
				continue;

			long diffInMillies = Math.abs(Date.from(Instant.now()).getTime() - assessment.getEndDate().getTime());
			int daysSinceAssessment = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			if (response.getDaysSinceLastAssessment() == null
					|| daysSinceAssessment < response.getDaysSinceLastAssessment())
				response.setDaysSinceLastAssessment(daysSinceAssessment);

			assessmentResponseList.add(populateAssessmentResponse(assessment));
		}
		return assignedAssessmentPresent;
	}

	private boolean skipAssessment(Assessment assessment, List<String> statuses) {
		boolean skipNotStartedAssessment = !statuses.contains(NOT_STARTED)
				&& assessment.getStatus().equals(ASSIGNED)
				&& assessment.getStartDate().getTime() <= Date.from(Instant.now()).getTime();

		boolean skipScheduledAssessment = !statuses.contains(SCHEDULED)
				&& assessment.getStatus().equals(ASSIGNED)
				&& assessment.getStartDate().getTime() >= Date.from(Instant.now()).getTime();

		return skipNotStartedAssessment || skipScheduledAssessment;
	}

	private AssessmentResponse populateAssessmentResponse(Assessment assessment) {
		var assessmentResponse = new AssessmentResponse();

		assessmentResponse.setAssessmentId(assessment.getAssessmentId());
		assessmentResponse.setBenchmark(assessment.getBenchmark());
		assessmentResponse.setStartDate(assessment.getStartDate());
		assessmentResponse.setEndDate(assessment.getEndDate());
		assessmentResponse.setLexileValue(assessment.getLexile());
		assessmentResponse.setStatus(assessment.getStatus());
		assessmentResponse.setCurrentQuestion(assessment.getCurrentQuestion());
		assessmentResponse.setProficiency(assessment.getProficiency());
		assessmentResponse.setTimeSpent(assessment.getTimeSpent());
		assessmentResponse.setAssignedByFirstName(assessment.getAssignedByFirstName());
		assessmentResponse.setAssignedByLastName(assessment.getAssignedByLastName());
		assessmentResponse.setAssignedById(assessment.getAssignedById());
		assessmentResponse.setTeacherJudgment(assessment.getTeacherJudgement());

		return assessmentResponse;
	}

	public AssessmentsQueryParams populateAssessmentsQueryParams(Integer schoolYear,
												   List<Integer> classIds, Date startDate, Date endDate, List<String> benchmark, List<String> status) {
		return new AssessmentsQueryParams(schoolYear, classIds, startDate,
				endDate, benchmark, status);
	}

    @Override
    @Transactional
    public void cancelAssessments(Set<Long> assessmentTestIds) {
        this.repository.cancelAssessmentsInProgress(assessmentTestIds);
        this.repository.cancelAssessmentsNotStarted();
    }
}
