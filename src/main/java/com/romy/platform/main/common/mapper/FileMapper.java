package com.romy.platform.main.common.mapper;


import com.romy.platform.main.common.dvo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import static com.romy.platform.main.common.dto.FileDto.*;


@Mapper
public interface FileMapper {

    List<FileDvo> selectFilesByFileRefCd(@Param("fileGrp") String fileGrp, @Param("fileRefCd") String fileRefCd, @Param("finalYn") String finalYn);
    FileDvo selectFileInfo(@Param("fileAttCd") String fileAttCd);

    void deleteFilesByFileRefCd(@Param("fileGrp") String fileGrp, @Param("fileRefCd") String fileRefCd);

    int insertFile(FileDvo dvo);

    int updateFileRefCd(@Param("fileAttCd") String fileAttCd, @Param("fileRefCd") String fileRefCd, @Param("fileGrp") String fileGrp, @Param("prjCd") String prjCd, @Param("procCd") String procCd);

    int deleteFile(@Param("fileAttCd") String fileAttCd);

    FileDownloadDvo selectFileInfoForDownload(@Param("fileAttCd") String fileAttCd);

    void insertFileDownloadHistory(@Param("fileAttCd") String fileAttCd, @Param("fileSchDiv") String fileSchDiv);

    void insertFileAttFwRequest(@Param("fileAttCd") String fileAttCd, @Param("fwRequest") String fwRequest, @Param("usrCd") String usrCd);

    void updateFileAttInfo(FileDvo dvo);
    void updateFileAttOnlyField(FileDvo dvo);

    void insertFileByCopy(FileCopyDvo dvo);

    void insertFileAttFolderLoc(FileAttFolderLocDvo dvo);
    void insertFileAttFolderLoc(@Param("fileAttCd") String fileAttCd, @Param("folderCd") String folderCd);

    List<String> selectNoPermissionFiles(FilePermissionCondDvo dvo);

    List<FileDvo> selectFileVersions(FileDvo dvo);
    void deleteFilesOnlyStatus(@Param("list") List<String> fileCds);

    List<SearchRes> selectFolderFilesByFolderCd(SearchReq dto);

    List<SearchRes> selectFolderFilesByFolderCd(@Param("folderGrp") String folderGrp, @Param("folderCd") String folderCd);

    FilePropDvo selectFileProperties(@Param("fileAttCd") String fileAttCd);

    List<GroupwareMailFileDvo> selectFilesForGwMail(@Param("list") List<String> fileCds);

    List<VersionRes> selectFileVersionsForMng(@Param("fileAttCd") String fileAttCd);

    String selectFilePermanent(@Param("fileAttCd") String fileAttCd);

    FileInfoRes selectFileInfoForAgent(@Param("fileAttCd") String fileAttCd);

    String selectFolderPathByFile(@Param("fileRefCd") String fileRefCd);

}
