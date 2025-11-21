package com.romy.platform.main.community.controller;

import static com.romy.platform.main.community.dto.CommunityNoticeDto.*;


import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.ValidationException;
import com.romy.platform.main.community.service.CommunityNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 공지사항")
@RequestMapping("/community/notice")
public class CommunityNoticeController {

    private final CommunityNoticeService noticeService;

    @GetMapping
    @Operation(summary = "공지사항 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<SearchRes> getCommunityNotices(@Valid @ParameterObject SearchReq dto) {
        return this.noticeService.getCommunityNotices(dto);
    }

    @GetMapping("/scopes")
    @Operation(summary = "공지사항 공개범위 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<ScopeRes> getNoticeScopes(@Parameter(description = "게시판코드") @RequestParam(name = "brdCd", required = false) String brdCd) {
        return this.noticeService.getNoticeScopes(brdCd);
    }

    @GetMapping("/detail")
    @Operation(summary = "공지사항 상세 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public DetailRes getNoticeDetail(@Parameter(description = "게시판코드") @RequestParam(name = "brdCd") String brdCd) {

        return this.noticeService.getNoticeDetail(brdCd);
    }

    @PostMapping("/{brdCd}/view")
    @Operation(summary = "공지사항 열람 처리")
    public int createNoticeViewHistory(@PathVariable @Parameter(description = "게시판코드") String brdCd) {
        return  this.noticeService.createNoticeViewHistory(brdCd);
    }

    @DeleteMapping("/{brdCd}")
    @Operation(summary = "공지사항 삭제")
    public int removeCommunityNotice(@PathVariable @Parameter(description = "게시판코드") String brdCd) {
        return this.noticeService.removeCommunityNotice(brdCd);
    }

    @PostMapping
    @Operation(summary = "공지사항 저장")
    public SaveRes saveCommunityNotice(@RequestBody @Valid SaveReq dto) throws IOException {

        // 공지팝업여부
        String  mainNoticeYn = dto.mainNoticeYn();
        if (PlatformConstant.YN_Y.equals(mainNoticeYn)) {
            // 공지 팝업에 대한 데이터 유효성 체크
            if (dto.noticePopupWidthSize() == 0 || dto.noticePopupHeightSize() == 0) {

                // 팝업크기는 필수 입력 항목입니다.
                throw new ValidationException(PlatformConstant.COMMU_NOTICE_POPUP_LOCATION);

            } else if(StringUtils.isBlank(dto.noticeStDt()) || StringUtils.isBlank(dto.noticeEdDt())) {
                // 공지팝업 기간은 필수 입력 항목입니다.
                throw new ValidationException(PlatformConstant.COMMU_NOTICE_POPUP_PERIOD);

            } else if (dto.noticePopupWidthSize() > 1600 || dto.noticePopupHeightSize() > 1200) {

                // 팝업의 너비(Width) 최대는 1600, 높이(Height) 최대는 1200 입니다.
                throw new ValidationException(PlatformConstant.COMMU_NOTICE_POPUP_SIZE);
            }
        }

        return this.noticeService.saveCommunityNotice(dto);
    }

    @GetMapping("/{brdCd}/readers")
    @Operation(summary = "공지사항 열람자 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<ReaderRes> getNoticeReaders(@PathVariable @Parameter(description = "게시판코드") String brdCd) {
        return this.noticeService.getNoticeReaders(brdCd);
    }

    @PostMapping("/{brdCd}/favorite")
    @Operation(summary = "공지사항 즐겨찾기 등록")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public FavoriteRes createNoticeFavorite(@PathVariable @Parameter(description = "게시판코드") String brdCd) {
        return this.noticeService.createNoticeFavorite(brdCd);
    }

    @GetMapping("/{brdCd}/training-user")
    @Operation(summary = "교육 대상자 조회")
    @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    public List<TrnUserRes> getTrainingUsers(@PathVariable @Parameter(description = "게시판코드") String brdCd) {
        return this.noticeService.getTrainingUsers(brdCd);
    }

    @PostMapping("/{brdCd}/training-user")
    @Operation(summary = "교육 대상자 등록")
    public int createTrainingUsers(@PathVariable @Parameter(description = "게시판코드") String brdCd
            , @RequestBody @Valid SaveUserReq dto) {
        return this.noticeService.createTrainingUsers(brdCd, dto.trnUsers());
    }

    @DeleteMapping("/{brdCd}/training-user")
    @Operation(summary = "교육 대상자 삭제")
    public int removeTrainingUsers(@PathVariable @Parameter(description = "게시판코드") String brdCd
            , @RequestBody @Valid SaveUserReq dto) {
        return this.noticeService.removeTrainingUsers(brdCd, dto.trnUsers());
    }



}
