package dvm.springbootweb.controller;

import dvm.springbootweb.entity.User;
import dvm.springbootweb.jwt.JwtTokenProvider;
import dvm.springbootweb.payload.request.LoginRequest;
import dvm.springbootweb.payload.request.SignupRequest;
import dvm.springbootweb.payload.response.JwtResponse;
import dvm.springbootweb.payload.response.MessageResponse;
import dvm.springbootweb.security.CustomUserDetail;
import dvm.springbootweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    @Value("$(spring.mail.username)")
    private String fromMail;
    /**
     * Login by google
     * @param request
     * @return
     */
    @PostMapping("/login-by-google")
    public ResponseEntity<?> user(@RequestBody SignupRequest request){
        String email = request.getEmail();
        String username = request.getUserName();
        User user = null;
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
     * @return
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
     * @return
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
     * @return
     */
    @GetMapping("/viewInfor")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> getInfor(@RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        if (userName == null) {
            LOGGER.error("Token is not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("Token is not valid"));
        }
        User user = userService.findByUserName(userName);
        return ResponseEntity.ok(user);
    }
    /**
     * Update infor
     * @param token
     * @param email
     * @param phoneNumber
     * @param address
     * @return
     */
    @PutMapping("/updateInfor")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @RequestParam(required = false) String email, @RequestParam(required = false) String phoneNumber,
                                    @RequestParam(required = false) String address){
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        if (userName == null) {
            LOGGER.error("Token is not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("Token is not valid"));
        }
        User user = userService.findByUserName(userName);
        if(email != null) user.setEmail(email);
        if(phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if(address != null) user.setAddress(address);
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("Update infor successfully"));
    }
    /**
     * Change password
     * @param token
     * @param oldPass
     * @param newPass
     * @return
     */
    @PostMapping("/changepassword")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String token, @RequestParam String oldPass, @RequestParam String newPass){
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        if (userName == null) {
            LOGGER.error("Token is not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("Token is not valid"));
        }
        User user = userService.findByUserName(userName);
        if(passwordEncoder.matches(oldPass, user.getPassword())) {
            String bcreptNewPass = passwordEncoder.encode(newPass);
            user.setPassword(bcreptNewPass);
            userService.saveOrUpdate(user);
            return ResponseEntity.ok(new MessageResponse("change password successfully"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("oldPassword incorrect"));
    }
    /**
     * Forgot password
     * @param username
     * @param email
     * @return
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> fogotPass(@RequestParam String username, @RequestParam String email) {
        User user = userService.findByUserName(username);
        if(user == null){
            LOGGER.error("Username not exists with username: " + username);
            return new ResponseEntity<>(new MessageResponse("Username not exists"),HttpStatus.NOT_FOUND);
        }
        if(!email.equals(user.getEmail())){
            LOGGER.error("Email not exists with email: " + email);
            return new ResponseEntity<>(new MessageResponse("Email not exists"), HttpStatus.NOT_FOUND);
        }
        // create otp
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject("OTP");
        simpleMailMessage.setText(String.valueOf(otp));
        simpleMailMessage.setTo(email);
        mailSender.send(simpleMailMessage);
        user.setOtp(String.valueOf(otp));
        userService.saveOrUpdate(user);
        return new ResponseEntity<>(new MessageResponse("OTP sent to " + email), HttpStatus.OK);
    }
    /**
     * Verify OTP
     * @param otp
     * @param username
     * @return
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestParam String otp,@RequestParam String username){
        User user = userService.findByUserName(username);
        if (user == null) {
            LOGGER.error("Username not exists with username: " + username);
            return new ResponseEntity<>(new MessageResponse("Username not exists"),HttpStatus.NOT_FOUND);
        }
        if(otp.equals(user.getOtp())){
            return ResponseEntity.ok(new MessageResponse("verified"));
        }
        LOGGER.error("Verification failed, please re-enter");
        return new ResponseEntity<>(new MessageResponse("Verification failed, please re-enter"), HttpStatus.NOT_FOUND);
    }
    /**
     * Set new password
     * @param newPass
     * @param username
     * @return
     */
    @PutMapping("/setpassword")
    public ResponseEntity<?> setPassword(@RequestParam String newPass, @RequestParam String username){
        User user = userService.findByUserName(username);
        if (user == null) {
            LOGGER.error("Username not exists with username: " + username);
            return new ResponseEntity<>(new MessageResponse("Username not exists"),HttpStatus.NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(newPass));
        userService.saveOrUpdate(user);
        return ResponseEntity.ok(new MessageResponse("Update password successfully"));
    }
}
