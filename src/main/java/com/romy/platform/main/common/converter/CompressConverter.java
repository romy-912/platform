package com.romy.platform.main.common.converter;

import static com.romy.platform.main.common.dto.CompressDto.*;
import static com.romy.platform.main.common.dto.FileDto.SearchRes;

import com.romy.platform.common.constants.PlatformConstant;
import static com.romy.platform.main.common.dto.FileDto.UploadRes;

import static com.romy.platform.main.common.dto.FolderDto.ValidReq;


import static com.romy.platform.main.common.dvo.FolderFileSetDvo.FileInfo;
import static com.romy.platform.main.common.dvo.FolderFileSetDvo.FolderInfo;

import com.romy.platform.main.common.dvo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompressConverter {

    @Mapping(target = "fileNm", expression = "java(\"N\".equals(dto.folderYn()) ? dto.fileNm() : null)")
    CompressDvo targetFileToCompressDvo(TargetFile dto);

    CompressDvo dataToCompressDvo(String folderYn, String folderPath);

    default TargetFile searchResToTargetFile(SearchRes dto, TargetFile file) {
        return new TargetFile(dto.folderYn(), dto.fileNm(), dto.fileAttCd(), dto.fileGrp(), file.prjCd(), file.procCd());
    }
    default List<TargetFile> searchResToTargetFileList(List<SearchRes> dtos, TargetFile file) {
        if (CollectionUtils.isEmpty(dtos)) return null;
        return dtos.stream().map(dto -> this.searchResToTargetFile(dto, file)).toList();
    }

    FileAttFolderLocDvo dataToFileAttFolderLocDvo(String fileAttCd, String folderCd);

    UploadRes fileDvoToUploadRes(FileDvo dvo);

    @Mapping(target = "action", constant = "COPY")
    ValidReq dataToValidReq(String folderGrp, List<String> fileCds, List<String> folderCds);

    @Mapping(target = "fileNm", source = "fileName")
    @Mapping(target = "fileDlCnt", constant = "0")
    @Mapping(target = "delYn", constant = PlatformConstant.YN_N)
    @Mapping(target = "fileFinalVerYn", constant = PlatformConstant.YN_Y)
    FileDvo fileInfoToFileDvo(FileInfo dvo);

    @Mapping(target = "folderNm", source = "folderName")
    @Mapping(target = "folderType", constant = "2")
    FolderDvo folderInfoToFolderDvo(FolderInfo dvo);

    @Mapping(target = "folderNm", source = "folderName")
    FileAttFolderDvo folderInfoToFileAttFolderDvo(FolderInfo dvo);

}
