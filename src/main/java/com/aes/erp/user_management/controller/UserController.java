package com.aes.erp.user_management.controller;

import com.aes.erp.user_management.dto.EmployeeUserDto;
import com.aes.erp.user_management.dto.UserDTO;
import com.aes.erp.user_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    public static final int SIZE = 10;
    public static final int PAGE = 0;
    private final UserService userService;

    /**
     * static is used so that it only happens once
     */
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/users")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody EmployeeUserDto userDto) {
        userService.create(userDto);
//        APIResponse apiResponse =
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

//    @PostMapping("/users/{id}/add-organizations")
//    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_SYS_ADMIN')")
//    public ResponseEntity<?> addOrganizations(@Valid @RequestBody OrganizationAddRemoveDTO organizationAddRemoveDTO, @PathVariable long id) {
//        APIResponse apiResponse = userService.addOrganizations(organizationAddRemoveDTO.getOrganizations(),id);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
//    }
//
//    @DeleteMapping("/users/{id}/remove-organizations")
//    @PreAuthorize("hasAnyAuthority('removeOrganizations','ROLE_SYS_ADMIN')")
//    public ResponseEntity<?> removeOrganizations(@Valid @RequestBody OrganizationAddRemoveDTO organizationAddRemoveDTO, @PathVariable long id) {
//
//        APIResponse apiResponse = userService.removeOrganizations(organizationAddRemoveDTO.getOrganizations(),id);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
//    }
//
//    @PutMapping("/users/{id}/update-organizations")
//    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_SYS_ADMIN')")
//    public ResponseEntity<?> updateOrganizations(@Valid @RequestBody OrganizationAddRemoveDTO organizationAddRemoveDTO, @PathVariable long id) {
//
//        APIResponse apiResponse = userService.updateOrganizations(organizationAddRemoveDTO.getOrganizations(),id);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
//    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE','ROLE_SYS_ADMIN','ROLE_VENDOR')")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDto, @PathVariable long id) {

        userService.update(userDto, id);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_SYS_ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable long id) {

        userService.deactivate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
    }

    @PutMapping("/users/{id}/activate")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_SYS_ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable long id) {

        userService.activate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        return apiResponse.isSuccess()? ok(apiResponse) : badRequest().body(apiResponse);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMPLOYEE','ROLE_SYS_ADMIN','ROLE_VENDOR')")
    public ResponseEntity<?> getUserDetails(@PathVariable int id) {

//        APIResponse apiResponse = ;
//        if (apiResponse.isSuccess()) {
//            return ok(apiResponse);
//        }
//        return badRequest().body(apiResponse);
        return new ResponseEntity<>(userService.read(id),HttpStatus.OK);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_SYS_ADMIN')")
    public ResponseEntity<?> getAllUsers(@RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size")Optional<Integer> size) {

//        APIResponse apiResponse = ;
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);

        return new ResponseEntity<>(
                userService.read("VENDOR", page.orElse(PAGE),size.orElse(SIZE)),
                HttpStatus.OK);
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_SYS_ADMIN')")
    public ResponseEntity<?> getAllEmployees(@RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size")Optional<Integer> size) {

//        APIResponse apiResponse = ;
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);

        return new ResponseEntity<>(
                userService.read("EMPLOYEE", page.orElse(PAGE),size.orElse(SIZE)),
                HttpStatus.OK);
    }

//    @GetMapping("/users/{id}/organizations")
//    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_SYS_ADMIN')")
//    public ResponseEntity<?> getOrganizations(@PathVariable long id) {
//
//        APIResponse apiResponse = userService.readOrganizations(id);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
//    }
}
