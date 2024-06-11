package com.scholastic.litplatformbaapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Getter
@Setter
public class AssessmentAnswerVO {

    @NotBlank(message = "id should not be blank")
    private String id;

    private String studentId;

    private Long timeSpent;

    private Integer currentQuestion;

    private String status;

    private BigDecimal lexile;

}
