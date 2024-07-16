package com.dvm.bookstore.payload.request;

import lombok.Getter;
import lombok.Setter;

/**
 * LoginRequest
 */
@Getter
@Setter
public class LoginRequest {
    private String userName;
    private String password;
}
