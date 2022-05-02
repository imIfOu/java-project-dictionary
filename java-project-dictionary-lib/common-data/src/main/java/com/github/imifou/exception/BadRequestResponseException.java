package com.github.imifou.exception;

import com.github.imifou.data.ErrorDto;

public class BadRequestResponseException extends ClientResponseException {

    public BadRequestResponseException(ErrorDto error) {
        super(error);
    }
}
