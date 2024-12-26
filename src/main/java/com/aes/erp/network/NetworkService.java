package com.aes.erp.network;

import com.aes.erp.inventory.dto.response.KeycloakOauth2Dto;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.network.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NetworkService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${keycloak}")
    private String keycloak;

    @Value("${clientId}")
    private String clientId;

    @Value("${clientSecret}")
    private String clientSecret;


    public String getAuthToken(String url, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> req = new HashMap<>();
        req.put("username", username);
        req.put("password", password);
        HttpEntity<Map<String, Object>> payload = new HttpEntity<>(req, headers);
        ResponseEntity<LoginResponse> response = post(url, payload, LoginResponse.class);
        LoginResponse loginResponse = null;
        if(response.getStatusCode() == HttpStatus.OK){
            loginResponse = response.getBody();
            return (loginResponse.getStatus().equals("active"))? loginResponse.getJwt(): null;
        }
        return null;
    }

    public <P,R> ResponseEntity<R> post(String url, HttpEntity<P> payload, Class<R> returnType){
        return restTemplate.postForEntity(url,payload,returnType);
    }
    public <P,R> ResponseEntity<R> put(String url, HttpEntity<P> payload, Class<R> returnType){
        return restTemplate.exchange(url,HttpMethod.PUT,payload,returnType);
    }

    public <P,R> ResponseEntity<R> get(String url, Class<R> returnType){
        return restTemplate.getForEntity(url,returnType);
    }

    public String getKeycloakAccessToken(Organization organization) {
        System.out.println("Keycloak Access Token ");
//        String AUTH_SERVER_URI = "http://172.17.17.254:8080/realms/aesl/protocol/openid-connect/token";
        String AUTH_SERVER_URI = keycloak;
        System.out.println(AUTH_SERVER_URI);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", "password");
        mapForm.add("client_id", clientId);
        // mapForm.add("client_id", "aclbe");
        mapForm.add("username", organization.getServiceUsername());
        mapForm.add("password", organization.getServicePassword());
        mapForm.add("client_secret", clientSecret);
        // mapForm.add("client_secret", "GKCp5BurtrMK9XjgkVZ5wQYh8zLpngfU");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        ResponseEntity<KeycloakOauth2Dto> response = restTemplate.postForEntity(AUTH_SERVER_URI, request , KeycloakOauth2Dto.class);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            return response.getBody().getAccess_token();
        }
        return null;

    }

}
