package com.aes.erp.vendor.utils;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.dto.EmailLoginDto;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorRegistrationMailSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void sendMail(VendorRegistrationMailSender mailBody){
        ObjectMapper objectMapper = new ObjectMapper();
        EmailLoginDto dto = new EmailLoginDto();
        dto.setUsername(emailConfig.getUsername());
        dto.setPassword(emailConfig.getPassword());
        log.info("Email server logging-in at "+emailConfig.getAddress().concat(loginUrl));
        log.info("username "+emailConfig.getUsername());
        log.info("username "+emailConfig.getPassword());
        log.info("Email server logging-in at "+emailConfig.getAddress().concat(emailUrl));

        try{
            String payload = objectMapper.writeValueAsString(dto);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    emailConfig.getAddress().concat(loginUrl), dto, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String token = jsonNode.path("content").path("token").asText();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            log.info(objectMapper.writeValueAsString(mailBody));
            HttpEntity<VendorRegistrationMailSender> requestEntity = new HttpEntity<>(mailBody, headers);
            ResponseEntity<String> responseMail = restTemplate.postForEntity(
                    emailConfig.getAddress().concat(emailUrl), requestEntity, String.class);
                    System.out.println(responseMail.getStatusCode());
                    System.out.println(responseMail.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new AesException("Error occurred while sending the confirmation mail to created vendor");
        }

    }
}
