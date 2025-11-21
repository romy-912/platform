package com.romy.platform.annotation;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubTransactional {

    Propagation propagation() default Propagation.REQUIRED;

    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
}
