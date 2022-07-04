package com.github.imifou.resource.exception;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestResponseException.class})
    public ResponseEntity<ErrorDto> toResponse(BadRequestResponseException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(
                        HttpStatus.BAD_REQUEST.name(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID))
                ));
    }

    @ExceptionHandler({NotFoundResponseException.class})
    public ResponseEntity<ErrorDto> toResponse(NotFoundResponseException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(
                        HttpStatus.NOT_FOUND.name(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID))
                ));
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ErrorDto> handleThrowable(Throwable exception) {
        log.error(exception.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ErrorDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID))
                ));
    }

}
