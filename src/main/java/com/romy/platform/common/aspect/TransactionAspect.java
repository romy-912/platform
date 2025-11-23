package com.romy.platform.common.aspect;

import com.romy.platform.annotation.PlatformTransactional;
import com.romy.platform.annotation.SubTransactional;

import com.romy.platform.annotation.MultiTransactional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Aspect
@Component
public class TransactionAspect {

    private final PlatformTransactionManager platformTransactionManager;
    private final PlatformTransactionManager subTransactionManager;

    public TransactionAspect(
            @Qualifier("platformTransactionManager") PlatformTransactionManager platformTransactionManager,
            @Qualifier("subTransactionManager") PlatformTransactionManager subTransactionManager
    ) {
        this.platformTransactionManager = platformTransactionManager;
        this.subTransactionManager = subTransactionManager;
    }

    @Around("@annotation(platformTransactional)")
    public Object handlePlatformTransaction(ProceedingJoinPoint joinPoint, PlatformTransactional platformTransactional) throws Throwable {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(platformTransactional.propagation().value());
        def.setTimeout(platformTransactional.timeout());
        def.setIsolationLevel(Isolation.READ_COMMITTED.value());

        TransactionStatus tx = this.platformTransactionManager.getTransaction(def);
        try {
            Object result = joinPoint.proceed();
            this.platformTransactionManager.commit(tx);
            return result;
        } catch (Throwable e) {
            this.platformTransactionManager.rollback(tx);
            throw e;
        }
    }

    @Around("@annotation(subTransactional)")
    public Object handleSubTransaction(ProceedingJoinPoint joinPoint, SubTransactional subTransactional) throws Throwable {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(subTransactional.propagation().value());
        def.setTimeout(subTransactional.timeout());
        def.setIsolationLevel(Isolation.READ_COMMITTED.value());

        TransactionStatus tx = this.subTransactionManager.getTransaction(def);
        try {
            Object result = joinPoint.proceed();
            this.subTransactionManager.commit(tx);
            return result;
        } catch (Throwable e) {
            this.subTransactionManager.rollback(tx);
            throw e;
        }
    }

    @Around("@annotation(multiTransactional)")
    public Object manageMultiTransaction(ProceedingJoinPoint joinPoint, MultiTransactional multiTransactional) throws Throwable {

        TransactionTemplate platformTemplate = new TransactionTemplate(this.platformTransactionManager);
        platformTemplate.setPropagationBehavior(multiTransactional.propagation().value());
        platformTemplate.setTimeout(multiTransactional.timeout());
        platformTemplate.setIsolationLevel(Isolation.READ_COMMITTED.value());

        TransactionTemplate subTemplate = new TransactionTemplate(this.subTransactionManager);
        subTemplate.setPropagationBehavior(multiTransactional.propagation().value());
        subTemplate.setTimeout(multiTransactional.timeout());
        subTemplate.setIsolationLevel(Isolation.READ_COMMITTED.value());

        final Object[] resultHolder = new Object[1];
        final Throwable[] exHolder = new Throwable[1];

        platformTemplate.executeWithoutResult(status1 -> {
            subTemplate.executeWithoutResult(status2 -> {
                try {
                    resultHolder[0] = joinPoint.proceed();
                } catch (Throwable ex) {
                    exHolder[0] = ex;
                    throw new RuntimeException(ex); // 내부 rollback 유도
                }
            });
        });

        if (exHolder[0] != null) throw exHolder[0];

        return resultHolder[0];
    }
}
