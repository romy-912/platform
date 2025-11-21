package com.romy.platform.main.auth.service;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.UnAuthorizationException;
import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.common.token.JwtTokenProvider;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.common.provider.RedisProvider;
import static com.romy.platform.main.auth.dto.AuthLoginDto.*;
import com.romy.platform.main.auth.dvo.LoginHistoryDvo;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import com.romy.platform.main.auth.mapper.LoginHistoryMapper;
import com.romy.platform.main.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthLoginService {

    private final UserMapper userMapper;
    private final LoginHistoryMapper loginHistoryMapper;

    private final JwtTokenProvider jwtProvider;



    /**
     * 토큰 발급
     */
    public TokenRes createAuthToken(String usrId) {

        AuthUserDvo dvo = this.getUserInfoByToken(usrId);
        if (dvo == null) {
            String msg = MessageProvider.getMessage(PlatformConstant.AUTH_USER_EMPTY, new Object[]{usrId});
            throw new UnAuthorizationException(msg);
        }

        String accessToken = this.jwtProvider.createAccessToken(dvo);
        String refreshToken = this.jwtProvider.createRefreshToken(dvo);

        return new TokenRes(accessToken, refreshToken);
    }
    /**
     * 토큰 발급을 위한 사용자 정보 조회
     */
    public AuthUserDvo getUserInfoByToken(String usrId) {
        return this.userMapper.selectUserInfoByToken(usrId);
    }


    /**
     * 로그아웃에 의한 로그인 이력 생성
     */
    @PlatformTransactional
    public void updateLoginHistoryByLogout(LogoutReq dto) {
        String usrCd = dto.usrCd();
        String usrId = dto.usrId();

        LoginHistoryDvo dvo = new LoginHistoryDvo();
        dvo.setUsrCd(usrCd);
        dvo.setLoginDtt(PlatformUtil.getDateTime());
        dvo.setDiv("LOGOUT(U)");

        LoginHistoryDvo histDvo = this.loginHistoryMapper.selectRecentLoginHistory(usrCd);
        if (histDvo != null) {
            dvo.setAccessIp(histDvo.getAccessIp());
            dvo.setBrowserDiv(histDvo.getBrowserDiv());
        }

        int count = this.loginHistoryMapper.insertLoginHistory(dvo);
        if (count > 0) {
            RedisProvider.removeRedisKey("spring:session:" + usrId);
        }
    }


}
