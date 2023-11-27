package com.aes.erp.user_management.dto;

import com.aes.erp.common.ReferenceObjectDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.aes.erp.config.RegexPattern.ALPHABET_WITH_SPACE;
import static com.aes.erp.config.RegexPattern.EMAIL_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUserDto {

    @Length(min = 6,  message = "Employee ID length should be 6 characters long")
    private String employeeId;

    @Length(min = 0, max = 150, message = "Employee name field can be at max 150 characters long")
    @NotBlank(message = "Employee name is mandatory")
    private String name;

    @Pattern(regexp = EMAIL_PATTERN, message = "Email id is invalid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Length(min = 15,  message = "Phone Number should be 11 characters long")
    private String phone;

    @Length(min = 8,  message = "Password length should be 8 characters long")
    private String password;

    private ReferenceObjectDto department;
    private ReferenceObjectDto designation;
    private ReferenceObjectDto reportingManager;


}
