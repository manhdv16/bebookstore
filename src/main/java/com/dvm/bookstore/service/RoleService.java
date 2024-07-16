package com.dvm.bookstore.service;

import com.dvm.bookstore.entity.ERole;
import com.dvm.bookstore.entity.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByRoleName(ERole roleName);
}
