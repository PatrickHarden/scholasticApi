package com.scholastic.litplatformbaapi.service.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholastic.litplatformbaapi.model.IdAndNameApiModel;
import com.scholastic.litplatformbaapi.model.SchoolCalenderApiModel;
import com.scholastic.litplatformbaapi.model.SectionApiModel;
import com.scholastic.litplatformbaapi.service.DemographicsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.scholastic.litplatformbaapi.util.Constants.SRM_PRODUCT_CODE;

@Service
public class DemographicsServiceImpl implements DemographicsService {

    @Value("${sdmBaseUrl}")
    public String sdmBaseUrl;

    @Value("${sdmBearerToken}")
    public String sdmBearerToken;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.create(sdmBaseUrl);
    }

    @Override
    public List<IdAndNameApiModel> getSDMSchoolsForDistrict(Long districtId) throws JsonProcessingException {
        Mono<String> sdmSchoolsInDistrictResponse = webClient.get()
                .uri("/composite/staff/0/organizations?parent=" + districtId)
                .headers(h -> h.setBearerAuth(sdmBearerToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        return Arrays.stream(new ObjectMapper().readValue(sdmSchoolsInDistrictResponse.block(), IdAndNameApiModel[].class)).toList();
    }

    @Override
    public boolean isSchoolEntitledToProduct(IdAndNameApiModel school) {
        Mono<String> sdmEntitlementsResponse = webClient.get()
                .uri("/subscriptions/" + school.getIdentifiers().get("ucn").toString().replace("\"", "") + "/lookup-by-ucn?productCode=" + SRM_PRODUCT_CODE)
                .headers(h -> h.setBearerAuth(sdmBearerToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        return sdmEntitlementsResponse.block() != null && !(sdmEntitlementsResponse.block().replace(" ", "").equals("[]"));
    }

    @Override
    public List<SchoolCalenderApiModel> getSchoolYearsForUser(String orgId, String staffId) throws JsonProcessingException {
        Mono<String> sdmSchoolCalenderResponse = webClient.get()
                .uri("/composite/staff/"  + staffId + "/organization/" + orgId + "/school-calendars" )
                .headers(h -> h.setBearerAuth(sdmBearerToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        
        return Arrays.stream(new ObjectMapper().readValue(sdmSchoolCalenderResponse.block(), SchoolCalenderApiModel[].class)).toList();
    }

    @Override
    public List<SectionApiModel> getSectionsForTeacher(String staffId) throws JsonProcessingException {
        Mono<String> sdmSchoolCalenderResponse = webClient.get()
                .uri("/roster/sections?staffId="  + staffId )
                .headers(h -> h.setBearerAuth(sdmBearerToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        return Arrays.stream(new ObjectMapper()
                        .readValue(sdmSchoolCalenderResponse.block(), SectionApiModel[].class)).filter(sectionApiModel
                        -> sectionApiModel.isActive()).collect(Collectors.toList());

    }
}
