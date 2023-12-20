package com.aes.erp.module_access.entity;

import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.user_management.entity.User;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "module_permissions")
public class ModuleAccessPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Department department;

    @ManyToOne
    private RoleNode designation;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ModuleAccess moduleAccess;

    @OneToMany(mappedBy = "moduleAccessPermission",cascade = CascadeType.ALL)
    private List<ModuleAccessFilter> filters = new ArrayList<>();

    @OneToMany(mappedBy = "moduleAccessPermission",cascade = CascadeType.ALL)
    private List<ModuleAccessVerifierConfig> verifiers = new ArrayList<>();



    private Boolean createPermission;
    private Boolean readPermission;
    private Boolean updatePermission;
    private Boolean deletePermission;

//    private Boolean rolePermissionDelete;
//    private Boolean userPermissionDelete;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
