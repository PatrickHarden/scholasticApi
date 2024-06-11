package com.scholastic.litplatformbaapi.service.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholastic.litplatformbaapi.model.IdAndNameApiModel;
import com.scholastic.litplatformbaapi.model.SchoolCalenderApiModel;
import com.scholastic.litplatformbaapi.model.SectionApiModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DemographicsServiceTest {

    @Mock
    WebClient webClientMock;

    @Mock
    WebClient.RequestHeadersUriSpec uriSpecMock;

    @Mock
    WebClient.RequestHeadersSpec headersSpecMock;

    @Mock
    WebClient.RequestHeadersSpec acceptMock;

    @Mock
    WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    DemographicsServiceImpl serviceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_getSDMSchoolsForDistrict_ReturnAllSchoolsAsObjects() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"ucn\":\"600000\"}");
        IdAndNameApiModel school1 = new IdAndNameApiModel(1,"School1", identifiers);
        IdAndNameApiModel school2 = new IdAndNameApiModel(2,"School2", identifiers);
        IdAndNameApiModel school3 = new IdAndNameApiModel(3,"School3", identifiers);

        List<IdAndNameApiModel> schools = new ArrayList<>();
        schools.add(school1);
        schools.add(school2);
        schools.add(school3);

        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.header(any(), any())).thenReturn(headersSpecMock);
        when(headersSpecMock.headers(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.accept(any())).thenReturn(acceptMock);
        when(acceptMock.retrieve()).thenReturn(responseSpecMock);
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(schools);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                .thenReturn(Mono.just(jsonStr));

        List<IdAndNameApiModel> response = serviceMock.getSDMSchoolsForDistrict(1L);
        assertEquals(1, response.get(0).getId());
        assertEquals("School1", response.get(0).getName());
        assertEquals(2, response.get(1).getId());
        assertEquals("School2", response.get(1).getName());
        assertEquals(3, response.get(2).getId());
        assertEquals("School3", response.get(2).getName());
    }

    @Test
    void test_getSchoolYears() throws Exception {
        SchoolCalenderApiModel schoolCalender1 =
                new SchoolCalenderApiModel("2021-08-01", "2022-07-01", "2021-2022 school year");
        List<SchoolCalenderApiModel> schoolCalenderApiModels = new ArrayList<>();
        schoolCalenderApiModels.add(schoolCalender1);
        
        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.header(any(), any())).thenReturn(headersSpecMock);
        when(headersSpecMock.headers(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.accept(any())).thenReturn(acceptMock);
        when(acceptMock.retrieve()).thenReturn(responseSpecMock);
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(schoolCalenderApiModels);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                .thenReturn(Mono.just(jsonStr));

        List<SchoolCalenderApiModel> response = serviceMock.getSchoolYearsForUser("10","50");
        assertEquals("2021-08-01", response.get(0).getStartDate());
        assertEquals("2022-07-01", response.get(0).getEndDate());
        assertEquals("2021-2022 school year", response.get(0).getDescription());
    }

    @Test
    void test_getSDMSchoolsForDistrict_NoSchools() throws Exception {
        List<IdAndNameApiModel> schools = new ArrayList<>();
        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.header(any(), any())).thenReturn(headersSpecMock);
        when(headersSpecMock.headers(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.accept(any())).thenReturn(acceptMock);
        when(acceptMock.retrieve()).thenReturn(responseSpecMock);
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(schools);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                .thenReturn(Mono.just(jsonStr));

        List<IdAndNameApiModel> response = serviceMock.getSDMSchoolsForDistrict(1L);
        assertEquals(0, response.size());
    }

    @Test
    void test_isSchoolEntitledToProduct_ReturnFalse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"ucn\":\"600000\"}");
        IdAndNameApiModel school1 = new IdAndNameApiModel(1,"School1", identifiers);

        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.header(any(), any())).thenReturn(headersSpecMock);
        when(headersSpecMock.headers(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.accept(any())).thenReturn(acceptMock);
        when(acceptMock.retrieve()).thenReturn(responseSpecMock);
        String jsonStr = "[ ]";
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                .thenReturn(Mono.just(jsonStr));

        boolean response = serviceMock.isSchoolEntitledToProduct(school1);
        assertEquals(false, response);
    }

    @Test
    void test_isSchoolEntitledToProduct_ReturnTrue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"ucn\":\"600000\"}");
        IdAndNameApiModel school1 = new IdAndNameApiModel(1,"School1", identifiers);

        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.header(any(), any())).thenReturn(headersSpecMock);
        when(headersSpecMock.headers(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.accept(any())).thenReturn(acceptMock);
        when(acceptMock.retrieve()).thenReturn(responseSpecMock);
        String jsonStr = "[\n" +
                "    {\n" +
                "        \"subscription\": {\n" +
                "            \"id\": \"50540\",\n" +
                "            \"subscriptionProductId\": \"108\",\n" +
                "            \"startDate\": \"2019-03-21T20:00:00.000-0400\",\n" +
                "            \"expireDate\": \"2022-12-15T19:00:00.000-0500\",\n" +
                "            \"assignment\": {\n" +
                "                \"schoolId\": \"305\"\n" +
                "            },\n" +
                "            \"license\": {\n" +
                "                \"type\": \"teacher\",\n" +
                "                \"quantity\": 100,\n" +
                "                \"remaining\": 98\n" +
                "            },\n" +
                "            \"gracePeriodDays\": 60,\n" +
                "            \"rosterSource\": \"Manual\",\n" +
                "            \"teacherDirected\": false,\n" +
                "            \"orgId\": 305,\n" +
                "            \"expired\": false\n" +
                "        },\n" +
                "        \"product\": {\n" +
                "            \"id\": \"108\",\n" +
                "            \"name\": \"Grade K-8 Full Library\",\n" +
                "            \"productCode\": \"LP_K6_FULL\",\n" +
                "            \"applicationId\": \"48\",\n" +
                "            \"active\": true,\n" +
                "            \"includesStudentAccess\": true,\n" +
                "            \"teacherDirectedSubscriptions\": {\n" +
                "                \"enabled\": false\n" +
                "            },\n" +
                "            \"isOffline\": false\n" +
                "        }\n" +
                "    }\n" +
                "]";
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                .thenReturn(Mono.just(jsonStr));

        boolean response = serviceMock.isSchoolEntitledToProduct(school1);
        assertEquals(true, response);
    }

    @Test
    void test_getSectionsForTeacher() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode identifiers = mapper.readTree("{\"primaryTeacherId\":\"600\"}");
        SectionApiModel section1 =
                new SectionApiModel("1", "20", identifiers,"test", "testname", "1", "k", "k", true, "2021", "test");
        List<SectionApiModel> sectionApiModels = new ArrayList<>();
        sectionApiModels.add(section1);

        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.header(any(), any())).thenReturn(headersSpecMock);
        when(headersSpecMock.headers(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.accept(any())).thenReturn(acceptMock);
        when(acceptMock.retrieve()).thenReturn(responseSpecMock);
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(sectionApiModels);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                .thenReturn(Mono.just(jsonStr));

        List<SectionApiModel> response = serviceMock.getSectionsForTeacher("100");
        assertEquals("1", response.get(0).getId());
        assertEquals("20", response.get(0).getOrganizationId());
        assertEquals("test", response.get(0).getName());
        assertEquals("testname", response.get(0).getNickname());
        assertEquals("1", response.get(0).getPeriod());
        assertEquals("k", response.get(0).getLowGrade());
        assertEquals("k", response.get(0).getHighGrade());
        assertEquals(true, response.get(0).isActive());
        assertEquals("2021", response.get(0).getSchoolYear());
        assertEquals("test", response.get(0).getSchoolCalenderId());
    }
}
