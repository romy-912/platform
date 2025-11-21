package com.romy.platform.main.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public class GroupwareMailDto {

    @Schema(name = "GroupwareMailDto.MailCheckReq", description = "그룹웨어 메일발송 파일 체크")
    public record MailCheckReq(
            @NotEmpty
            @Schema(description = "파일코드 리스트")
            List<String> fileCds,
            @NotBlank
            @Schema(description = "파일그룹코드")
            String fileGrp
    ) {}

    @Schema(name = "GroupwareMailDto.MailReq", description = "그룹웨어 메일발송")
    public record MailReq(
            @NotBlank
            @Schema(description = "암호화된 사번")
            String user,
            @NotEmpty
            @Schema(description = "파일코드 리스트")
            List<String> fileCds
    ) {}

    @Schema(name = "GroupwareMailDto.MailRes", description = "그룹웨어 메일발송 결과")
    public record MailRes(
            @Schema(description = "결과 URL")
            String resultUrl
    ) {}

}
