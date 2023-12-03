package com.aes.erp.vendor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CallbackController {

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, String> callbackData) {
        // Handle the callback data
        System.out.println("Callback received. Data: " + callbackData);

        // Perform any processing or business logic here

        // Send a response
        return new ResponseEntity<>("Callback handled successfully", HttpStatus.OK);
    }
}