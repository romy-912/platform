package com.romy.platform.sub.test.service;

import com.romy.platform.annotation.MultiTransactional;

import com.romy.platform.sub.test.mapper.TestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TestService {

    private final TestMapper mapper;


    @MultiTransactional
    public void saveTest() {

        this.mapper.insertTest();

        throw new RuntimeException("test");

    }


}
