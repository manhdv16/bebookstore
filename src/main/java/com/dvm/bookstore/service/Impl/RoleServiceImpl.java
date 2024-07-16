package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.entity.ERole;
import com.dvm.bookstore.repository.RoleRepository;
import com.dvm.bookstore.service.RoleService;
import com.dvm.bookstore.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * RoleServiceImpl class implements RoleService interface
 * @see RoleService
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public Optional<Role> findByRoleName(ERole roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
