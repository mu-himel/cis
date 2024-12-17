//package com.aes.erp.indent.cotroller;
//
//import com.aes.erp.exception.AesException;
//import com.aes.erp.indent.dto.request.PrIndentRequestDto;
//import com.aes.erp.indent.dto.request.UpdatePrIndentDetailRequestDto;
//import com.aes.erp.indent.service.PrIndentService;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/pr-indents")
//public class PrIndentController {
//
//    private final PrIndentService prIndentService;
//
//    public PrIndentController(PrIndentService prIndentService) {
//        this.prIndentService = prIndentService;
//    }
//
//    @PostMapping
//    @ApiOperation(value = "Create PR Indent ")
//    public ResponseEntity<?> addIndent(
//            @RequestBody @Valid PrIndentRequestDto prIndentRequestDto
//    ) {
//        prIndentService.createPrIndent(prIndentRequestDto);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    @ApiOperation(value = "Get PR Indent with Pagination")
//    public ResponseEntity<?> getIndents(
//            @RequestParam("page") Optional<Integer> page,
//            @RequestParam("size") Optional<Integer> size,
//            @RequestParam("categoryId") Optional<Long> categoryId,
//            @RequestParam("subCategoryId") Optional<Long> subCategoryId,
//            @RequestParam("startDate") Optional<String> priority
//    ) {
//
//        return new ResponseEntity<>(
//                prIndentService.getAllPrIndents(page, size, categoryId, subCategoryId, priority),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/{id}")
//    @ApiOperation(value = "Get indent by id")
//    public ResponseEntity<?> getPrIndentById(
//            @PathVariable("id") Optional<Long> id
//    ) {
//
//        return new ResponseEntity<>(
//                prIndentService.getPrIndentById(id),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/getByIds")
//    @ApiOperation(value = "Get indent by ids")
//    public ResponseEntity<?> getPrIndentByIds(
//            @RequestParam("ids") Optional<List<Long>> ids
//    ) {
//
//        return new ResponseEntity<>(
//                prIndentService.getPrIndentByIds(ids),
//                HttpStatus.OK
//        );
//    }
//
//    @PutMapping("/{id}")
//    @ApiOperation(value = "Get indent by ids")
//    public ResponseEntity<?> updateOrderDetailsOrderQty(
//            @PathVariable("id") @Valid Optional<Long> id,
//            @RequestBody @Valid UpdatePrIndentDetailRequestDto updateIndentDetailRequestDto
//    ) {
//        id.orElseThrow(() -> new AesException("Id should not be empty"));
//        updateIndentDetailRequestDto.setId(id.get());
//        return new ResponseEntity<>(
//                prIndentService.updateOrderDetailsOrderQty(updateIndentDetailRequestDto),
//                HttpStatus.OK
//        );
//    }
//}
