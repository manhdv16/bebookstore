package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.dto.request.SignupRequest;
import com.dvm.bookstore.dto.request.UserRequest;
import com.dvm.bookstore.dto.response.UserResponse;
import com.dvm.bookstore.entity.*;
import com.dvm.bookstore.exception.AppException;
import com.dvm.bookstore.exception.ErrorCode;
import com.dvm.bookstore.repository.UserRepository;
import com.dvm.bookstore.service.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * UserServiceImpl class implements UserService interface
 * @see UserService
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final EntityManager entityManager;

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final RoleService roleService;
    final ModelMapper modelMapper;
    final JavaMailSender mailSender;
    final PasswordService passwordService;
    final OrderService orderService;
    final CommentService commentService;
    @Value("${spring.mail.username}")
    String fromMail;

    @Override
    public User findByUserName(String username) {
        return userRepository.findByUserName(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
    }
    @Override
    public User update(String username, UserRequest request) {
        User user = findByUserName(username);
        modelMapper.map(request, user);
        return userRepository.save(user);
    }

    @Override
    public void saveOrUpdate(User user) {
        userRepository.save(user);
    }

    @Override
    public Boolean existsByUserName(String username) {
        return userRepository.existsUserByUserName(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    /**
     * signUp method is used to create a new user
     * @param request
     * @return User
     */
    @Override
    public User signUp(SignupRequest request) {
        User user = new User();
        if (request.getUserName() != null) user.setUserName(request.getUserName());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        user.setCreated(new Date());
        // Add roles
        Set<String> strRole = request.getListRoles();
        Set<Role> listRoles = new HashSet<>();
        if (strRole == null) {
            Role userRole = roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: role is not found"));
            listRoles.add(userRole);
        } else {
            strRole.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: role is not found"));
                        listRoles.add(adminRole);
                    case "manager":
                        Role managerRole = roleService.findByRoleName(ERole.ROLE_MANAGER).orElseThrow(() -> new RuntimeException("Error: role is not found"));
                        listRoles.add(managerRole);
                    case "user":
                        Role userRole = roleService.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: role is not found"));
                        listRoles.add(userRole);
                }
            });
        }
        user.setListRoles(listRoles);
        return user;
    }

    @Override
    public void updatePassword(String username, String oldPass, String newPass) {
        User user = findByUserName(username);
        if(passwordEncoder.matches(oldPass, user.getPassword())) {
            String bcreptNewPass = passwordEncoder.encode(newPass);
            user.setPassword(bcreptNewPass);
            userRepository.save(user);
        } else{
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    @Override
    public void sendNewPasswordForUser(String email, User user) {
        String newPassword = passwordService.generateRandomPassword();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject("Your New Password");
        simpleMailMessage.setText("Your new password is: " + newPassword);
        simpleMailMessage.setTo(email);

        mailSender.send(simpleMailMessage);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void insertBatchUser() {
        int batchSize = 100;
        for(int i =0;i<1000;i++) {
            User u = User.builder()
                    .userName("user" + i)
                    .password(passwordEncoder.encode("123456"))
                    .email("user" + i + "@gmail.com")
                    .phoneNumber("0123456789" + i)
                    .address("Address " + i)
                    .build();
            entityManager.persist(u);
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Override
    public CompletableFuture<UserResponse> getProfile(String username) {
        User user = findByUserName(username);

        CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(
                () -> orderService.getAllOrderByUser(user)
        );
        CompletableFuture<List<Comment>> commentsFuture = CompletableFuture.supplyAsync(
                () -> (List<Comment>) commentService.findAllCommentByUserId(user.getUserId())
        );

        // Combine the results of both futures to create a UserResponse
        return ordersFuture.thenCombineAsync(commentsFuture, (orders, comments) -> {
            UserResponse userResponse = UserResponse.builder()
                    .comments(comments)
                    .orders(orders)
                    .username(user.getUserName())
                    .email(user.getEmail())
                    .build();
            return userResponse;
        });
    }
}
