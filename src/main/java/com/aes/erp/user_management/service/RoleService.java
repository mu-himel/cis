package com.aes.erp.user_management.service;

import com.aes.erp.user_management.dto.RoleDTO;
import com.aes.erp.user_management.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;


    public void update(RoleDTO roleDTO, long id) {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if(Objects.nonNull(existingRole)) {
            existingRole.setRoleName(roleDTO.getRoleName());
            roleRepository.save(existingRole);
        }
    }

    public Role read(String roleName) {
        Role role = roleRepository.findRoleByRoleName(roleName);
        if(Objects.nonNull(role)) {
        }
        return role;
    }

    public Optional<Role> getRoleById(long id) {
        return roleRepository.findById(id);
    }
    public long getIdByRoleName(String roleName) {return roleRepository.findRoleByRoleName(roleName).getId();}

    public Role getRoleByRoleName(String roleName) {return roleRepository.findRoleByRoleName(roleName);}

    public List<Role> getRoleByRoleNames(List<String> roleNames) {
        return roleRepository.findAllByRoleNames(roleNames);
    }

    public void createRoles(List<String> roleNames) {
        List<Role> roles = roleNames.stream().map(name-> new Role(name)).collect(Collectors.toList());
        roleRepository.saveAll(roles);
    }
}
