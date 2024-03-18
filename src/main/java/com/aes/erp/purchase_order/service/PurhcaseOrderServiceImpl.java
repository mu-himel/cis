package com.aes.erp.purchase_order.service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.html.FormSubmitEvent.MethodType;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.network.NetworkService;
import com.aes.erp.purchase_order.dto.request.PoReceiveRequestDto;
import com.aes.erp.purchase_order.entity.PurchaseOrder;
import com.aes.erp.purchase_order.entity.PurchaseOrderDetail;
import com.aes.erp.purchase_order.repository.PoRepository;
import com.aes.erp.purchase_order.repository.PoRepository.PurchaseOrderDetailInfo;
import com.aes.erp.purchase_order.repository.PoRepository.PurchaseOrderInfo;
import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.scm.dto.remote.GoodReceiveItemDetailDto;
import com.aes.erp.scm.dto.remote.GoodReceiveNoteCreateDto;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.aes.erp.vendor.service.offer_services.OfferService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PurhcaseOrderServiceImpl implements PurchaseOrderService{

    private static final Integer PAGE_SIZE = 10;

    @Autowired
    private PoRepository poRepository;

    @Autowired
    private OfferService offerService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private NetworkService networkService;

    

    @Override
    @Transactional
    public void receivePO(Organization organization,PoReceiveRequestDto pgGroup) {
        pgGroup.getPurchaseOrders().stream().forEach(poDto->{
            Optional<Offer> offerOp = offerService.getById(poDto.getOfferId());
            if(offerOp.isEmpty()){
                throw new AesException("Sorry! Offer not found");
            }
            Offer offer = offerOp.get();
            
            // Long id = (long)loggedInUser.getUserInfoDto().get("id");
            PurchaseOrder po = new PurchaseOrder();
            po.setPoDate(poDto.getPoDate());
            po.setPoNo(poDto.getPoNo());
            po.setTenderNo(poDto.getTenderNo());
            po.setRemotePoId(poDto.getId());
            po.setVendor(new Vendor(poDto.getVendorId()));
            po.setDeliveryDate(poDto.getDeliveryDate());
            po.setOrg(organization);
            po.setPoStatus("PENDING");
            po.setCategoryCode(poDto.getCategoryCode());
            po.setOrderDetails(poDto.getOrderDetails().stream().map(od->{
                Optional<OfferItem> offerItemOp = offer.getOfferItems().stream()
                        .filter(oi->oi.getProductDescription().equals(od.getItemName()))
                        .findFirst();
                if(offerItemOp.isEmpty()){
                    throw new AesException("Sorry! Offer Item not found");
                }
               PurchaseOrderDetail pod = new PurchaseOrderDetail();
               pod.setItemName(od.getItemName());
               pod.setItemQty(od.getItemQty());
               pod.setOfferItem(offerItemOp.get());
               pod.setPurchaseOrder(po);
               return pod; 
            }).collect(Collectors.toList()));
            poRepository.save(po);
        });
        
    }

    @Override
    public Page<?> getPendingPOs(ClaimResponseDto loggedInUser, Optional<Integer> page, Optional<Integer> size) {
        
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(PAGE_SIZE));
        Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        return poRepository.findAllPendingPOs(vendorId,pageable);
    }


    @Override
    public Page<?> getClosedPOs(ClaimResponseDto loggedInUser, Optional<Integer> page, Optional<Integer> size) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(PAGE_SIZE));
        Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        return poRepository.findAllClosedPOs(vendorId,pageable);
    }

    @Override
    public <T> Optional<T> getDetailById(ClaimResponseDto loggedInUser, Long id, Class<T> t) {
      
        var detailOp = poRepository.findById(id,t);

        if(detailOp.isPresent()){
            var detail = detailOp.get();
            if (detail  instanceof PurchaseOrderInfo){
                Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
                if(!((PurchaseOrderInfo)detail).getVendor().getId().equals(vendorId)){
                    throw new AesException("Sorry! po not for this user");
                }
            }
        }

        return detailOp;
        
    }

    @Override
    @Transactional
    public void uploadInvoice(ClaimResponseDto loggedInUser, Long id, Optional<MultipartFile> fileOp) {
        
        Optional<PurchaseOrder> poOp = poRepository.findById(id, PurchaseOrder.class);

        if(poOp.isEmpty()){
            throw new AesException("Sorry! Purchase Order Not Found");
        }
        PurchaseOrder po = poOp.get();

        if(fileOp.isPresent()){
            MultipartFile file = fileOp.get();

            if(!fileUploadService.validFileSize(file.getSize(), Long.valueOf(5*(1024*1024)))){
                throw new AesException("Sorry! Valid file size upto 5M");
            }

            Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
            Path path = Path.of("./uploads/vendor/"+vendorId+"/po/invoice");
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(path, file);
            if(fileUploadResponse!=null){

                po.setInvoicePath(path.resolve(fileUploadResponse.getFilename()).toString());
                

            }
        }
        
    }

    @Override
    @Transactional
    public void sendPO(Long id) {
        Optional<PurchaseOrder> poOp = poRepository.findById(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            Organization organization = po.getOrg();
            po.setIsPoSent(true);
            GoodReceiveNoteCreateDto grn = new GoodReceiveNoteCreateDto();
            grn.setRemotePoId(po.getRemotePoId());
            grn.setPoId(po.getId());
            List<GoodReceiveItemDetailDto> grids = new ArrayList<>();
            po.getOrderDetails().stream().forEach(od->{
                GoodReceiveItemDetailDto grid = new GoodReceiveItemDetailDto();
                grid.setItemAttribute(od.getItemName());
                grid.setBrandName(od.getOfferItem().getBrandName());
                grid.setReceiveQty(od.getItemQty());
                grid.setSubCategoryCode(po.getCategoryCode());
                grids.add(grid);
            });
            grn.setDetails(grids);

            String authToken = login(organization);
            
            log.info("authtoken:" +authToken);
            sendGrnRequest(organization, authToken, grn);
        }
        
    }

    @Override
    @Transactional
    public void grnReceive(Long id) {
        Optional<PurchaseOrder> poOp = poRepository.findById(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            po.setPoStatus("RECEIVED");
            po.setIsGrnReceived(true);
            po.setGrnReceiveDate(Instant.now().toEpochMilli());
        }
    }

    

    @Override
    @Transactional
    public void declineGrn(Long id, NoteDto noteDto) {
        Optional<PurchaseOrder> poOp = poRepository.findById(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            po.setPoStatus("DECLINED");
            po.setIsGrnReceived(false);
            po.setGrnReceiveDate(Instant.now().toEpochMilli());
            po.setGrnDeclineNote(noteDto.getNote());
        }
        
    }

    private String login(Organization organization){
       
        String url = organization.getServiceIpAddress().replace("/api/v1","")
                            .concat("/authenticate");
        String username = organization.getServiceUsername();
        String password = organization.getServicePassword();
        return networkService.getAuthToken(url,username,password);
    }

   
    private void sendGrnRequest(Organization organization, String authToken, GoodReceiveNoteCreateDto grn){
        StringBuilder sb = new StringBuilder("/goods-receive-note/receive");
        
        String priceQuotationEndpoint = organization.getServiceIpAddress().concat(sb.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoodReceiveNoteCreateDto> pqPayload = new HttpEntity<>(grn, headers);
        ResponseEntity<Void> response = networkService.post(priceQuotationEndpoint,pqPayload,Void.class);
        if(!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new AesException("something wrong.");
        }
    }
 
}
