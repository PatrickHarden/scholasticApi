package com.scholastic.litplatformbaapi.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Assessment {
	@Id
	@Column(name = "id")
	private Integer assessmentId;

	@Column(name = "student_id")
	private Integer studentId;

	@Column(name = "benchmark")
	private String benchmark;

	@Column(name = "assigned_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "lexile")
	private BigDecimal lexile;

	@Column(name = "proficiency")
	private String proficiency;

	@Column(name = "status")
	private String status;

	@Column(name = "current_question")
	private Integer currentQuestion;

	@Column(name = "time_spent")
	private Long timeSpent;

	@Column(name = "teacher_first_name")
	private String assignedByFirstName;

	@Column(name = "teacher_last_name")
	private String assignedByLastName;

	@Column(name = "teacher_id")
	private Integer assignedById;

	@Column(name = "teacher_judgement")
	private String teacherJudgement;
}
