package com.scholastic.litplatformbaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class SchoolCalenderApiModel {
    private String startDate;
    private String endDate;
    private String description;
}
