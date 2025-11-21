package com.romy.platform.common.exception;

import com.romy.platform.common.provider.MessageProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NestedRuntimeException;

import java.util.ArrayList;


@Getter
@Setter
public class GeneralException extends NestedRuntimeException {

    protected transient ArrayList<ErrorElement> errors = new ArrayList<>();

    @JsonIgnore
    private ErrorElement element;


    protected GeneralException(String code, String details, String msgKey, String message, Throwable e) {
        super(message, e);

        String convMsg = MessageProvider.getMessage(message);
        String internalMsg = (e != null && StringUtils.isBlank(convMsg)) ? e.getMessage() : convMsg;

        this.element = new ErrorElement(code, details, msgKey);
        this.addErrorElement(internalMsg);
    }

    protected GeneralException(String code, String details, String msgKey, Object[] args, String message, Throwable e) {
        super(message, e);

        String convMsg = MessageProvider.getMessage(message, args);
        String internalMsg = (e != null && StringUtils.isBlank(convMsg)) ? e.getMessage() : convMsg;

        this.element = new ErrorElement(code, details, msgKey);
        this.addErrorElement(internalMsg);
    }

    public void addErrorElement(String message) {
        ErrorElement element = new ErrorElement(this.element);
        element.setMessage(message);

        this.errors.add(element);
    }


}
