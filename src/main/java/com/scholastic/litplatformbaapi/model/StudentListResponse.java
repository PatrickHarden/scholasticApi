package com.scholastic.litplatformbaapi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StudentListResponse {
	private List<StudentResponse> results;
}
