package com.romy.platform.main.community.service;

import static com.romy.platform.main.community.dto.CommunityOfficialFormDto.*;
import com.romy.platform.main.community.mapper.CommunityOfficialFormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityOfficialFormService {

    private final CommunityOfficialFormMapper mapper;


    /**
     * 행정양식 카테고리 조회
     */
    public List<CategoryRes> getOfficialFormCategory() {
        return this.mapper.selectOfficialFormCategory();
    }

    /**
     * 행정양식 조회
     */
    public List<SearchRes> getOfficialForms(String ctgCd) {
        return this.mapper.selectOfficialForms(ctgCd);
    }

}

