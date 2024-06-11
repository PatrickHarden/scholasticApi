package com.scholastic.litplatformbaapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AssessmentResponse {
	private Integer assessmentId;
    private String benchmark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "America/New_York")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "America/New_York")
	private Date endDate;
    private BigDecimal lexileValue;
    private String status;
    private Integer currentQuestion;
    private String proficiency;
    private Long timeSpent;
    private String assignedByFirstName;
    private String assignedByLastName;
    private Integer assignedById;
    private String teacherJudgment;
}
