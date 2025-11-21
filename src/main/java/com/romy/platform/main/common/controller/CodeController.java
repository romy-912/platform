package com.romy.platform.main.common.controller;

import static com.romy.platform.main.common.dto.CodeDto.*;
import com.romy.platform.main.common.dvo.CodeDvo;
import com.romy.platform.main.common.service.CodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "공통코드")
@RequestMapping("/common/code")
public class CodeController {

    private final CodeService codeService;


    @GetMapping
    @Operation(summary = "공통코드 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<CodeDvo> getCommonCode(@Parameter(description = "부모코드") @RequestParam(name = "parentCd") String parentCd) {
        return this.codeService.getCommonCode(parentCd);
    }

    @PostMapping("/multiple")
    @Operation(summary = "공통코드 조회 (멀티)")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<MultiRes> getCommonCodeMultiple(@RequestBody @Valid MultiReq dtos) {
        return this.codeService.getCommonCodeMultiple(dtos);
    }

}
