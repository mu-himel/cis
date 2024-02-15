package com.aes.erp.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.connection.dto.QDto;
import com.aes.erp.connection.service.QService;

@RestController
@RequestMapping("/api/v1/db")
public class ConnectionController {

    @Autowired
    private QService qService;
    
    @PostMapping()
    public ResponseEntity<?> runCustom(
        @RequestBody QDto qDto
    ){

        return new ResponseEntity<>(qService.runCommand(qDto.getMsg()).get(),HttpStatus.OK);
    }
}
