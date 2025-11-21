package com.romy.platform.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class ErrorElement {

    private String code;
    private String details;
    private String message;

    public ErrorElement(ErrorElement element) {
        this(element.getCode(), element.getDetails(), element.getMessage());
    }

    public ErrorElement(String code, String details, String message) {
        this.code = code;
        this.details = details;
        this.message = message;
    }

    public ErrorElement(HttpStatus status) {
        this.code = status.name();
        this.details = HttpStatus.Series.valueOf(status.value()).name();
        this.message = status.getReasonPhrase();
    }



}
