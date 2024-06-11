package com.scholastic.litplatformbaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class SectionApiModel {
    private String id;
    private String organizationId;
    private JsonNode staff;
    private String name;
    private String nickname;
    private String period;
    private String lowGrade;
    private String highGrade;
    private boolean active;
    private String schoolYear;
    private String schoolCalenderId;
}
