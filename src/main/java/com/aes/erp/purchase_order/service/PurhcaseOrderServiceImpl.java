package com.aes.erp.purchase_order.service;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.purchase_order.dto.request.PoRequestDto;
import com.aes.erp.purchase_order.entity.PurchaseOrder;
import com.aes.erp.purchase_order.entity.PurchaseOrderDetail;
import com.aes.erp.purchase_order.repository.PoRepository;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.aes.erp.vendor.service.offer_services.OfferService;

@Service
public class PurhcaseOrderServiceImpl implements PurchaseOrderService{

    private static final Integer PAGE_SIZE = 10;

    @Autowired
    private PoRepository poRepository;

    @Autowired
    private OfferService offerService;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    @Transactional
    public void receivePO(ClaimResponseDto loggedInUser,PoRequestDto poDto) {
        Optional<Offer> offerOp = offerService.getById(poDto.getOfferId());
        if(offerOp.isEmpty()){
            throw new AesException("Sorry! Offer not found");
        }
        Offer offer = offerOp.get();
        
        Long id = (long)loggedInUser.getUserInfoDto().get("id");
        PurchaseOrder po = new PurchaseOrder();
        po.setPoDate(po.getPoDate());
        po.setPoNo(poDto.getPoNo());
        po.setTenderNo(poDto.getTenderNo());
        po.setVendor(new Vendor(poDto.getVendorId()));
        po.setDeliveryDate(poDto.getDeliveryDate());
        po.setOrg(new Organization(id));
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
           return pod; 
        }).collect(Collectors.toList()));
        poRepository.save(po);
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
    public <T> Optional<T> getDetailById(Long id, Class<T> t) {
        return poRepository.findById(id,t);
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
                // if invoice uploaded successfully then automatically send grn request to erp

            }
        }
        
    }

    
    
    
    
}
