package com.scholastic.litplatformbaapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholastic.litplatformbaapi.model.IdAndNameApiModel;
import com.scholastic.litplatformbaapi.model.SchoolCalenderApiModel;
import com.scholastic.litplatformbaapi.model.SectionApiModel;
import com.scholastic.litplatformbaapi.service.DemographicsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/demographics", produces = MediaType.APPLICATION_JSON_VALUE)
public class DemographicsController {

    @Autowired
    DemographicsService demographicsService;

    @Operation(summary = "Returns a list of schools for a given organization in a school year",
            description = "Returns a list of schools for a given organization in a school year")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @GetMapping(value = "{districtId}/schools", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IdAndNameApiModel> getSchoolsForDistrict(@PathVariable("districtId") Long districtId) throws JsonProcessingException {
        List<IdAndNameApiModel> listOfSchoolsInDistrict = demographicsService.getSDMSchoolsForDistrict(districtId);
        return listOfSchoolsInDistrict.stream()
                .filter(school -> demographicsService.isSchoolEntitledToProduct(school))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Returns a list of school years for a given organization",
            description = "Returns a list of school years for a given organization")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @GetMapping(value = "organization/{orgId}/staff/{staffId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SchoolCalenderApiModel> getSchoolYears(@PathVariable("orgId") String orgId, @PathVariable("staffId") String staffId) throws JsonProcessingException {
        return demographicsService.getSchoolYearsForUser(orgId, staffId);
    }

    @Operation(summary = "Returns a list of sections for a given teacher",
            description = "Returns a list of sections for a given teacher")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @GetMapping(value = "sections/{staffId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SectionApiModel> getSectionsForTeacher(@PathVariable("staffId") String staffId) throws JsonProcessingException {
        return demographicsService.getSectionsForTeacher(staffId);
    }



}
