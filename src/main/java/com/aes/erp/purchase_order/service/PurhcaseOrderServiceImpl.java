package com.aes.erp.purchase_order.service;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
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

    @Autowired
    private PoRepository poRepository;

    @Autowired
    private OfferService offerService;

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
    
}
