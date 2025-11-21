package com.romy.platform.main.common.converter;

import com.romy.platform.main.common.dvo.FileDvo;
import com.romy.platform.main.common.dvo.FileInfoDvo;
import com.romy.platform.main.common.dvo.FilePropDvo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

import static com.romy.platform.main.common.dto.FileDto.*;
import static com.romy.platform.main.community.dto.CommunityAuthDto.AuthRes;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileConverter {

    FileInfoDvo fileDvoToFileInfoDvo(FileDvo dvo);

    VersionUploadRes fileDvoToVersionUploadRes(FileDvo dvo);

    List<UploadRes> fileInfoDvoToUploadResList(List<FileInfoDvo> dvos);

    FileDvo dataToFileDvo(String fileAttCd, String fileNm);

    @Mapping(target = "fileAttCd", source = "dvo.fileAttCd")
    @Mapping(target = "fileNm", source = "dvo.fileNm")
    @Mapping(target = "fileGrp", source = "dvo.fileGrp")
    @Mapping(target = "fileExt", source = "dvo.fileExt")
    @Mapping(target = "folderCd", source = "dvo.folderCd")
    @Mapping(target = "sectorCd", source = "dvo.sectorCd")
    @Mapping(target = "folderPath", source = "dvo.folderPath")
    @Mapping(target = "fileSize", source = "dvo.fileSize")
    @Mapping(target = "regDtt", source = "dvo.regDtt")
    @Mapping(target = "modDtt", source = "dvo.modDtt")
    @Mapping(target = "regUsrNm", source = "dvo.regUsrNm")
    @Mapping(target = "readonlyYn", source = "dvo.readonlyYn")
    @Mapping(target = "securityYn", source = "dvo.securityYn")
    @Mapping(target = "editYn", source = "dvo.editYn")
    PropRes dataToPropRes(FilePropDvo dvo, String menuPath, List<AuthRes> auths);


}
