package com.romy.platform.main.common.controller;

import static com.romy.platform.main.common.dto.GroupwareMailDto.*;
import com.romy.platform.main.common.service.GroupwareMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "그룹웨어 메일 관련")
@RequestMapping("/common/groupware/mail")
public class GroupWareMailController {


    private final GroupwareMailService service;

    @PostMapping("/permission")
    @Operation(summary = "그룹웨어 메일 발송 파일 권한 체크")
    public void validGroupwareMailPermission(@Valid @RequestBody MailCheckReq dto) {
        this.service.validGroupwareMailPermission(dto.fileCds(), dto.fileGrp());
    }

    @PostMapping
    @Operation(summary = "그룹웨어 메일 발송")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public MailRes getGroupwareMailSend(@Valid @RequestBody MailReq dto) {
        return this.service.getGroupwareMailSend(dto);
    }


}
