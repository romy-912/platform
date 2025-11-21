package com.romy.platform.main.community.controller;

import static com.romy.platform.main.community.dto.CommunityOfficialFormDto.*;
import com.romy.platform.main.community.service.CommunityOfficialFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 행정양식")
@RequestMapping("/community/official-form")
public class CommunityOfficialFormController {

    private final CommunityOfficialFormService service;


    @GetMapping("/categories")
    @Operation(summary = "커뮤니티 세부 카테고리 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<CategoryRes> getOfficialFormCategory() {
        return this.service.getOfficialFormCategory();
    }


    @GetMapping("/{ctgCd}")
    @Operation(summary = "커뮤니티 행정양식 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<SearchRes> getOfficialForms(@PathVariable @Parameter(description = "카테고리코드") String ctgCd) {

        return this.service.getOfficialForms(ctgCd);
    }
}
