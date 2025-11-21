package com.romy.platform.main.common.service;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.PlatformException;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.config.WebClientConfig;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.common.converter.GroupwareMailConverter;
import static com.romy.platform.main.common.dto.GroupwareMailDto.*;
import com.romy.platform.main.common.dvo.FilePermissionCondDvo;
import com.romy.platform.main.common.dvo.GroupwareMailFileDvo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class GroupwareMailService {

    @Value("${gw.server.url}")
    private String gwServerUrl;

    private final GroupwareMailConverter converter;

    private final FileService fileService;

    private final WebClientConfig webClientConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 그룹웨어 메일 발송 파일 권한체크
     */
    public void validGroupwareMailPermission(List<String> fileCds, String fileGrp) {

        // 커뮤니티 파일이 아닌 경우 skip
        if (!"DEPT".equals(fileGrp) && !"GROUP".equals(fileGrp)) return;

        FilePermissionCondDvo dvo = this.converter.dataToFilePermissionCondDvo(fileGrp, PlatformConstant.YN_N);
        dvo.setFileCds(fileCds);

        List<String> notPermissions = this.fileService.getNoPermissionFiles(dvo);

        if (CollectionUtils.isNotEmpty(notPermissions)) {
            // 권한 없는 파일이 존재합니다.
            throw new PlatformException(PlatformConstant.AUTH_NO_PERMISSION_FILE);
        }
    }

    /**
     * 그룹웨어 메일 발송
     */
    public MailRes getGroupwareMailSend(MailReq dto) {
        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        String email = userDvo.getUsrMail();
        String user = dto.user();
        List<String> fileCds = dto.fileCds();

        var mailFromNode = this.objectMapper.createObjectNode()
                                            .put("user", user)
                                            .put("email", email);

        List<String> limited = fileCds.size() > 20 ? fileCds.subList(0, 20) : fileCds;
        var fileListNode = this.objectMapper.createArrayNode();

        // 최대 20개
        List<GroupwareMailFileDvo> files = this.fileService.getFilesForGwMail(limited);
        if (CollectionUtils.isNotEmpty(files)) {
            for (GroupwareMailFileDvo file : files) {
                var fileNode = this.objectMapper.createObjectNode()
                                                .put("fileAttCd", file.getFileAttCd())
                                                .put("usrCd", file.getUsrCd())
                                                .put("fileNm", file.getFileNm())
                                                .put("fileSize", file.getFileSize());
                fileListNode.add(fileNode);
            }
        }

        var rootNode = this.objectMapper.createObjectNode();
        rootNode.set("mailFrom", mailFromNode);
        rootNode.set("fileInfo", this.objectMapper.createObjectNode().set("fileList", fileListNode));

        String url = this.gwServerUrl + "/ekp/view/openapi/IF";

        ResponseEntity<JsonNode> response = this.webClientConfig.webClient()
                                                           .method(HttpMethod.POST)
                                                           .uri(url)
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .accept(MediaType.APPLICATION_JSON)
                                                           .bodyValue(rootNode)  // JSON 그대로 전송
                                                           .retrieve()
                                                           .toEntity(JsonNode.class)
                                                           .block();

        if (response == null || response.getBody() == null) {
            throw new PlatformException("GW Mail Response is null");
        }

        JsonNode responseJson = response.getBody();
        log.info("GW Mail Response: {}", responseJson);

        String resultUrl = responseJson.has("data") ? responseJson.get("data").asText() : "Unknown error";

        return new MailRes(resultUrl);
    }

}
