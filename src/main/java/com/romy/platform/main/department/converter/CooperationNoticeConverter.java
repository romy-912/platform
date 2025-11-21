package com.romy.platform.main.department.converter;

import com.romy.platform.main.common.dvo.FileCopyDvo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CooperationNoticeConverter {

    FileCopyDvo dataToFileCopyDvo(String fileAttCd, String newFileAttCd, String newFilePhyPath, String newFileGrp
            , String newFileRefCd);

}
