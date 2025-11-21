package com.romy.platform.main.common.converter;

import com.romy.platform.main.common.dvo.FilePermissionCondDvo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupwareMailConverter {

    FilePermissionCondDvo dataToFilePermissionCondDvo(String fileGrp, String writeYn);
}
