package com.romy.platform.main.common.controller;

import com.romy.platform.main.common.converter.CompressConverter;
import static com.romy.platform.main.common.dto.FileDto.UploadRes;

import static com.romy.platform.main.common.dto.CompressDto.*;

import com.romy.platform.main.common.dvo.FileDvo;
import com.romy.platform.main.common.service.CompressService;
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

import java.io.IOException;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "폴더/파일 압축 관련")
@RequestMapping("/common")
public class CompressController {

    private final CompressService service;
    private final CompressConverter converter;


    @PostMapping("/compress/items")
    @Operation(summary = "폴더/파일 압축")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public UploadRes createCompressFile(@Valid @RequestBody CompressReq dto) throws IOException {

        FileDvo dvo = this.service.createCompressFile(dto);

        return this.converter.fileDvoToUploadRes(dvo);
    }

    @PostMapping("/decompress/zip")
    @Operation(summary = "파일 압축 해제")
    public int createDecompressFile(@Valid @RequestBody DecompressReq dto) throws IOException {
        return this.service.createDecompressFile(dto);
    }

}
