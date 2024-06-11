package com.scholastic.litplatformbaapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholastic.litplatformbaapi.model.IdAndNameApiModel;
import com.scholastic.litplatformbaapi.model.SchoolCalenderApiModel;
import com.scholastic.litplatformbaapi.model.SectionApiModel;

import java.util.List;

public interface DemographicsService {
    List<IdAndNameApiModel> getSDMSchoolsForDistrict(Long districtId) throws JsonProcessingException;
    boolean isSchoolEntitledToProduct(IdAndNameApiModel school);
    List<SchoolCalenderApiModel> getSchoolYearsForUser(String orgId, String staffId) throws JsonProcessingException;
    List<SectionApiModel> getSectionsForTeacher(String staffId) throws JsonProcessingException;
}
