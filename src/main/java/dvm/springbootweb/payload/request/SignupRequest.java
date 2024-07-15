package dvm.springbootweb.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * SignupRequest class is used to receive data from client when they want to register a new account
 * It contains userName, password, email, phoneNumber
 */
@Getter
@Setter
public class SignupRequest {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date created;
    private byte gender;
    private String address;
    private Set<String> listRoles;
}
