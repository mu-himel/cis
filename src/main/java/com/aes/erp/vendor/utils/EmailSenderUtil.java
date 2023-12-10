package com.aes.erp.vendor.utils;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.dto.EmailLoginDto;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorRegistrationMailSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

@Service
@Slf4j
public class EmailSenderUtil {

    private final String loginUrl = "/auth/login";
    private final RestTemplate restTemplate;
    private final String emailUrl = "/mail/send-email";

    @Autowired
    private EmailConfig emailConfig;


    public EmailSenderUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public void sendMail(VendorRegistrationMailSender mailBody){
        EmailLoginDto dto = new EmailLoginDto();
        dto.setPassword(emailConfig.getUsername());
        dto.setUsername(emailConfig.getPassword());
        log.info("Email server logging-in at "+emailConfig.getAddress());
        ResponseEntity<String> response = restTemplate.postForEntity(
                emailConfig.getAddress().concat(loginUrl), dto, String.class);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String token = jsonNode.path("content").path("token").asText();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<VendorRegistrationMailSender> requestEntity = new HttpEntity<>(mailBody, headers);
            ResponseEntity<String> responseMail = restTemplate.postForEntity(
                    emailConfig.getAddress().concat(emailUrl), requestEntity, String.class);
        }catch (Exception e){
            throw new AesException("Error occurred while sending the confirmation mail to created vendor");
        }

    }
}
