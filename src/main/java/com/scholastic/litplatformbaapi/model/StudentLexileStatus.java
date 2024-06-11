package com.scholastic.litplatformbaapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StudentLexileStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer studentId;
    private Integer teacherId;
    private String benchmark;
    private Date assignedDate;
    private Date completedDate;
    private BigDecimal lexile;
    private String status;
    private Integer assessmentTest;
    private Date endDate;
    private Long adminAssignmentId;
    private String teacherJudgement;
    private Integer cancelledByUserId;
    private Date modifiedDate;
    private Integer currentQuestion;
    private Long timeSpent;
}
