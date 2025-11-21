package com.romy.platform.common.utils;

import com.romy.platform.main.auth.dvo.AuthUserDvo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Random;
import java.util.Set;


public class PlatformUtil {

    private static final DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    public static String getDateTime() {
        return LocalDateTime.now().format(YYYYMMDDHHMMSS);
    }

    /**
     * 세션 사용자 정보
     */
    public static AuthUserDvo getUserInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) return new AuthUserDvo();

        Authentication authentication = context.getAuthentication();
        if (authentication == null) return new AuthUserDvo();

        if (authentication.getPrincipal() instanceof AuthUserDvo dvo) return dvo;

        return new AuthUserDvo();
    }

    /**
     * 세션 체크
     */
    public static boolean hasSession() {
        AuthUserDvo userDvo = getUserInfo();
        return StringUtils.isNotBlank(userDvo.getUsrCd());
    }

    /**
     * Content-Disposition 설정
     */
    public static String buildContentDisposition(String fileName, String userAgent) {
        String encodedFileNm = getEncodeFileName(userAgent, fileName);
        String encodedFileNmRFC5987 = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                                                .replace("+", "%20");

        return "attachment; filename=\"" + encodedFileNm + "\"; filename*=UTF-8''" + encodedFileNmRFC5987;
    }

    /**
     * 브라우저별 한글 파일명 인코딩 처리
     */
    private static String getEncodeFileName(String userAgent, String fileName) {
        if (StringUtils.isBlank(userAgent)) {
            return fileName;
        }

        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        } else if (userAgent.contains("Chrome")) {
            StringBuilder sb = new StringBuilder();
            for (char c : fileName.toCharArray()) {
                if (c > '~') {
                    sb.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
    }

    /**
     * 파일 확장자 조회
     */
    public static String getExtension(String fileNm) {
        int dotIndex = fileNm.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileNm.length() - 1) {
            return ""; // 확장자 없음
        }
        return fileNm.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 물리경로 조회 (format : 연도/월일/시간/)
     */
    public static String getPhyPath() {
        LocalDateTime now = LocalDateTime.now();

        return String.format("%d/%02d%02d/%02d/",
                now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(),
                now.getHour());
    }

    /**
     * 랜덤문자열 생성(영문+숫자)
     */
    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int rIndex = random.nextInt(2);
            switch (rIndex) {
                case 0:
                    // a-z
                    sb.append((char) ((random.nextInt(26)) + 97));
                    break;
                case 1:
                    // 0-9
                    sb.append((random.nextInt(10)));
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * 중복이름 처리
     */
    public static String generateUniqueName(String baseName, Set<String> usedNames) {
        int count = 1;
        String candidate;
        do {
            candidate = PlatformUtil.getChangedName(baseName, count++);
        } while (usedNames.contains(candidate));
        return candidate;
    }

    /**
     * 이름 변경
     */
    public static String getChangedName(String orgNameWithoutExt, int count) {
        return orgNameWithoutExt + " (" + count + ")";
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

}
