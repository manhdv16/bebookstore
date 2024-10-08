package com.dvm.bookstore.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * JwtResponse
 */
@Setter
@Getter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String userName;
    private String email;
    private List<String> listRoles;

    public JwtResponse(String token, String userName, String email, List<String> listRoles) {
        this.token = token;
        this.userName = userName;
        this.email = email;
        this.listRoles = listRoles;
    }
}
