package com.aes.erp.user_management.dto;

import com.aes.erp.user_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private long id;

    /*@Pattern(regexp = ALPHABET_WITH_SPACE, message = "Role Name: Field cannot have numeric or special characters")*/
    private String roleName;

    public static Role getEntity(RoleDTO roleDTO){

        Role role = new Role();
        role.setId(roleDTO.getId());
        role.setRoleName(roleDTO.getRoleName());

        return role;
    }
}
