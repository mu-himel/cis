package com.aes.erp.network;

import com.aes.erp.network.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NetworkService {

    @Autowired
    private RestTemplate restTemplate;

    public String getAuthToken(String url, String username, String password){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> req = new HashMap<>();
        req.put("username",username);
        req.put("password",password);
        HttpEntity<Map<String,Object>> payload = new HttpEntity<>(req,headers);
        ResponseEntity<LoginResponse> response = post(url,payload, LoginResponse.class);
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

}
