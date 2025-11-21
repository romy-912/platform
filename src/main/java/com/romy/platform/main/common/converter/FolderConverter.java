package com.romy.platform.main.common.converter;


import com.romy.platform.main.common.dvo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

import static com.romy.platform.main.common.dto.FolderDto.*;
import static com.romy.platform.main.community.dto.CommunityAuthDto.AuthRes;



@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FolderConverter {

    FolderHistoryDvo dataToFolderHistoryDvo(String folderCd, String folderGrp, String folDownGrp);

    FolderUpDownHistDvo dataToFolderUpDownHistDvo(String upRefCd, String udType, String folderGrp, String endType);

    List<TreeAllRes> folderTreeDvoToTreeAllResList(List<FolderTreeDvo> dvos);

    @Mapping(target = "folderType", constant = "2")
    FolderDvo createReqToFolderDvo(CreateReq dto);

    @Mapping(target = "targetCd", source = "fileAttCd")
    @Mapping(target = "targetNm", source = "fileNm")
    @Mapping(target = "div", constant = "FILE")
    @Mapping(target = "location", constant = "EFIF")
    @Mapping(target = "parentCd", source = "fileRefCd")
    @Mapping(target = "rootYn", constant = "N")
    @Mapping(target = "delYn", constant = "N")
    @Mapping(target = "restoreYn", constant = "N")
    TrashDvo fileDvoToTrashDvo(FileDvo dvo);

    @Mapping(target = "targetCd", source = "folderCd")
    @Mapping(target = "targetNm", source = "folderNm")
    @Mapping(target = "div", constant = "FOLDER")
    @Mapping(target = "location", constant = "EF")
    @Mapping(target = "parentCd", source = "parentCd")
    @Mapping(target = "delYn", constant = "N")
    @Mapping(target = "restoreYn", constant = "N")
    TrashDvo folderChildDvoToTrashDvo(FolderChildDvo dvo);

    FilePermissionCondDvo dataToFilePermissionCondDvo(String fileGrp, String writeYn);

    @Mapping(target = "action", constant = "DEL")
    ValidReq removeReqToValidReq(RemoveReq dto);

    @Mapping(target = "folderGrp", source = "dto.folderGrp")
    @Mapping(target = "fileCds", source = "dto.fileCds")
    @Mapping(target = "folderCds", source = "dto.folderCds")
    ValidReq clipboardReqToValidReq(ClipboardReq dto, String action);

    FileCopyDvo dataToFileCopyDvo(String fileAttCd, String newFileAttCd, String newFilePhyPath, String newFileGrp
            , String newFileRefCd, String newPrjCd, String newProcCd);

    @Mapping(target = "folderType", constant = "2")
    FolderDvo dataToFolderDvo(String folderCd, String folderNm, String parentCd, String folderGrp, String sectorCd);

    @Mapping(target = "div", constant = "COPY")
    @Mapping(target = "FCd", source = "fileAttCd")
    @Mapping(target = "FGrp", source = "newFileGrp")
    @Mapping(target = "BGrp", source = "newFileGrp")
    @Mapping(target = "AGrp", source = "newFileGrp")
    @Mapping(target = "afterCd", source = "newFileAttCd")
    CopyHistoryDvo fileCopyDvoToCopyHistoryDvo(FileCopyDvo dvo);

    FolderDvo folderCdToFolderDvo(String folderCd);
    FileAttFolderDvo folderCdToFileAttFolderDvo(String folderCd);

    @Mapping(target = "prjCd", source = "beforePrjCd")
    @Mapping(target = "procCd", source = "beforeProcCd")
    FileDvo copyTemporaryDvoToFileDvo(CopyTemporaryDvo dvo);

    @Mapping(target = "beforeFileGrp", source = "fileGrp")
    @Mapping(target = "beforePrjCd", source = "prjCd")
    @Mapping(target = "beforePrjProcCd", source = "prjProcCd")
    @Mapping(target = "beforeProcCd", source = "procCd")
    @Mapping(target = "beforeParentFolderCd", source = "parentFolderCd")
    CopyTemporaryDvo copyTempToCopyTemporaryDvo(CopyTemp dto);

    FolderDvo dataToFolderDvo(String folderCd, String folderGrp, String sectorCd);

    @Mapping(target = "folderCd", source = "dvo.folderCd")
    @Mapping(target = "folderNm", source = "dvo.folderNm")
    @Mapping(target = "folderGrp", source = "dvo.folderGrp")
    @Mapping(target = "parentCd", source = "dvo.parentCd")
    @Mapping(target = "sectorCd", source = "dvo.sectorCd")
    @Mapping(target = "regDtt", source = "dvo.regDtt")
    @Mapping(target = "modDtt", source = "dvo.modDtt")
    PropRes dataToPropRes(FolderDvo dvo, String menuPath, String folderPath, long fileSize, List<AuthRes> auths);

}
