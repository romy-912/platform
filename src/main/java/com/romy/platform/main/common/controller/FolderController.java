package com.romy.platform.main.common.controller;


import static com.romy.platform.main.common.dto.FolderDto.*;


import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;

import com.romy.platform.main.common.converter.FolderConverter;
import com.romy.platform.main.common.dvo.FolderDvo;
import com.romy.platform.main.common.dvo.FolderTreeDvo;
import com.romy.platform.main.common.service.FolderService;
import com.romy.platform.main.community.service.CommunityAuthService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "폴더 관련")
@RequestMapping("/common/folder")
public class FolderController {

    private final FolderService folderService;
    private final FolderConverter converter;

    private final CommunityAuthService commuAuthService;



    @CrossOrigin
    @PostMapping("/download-history")
    @Operation(summary = "폴더 다운로드 이력생성")
    public int createFolderDownloadHistory(@RequestBody @Valid CreateDownReq dto) {
        return this.folderService.createFolderDownloadHistory(dto);
    }

    @CrossOrigin
    @PostMapping("/upload-history")
    @Operation(summary = "폴더 업로드 이력생성")
    public int createFolderUploadHistory(@RequestBody @Valid CreateUpReq dto) {
        return this.folderService.createFolderUploadHistory(dto);
    }

    @CrossOrigin
    @PutMapping("/history/result")
    @Operation(summary = "폴더 업/다운로드 이력 결과 업데이트")
    public int updateFolderDownHisotry(@RequestBody @Valid UpdateHistReq dto) {
        return this.folderService.updateFolderUpDownHistory(dto);
    }

    @GetMapping("/trees")
    @Operation(summary = "폴더 트리구조 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<TreeAllRes> getFolderAllTrees(@Parameter(description = "부문/사용자코드", required = true) @RequestParam(name = "sectorCd") String sectorCd
            , @Parameter(description = "폴더그룹코드", required = true) @RequestParam(name = "folderGrp") String folderGrp) {

        // 권한체크
        this.commuAuthService.checkCommuFolderFileAuth(folderGrp, sectorCd, null);

        // 폴더명 중복 업데이트
        this.folderService.updateDupFolderName(folderGrp, sectorCd);

        List<FolderTreeDvo> dvos = this.folderService.getFolderAllTrees(folderGrp, sectorCd);

        return this.converter.folderTreeDvoToTreeAllResList(dvos);
    }

    @PostMapping("/child")
    @Operation(summary = "하위 폴더 생성")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public CreateRes createChildFolder(@Valid @RequestBody CreateReq dto) {

        FolderDvo dvo = this.converter.createReqToFolderDvo(dto);

        return new CreateRes(this.folderService.createChildFolder(dvo));
    }

    @PostMapping("/validation")
    @Operation(summary = "폴더/파일 유효성 체크 (삭제, 복사, 잘라내기)")
    public String checkFolderValidation(@RequestBody @Valid ValidReq dto) {
        List<String> fileCds = dto.fileCds();
        List<String> folderCds = dto.folderCds();

        if (CollectionUtils.isEmpty(fileCds) && CollectionUtils.isEmpty(folderCds)) {
            // 파일 코드와 폴더 코드 중 적어도 하나는 반드시 입력해야 합니다.
            throw new PlatformException(PlatformConstant.COMMON_FILEORFOLDER_REQUIRED);
        }

        this.folderService.checkFolderValidation(dto);

        return PlatformConstant.YN_Y;
    }

    @DeleteMapping("/items")
    @Operation(summary = "폴더/파일 삭제")
    public int removeFolderAndChild(@RequestBody @Valid RemoveReq dto) {
        List<String> fileCds = dto.fileCds();
        List<String> folderCds = dto.folderCds();

        if (CollectionUtils.isEmpty(fileCds) && CollectionUtils.isEmpty(folderCds)) {
            // 파일 코드와 폴더 코드 중 적어도 하나는 반드시 입력해야 합니다.
            throw new PlatformException(PlatformConstant.COMMON_FILEORFOLDER_REQUIRED);
        }

        return this.folderService.removeFolderAndChild(dto);
    }

    @PostMapping("/copy-paste")
    @Operation(summary = "폴더/파일 복사 붙여넣기")
    public int createCopyPaste(@RequestBody @Valid ClipboardReq dto) throws IOException {
        List<String> fileCds = dto.fileCds();
        List<String> folderCds = dto.folderCds();

        if (CollectionUtils.isEmpty(fileCds) && CollectionUtils.isEmpty(folderCds)) {
            // 파일 코드와 폴더 코드 중 적어도 하나는 반드시 입력해야 합니다.
            throw new PlatformException(PlatformConstant.COMMON_FILEORFOLDER_REQUIRED);
        }

        return this.folderService.createCopyCutPaste(dto, "COPY");
    }

    @PostMapping("/cut-paste")
    @Operation(summary = "폴더/파일 잘라내기 붙여넣기")
    public int createCutPaste(@RequestBody @Valid ClipboardReq dto) throws IOException {
        List<String> fileCds = dto.fileCds();
        List<String> folderCds = dto.folderCds();

        if (CollectionUtils.isEmpty(fileCds) && CollectionUtils.isEmpty(folderCds)) {
            // 파일 코드와 폴더 코드 중 적어도 하나는 반드시 입력해야 합니다.
            throw new PlatformException(PlatformConstant.COMMON_FILEORFOLDER_REQUIRED);
        }
        
        return this.folderService.createCopyCutPaste(dto, "CUT");
    }

    @PostMapping("/copy")
    @Operation(summary = "폴더/파일 복사")
    public int createClipboardCopy(@RequestBody @Valid CopyCutReq dto) {
        return this.folderService.createCopyDatas(dto, "COPY");
    }

    @PostMapping("/cut")
    @Operation(summary = "폴더/파일 잘라내기")
    public int createClipboardCut(@RequestBody @Valid CopyCutReq dto) {
        return this.folderService.createCopyDatas(dto, "CUT");
    }

    @PostMapping("/paste")
    @Operation(summary = "폴더/파일 붙여넣기")
    public int createClipboardPaste(@RequestBody @Valid PasteReq dto) throws IOException {
        return this.folderService.createPaste(dto);
    }

    @GetMapping("/{folderCd}/properties")
    @Operation(summary = "폴더 속성 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public PropRes getFolderProperties(@PathVariable @Parameter(description = "폴더코드") String folderCd
            , @Parameter(description = "메뉴타입", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PRJ", "CAB"}))
                                        @RequestParam(name = "menuType") String menuType) {

        return this.folderService.getFolderProperties(folderCd, menuType);
    }

    @GetMapping("/{folderCd}/size")
    @Operation(summary = "폴더 사이즈 조회")
    public long getFolderSize(@PathVariable @Parameter(description = "폴더코드") String folderCd) {
        return this.folderService.getFolderSize(folderCd);
    }




}
