package com.scholastic.litplatformbaapi.model;

import lombok.Getter;

@Getter
public enum LexileStatus {
	ASSIGNED("ASSIGNED"), IN_PROGRESS("IN PROGRESS"), COMPLETED("COMPLETED"), PRACTICE("PRACTICE"),
	CANCELED("CANCELED");

	private String status;

	LexileStatus(String status) {
		this.status = status;
	}
}
