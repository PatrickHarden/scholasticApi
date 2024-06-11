package com.scholastic.litplatformbaapi.repository;

import com.scholastic.litplatformbaapi.model.StudentLexileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SrmStatusRepository extends JpaRepository<StudentLexileStatus, Integer> {
    @Query(nativeQuery = true,
            value=""" 
                    SELECT * FROM student_lexile_status
                    WHERE student_id = :studentId AND assigned_date <= now() AND status <> 'COMPLETED' AND status <> 'CANCELED' AND status <> 'DELETED'
                    ORDER BY assigned_date ASC LIMIT 1;""")
    Optional<StudentLexileStatus> findStudentLexileStatusByStudentId(int studentId);

    Optional<StudentLexileStatus> findByIdAndStudentId(Integer id, Integer studentId);

	@Query(nativeQuery = true, value = """
			SELECT * FROM student_lexile_status
			WHERE student_id in :studentIds AND status = 'COMPLETED';""")
	List<StudentLexileStatus> findCompletedByStudentId(@Param("studentIds") List<Integer> studentIds);

    @Modifying
    @Query(nativeQuery = true,
            value=""" 
                    UPDATE student_lexile_status sls
                     SET sls.status = 'CANCELED', sls.modified_date = now()
                     WHERE sls.status = 'ASSIGNED'
                     AND (sls.end_date <= NOW()
                      OR (datediff(now(), sls.assigned_date) >= 28 and sls.end_date is null));""")
    void cancelAssessmentsNotStarted();

    @Modifying
    @Query(nativeQuery = true,
            value=""" 
                    UPDATE student_lexile_status sls
                     SET sls.status = 'CANCELED', sls.modified_date = now()
                     WHERE sls.status = 'IN PROGRESS'
                     AND sls.assessment_test IN ( :assessmentTestIds );""")
    void cancelAssessmentsInProgress(@Param("assessmentTestIds") Set<Long> assessmentTestIds);
}
