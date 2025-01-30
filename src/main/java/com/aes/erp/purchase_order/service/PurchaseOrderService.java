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

    Page<?> getPendingPOs(ClaimResponseDto loggedInUser,
                          Optional<Integer> page,
                          Optional<Integer> size,
                          Optional<String> poNo,
                          Optional<String> tenderNo,
                          Optional<Long> organizationId,
                          Optional<String> categoryId,
                          Optional<String> subCategoryId,
                          Optional<String> deliveryDate,
                          Optional<String> status,
                          Optional<String> fromDate,
                          Optional<String> toDate);

    <T> Optional<T> getDetailById(ClaimResponseDto loggedInUser, Long id, Class<T> t);

    Page<?> getClosedPOs(ClaimResponseDto loggedInUser,
                         Optional<Integer> page,
                         Optional<Integer> size,
                         Optional<String> poNo,
                         Optional<String> tenderNo,
                         Optional<Long> organizationId,
                         Optional<String> categoryId,
                         Optional<String> subCategoryId,
                         Optional<String> deliveryDate,
                         Optional<String> fromDate,
                         Optional<String> toDate);

    void uploadInvoice(ClaimResponseDto loggedInUser, Long id, Optional<MultipartFile> fileOp);

    void sendPO(Long id);

    void grnReceive(Organization organization, String id);

    void declineGrn(Organization organization, String id, NoteDto noteDto);

    void declineQc(Organization organization, String id, QcResultDto qcResultDto);

    void receiveQc(Organization organization, String id, QcResultDto qcResultDto);

}
