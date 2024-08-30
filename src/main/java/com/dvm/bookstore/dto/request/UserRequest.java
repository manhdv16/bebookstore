package com.dvm.bookstore.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
     String email;
     String phoneNumber;
     String address;
}
