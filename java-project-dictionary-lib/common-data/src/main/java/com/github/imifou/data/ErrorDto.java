package com.github.imifou.data;

import java.sql.Timestamp;
import java.util.UUID;

public record ErrorDto(String code, String message, UUID correlationId){
    public ErrorDto {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Invalid code, cannot be blank");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Invalid message, cannot be blank");
        }

        if (correlationId == null) {
            throw new IllegalArgumentException("Invalid correlationId, cannot be null");
        }
    }
}
