package com.scholastic.litplatformbaapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholastic.litplatformbaapi.model.IdAndNameApiModel;
import com.scholastic.litplatformbaapi.model.SchoolCalenderApiModel;
import com.scholastic.litplatformbaapi.model.SectionApiModel;
import com.scholastic.litplatformbaapi.service.DemographicsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DemographicsControllerTest {

    @Mock
    DemographicsService demographicsService;

    @InjectMocks
    DemographicsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetSchoolsForDistrict_NoSchools() throws JsonProcessingException {
        when(demographicsService.getSDMSchoolsForDistrict(any())).thenReturn(new ArrayList<>());
        assertEquals(0, controller.getSchoolsForDistrict(1L).size());
    }

    @Test
    void testGetSchoolsForDistrict_WithSchoolsAllEntitled() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"ucn\":\"600000\"}");
        IdAndNameApiModel school1 = new IdAndNameApiModel(1,"School1", identifiers);
        IdAndNameApiModel school2 = new IdAndNameApiModel(2,"School2", identifiers);
        IdAndNameApiModel school3 = new IdAndNameApiModel(3,"School3", identifiers);

        List<IdAndNameApiModel> schools = new ArrayList<>();
        schools.add(school1);
        schools.add(school2);
        schools.add(school3);

        when(demographicsService.isSchoolEntitledToProduct(any())).thenReturn(true);
        when(demographicsService.getSDMSchoolsForDistrict(any())).thenReturn(schools);
        assertEquals(3, controller.getSchoolsForDistrict(1L).size());
    }

    @Test
    void testGetSchoolsForDistrict_WithSchoolsNotEntitled() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"ucn\":\"600000\"}");
        IdAndNameApiModel school1 = new IdAndNameApiModel(1,"School1", identifiers);
        IdAndNameApiModel school2 = new IdAndNameApiModel(2,"School2", identifiers);
        IdAndNameApiModel school3 = new IdAndNameApiModel(3,"School3", identifiers);

        List<IdAndNameApiModel> schools = new ArrayList<>();
        schools.add(school1);
        schools.add(school2);
        schools.add(school3);

        when(demographicsService.isSchoolEntitledToProduct(school1)).thenReturn(true);
        when(demographicsService.isSchoolEntitledToProduct(school2)).thenReturn(false);
        when(demographicsService.isSchoolEntitledToProduct(school3)).thenReturn(true);
        when(demographicsService.getSDMSchoolsForDistrict(any())).thenReturn(schools);
        assertEquals(2, controller.getSchoolsForDistrict(1L).size());
    }

    @Test
    void testGetSchoolYearsForUser() throws JsonProcessingException {
        SchoolCalenderApiModel schoolCalender1 =
                new SchoolCalenderApiModel("2021-08-01", "2022-07-01", "2021-2022 school year");
        List<SchoolCalenderApiModel> schoolCalenderApiModels = new ArrayList<>();
        schoolCalenderApiModels.add(schoolCalender1);

        when(demographicsService.getSchoolYearsForUser(any(String.class), any(String.class)))
                .thenReturn(schoolCalenderApiModels);

        assertEquals(1, controller.getSchoolYears("7925", "200000").size());

    }

    @Test
    void testGetSectionsForTeacher() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"primaryTeacherId\":\"600\"}");
        SectionApiModel section1 =
                new SectionApiModel("1", "20", identifiers,"300", "testname", "1", "k", "k", true, "2021", "test");
        List<SectionApiModel> sectionApiModels = new ArrayList<>();
        sectionApiModels.add(section1);
        when(demographicsService.getSectionsForTeacher(any(String.class))).thenReturn(sectionApiModels);

        assertEquals(1, controller.getSectionsForTeacher("1").size());

    }
}
