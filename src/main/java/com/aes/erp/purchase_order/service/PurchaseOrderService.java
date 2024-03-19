package com.aes.erp.purchase_order.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.purchase_order.dto.request.PoReceiveRequestDto;
import com.aes.erp.purchase_order.dto.request.QcResultDto;
import com.aes.erp.scm.dto.NoteDto;

public interface PurchaseOrderService {

    void receivePO(Organization organization, PoReceiveRequestDto poDto);
    
    Page<?> getPendingPOs(ClaimResponseDto loggedInUser, Optional<Integer>page, Optional<Integer>size);

    <T> Optional<T> getDetailById(ClaimResponseDto loggedInUser, Long id, Class<T> t);

    Page<?> getClosedPOs(ClaimResponseDto loggedInUser, Optional<Integer> page, Optional<Integer> size);

    void uploadInvoice(ClaimResponseDto loggedInUser, Long id, Optional<MultipartFile> fileOp);

    void sendPO(Long id);
    void grnReceive(Long id);
    void declineGrn(Long id,NoteDto noteDto);
    void declineQc(Long id, QcResultDto qcResultDto);
    void receiveQc(Long id, QcResultDto qcResultDto);
    
}
