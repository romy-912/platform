package com.romy.platform.common.provider;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Locale;


@Component
public class MessageProvider {

    @Resource
    private MessageSource source;

    static MessageSource messageSource;

    @PostConstruct
    public void initialize() {
        messageSource = this.source;
    }

    /**
     * 메시지 조회
     *
     * @param msgCd 메시지 코드
     * @return 메시지
     */
    public static String getMessage(String msgCd) {
        if (StringUtils.isBlank(msgCd)) return "";

        try {
            return messageSource.getMessage(msgCd, null, Locale.KOREAN);
        } catch (NoSuchMessageException e) {
            return msgCd;
        }
    }

    /**
     * 메시지 조회
     *
     * @param msgCd 메시지 코드
     * @param args  파라미터
     * @return 메시지
     */
    public static String getMessage(String msgCd, Object[] args) {
        if (StringUtils.isBlank(msgCd)) {
            return "";
        } else if (args == null || args.length == 0) {
            return getMessage(msgCd);
        }

        try {
            return messageSource.getMessage(msgCd, args, Locale.KOREAN);
        } catch (NoSuchMessageException e) {
            return msgCd;
        }
    }


    /**
     * 널 체크 (Null일 경우 에러 메시지)
     */
    public static void checkNotNullData(Object obj, String field ) {
        if (obj == null) {
            String fieldNm = MessageProvider.getMessage(field);
            String msg = MessageProvider.getMessage(PlatformConstant.COMMON_REQUIRED_VALUE, new Object[]{fieldNm});
            throw new ValidationException(msg);
        }

        if (obj instanceof Collection<?> col && CollectionUtils.isEmpty(col)) {
            String fieldNm = MessageProvider.getMessage(field);
            String msg = MessageProvider.getMessage(PlatformConstant.COMMON_REQUIRED_VALUE, new Object[]{fieldNm});
            throw new ValidationException(msg);
        }

        if (obj instanceof String str && StringUtils.isEmpty(str)) {
            String fieldNm = MessageProvider.getMessage(field);
            String msg = MessageProvider.getMessage(PlatformConstant.COMMON_REQUIRED_VALUE, new Object[]{fieldNm});
            throw new ValidationException(msg);
        }
    }

}
