package com.romy.platform.common.handler;

import com.romy.platform.common.constants.PlatformConstant;

import com.romy.platform.common.exception.*;
import com.romy.platform.common.provider.MessageProvider;
import com.romy.platform.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;

import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLTransientException;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Slf4j
@RestControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler({ServletRequestBindingException.class, ValidationException.class,
            MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest request, Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = errorResponse(request);

        if (e instanceof MethodArgumentNotValidException ex) {
            FieldError fieldError = ex.getFieldError();
            String field = Optional.ofNullable(fieldError).map(FieldError::getField).orElse("");
            String message = Optional.ofNullable(fieldError).map(FieldError::getDefaultMessage).orElse("");
            this.addError(errorResponse, status, field, message);
            return new ResponseEntity<>(errorResponse, status);

        } else if (e instanceof ConstraintViolationException ex) {
            for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                String field = StreamSupport
                        .stream(violation.getPropertyPath().spliterator(), false)
                        .reduce((first, second) -> second)
                        .map(Path.Node::getName)
                        .orElse("");
                this.addError(errorResponse, status, field, violation.getMessage());
            }
            return new ResponseEntity<>(errorResponse, status);
        } else if (e instanceof MissingServletRequestParameterException ex) {
            String field = ex.getParameterName();
            String msg = MessageProvider.getMessage(PlatformConstant.COMMON_REQUIRED_VALUE, new Object[] {field});
            this.addError(errorResponse, status, msg);

            return new ResponseEntity<>(errorResponse, status);
        }

        return new ResponseEntity<>(errorResponse(request, e, status), status);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(HttpServletRequest request,
                                                              NoHandlerFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(this.errorResponse(request, e, status), status);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpServletRequest request,
                                                                HttpRequestMethodNotSupportedException e) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;

        return new ResponseEntity<>(this.errorResponse(request, e, status), status);
    }

    @ExceptionHandler({HttpMediaTypeException.class})
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpServletRequest request,
                                                                     HttpMediaTypeException e) {
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        return new ResponseEntity<>(this.errorResponse(request, e, status), status);
    }

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<ErrorResponse> handleReponseStatusException(HttpServletRequest request,
                                                                      ResponseStatusException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());

        return new ResponseEntity<>(this.errorResponse(request, e, status), status);
    }

    @ExceptionHandler({PlatformException.class})
    public ResponseEntity<ErrorResponse> handlePrimeException(HttpServletRequest request,
                                                              PlatformException e) {
        int status = PlatformConstant.PLATFORM_EXCEPTION_STATUS;

        return new ResponseEntity<>(this.errorResponse(request, e), null, status);
    }

    @ExceptionHandler({UnAuthorizationException.class})
    public ResponseEntity<ErrorResponse> handleUnAuthorizationException(HttpServletRequest request,
                                                                        UnAuthorizationException e) {
        int status = HttpStatus.UNAUTHORIZED.value();

        return new ResponseEntity<>(this.errorResponse(request, e), null, status);
    }

    @ExceptionHandler({GeneralException.class})
    public ResponseEntity<ErrorResponse> handleGeneralException(HttpServletRequest request,
                                                                GeneralException e) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

        return new ResponseEntity<>(this.errorResponse(request, e), null, status);
    }

    @ExceptionHandler({RestClientResponseException.class})
    public ResponseEntity<ErrorResponse> handleRestClientResponseException(HttpServletRequest request,
                                                                           RestClientResponseException e) {
        ErrorResponse errorResponse = this.errorResponse(request);
        int statusCode = e.getStatusCode().value();
        ErrorElement element = new ErrorElement(String.valueOf(statusCode),
                HttpStatus.Series.CLIENT_ERROR.name(), "");

        element.setMessage(e.getMessage());
        errorResponse.getErrors().add(element);

        return new ResponseEntity<>(errorResponse, null, statusCode);
    }

    @ExceptionHandler({PersistenceException.class, SQLTransientException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponse> handlePersistenceException(HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = this.errorResponse(request);
        ErrorElement element = new ErrorElement(status);
        element.setMessage("Database is error.");

        errorResponse.getErrors().add(element);

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("Exception : {}", e.getMessage());

        return new ResponseEntity<>(this.errorResponse(request, e, status), status);
    }

    private void addError(ErrorResponse response, HttpStatus status, String field, String message) {
        String convertMsg = StringUtils.isBlank(field) ? message : "[" + field + "] " + message;
        response.getErrors().add(new ErrorElement(
                String.valueOf(status.value()),
                status.name(),
                convertMsg
        ));
    }

    private void addError(ErrorResponse response, HttpStatus status, String message) {
        response.getErrors().add(new ErrorElement(
                String.valueOf(status.value()),
                status.name(),
                message
        ));
    }

    private ErrorResponse errorResponse(HttpServletRequest request, Exception e, HttpStatus status) {
        ErrorResponse errRes = new ErrorResponse(request.getRequestURI());

        if (e instanceof GeneralException) {
            errRes.getErrors().addAll( ((GeneralException) e).getErrors());
        } else {
            ErrorElement element = new ErrorElement(status);
            element.setMessage(e.getMessage());
            errRes.getErrors().add(element);
        }

        return errRes;
    }

    private ErrorResponse errorResponse(HttpServletRequest request, GeneralException e) {
        ErrorResponse errRes = new ErrorResponse(request.getRequestURI());
        errRes.getErrors().addAll(e.getErrors());

        return errRes;
    }

    private ErrorResponse errorResponse(HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURI());
    }


}
