package com.romy.platform.main.common.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.main.common.mapper.InGenerateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;


@Service
@RequiredArgsConstructor
public class IdGenerateService {

    private final InGenerateMapper inGenerateMapper;


    /**
     * sequence 값 조회
     */
    @PlatformTransactional(propagation = Propagation.REQUIRES_NEW)
    public String getNextStringId() {
        String nextId = this.inGenerateMapper.selectSeqNextId();
        // NextId 증가
        this.inGenerateMapper.updateSeqNextId();

        return nextId;
    }


}
