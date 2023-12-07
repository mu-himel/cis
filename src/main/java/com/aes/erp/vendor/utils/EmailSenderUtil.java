package com.aes.erp.vendor.utils;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.dto.EmailLoginDto;
import com.aes.erp.vendor.dto.VendorRegistrationMailSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

@Service
public class EmailSenderUtil {
    private final String username = "admin";
    private final String password = "admin123";
    private final String loginUrl = "http://172.17.17.75:9090/api/auth/login";
    private final RestTemplate restTemplate;
    private final String emailUrl = "http://172.17.17.75:9090/api/mail/send-email";


    public EmailSenderUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public void sendMail(VendorRegistrationMailSender mailBody){
        EmailLoginDto dto = new EmailLoginDto();
        dto.setPassword(password);
        dto.setUsername(username);
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, dto, String.class);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String token = jsonNode.path("content").path("token").asText();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<VendorRegistrationMailSender> requestEntity = new HttpEntity<>(mailBody, headers);
            ResponseEntity<String> responseMail = restTemplate.postForEntity(emailUrl, requestEntity, String.class);
        }catch (Exception e){
            throw new AesException("Error occurred while sending the confirmation mail to created vendor");
        }

    }
}
