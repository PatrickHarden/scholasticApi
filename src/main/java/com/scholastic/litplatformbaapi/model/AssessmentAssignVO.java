package com.scholastic.litplatformbaapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AssessmentAssignVO {

	@NotBlank(message = "studentId should not be blank")
    private String studentId;

	@NotBlank(message = "benchmark should not be blank")
    private String benchmark;

    private String teacherAppraisal;

	@NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "America/New_York")
	private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "America/New_York")
    private Date endDate;

    public String getBenchmark() {
        if (benchmark != null)
            benchmark = benchmark.toLowerCase();
        return benchmark;
    }
}
