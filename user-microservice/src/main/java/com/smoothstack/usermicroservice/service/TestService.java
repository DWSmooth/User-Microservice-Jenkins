package com.smoothstack.usermicroservice.service;

import com.smoothstack.common.services.CommonLibraryTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    CommonLibraryTestingService commonLibraryTestingService;

    /**
     * Populates the database with the data found in the common library
     */
    public void loadTestData() {
        commonLibraryTestingService.createTestData();
    }
}
