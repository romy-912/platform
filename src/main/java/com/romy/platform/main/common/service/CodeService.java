package com.romy.platform.main.common.service;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.main.common.converter.CodeConverter;
import static com.romy.platform.main.common.dto.CodeDto.*;
import com.romy.platform.main.common.dvo.CodeDvo;
import com.romy.platform.main.common.mapper.CodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CodeService {

    private final CodeConverter converter;
    private final CodeMapper codeMapper;



    /**
     * 코드 조회 (부모코드 단건)
     */
    public List<CodeDvo> getCommonCode(String parentCd) {

        // 부모코드 필수 체크
        MessageProvider.checkNotNullData(parentCd, PlatformConstant.PARENT_CD);

        return this.codeMapper.selectCommonCode(parentCd);
    }

    /**
     * 코드 조회 (부모코드 다건)
     */
    public List<MultiRes> getCommonCodeMultiple(MultiReq dtos) {

        // 부모코드 필수 체크
        MessageProvider.checkNotNullData(dtos.parentCds(), PlatformConstant.PARENT_CD);

        List<MultiRes> resDtos = new ArrayList<>();

        for (String parentCd : dtos.parentCds()) {

            List<CodeDvo> codes = this.codeMapper.selectCommonCode(parentCd);
            MultiRes dto = this.converter.dataToMultiRes(parentCd, codes);

            resDtos.add(dto);
        }

        return resDtos;
    }



}
