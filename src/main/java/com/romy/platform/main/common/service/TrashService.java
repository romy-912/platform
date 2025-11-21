package com.romy.platform.main.common.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.main.common.dvo.TrashDvo;
import com.romy.platform.main.common.mapper.TrashMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TrashService {

    private final TrashMapper mapper;

    /**
     * 휴지통 추가
     */
    @PlatformTransactional
    public void insertTrashs(List<TrashDvo> dvos) {
        int batchSize = 100; // 100개씩 잘라서 넣기

        for (int i = 0; i < dvos.size(); i += batchSize) {
            int end = Math.min(i + batchSize, dvos.size());
            this.mapper.insertTrashs(dvos.subList(i, end));
        }

    }
}
