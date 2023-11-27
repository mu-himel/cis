package com.aes.erp.user_management.dto;

import com.aes.erp.user_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.aes.erp.config.RegexPattern.ALPHABET_WITH_SPACE;
import static com.aes.erp.config.RegexPattern.EMAIL_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class UserDTO {

    private long id;

    @Length(min = 0, max = 50, message = "First name field can be at max 50 characters long")
    @Pattern(regexp = ALPHABET_WITH_SPACE, message = "First name field cannot have numeric or special characters")
    @NotBlank(message = "firstName is mandatory")
    private String firstName;

    @Length(min = 0, max = 50, message = "Last name field can be at max 50 characters long")
    @Pattern(regexp = ALPHABET_WITH_SPACE, message = "Last name field cannot have numeric or special characters")
    @NotBlank(message = "lastName is mandatory")
    private String lastName;

    @Pattern(regexp = EMAIL_PATTERN, message = "email id is invalid")
    @NotBlank(message = "email is mandatory")
    private String emailAddress;


    private String password;

//    private List<Long> organizations;
//
//    private List<Long> organizationFiles;

    public User dtoToEntity(UserDTO userDTO){

        User user = new User();
        user.setId(userDTO.getId());
        user.setEmailAddress(userDTO.getEmailAddress());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        return user;
    }

    public static UserDTO getUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;
    }

    public static User getUserEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmailAddress(userDTO.getEmailAddress());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        return user;
    }
}

