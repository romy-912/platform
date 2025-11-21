package com.romy.platform.main.common.converter;

import static com.romy.platform.main.common.dto.CodeDto.*;
import com.romy.platform.main.common.dvo.CodeDvo;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CodeConverter {

    MultiRes dataToMultiRes(String parentCd, List<CodeDvo> codes);

}
