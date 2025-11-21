package com.romy.platform.main.community.converter;

import static com.romy.platform.main.community.dto.CommunityNoticeDto.*;

import com.romy.platform.main.common.dvo.FileDvo;
import com.romy.platform.main.common.dvo.FileInfoDvo;
import com.romy.platform.main.community.dvo.NoticeCondDvo;
import com.romy.platform.main.community.dvo.NoticeDetailDvo;
import com.romy.platform.main.community.dvo.NoticeDvo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityNoticeConverter {

    NoticeCondDvo searchReqToNoticeCondDvo(SearchReq dto);

    List<FileInfoDvo> fileDvoToFileInfoDvoList(List<FileDvo> dvos);

    DetailRes noticeDetailDvoToDetailRes(NoticeDetailDvo dvo);

    NoticeDvo saveReqToNoticeDvo(SaveReq dto);

}
