package com.scholastic.litplatformbaapi.repository;

import com.scholastic.litplatformbaapi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
	@Query(nativeQuery = true, value = """
			SELECT student.id AS student_id, student.first_name, student.last_name
			               FROM organization
			               LEFT JOIN org_staff os ON organization.id = os.org_id
			               LEFT JOIN class_section_teacher cst ON os.staff_id = cst.staff_id
			               LEFT JOIN class_section cs ON cst.class_section_id = cs.id
			               LEFT JOIN school_calendars sc ON cs.school_calendar_id = sc.id
			               LEFT JOIN class_section_student css ON cst.class_section_id = css.class_section_id
			               LEFT JOIN person student ON css.student_id = student.id
			               WHERE organization.id = :orgId AND os.staff_id = :staffId
			               AND (:schoolYear IS NULL OR sc.school_year = :schoolYear)
			               AND (coalesce( null, :classIds ) IS NULL OR css.class_section_id IN (:classIds))
			               GROUP BY student_id, student.first_name, student.last_name;
			               """)
	List<Student> findStudents(Integer staffId, Integer orgId, @Param("schoolYear") Integer schoolYear,
							   @Param("classIds") List<Integer> classIds);
}

