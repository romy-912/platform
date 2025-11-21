package com.romy.platform.main.common.dto;

import com.romy.platform.main.common.dvo.CodeDvo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public class CodeDto {

    @Schema(name = "CodeDto.MultiReq", description = "공통코드 멀티 조건")
    public record MultiReq(
            @NotEmpty
            @Schema(description = "부모코드 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
            List<String> parentCds
    ){}

    @Schema(name = "CodeDto.MultiRes", description = "공통코드 멀티 조회")
    public record MultiRes(
            @Schema(description = "부모코드")
            String parentCd,
            @Schema(description = "코드 리스트")
            List<CodeDvo> codes
    ){}

}
