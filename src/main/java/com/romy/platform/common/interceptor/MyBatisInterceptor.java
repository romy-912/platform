package com.romy.platform.common.interceptor;

import com.romy.platform.common.token.SessionDvo;
import com.romy.platform.common.utils.PlatformUtil;
import com.romy.platform.main.auth.dvo.AuthUserDvo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;


@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MyBatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        Object originalParam = args.length > 1 ? args[1] : null;

        Object processedParam = this.injectSession(originalParam);
        args[1] = processedParam;

        // Log 출력
        MappedStatement ms = (MappedStatement) args[0];
        BoundSql boundSql = ms.getBoundSql(processedParam);
        Configuration configuration = ms.getConfiguration();

        String sqlId = ms.getId();
        log.info("/*--------------- Mapper ID: {} [BEGIN] ---------------*/", sqlId);
        log.info("==> SQL : /* {} */", sqlId);
        log.info("\n        {}", this.generateSql(configuration, boundSql));

        long start = System.currentTimeMillis();

        Object result;

        try {
            result = invocation.proceed();
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("SQL 실행 오류: {}", e.getMessage(), e);
            throw e;
        }

        long end = System.currentTimeMillis();

        log.info("<== END：{} ms", end - start);

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    /**
     * 로그 생성 용 SQL
     */
    private String generateSql(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        MetaObject metaObject = (parameterObject != null) ? configuration.newMetaObject(parameterObject) : null;

        Map<String, String> paramCache = new HashMap<>();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (CollectionUtils.isNotEmpty(parameterMappings)) {
            for (ParameterMapping mapping : parameterMappings) {
                String propertyName = mapping.getProperty();
                String valueStr;

                if (paramCache.containsKey(propertyName)) {
                    valueStr = paramCache.get(propertyName);
                } else {
                    Object value = null;
                    boolean hasValue = false;

                    // MetaObject 탐색
                    if (metaObject != null && metaObject.hasGetter(propertyName)) {
                        value = metaObject.getValue(propertyName);
                        hasValue = true;
                    }

                    // AdditionalParameter 체크
                    if (!hasValue && boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                        hasValue = true;
                    }

                    valueStr = hasValue ? getParameterValue(value) : "null";
                    paramCache.put(propertyName, valueStr);
                }

                sql = sql.replaceFirst("\\?", valueStr);
            }
        }

        return sql;
    }

    /**
     * 파라미터 값 추출
     */
    private String getParameterValue(Object param) {

        if (param == null) {
            return "null";
        } else if (param instanceof LocalDate obj) {
            // yyyy-MM-dd
            return "'" + obj.format(DateTimeFormatter.ISO_DATE) + "'";
        } else if (param instanceof LocalDateTime obj) {
            // yyyy-MM-ddTHH24:mi:ss
            return "'" + obj.format(DateTimeFormatter.ISO_DATE_TIME) + "'";
        } else if (param instanceof Integer || param instanceof Byte || param instanceof Short || param instanceof Long) {
            return String.valueOf(param);
        }

        return "'" + Matcher.quoteReplacement(String.valueOf(param).replace("'", "''")) + "'";
    }

    /**
     * session 정보 주입
     */
    private Object injectSession(Object param) {
        SessionDvo session = buildSession();
        Map<String, Object> newParam = new HashMap<>();
        if (session != null) {
            newParam.put("session", session);
        }

        if (param == null) {
            return newParam;
        }

        if (param instanceof Map<?, ?> mapParam) {

            for (Map.Entry<?, ?> entry : mapParam.entrySet()) {
                if (entry.getKey() instanceof String key) {
                    newParam.put(key, entry.getValue());
                }
            }

        } else {
            newParam.put("_origin", param);

            // 파라미터 객체의 필드 분해
            MetaObject meta = SystemMetaObject.forObject(param);
            for (String name : meta.getGetterNames()) {
                if (!newParam.containsKey(name)) {
                    newParam.put(name, meta.getValue(name));
                }
            }
        }

        return newParam;
    }

    /**
     * Audit Column 셋팅
     */
    private SessionDvo buildSession() {
        if (!PlatformUtil.hasSession()) return null;

        AuthUserDvo userDvo = PlatformUtil.getUserInfo();
        String usrCd = userDvo.getUsrCd();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter datetimeFmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        SessionDvo session = new SessionDvo();
        session.setRegDt(now.format(dateFmt));
        session.setRegDtt(now.format(datetimeFmt));
        session.setRegUsrCd(usrCd);
        session.setModDt(now.format(dateFmt));
        session.setModDtt(now.format(datetimeFmt));
        session.setModUsrCd(usrCd);
        session.setDelDtt(now.format(datetimeFmt));
        session.setDelUsrCd(usrCd);
        session.setUsrCd(usrCd);
        session.setCreatedBy(usrCd);
        session.setUsrId(userDvo.getUsrId());
        session.setDeptCd(userDvo.getDeptCd());
        session.setSapGroupCd(userDvo.getSapGroupCd());

        return session;
    }
}
