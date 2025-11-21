package com.romy.platform.main.community.service;

import static com.romy.platform.main.community.dto.CommunityNoticeDto.*;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.common.dvo.FileDvo;
import com.romy.platform.main.common.dvo.FileInfoDvo;
import com.romy.platform.main.common.service.FavoriteService;
import com.romy.platform.main.common.service.FileService;
import com.romy.platform.main.common.service.IdGenerateService;
import com.romy.platform.main.community.converter.CommunityNoticeConverter;
import com.romy.platform.main.community.dvo.NoticeCondDvo;
import com.romy.platform.main.community.dvo.NoticeDetailDvo;
import com.romy.platform.main.community.dvo.NoticeDvo;
import com.romy.platform.main.community.dvo.NoticeHistoryDvo;
import com.romy.platform.main.community.mapper.CommunityNoticeMapper;
import com.romy.platform.main.department.service.CooperationNoticeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityNoticeService {

    private final CommunityNoticeConverter converter;
    private final CommunityNoticeMapper noticeMapper;

    private final FileService fileService;
    private final IdGenerateService idGenService;
    private final FavoriteService favoriteService;
    private final CooperationNoticeService coopNoticeService;

    private static final String BRD_MST_CD = "NOTICE";


    /**
     * 공지사항 조회
     */
    public List<SearchRes> getCommunityNotices(SearchReq dto) {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();

        NoticeCondDvo condDvo = this.converter.searchReqToNoticeCondDvo(dto);
        condDvo.setAdminYn(userDvo.getAdminYn());

        // 소속 부문코드 조회
        NoticeCondDvo sectorDvo = this.getSectorInfoByDeptCd(null);
        condDvo.setSectorCd(sectorDvo.getSectorCd());
        condDvo.setSectorDeptCd(sectorDvo.getSectorDeptCd());

        return this.noticeMapper.selectCommunityNotices(condDvo);
    }

    /**
     * 공지사항 공개범위 조회
     */
    public List<ScopeRes> getNoticeScopes(String brdCd) {
        String adminYn = PlatformUtil.getUserInfo().getAdminYn();

        List<ScopeRes> datas = new ArrayList<>();

        if (PlatformConstant.YN_Y.equals(adminYn)) {
            // 전체(전사)
            datas.add(new ScopeRes("", PlatformConstant.DEPT_NM_ALL));
        }

        List<ScopeRes> res = this.noticeMapper.selectNoticeExposureScopes(brdCd);
        datas.addAll(res);

        return datas;
    }

    /**
     * 공지사항 상세조회
     */
    public DetailRes getNoticeDetail(String brdCd) {
        NoticeDetailDvo dvo = this.noticeMapper.selectNoticeDetail(brdCd);
        if (dvo == null) {
            // 공지사항이 존재하지 않습니다.
            throw new PlatformException(PlatformConstant.COMMU_NOTICE_NOT_EXIST);
        }
        dvo.setBrdSchCont("");
        // 첨부파일 리스트 조회
        List<FileDvo> attFiles = this.fileService.getFilesByFileRefCd(BRD_MST_CD, brdCd, true);
        List<FileInfoDvo> files = this.converter.fileDvoToFileInfoDvoList(attFiles);
        dvo.setFiles(files);

        return this.converter.noticeDetailDvoToDetailRes(dvo);
    }

    /**
     * 조회 이력 생성
     */
    @PlatformTransactional
    public int createNoticeViewHistory(String brdCd) {

        String regYn = this.noticeMapper.selectNoticeRegUserYn(brdCd);
        // 등록자일 경우 skip
        if (PlatformConstant.YN_Y.equals(regYn)) return 1;

        // 조회 수 증가
        int count = this.noticeMapper.updateNoticeViewCount(brdCd);
        // 조회 이력 생성
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        NoticeHistoryDvo dvo = new NoticeHistoryDvo();
        dvo.setBrdCd(brdCd);
        dvo.setRegUsrNm(userDvo.getUsrNm());
        dvo.setRegUsrSpot(userDvo.getSapSpotNm());
        dvo.setRegUsrDept(userDvo.getDeptNm());
        this.noticeMapper.insertNoticeViewHistory(dvo);

        return count;

    }

    /**
     * 공지사항 삭제
     */
    @PlatformTransactional
    public int removeCommunityNotice(String brdCd) {
        // 공지사항 삭제
        int count = this.noticeMapper.deleteCommunityNotice(brdCd);

        // 부서운영현황 공지 삭제
        this.coopNoticeService.removeCoopNoticeByBrdCd(brdCd);

        // 첨부파일 삭제
        this.fileService.removeFilesByFileRefCd(BRD_MST_CD, brdCd);

        return count;
    }

    /**
     * 부서코드에 해당하는 부문정보 조회
     */
    public NoticeCondDvo getSectorInfoByDeptCd(String deptCd) {
        return this.noticeMapper.selectSectorInfoByDeptCd(deptCd);
    }


    /**
     * 공지사항 저장
     */
    @PlatformTransactional
    public SaveRes saveCommunityNotice(SaveReq dto) throws IOException {

        String brdCd = dto.brdCd();
        if (StringUtils.isBlank(brdCd)) {
            brdCd = this.idGenService.getNextStringId();
        }

        // 공지유형
        String noticeType = dto.noticeType();

        AuthUserDvo userDvo = PlatformUtil.getUserInfo();

        NoticeDvo dvo = this.converter.saveReqToNoticeDvo(dto);
        dvo.setBrdCd(brdCd);
        dvo.setDelYn(PlatformConstant.YN_N);
        dvo.setOpenYn(PlatformConstant.YN_Y);
        dvo.setAnswerYn(PlatformConstant.YN_N);
        dvo.setRegUsrNm(userDvo.getUsrNm());
        dvo.setRegUsrSpot(userDvo.getSapSpotNm());
        dvo.setRegUsrDept(userDvo.getDeptNm());

        String brdCont = dvo.getBrdCont();
        String brdSchCont = dvo.getBrdSchCont();

        if (StringUtils.isNotBlank(brdCont) && StringUtils.isBlank(brdSchCont)) {
            dvo.setBrdSchCont(Jsoup.parse(brdCont).text());
        }

        String deptCd = dvo.getDeptCd();

        if (StringUtils.isNotBlank(deptCd)) {
            // 부문코드 조회
            NoticeCondDvo sectorDvo = this.getSectorInfoByDeptCd(deptCd);
            dvo.setBrdDivDetailCd(sectorDvo.getSectorCd());
        }

        // 공지사항 저장
        this.noticeMapper.mergeCommunityNotice(dvo);

        // 파일정보 업데이트
        this.fileService.updateFileRefCd(dto.files(), brdCd, BRD_MST_CD);

        // 부서운영현황 공지사항
        String deptOperNoticeYn = dto.deptOperNoticeYn();
        // 공지유형이 공지인 경우만 해당
        if ("NTC".equals(noticeType)) {
            if (PlatformConstant.YN_Y.equals(deptOperNoticeYn)) {
                this.coopNoticeService.saveCoopNoticeForCommuNotice(brdCd, dto.files());
            } else {
                this.coopNoticeService.removeCoopNoticeByBrdCd(brdCd);
            }
        }

        // 교육대상자 생성
        List<String> trnUsers = dto.trnUsers();
        if (StringUtils.isBlank(dto.brdCd()) && "TRN".equals(noticeType) && CollectionUtils.isNotEmpty(trnUsers)) {
            this.createTrainingUsers(brdCd, trnUsers);
        }

        return new SaveRes(brdCd);
    }

    /**
     * 교육대상자 조회
     */
    public List<TrnUserRes> getTrainingUsers(String brdCd) {
        return this.noticeMapper.selectTrainingUsers(brdCd);
    }

    /**
     * 교육 대상자 생성
     */
    @PlatformTransactional
    public int createTrainingUsers(String brdCd, List<String> trnUsers) {
        return this.noticeMapper.insertTrainingUsers(brdCd, trnUsers);
    }

    /**
     * 교육 대상자 삭제
     */
    @PlatformTransactional
    public int removeTrainingUsers(String brdCd, List<String> trnUsers) {
        return this.noticeMapper.deleteTrainingUsers(brdCd, trnUsers);
    }

    /**
     * 공지사항 열람자 조회
     */
    public List<ReaderRes> getNoticeReaders(String brdCd) {
        return this.noticeMapper.selectNoticeReaders(brdCd);
    }

    /**
     * 공지사항 즐겨찾기 등록
     */
    @PlatformTransactional
    public FavoriteRes createNoticeFavorite(String brdCd) {

        String favoriteCd = this.favoriteService.createFavorite(BRD_MST_CD, brdCd);

        return new FavoriteRes(favoriteCd);
    }

}
