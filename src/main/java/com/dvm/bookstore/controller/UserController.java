package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.LoginRequest;
import com.dvm.bookstore.dto.request.SignupRequest;
import com.dvm.bookstore.dto.request.UserRequest;
import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.dto.response.JwtResponse;
import com.dvm.bookstore.dto.response.MessageResponse;
import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.jwt.JwtTokenProvider;
import com.dvm.bookstore.security.CustomUserDetail;
import com.dvm.bookstore.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {
    UserService userService;
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;
    PasswordEncoder passwordEncoder;
    static Logger LOGGER = LogManager.getLogger(UserController.class);

    /**
     * Login by google
     * @param request
     * @return jwt
     */
    @PostMapping("/login-by-google")
    public ResponseEntity<?> user(@RequestBody SignupRequest request){
        String email = request.getEmail();
        String username = request.getUserName();
        User user;
        if(!userService.existsByUserName(username) && !userService.existsByEmail(email)) {
            user = userService.signUp(request);
            userService.saveOrUpdate(user);
        }
        user = userService.findByUserName(username);
        String jwt = jwtTokenProvider.generateToken(username);
        List<String> listRoles = user.getListRoles().stream()
                .map(item -> item.getRoleName().name()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, username, email, listRoles));
    }
    /**
     * Sign in
     * @param request
     * @return jwt
     */
    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(customUserDetail.getUsername());
        List<String> listRoles = customUserDetail.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, customUserDetail.getUsername(), customUserDetail.getEmail(), listRoles));
    }
    /**
     * Sign up
     * @param request
     * @return message
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        if(userService.existsByUserName(request.getUserName())){
            LOGGER.error("Username already exists with username: " + request.getUserName());
            return new ResponseEntity<>(new MessageResponse("Username already exists"), HttpStatus.BAD_REQUEST);
        }
        if(userService.existsByEmail(request.getEmail())){
            LOGGER.error("Email already exists with email: " + request.getEmail());
            return new ResponseEntity<>(new MessageResponse("Email already exists"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.signUp(request);
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
    /**
     * View infor
     * @param token
     * @return user
     */
    @GetMapping("/viewInfor")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> getInfor(@RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        User user = userService.findByUserName(userName);
        return ResponseEntity.ok(user);
    }
    /**
     * Update infor
     * @param token
     * @param request
     * @return message
     */
    @PutMapping("/updateInfor")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public APIResponse<?> update(@RequestHeader("Authorization") String token, UserRequest request){
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        userService.update(userName, request);
        return APIResponse.builder()
                .code(200)
                .message("Update successfully")
                .build();
    }
    /**
     * Change password
     * @param token
     * @param oldPass
     * @param newPass
     * @return message
     */
    @PostMapping("/changepassword")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public APIResponse<?> changePassword(@RequestHeader("Authorization") String token, @RequestParam String oldPass, @RequestParam String newPass){
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        userService.updatePassword(userName, oldPass, newPass);
        return APIResponse.builder()
                .code(200)
                .message("Change password successfully")
                .build();
    }
    /**
     * Forgot password
     * @param username
     * @param email
     * @return  message
     */
    @PostMapping("/forgot-password")
    public APIResponse<?> fogotPass(@RequestParam String username, @RequestParam String email) {
        User user = userService.findByUserName(username);
        if(user == null){
            LOGGER.error("Username not exists with username: " + username);
            return APIResponse.builder()
                    .code(404)
                    .message("Username not exists")
                    .build();
        }
        if(!email.equals(user.getEmail())){
            LOGGER.error("Email not exists with email: " + email);
            return APIResponse.builder()
                    .code(404)
                    .message("Email not exists")
                    .build();
        }
        userService.sendNewPasswordForUser(email, user);

        return APIResponse.builder()
                .code(200)
                .message("Send new password successfully")
                .build();
    }

}
