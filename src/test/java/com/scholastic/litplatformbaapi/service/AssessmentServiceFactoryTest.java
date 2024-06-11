package com.scholastic.litplatformbaapi.service;

import com.scholastic.litplatformbaapi.service.support.SrmServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceFactoryTest {
    @Mock
    private SrmServiceImpl serviceImpl;

    @InjectMocks
    AssessmentServiceFactory serviceFactory;

    @Test
    @DisplayName("Incorrect assessment type")
    void getService() {
        assertNull(serviceFactory.getService("random"));
    }

    @Test
    @DisplayName("srm assessment type")
    void getService_srmAssessmentType() {
        var service = serviceFactory.getService("srm");

        assertNotNull(service);
        assertTrue(service instanceof SrmServiceImpl);
    }
}
