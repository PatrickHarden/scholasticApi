package com.scholastic.litplatformbaapi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scholastic.litplatformbaapi.model.Assessment;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
	@Query(nativeQuery = true, value = """ 
			SELECT sls.id, sls.student_id, sls.benchmark, sls.assigned_date, sls.end_date,
			sls.lexile, lb.level AS proficiency, sls.status, sls.current_question, sls.time_spent, teacher.first_name AS teacher_first_name, teacher.last_name AS teacher_last_name,
			teacher_id, sls.teacher_judgement
			FROM student_lexile_status AS sls
			LEFT JOIN person teacher ON sls.teacher_id = teacher.id
			LEFT JOIN student ON sls.student_id = student.id
			LEFT JOIN lexile_benchmarks lb ON sls.benchmark = lb.time_period AND (lb.lower_range IS NULL OR sls.lexile >= lb.lower_range) AND (lb.upper_range IS NULL OR sls.lexile <= lb.upper_range) AND student.grade = lb.grade
			WHERE student_id in :studentIds
			AND (:startDate IS NULL OR sls.assigned_date >= :startDate)
			AND (:endDate IS NULL OR sls.assigned_date <= :endDate)
			AND (coalesce( null, :benchmark ) IS null OR sls.benchmark IN (:benchmark))
			AND (coalesce( null, :status ) IS null OR sls.status IN (:status))
			ORDER BY sls.student_id, sls.assigned_date ASC;
			""")
	List<Assessment> findAssessments(@Param("studentIds") List<Integer> studentIds,
                                     @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                     @Param("benchmark") List<String> benchmark, @Param("status") List<String> status);

}
