package com.github.imifou.exception;

import com.github.imifou.data.ErrorDto;

public class ClientResponseException extends RuntimeException{

    private ErrorDto error;

    public ClientResponseException(ErrorDto error) {
        super(error.message());
        this.error = error;
    }

    public ErrorDto getError() {
        return error;
    }
}
