package com.github.imifou.exception;

import com.github.imifou.data.ErrorDto;

public class NotFoundResponseException extends ClientResponseException {

    public NotFoundResponseException(ErrorDto error) {
        super(error);
    }

    public NotFoundResponseException() {
        super();
    }
}
