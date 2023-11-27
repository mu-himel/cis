package com.aes.erp.organogram_system.user_assignment_system.entity;

import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.user_management.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne//(fetch = FetchType.LAZY)
    //@Basic(fetch = FetchType.LAZY)
    private Department department;

    @ManyToOne//(fetch = FetchType.LAZY)
    //@Basic(fetch = FetchType.LAZY)
    private RoleNode roleNode;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "parent_user_assignment_id", referencedColumnName = "id")
    private UserAssignment parentUserAssignment;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentUserAssignment", cascade = CascadeType.REMOVE)
    private List<UserAssignment> childs = new ArrayList<>();

}
