package com.romy.platform.main.common.service;

import com.romy.platform.main.common.mapper.FileGrpMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileGrpService {

    private final FileGrpMapper mapper;

    /**
     * 파일그룹에 해당하는 메뉴 경로 조회
     */
    public String getMenuPathByFileGrp(String fileGrp) {
        return this.mapper.selectMenuPathByFileGrp(fileGrp);
    }

}
