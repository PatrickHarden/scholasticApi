package com.scholastic.litplatformbaapi.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Assessment {
    private SrmStatus srmStatus;
}
