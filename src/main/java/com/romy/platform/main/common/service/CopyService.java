package com.romy.platform.main.common.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.main.common.dvo.CopyHistoryDvo;
import com.romy.platform.main.common.dvo.CopyTemporaryDvo;
import com.romy.platform.main.common.mapper.CopyMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CopyService {

    private final CopyMapper mapper;


    /**
     * 복사 객체 정보 조회
     */
    public List<CopyTemporaryDvo> getCopyTemporary(String usrCd, String div, String fileGrp) {
        return this.mapper.selectCopyTemporary(usrCd, div, fileGrp);
    }

    /**
     * 복사 개체 이력 생성
     */
    @PlatformTransactional
    public void createCopyHistory(CopyHistoryDvo dvo) {
        this.mapper.insertCopyHistory(dvo);
    }

    /**
     * 파일 잘라내기 이력 생성
     */
    @PlatformTransactional
    public void createCopyHistoryForFileCut(List<String> fileCds, String newFileGrp) {
        if (CollectionUtils.isEmpty(fileCds)) return;

        this.mapper.insertCopyHistoryForFileCut(fileCds, newFileGrp);
    }

    /**
     * 복사개체 삭제
     */
    @PlatformTransactional
    public void removeCopyTemporary(String usrCd) {
        this.mapper.deleteCopyTemporary(usrCd);
    }

    /**
     * 복사 개체 추가
     */
    @PlatformTransactional
    public void createCopyTemporarys(List<CopyTemporaryDvo> dvos) {
        this.mapper.insertCopyTemporarys(dvos);
    }


}
