package com.dvm.bookstore.payload.response;

import lombok.Getter;
import lombok.Setter;

/**
 * MessageResponse
 */
@Getter
@Setter
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
