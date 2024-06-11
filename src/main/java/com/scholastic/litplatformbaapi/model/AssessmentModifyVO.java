package com.scholastic.litplatformbaapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
public class AssessmentModifyVO {

    @NotBlank(message = "studentId should not be blank")
    private String studentId;

    @NotBlank(message = "id should not be blank")
    private String id;

    private String benchmark;

    private String teacherAppraisal;

    @NotBlank(message = "action should not be blank")
    private String action;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "America/New_York")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "America/New_York")
    private Date endDate;

    public Integer getStudentId(){
        return Integer.parseInt(studentId);
    }
    public Integer getId(){
        return Integer.parseInt(id);
    }
}
