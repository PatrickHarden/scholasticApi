package com.scholastic.litplatformbaapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentsQueryParams {
    private Integer schoolYear;
    private List<Integer> classIds;
    private Date startDate;
    private Date endDate;
    private List<String> benchmark;
    private List<String> status;
}
