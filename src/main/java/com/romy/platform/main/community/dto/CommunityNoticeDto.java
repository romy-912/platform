package com.romy.platform.main.community.dto;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.main.common.dvo.FileInfoDvo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;


public class CommunityNoticeDto {

    @Schema(name = "NoticeDto.SearchReq", description = "공지사항 조회조건")
    public record SearchReq(
            @Parameter(description = "조건 구분", schema = @Schema(allowableValues = {"TIT", "CONT", "REG"}))
            String searchCond,
            @Parameter(description = "검색어")
            String searchKeyword,
            @Parameter(description = "연도")
            String year,
            @Parameter(description = "중요공지여부", schema = @Schema(allowableValues = {"Y", "N"}))
            String favoriteYn,
            @Parameter(description = "페이지 번호")
            int startIdx,
            @Parameter(description = "페이지 건수")
            int listSize
    ) {
        public SearchReq {
            if (!PlatformConstant.YN_Y.equals(favoriteYn)) favoriteYn = PlatformConstant.YN_N;
        }
    }

    @Schema(name = "NoticeDto.SearchRes", description = "공지사항 리스트")
    public record SearchRes(
            @Schema(description = "유형코드 (공통코드 : NOTICE_TYPE)", allowableValues = {"NTC", "ANN", "COO", "TRN", "EVENT"})
            String noticeType,
            @Schema(description = "공지유형")
            String noticeTypeNm,
            @Schema(description = "게시판코드")
            String brdCd,
            @Schema(description = "제목")
            String brdTit,
            @Schema(description = "등록사용자")
            String regUsrNm,
            @Schema(description = "등록자부서")
            String regUsrDept,
            @Schema(description = "등록일자")
            String modDt,
            @Schema(description = "상단공지여부", allowableValues = {"Y", "N"})
            String noticeYn,
            @Schema(description = "조회수")
            Long schCnt,
            @Schema(description = "파일건수")
            int fileCnt,
            @Schema(description = "파일명")
            String fileNms,
            @Schema(description = "파일명 리스트")
            List<String> fileNames,
            @Schema(description = "읽음여부", allowableValues = {"Y", "N"})
            String readYn,
            @Schema(description = "편집여부", allowableValues = {"Y", "N"})
            String editYn,
            @Schema(description = "즐겨찾기여부", allowableValues = {"Y", "N"})
            String favoriteYn,
            @Schema(description = "즐겨찾기코드")
            String favoriteCd
    ){
        public SearchRes {
            if (StringUtils.isNotBlank(fileNms)) {
                fileNames = Arrays.asList(fileNms.split("\\|\\|"));
            } else {
                fileNames = List.of();
            }
        }
    }

    @Schema(name = "NoticeDto.ScopeRes", description = "공지사항 공개범위")
    public record ScopeRes(
            @Schema(description = "부서코드")
            String deptCd,
            @Schema(description = "부서명")
            String deptNm
    ) {}

    @Schema(name = "NoticeDto.DetailRes", description = "공지사항 상세정보")
    public record DetailRes(
            @Schema(description = "게시판코드")
            String brdCd,
            @Schema(description = "게시판마스터코드")
            String brdMstCd,
            @Schema(description = "유형코드 (공통코드 : NOTICE_TYPE)", allowableValues = {"NTC", "ANN", "HR", "TRN"})
            String noticeType,
            @Schema(description = "공지유형")
            String noticeTypeNm,
            @Schema(description = "제목")
            String brdTit,
            @Schema(description = "내용")
            String brdCont,
            @Schema(description = "검색내용")
            String brdSchCont,
            @Schema(description = "조회수")
            Long schCnt,
            @Schema(description = "상단공지여부", allowableValues = {"Y", "N"})
            String noticeYn,
            @Schema(description = "부서운영현황 공지여부", allowableValues = {"Y", "N"})
            String deptOperNoticeYn,
            @Schema(description = "메인공지여부", allowableValues = {"Y", "N"})
            String mainNoticeYn,
            @Schema(description = "공지팝업 TOP위치")
            Integer noticePopupTopLocation,
            @Schema(description = "공지팝업 LEFT위치")
            Integer noticePopupLeftLocation,
            @Schema(description = "공지팝업 가로사이즈")
            Integer noticePopupWidthSize,
            @Schema(description = "공지팝업 세로사이즈")
            Integer noticePopupHeightSize,
            @Schema(description = "팝업게시 시작일자")
            String noticeStDt,
            @Schema(description = "팝업게시 종료일자")
            String noticeEdDt,
            @Schema(description = "등록일자")
            String modDt,
            @Schema(description = "등록사용자")
            String regUsrNm,
            @Schema(description = "등록자부서")
            String regUsrDept,
            @Schema(description = "부문코드")
            String brdDivDetailCd,
            @Schema(description = "부서코드(공개범위코드)")
            String deptCd,
            @Schema(description = "공개범위")
            String exposureScope,
            @Schema(description = "즐겨찾기여부", allowableValues = {"Y", "N"})
            String favoriteYn,
            @Schema(description = "즐겨찾기코드")
            String favoriteCd,
            @Schema(description = "첨부파일 리스트", subTypes = {FileInfoDvo.class})
            List<FileInfoDvo> files
    ){
        public DetailRes {
            if (deptCd == null) deptCd = "";
        }

    }

    @Schema(name = "NoticeDto.SaveReq", description = "공지사항 저장")
    public record SaveReq(
            @Size(max = 20)
            @Schema(description = "게시판코드")
            String brdCd,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "게시판마스터코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String brdMstCd,
            @NotBlank
            @Size(max = 20)
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "유형코드 (공통코드 : NOTICE_TYPE)"
                    , allowableValues = {"NTC", "ANN", "HR", "TRN"})
            String noticeType,
            @NotBlank
            @Size(max = 200)
            @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
            String brdTit,
            @NotBlank
            @Schema(description = "내용")
            String brdCont,
            @Schema(description = "검색내용")
            String brdSchCont,
            @Schema(description = "조회수")
            Long schCnt,
            @Schema(description = "상단공지여부", allowableValues = {"Y", "N"})
            String noticeYn,
            @Schema(description = "부서운영현황 공지여부", allowableValues = {"Y", "N"})
            String deptOperNoticeYn,
            @Schema(description = "메인공지여부", allowableValues = {"Y", "N"})
            String mainNoticeYn,
            @Schema(description = "공지팝업 TOP위치")
            Integer noticePopupTopLocation,
            @Schema(description = "공지팝업 LEFT위치")
            Integer noticePopupLeftLocation,
            @Schema(description = "공지팝업 가로사이즈")
            Integer noticePopupWidthSize,
            @Schema(description = "공지팝업 세로사이즈")
            Integer noticePopupHeightSize,
            @Schema(description = "팝업게시 시작일자")
            String noticeStDt,
            @Schema(description = "팝업게시 종료일자")
            String noticeEdDt,
            @Schema(description = "부서코드(공개범위코드)")
            String deptCd,
            @Schema(description = "파일첨부 코드 리스트")
            List<FileInfoDvo> files,
            @Schema(description = "교육 대상자 리스트")
            List<String> trnUsers
    ){
        public SaveReq {
            brdMstCd = "NOTICE";

            if (schCnt == null) schCnt = 0L;

            if (noticePopupTopLocation == null) noticePopupTopLocation = 0;
            if (noticePopupLeftLocation == null) noticePopupLeftLocation = 0;
            if (noticePopupWidthSize == null) noticePopupWidthSize = 0;
            if (noticePopupHeightSize == null) noticePopupHeightSize = 0;

            if (!PlatformConstant.YN_Y.equals(noticeYn)) noticeYn = PlatformConstant.YN_N;
            if (!PlatformConstant.YN_Y.equals(deptOperNoticeYn)) deptOperNoticeYn = PlatformConstant.YN_N;
            if (!PlatformConstant.YN_Y.equals(mainNoticeYn)) mainNoticeYn = PlatformConstant.YN_N;

        }
    }

    @Schema(name = "NoticeDto.SaveRes", description = "공지사항 저장 결과")
    public record SaveRes(
            @Schema(description = "게시판코드")
            String brdCd
    ) {}

    @Schema(name = "NoticeDto.TrnUserRes", description = "교육 대상자 조회")
    public record TrnUserRes(
            @Schema(description = "게시판코드")
            String brdCd,
            @Schema(description = "사용자코드")
            String usrCd,
            @Schema(description = "성명")
            String usrNm,
            @Schema(description = "부서명")
            String deptNm,
            @Schema(description = "직위")
            String sapSpotNm
    ) {}

    @Schema(name = "NoticeDto.SaveUserReq", description = "교육 대상자 생성/삭제")
    public record SaveUserReq(
            @NotEmpty
            @Schema(description = "교육 대상자 리스트")
            List<String> trnUsers
    ) {}
    
    @Schema(name = "NoticeDto.ReaderRes", description = "공지사항 열람자 조회")
    public record ReaderRes(
            @Schema(description = "성명")
            String regUsrNm,
            @Schema(description = "직위")
            String regUsrSpot,
            @Schema(description = "부서")
            String regUsrDept,
            @Schema(description = "조회일시")
            String schDtt
    ){}

    @Schema(name = "NoticeDto.FavoriteRes", description = "공지사항 즐겨찾기 등록")
    public record FavoriteRes(
            @Schema(description = "즐겨찾기 코드")
            String favoriteCd
    ){}

}
