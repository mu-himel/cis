package com.aes.erp.indent.cotroller;

import com.aes.erp.exception.AesException;
import com.aes.erp.indent.dto.request.IndentRequestDto;
import com.aes.erp.indent.dto.request.MoveIndentRequestDto;
import com.aes.erp.indent.dto.request.UpdatePrIndentDetailRequestDto;
import com.aes.erp.indent.service.IndentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/indents")
public class IndentController {

    private final IndentService indentService;

    public IndentController(IndentService indentService) {
        this.indentService = indentService;
    }

    @PostMapping
    @ApiOperation(value = "Create Indent ")
    public ResponseEntity<?> addIndent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestHeader("uri") String uri,
            @RequestBody @Valid IndentRequestDto indentRequestDto
    ) {
        indentService.createIndent(token, uri, indentRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping
    @ApiOperation(value = "Get Indent with Pagination")
    public ResponseEntity<?> getIndents(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("categoryId") Optional<Long> categoryId,
            @RequestParam("subCategoryId") Optional<Long> subCategoryId,
            @RequestParam("startDate") Optional<String> priority
    ) {

        return new ResponseEntity<>(
                indentService.getAllIndents(page, size, categoryId, subCategoryId, priority),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get indent by id")
    public ResponseEntity<?> getIndentById(
            @PathVariable("id") Optional<Long> id
    ) {

        return new ResponseEntity<>(
                indentService.getIndentById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/getByIds")
    @ApiOperation(value = "Get indent by ids")
    public ResponseEntity<?> getIndentByIds(
            @RequestParam("indentIds") Optional<List<Long>> indentIds
    ) {

        return new ResponseEntity<>(
                indentService.getIndentByIds(indentIds),
                HttpStatus.OK
        );
    }

    @PutMapping("/move-indents")
    @ApiOperation(value = "Get indent by ids")
    public ResponseEntity<?> moveIndentByIds(
            @RequestBody @Valid MoveIndentRequestDto moveIndent
    ) {

        return new ResponseEntity<>(
                indentService.moveIndentByIds(moveIndent),
                HttpStatus.OK
        );
    }

}
