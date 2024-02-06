package com.aes.erp.user_management.service;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.employee.service.EmployeeService;
import com.aes.erp.exception.AesException;
import com.aes.erp.organogram_system.dto.APIResponse;
import com.aes.erp.authentication.Authorization;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.user_assignment_system.UserAssignmentOps;
import com.aes.erp.organogram_system.user_assignment_system.dto.UserAssignmentDTO;
import com.aes.erp.user_management.dto.EmployeeUserDto;
import com.aes.erp.user_management.dto.UserDTO;
import com.aes.erp.user_management.entity.Role;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.entity.UserCredentialToRole;
import com.aes.erp.user_management.user_credential.entity.PasswordResetToken;
import com.aes.erp.user_management.user_credential.entity.UserCredential;
import com.aes.erp.user_management.user_credential.service.PasswordTokenRepository;
import com.aes.erp.user_management.user_credential.service.UserCredentialRepository;
import com.aes.erp.vendor.dto.VendorDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.aes.erp.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class UserService {


    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

//    private final EmailSender emailSender;
//
    private APIResponse apiResponse = APIResponse.getApiResponse();

    private final Authorization authorization;
    private final PasswordTokenRepository passwordTokenRepository;
    private final UserCredentialToRoleRepository userCredentialToRoleRepository;

    private final UserCredentialRepository userCredentialRepository;

    private final RoleService roleService;

    private final UserAssignmentOps userAssignmentOps;


    private final EmployeeService employeeService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Transactional(rollbackFor = Exception.class)
    public void create(EmployeeUserDto userDto) {
//        Employee reportingManager = null;
//        if(userDto.getReportingManager()!=null && userDto.getReportingManager().getId()!=null) {
//                Optional<Employee> employeeOptional = employeeService.getEmployeeByUserId(userDto.getReportingManager().getId());
//                if (employeeOptional.isEmpty()) {
//                    throw new AesException("Reporting Manager Not Found");
//                }
//                reportingManager = employeeOptional.get();
//
//        }

        User user = new User();
        Optional<User> existUserChecking = userRepository.findByEmailAddress(userDto.getEmail());
        if(!existUserChecking.isEmpty()){
            throw new AesException(EMAIL_ALREADY_EXIST);
        }
        user.setEmailAddress(userDto.getEmail());
        user.setFirstName(userDto.getName());

        UserCredential userCredential = new UserCredential();
        userCredential.setEmailAddress(userDto.getEmail());
        if(userDto.getPassword().isEmpty()){
            userCredential.setPassword(passwordEncoder.encode("123456"));
        }else{
            userCredential.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        /*userCredential.setRoles(userDto.getRoles());*/
        userCredential.setActive(true);
        user.setUserCredential(userCredential);

        UserCredentialToRole userCredentialToRole = new UserCredentialToRole();
        userCredentialToRole.setUserCredential(userCredential);
        String role = (userDto.getEmployeeId()!=null)? "EMPLOYEE" : "USER";
        userCredentialToRole.setRole((Role) roleService.read(role));
        userCredentialToRoleRepository.save(userCredentialToRole);
        user = userRepository.save(user);
        Employee employee = new Employee(
                userDto.getEmployeeId(),
                userDto.getName(),
                userDto.getPhone(),
                user
        );
        employee.setEmployeeType(userDto.getEmployeeType());
        employee.setDepartment(null);
        employee.setRoleNode(null);
        employee.setReportingManager(null);
//        userAssignmentOps.assignUser(new UserAssignmentDTO(
//                user.getId(),
//                userDto.getDepartment().getId(),
//                userDto.getDesignation().getId(),
//                null
//        ));
        employeeService.createEmployee(employee);
//      if (Objects.nonNull(createdUser)) {
//            emailSender.send(userDto.dtoToEntity(userDto).getEmailAddress(),buildEmailText(userDto.dtoToEntity(userDto).getEmailAddress()));
//      }



    }

    public User createVendorUserAccount(VendorDto vendorDto){

        Optional<User> existUserChecking = userRepository.findByEmailAddress(vendorDto.getEmail());
        if(!existUserChecking.isEmpty()){
            throw new AesException(EMAIL_ALREADY_EXIST);
        }
        User user = new User();
        user.setEmailAddress(vendorDto.getEmail());
        user.setFirstName(vendorDto.getName());

        UserCredential userCredential = new UserCredential();
        userCredential.setEmailAddress(vendorDto.getEmail());
        if(vendorDto.getPassword().isEmpty()){
            userCredential.setPassword(passwordEncoder.encode("123456"));
        }else{
            userCredential.setPassword(passwordEncoder.encode(vendorDto.getPassword()));
        }
        /*userCredential.setRoles(userDto.getRoles());*/
        userCredential.setActive(true);
        user.setUserCredential(userCredential);

        UserCredentialToRole userCredentialToRole = new UserCredentialToRole();
        userCredentialToRole.setUserCredential(userCredential);
        String role = "VENDOR";
        userCredentialToRole.setRole(roleService.read(role));
        userCredentialToRoleRepository.save(userCredentialToRole);
        return userRepository.save(user);
    }

    public void update(UserDTO userDto, long id) {
        Optional<User> existingUserOp = userRepository.findById(id);
        if(existingUserOp.isEmpty()) {
            throw new AesException(USER_NOT_FOUND);
        }
        User existingUser = existingUserOp.get();
        if (!authorization.isAuthorized(existingUser)) {
            throw new AesException(PERMISSION_DENIED);
        }
        //            &&
        existingUser.setEmailAddress(userDto.getEmailAddress());
        existingUser.getUserCredential().setEmailAddress(userDto.getEmailAddress());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        // existingUser.setRoles(userDto.getRoles());

        userRepository.save(existingUser);

    }

    public Optional<User> read(long id) {
        Optional<User> existingUserOp = userRepository.findById(id);
        if(existingUserOp.isEmpty()){
            throw new AesException(USER_NOT_FOUND);
        }
        User existingUser = existingUserOp.get();

        if(!existingUser.getUserCredential().isActive()) {
             throw new AesException(ACTIVATE_YOUR_USER_ACCOUNT);

        }

        if(!authorization.isAuthorized(existingUser)) {
            throw new AesException(PERMISSION_DENIED);
        }

        return Optional.ofNullable(existingUser);
    }

    public Optional<User> deactivate(long id) {
        Optional<User> existingUserOp = userRepository.findById(id);
        if(existingUserOp.isEmpty()) {
            throw new AesException(USER_NOT_FOUND);
        }
        User existingUser = existingUserOp.get();
            existingUser.getUserCredential().setActive(false);
            userRepository.save(existingUser);
        return Optional.ofNullable(existingUser);
    }

    public Optional<User> activate(long id) {
        Optional<User> existingUserOp = userRepository.findById(id);
        if(existingUserOp.isEmpty()){
            throw new AesException(USER_NOT_FOUND);
        }

            User existingUser = existingUserOp.get();
            existingUser.getUserCredential().setActive(true);
            userRepository.save(existingUser);


        return Optional.ofNullable(existingUser);
    }


    public Page<?> read(String roleName, Integer page,
                                     Integer size) {

        Pageable pageable = PageRequest.of(page,size);
        Page<?> existingUsers = userRepository.findAllByRoleName(roleName,pageable);
        return existingUsers;
    }

    public Page<?> read(String roleName, Integer page,
                        Integer size,
                        Optional<String> employeeId,
                        Optional<String> name,
                        Optional<String> email,
                        Optional<String> phone,
                        Optional<String> employeeType
    ) {

        Pageable pageable = PageRequest.of(page,size);
        Page<?> existingUsers = userRepository.findAllByRoleName(roleName,
                employeeId.orElse(null),
                name.orElse(null),
                email.orElse(null),phone.orElse(null),employeeType.orElse(null),pageable
        );
        return existingUsers;
    }

    public User getUserByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user;
    }

    public String buildEmailText(String emailAddress) {
        String randomToken = UUID.randomUUID().toString();
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE,3);
        Date currentDatePlusThree = c.getTime();
        String url = "http://192.168.103.113:4200/auth/change-password-request?token=" + randomToken ;
        String body = "<HTML><body> <a href=" + url + ">" + "Set password</a></body></HTML>";
        passwordTokenRepository.save(new PasswordResetToken(emailAddress, currentDatePlusThree, randomToken));
        return (body);
    }

    public User getCurrentUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) return null;
        return userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
    }

    @Transactional
    public void createSuperAdmin(String username, String pass) {
        User user = new User();
        user.setEmailAddress(username);
        user.setFirstName("Super");
        user.setLastName("Admin");

        UserCredential userCredential = new UserCredential();
        userCredential.setEmailAddress(user.getEmailAddress());
        userCredential.setPassword(passwordEncoder.encode(pass));
        userCredential.setActive(true);
        userCredentialRepository.save(userCredential);
        user.setUserCredential(userCredential);


        UserCredentialToRole userCredentialToRole = new UserCredentialToRole();
        userCredentialToRole.setUserCredential(userCredential);
        Role role = roleService.read("SYS_ADMIN");
        userCredentialToRole.setRole(role);
        userCredentialToRoleRepository.save(userCredentialToRole);
        user = userRepository.save(user);
        Employee employee = new Employee(
                employeeService.getNextEmployeeId(),
                user.getFirstName()+ " "+user.getLastName(),
                null,
                user
        );
//        employee.setWarehouse(null);
        employee.setDepartment(null);
        employee.setRoleNode(null);
        employee.setReportingManager(null);
        employeeService.createEmployee(employee);
    }

    public User getUserByEmail(String s) {
        User user = userRepository.findByEmailAddress(s).orElse(null);
        return user;
    }
}
