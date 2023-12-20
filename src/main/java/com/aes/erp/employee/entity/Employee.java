package com.aes.erp.employee.entity;

import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.user_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;

    private String name;

    private String phone;

    @OneToOne
    private Department department;

    @OneToOne
    private RoleNode roleNode;

    @OneToOne
    private Employee reportingManager;

    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;

    @OneToOne
    private User user;

    public Employee(String employeeId, String name, String phone, User user) {
        this.employeeId = employeeId;
        this.name = name;
        this.phone = phone;
        this.user = user;
    }

    public Employee(Long id) {
        this.id = id;
    }
}
