package com.aes.erp.verification.controller;

import com.aes.erp.verification.dto.request.CommentDto;
import com.aes.erp.verification.service.CommentService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody @ApiParam(value = "Comment Request Body") CommentDto commentDto){
        commentService.addComment(commentDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
