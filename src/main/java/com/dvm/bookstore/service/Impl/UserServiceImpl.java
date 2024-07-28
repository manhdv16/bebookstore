package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.entity.ERole;
import com.dvm.bookstore.entity.Role;
import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.dto.request.SignupRequest;
import com.dvm.bookstore.repository.UserRepository;
import com.dvm.bookstore.service.RoleService;
import com.dvm.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * UserServiceImpl class implements UserService interface
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    @Override
    public User findByUserName(String username) {
        return userRepository.findByUserName(username);
    }
    @Override
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
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
}
