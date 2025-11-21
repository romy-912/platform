package com.romy.platform.main.community.mapper;

import static com.romy.platform.main.community.dto.CommunityNoticeDto.*;

import com.romy.platform.main.community.dvo.NoticeCondDvo;
import com.romy.platform.main.community.dvo.NoticeDetailDvo;
import com.romy.platform.main.community.dvo.NoticeDvo;
import com.romy.platform.main.community.dvo.NoticeHistoryDvo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CommunityNoticeMapper {

    NoticeCondDvo selectSectorInfoByDeptCd(@Param("deptCd") String deptCd);

    List<SearchRes> selectCommunityNotices(NoticeCondDvo dvo);

    List<ScopeRes> selectNoticeExposureScopes(@Param("brdCd") String brdCd);

    NoticeDetailDvo selectNoticeDetail(@Param("brdCd") String brdCd);

    String selectNoticeRegUserYn(@Param("brdCd") String brdCd);

    int updateNoticeViewCount(@Param("brdCd") String brdCd);

    void insertNoticeViewHistory(NoticeHistoryDvo dvo);

    int deleteCommunityNotice(@Param("brdCd") String brdCd);

    void mergeCommunityNotice(NoticeDvo dvo);
    int insertTrainingUsers(@Param("brdCd") String brdCd, @Param("list") List<String> trnUsers);
    int deleteTrainingUsers(@Param("brdCd") String brdCd, @Param("list") List<String> trnUsers);

    List<ReaderRes> selectNoticeReaders(@Param("brdCd") String brdCd);

    List<TrnUserRes> selectTrainingUsers(@Param("brdCd") String brdCd);

}
