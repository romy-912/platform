package com.romy.platform.common.token;

import com.romy.platform.common.constants.PlatformConstant;
import com.romy.platform.common.exception.UnAuthorizationException;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtKeyProvider keyProvider;


    /**
     * Access Token 생성
     */
    public String createAccessToken(AuthUserDvo dvo) {
        LocalDateTime now = LocalDateTime.now();
        Key signingKey = this.keyProvider.getSigningKey();

        long ACCESS_EXPIRE_HOURS = 12;
        Claims claims = Jwts.claims().add("usrNm", dvo.getUsrNm())
                            .add("usrCd", dvo.getUsrCd())
                            .add("usrMail", dvo.getUsrMail())
                            .add("deptCd", dvo.getDeptCd())
                            .add("deptNm", dvo.getDeptNm())
                            .add("sapDeptCd", dvo.getSapDeptCd())
                            .add("sapDeptNm", dvo.getSapDeptNm())
                            .add("sapMisDeptCd", dvo.getSapMisDeptCd())
                            .add("sapMisDeptNm", dvo.getSapMisDeptNm())
                            .add("sapSpotCd", dvo.getSapSpotCd())
                            .add("sapSpotNm", dvo.getSapSpotNm())
                            .add("sapGroupCd", dvo.getSapGroupCd())
                            .add("adminYn", dvo.getAdminYn())
                            .id(dvo.getUsrId()).issuer(dvo.getUsrNm())
                            .issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                            .expiration(Date.from(now.plusHours(ACCESS_EXPIRE_HOURS).atZone(ZoneId.systemDefault()).toInstant()))
                            .build();

        return Jwts.builder().signWith(signingKey).subject(dvo.getUsrCd()).claims(claims).compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(AuthUserDvo dvo) {
        LocalDateTime now = LocalDateTime.now();
        Key signingKey = this.keyProvider.getSigningKey();

        long REFRESH_EXPIRE_DAYS = 5;
        Claims claims = Jwts.claims().id(dvo.getUsrId()).issuer(dvo.getUsrNm())
                            .issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                            .expiration(Date.from(now.plusDays(REFRESH_EXPIRE_DAYS).atZone(ZoneId.systemDefault()).toInstant()))
                            .build();

        return Jwts.builder().signWith(signingKey).subject(dvo.getUsrCd()).claims(claims).compact();
    }

    /**
     * 만료일자 추출
     */
    private Date extExpireDate(String token) {

        return this.extClaim(token, Claims::getExpiration);
    }

    /**
     * 사번 추출
     */
    public String extUsrId(String token) {
        if (StringUtils.isBlank(token) || "null".equals(token)) return "";

        return this.extClaim(token, Claims::getId);
    }

    /**
     * 사용자코드 추출
     */
    public String extUsrCd(String token) {
        if (StringUtils.isBlank(token) || "null".equals(token)) return "";

        return this.extClaim(token, Claims::getSubject);
    }

    /**
     * 성명 추출
     */
    public String extUsrNm(String token) {
        if (StringUtils.isBlank(token) || "null".equals(token)) return "";

        return this.extClaim(token, Claims::getIssuer);
    }

    /**
     * 토큰 정보 조회
     */
    private <T> T extClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claims = this.extAllClaims(token);

        if (claims == null) {
            // 잘못된 접근입니다.
            throw new UnAuthorizationException(PlatformConstant.AUTH_INVALID_ACCESS);
        }

        return claimsResolver.apply(claims);
    }

    /**
     * 토큰 파싱
     */
    private Claims extAllClaims(String token) {

        Claims body = null;
        Key verifyKey = this.keyProvider.getVerifyingKey();
        boolean useRS256 = this.keyProvider.isRS256();

        try {
            if (useRS256) {
                PublicKey publicKey = (PublicKey) verifyKey;
                body = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
            } else {
                SecretKey secretKey = (SecretKey) verifyKey;
                body = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            }
        } catch (ExpiredJwtException e) {
            log.debug(e.getMessage());
            throw e;

        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.debug(e.getMessage());
        }

        return body;
    }

    /**
     * 토큰 만료 체크
     */
    public boolean isTokenExpired(String token) {
        if (StringUtils.isBlank(token) || "null".equals(token)) return true;

        try {
            Date date = this.extExpireDate(token);
            return date.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 토큰 유효성 체크
     */
    public boolean isValidToken(String token) {
        if (StringUtils.isBlank(token) || "null".equals(token)) return false;

        final String usrCd = this.extUsrCd(token);

        return StringUtils.isNotBlank(usrCd);
    }

    /**
     * Authentication 셋팅
     */
    public void setAuthentication(String token) {
        if (StringUtils.isBlank(token) || "null".equals(token)) return;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) return;
        final Claims claims = this.extAllClaims(token);
        AuthUserDvo dvo = this.claimsToUserTokenDvo(claims);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dvo, null,
                        dvo.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /**
     * Claims Convert UserTokenDvo
     */
    private AuthUserDvo claimsToUserTokenDvo(Claims claims) {
        AuthUserDvo dvo = new AuthUserDvo();
        if (claims == null) {
            return dvo;
        }

        String usrCd = claims.getSubject();
        String usrNm = claims.getIssuer();
        String usrId = claims.getId();
        String usrMail = claims.get("usrMail").toString();
        String deptCd = claims.get("deptCd").toString();
        String deptNm = claims.get("deptNm").toString();
        String sapDeptCd = claims.get("sapDeptCd").toString();
        String sapDeptNm = claims.get("sapDeptNm").toString();
        String sapMisDeptCd = claims.get("sapMisDeptCd").toString();
        String sapMisDeptNm = claims.get("sapMisDeptNm").toString();
        String sapSpotCd = claims.get("sapSpotCd").toString();
        String sapSpotNm = claims.get("sapSpotNm").toString();
        String sapGroupCd = claims.get("sapGroupCd").toString();
        String adminYn = claims.get("adminYn").toString();

        dvo.setUsrCd(usrCd);
        dvo.setUsrNm(usrNm);
        dvo.setUsrId(usrId);
        dvo.setUsrMail(usrMail);
        dvo.setDeptCd(deptCd);
        dvo.setDeptNm(deptNm);
        dvo.setSapDeptCd(sapDeptCd);
        dvo.setSapDeptNm(sapDeptNm);
        dvo.setSapMisDeptCd(sapMisDeptCd);
        dvo.setSapMisDeptNm(sapMisDeptNm);
        dvo.setSapSpotCd(sapSpotCd);
        dvo.setSapSpotNm(sapSpotNm);
        dvo.setSapGroupCd(sapGroupCd);
        dvo.setAdminYn(adminYn);

        return dvo;
    }



}
