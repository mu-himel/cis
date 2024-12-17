//package com.aes.erp.demand.controller;
//
//import com.aes.erp.authentication.JwtUtil;
//import com.aes.erp.demand.dto.request.DemandReceiveDto;
//import com.aes.erp.demand.dto.request.DemandRequestDto;
//import com.aes.erp.demand.dto.request.ReviewDto;
//import com.aes.erp.demand.service.DemandService;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/demands")
//public class DemandController {
//
//    @Autowired
//    private DemandService demandService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping
//    @ApiOperation(value = "Create New Demand")
//    public ResponseEntity<?> createItemDemand(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
//            @RequestHeader("uri") String uri,
//            @RequestBody @Valid DemandRequestDto demandRequestDto){
//        demandService.createDemand(token, uri, demandRequestDto);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    @GetMapping("/my")
//    @ApiOperation(value = "Get My Demands")
//    public ResponseEntity<?> getMyDemands(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
//                                          @RequestParam("page") Optional<Integer> page,
//                                          @RequestParam("size") Optional<Integer> size){
//
//        Long id = jwtUtil.extractId(token).getId();
//        return new ResponseEntity<>(
//                demandService.getMyDemands(id,page,size),
//            HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/{id}")
//    @ApiOperation(value = "Get Demand Detail")
//    public ResponseEntity<?> getDemandDetail(@PathVariable("id") Long id){
//        return new ResponseEntity<>(
//                demandService.getDemandDetail(id),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping
//    @ApiOperation(value = "Get All Pending Demands")
//    public ResponseEntity<?> getPendingDemands(
//                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
//                    @RequestParam("page") Optional<Integer> page,
//                    @RequestParam("size") Optional<Integer> size){
//
//        return new ResponseEntity<>(
//                demandService.getAllDemands(token, page,size),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/pending-verification")
//    @ApiOperation(value = "Get All Pending Verification Demands")
//    public ResponseEntity<?> getPendingVerificationDemands(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
//            @RequestParam("page") Optional<Integer> page,
//            @RequestParam("size") Optional<Integer> size){
//
//        return new ResponseEntity<>(
//                demandService.getAllPendingVerificationDemands(token, page,size),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/pending-approval")
//    @ApiOperation(value = "Get All Pending Approval Demands")
//    public ResponseEntity<?> getPendingApprovalDemands(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
//            @RequestParam("page") Optional<Integer> page,
//            @RequestParam("size") Optional<Integer> size){
//
//        return new ResponseEntity<>(
//                demandService.getAllPendingApprovalDemands(token, page,size),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/close")
//    @ApiOperation(value = "Get All Closed Demands")
//    public ResponseEntity<?> getCloseDemands(
//            @RequestParam("page") Optional<Integer> page,
//            @RequestParam("size") Optional<Integer> size){
//
//        return new ResponseEntity<>(
//                demandService.getAllCloseDemands(page,size),
//                HttpStatus.OK
//        );
//    }
//
//    @PostMapping("/receive")
//    @ApiOperation(value = "Receive Demand By Initiator")
//    public ResponseEntity<?> demandReceive(
//            @RequestHeader("Authorization") String token,
//            @RequestBody DemandReceiveDto demandReceiveDto
//    ){
//        demandService.receiveDemandItem(token, demandReceiveDto);
//        return new ResponseEntity<>(
//                HttpStatus.NO_CONTENT
//        );
//    }
//
//    @PostMapping("/sent")
//    @ApiOperation(value = "Sent Demand By Store")
//    public ResponseEntity<?> demandReceive(
//            @RequestBody DemandReceiveDto demandReceiveDto
//    ){
//        demandService.sentDemandItem(demandReceiveDto);
//        return new ResponseEntity<>(
//                HttpStatus.NO_CONTENT
//        );
//    }
//
//    @PostMapping("/decline")
//    @ApiOperation(value = "Reject Demand By Initiator")
//    public ResponseEntity<?> declineDemand(
//            @RequestHeader("Authorization") String token,
//            @RequestBody DemandReceiveDto demandReceiveDto
//    ){
//        demandService.declineDemandItem(token,demandReceiveDto);
//        return new ResponseEntity<>(
//                HttpStatus.NO_CONTENT
//        );
//    }
//
//    @PutMapping("/reject")
//    @ApiOperation(value = "Reject Demand By Initiator")
//    public ResponseEntity<?> rejectDemand(
//            @RequestHeader("Authorization") String token,
//            @RequestBody DemandReceiveDto demandReceiveDto
//    ){
//        demandService.rejectDemandItem(token,demandReceiveDto);
//        return new ResponseEntity<>(
//                HttpStatus.NO_CONTENT
//        );
//    }
//
//    @PutMapping("/resent")
//    @ApiOperation(value = "Reject Demand By Initiator")
//    public ResponseEntity<?> resentDemand(
//            @RequestHeader("Authorization") String token,
//            @RequestBody DemandReceiveDto demandReceiveDto
//    ){
//        demandService.resendDemandItem(token,demandReceiveDto);
//        return new ResponseEntity<>(
//                HttpStatus.NO_CONTENT
//        );
//    }
//
//    @PostMapping("/review/{id}")
//    public ResponseEntity<?> review(
//            @RequestHeader("Authorization") String token,
//            @PathVariable("id") Long id,
//            @RequestBody ReviewDto reviewDto
//    ){
//        demandService.reviewDemand(token,id,reviewDto);
//        return new ResponseEntity<>(
//                HttpStatus.NO_CONTENT
//        );
//    }
//
//
//    @GetMapping("/next-id")
//    @ApiOperation(value = "Get New Demand No")
//    public ResponseEntity<?> getNextId(){
//        Map<String,Object> response = new HashMap<>();
//        response.put("code",demandService.getNextDemandNo());
//        return new ResponseEntity<>(
//                response,
//                HttpStatus.OK
//        );
//    }
//}
