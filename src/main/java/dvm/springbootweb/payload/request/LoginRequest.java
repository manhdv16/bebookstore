package dvm.springbootweb.payload.request;

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
