package com.aes.erp.user_management.user_credential.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserRepository;
import com.aes.erp.user_management.user_credential.dto.PasswordResetTokenDTO;
import com.aes.erp.user_management.user_credential.dto.UserCredentialDTO;
import com.aes.erp.user_management.user_credential.entity.PasswordResetToken;
import com.aes.erp.user_management.user_credential.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.aes.erp.exception.ExceptionMessage.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserCredentialService {
    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserCredentialRepository userCredentialRepository;

    private final UserRepository userRepository;

    private final PasswordTokenRepository passwordTokenRepository;

//    private final EmailSender emailSender;

//    private APIResponse apiResponse = getApiResponse();

    public PasswordResetToken setPassword(PasswordResetTokenDTO passwordResetTokenDTO) {

        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(passwordResetTokenDTO.getToken()).orElse(null);

        if (Objects.nonNull(passwordResetToken) && passwordResetToken.getExpiryDate().compareTo(new Date()) > 0 ) {
            Optional<UserCredential> userCredentialOp = getEmployee(passwordResetToken.getEmailAddress());
            if (userCredentialOp.isPresent()) {
                UserCredential userCredential = userCredentialOp.get();
                userCredential.setPassword(passwordEncoder.encode(passwordResetTokenDTO.getPassword()));
                UserCredential updatedUserCredential = userCredentialRepository.save(userCredential);
//                if (Objects.nonNull(updatedUserCredential)) {
////                    apiResponse.setResponse(USER_CREDENTIAL_SET_SUCCESSFULLY, TRUE, updatedUserCredential, SUCCESS);
//                } else {
////                    apiResponse.setResponse(USER_CREDENTIAL_SET_FAILED, FALSE, NULL, ERROR);
//                }
            }
//            else {
////                apiResponse.setResponse(USER_CREDENTIAL_NOT_FOUND, FALSE, NULL, ERROR);
//            }
        }
        else {
//            apiResponse.setResponse(INVALID_TOKEN, FALSE, NULL, ERROR);
        }
        return passwordResetToken;
    }

    public void update(UserCredential userCredential) {

        Optional<UserCredential> existingUserCredentialOp = getEmployee(userCredential.getEmailAddress());
        if (existingUserCredentialOp.isPresent()) {
            UserCredential existingUserCredential =  existingUserCredentialOp.get();
            existingUserCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
            UserCredential updatedUserCredential = userCredentialRepository.save(existingUserCredential);
        }


//        return Objects.nonNull(updatedUserCredential) ?
//                apiResponse.setResponse(USER_CREDENTIAL_UPDATED_SUCCESSFULLY, TRUE, updatedUserCredential, SUCCESS) :
//                apiResponse.setResponse(USER_CREDENTIAL_UPDATE_FAILED, FALSE, NULL, ERROR);
    }

    public void verifyPassword(UserCredentialDTO userCredentialDTO) {

        UserCredential userCredential = userCredentialRepository.findByEmailAddress(userCredentialDTO.getEmailAddress())
                .orElseThrow(() -> new AesException(RESOURCE_NOT_FOUND));
//        return passwordEncoder.matches(userCredentialDTO.getPassword(), userCredential.getPassword()) ?
//                apiResponse.setResponse(VALID_PASSWORD, TRUE, userCredential, SUCCESS) :
//                apiResponse.setResponse(INVALID_PASSWORD, FALSE, NULL, ERROR);
    }

    public Optional<UserCredential> getEmployee(String emailAddress) {
        return userCredentialRepository.findByEmailAddress(emailAddress);
    }

    public Optional<UserCredential> getEmployeeById(long id) {
        return userCredentialRepository.findById(id);
    }

    public Optional<User> changePassword(String email) {
//        apiResponse.setResponse(USER_NOT_FOUND, FALSE, NULL, ERROR);
        Optional<User> user = userRepository.findByEmailAddress(email);
        if (user.isEmpty()) {
             throw new AesException("User not found");
//            apiResponse.setResponse(EMAIL_SENT,TRUE,NULL,SUCCESS);
        }
        sendEmail(email);
        return user;
    }

    public void sendEmail(String email) {

//        String messageBody = emailSender.buildEmailText(email);
//        emailSender.send(email, messageBody);
    }

    public String generatePassword(Integer length) {

        Long min = (long) Math.pow(10, length - 1);
        Long max = (long) Math.pow(10, length) - 1;

        Random random = new Random();
        String password = Long.toString(random.nextLong(max - min) + min);
        return password;
    }
}
