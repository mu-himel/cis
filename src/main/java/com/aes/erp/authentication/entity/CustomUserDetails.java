package com.aes.erp.authentication.entity;

import com.aes.erp.authentication.dto.EmployeeInfoDto;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.user_credential.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Class documentation Comments to be added
 * */
public class CustomUserDetails implements UserDetails {
    private User user;
    private EmployeeInfoDto employee;
    private UserCredential userCredential;

    private String username;
    private String password;
    private boolean active;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super();
        this.user = user;
        this.userCredential = user.getUserCredential();
        this.username = user.getEmailAddress();
        this.password = user.getUserCredential().getPassword();
        this.active = user.getUserCredential().isActive();
        this.authorities = authorities;
    }

    public CustomUserDetails() {

    }


    public EmployeeInfoDto getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = new EmployeeInfoDto();
        this.employee.setId(employee.getId());
        this.employee.setEmployeeId(employee.getEmployeeId());
        this.employee.setName(employee.getName());
        this.employee.setDepartmentId(employee.getDepartment().getId());
        this.employee.setLevel(employee.getDepartment().getLevel());
        this.employee.setParentDepartmentId(employee.getDepartment().getParentDepartment().getId());
        this.employee.setDepartmentName(employee.getDepartment().getName());
        this.employee.setDesignationId(employee.getRoleNode().getId());
        this.employee.setDesignationName(employee.getRoleNode().getName());
        if(employee.getReportingManager() != null) {
            this.employee.setReportingManagerId(employee.getReportingManager().getId());
            this.employee.setReportingManagerName(employee.getReportingManager().getName());
        }
    }

    public void setUser(User user) {
        this.user = user;
        this.username = user.getEmailAddress();
        this.password = user.getUserCredential().getPassword();
        this.active = user.getUserCredential().isActive();
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /*private UserCredential user;

    public CustomUserDetails(UserCredential user) {
        super();
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(user.getRoles().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }*/

    public Long getId(){
        return this.user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userCredential.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        /*return user.isActive();*/
        return true;
    }
}
