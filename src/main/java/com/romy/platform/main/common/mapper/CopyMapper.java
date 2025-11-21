package com.romy.platform.main.common.mapper;

import com.romy.platform.main.common.dvo.CopyHistoryDvo;
import com.romy.platform.main.common.dvo.CopyTemporaryDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CopyMapper {

    List<CopyTemporaryDvo> selectCopyTemporary(@Param("usrCd") String usrCd, @Param("div") String div
            , @Param("fileGrp") String fileGrp);

    void insertCopyHistory(CopyHistoryDvo dvo);

    void insertCopyHistoryForFileCut(@Param("list") List<String> fileCds, @Param("newFileGrp") String fileGrp);

    void deleteCopyTemporary(@Param("usrCd") String usrCd);

    void insertCopyTemporarys(@Param("list") List<CopyTemporaryDvo> dvos);

}
