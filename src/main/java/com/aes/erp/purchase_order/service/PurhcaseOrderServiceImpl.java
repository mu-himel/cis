package com.aes.erp.purchase_order.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.repository.ItemRepository;
import com.aes.erp.purchase_order.dto.request.PoDeliveryDetailsDto;
import com.aes.erp.purchase_order.entity.PurchaseOrderDeliveryDetail;
import com.aes.erp.scm.Entities.TenderDeliveryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.repository.CategoryRepository;
import com.aes.erp.network.NetworkService;
import com.aes.erp.purchase_order.dto.request.PoDetailReqDto;
import com.aes.erp.purchase_order.dto.request.PoReceiveRequestDto;
import com.aes.erp.purchase_order.dto.request.QcResultDto;
import com.aes.erp.purchase_order.entity.PoQcDetail;
import com.aes.erp.purchase_order.entity.PurchaseOrder;
import com.aes.erp.purchase_order.entity.PurchaseOrderDetail;
import com.aes.erp.purchase_order.repository.PoDetailRepository;
import com.aes.erp.purchase_order.repository.PoRepository;
import com.aes.erp.purchase_order.repository.PoRepository.PurchaseOrderInfo;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.scm.dto.remote.GoodReceiveNoteCreateDto;
import com.aes.erp.scm.dto.remote.GoodReceivedManualRequestDto;
import com.aes.erp.scm.dto.remote.GrnManualItemDetailDto;
import com.aes.erp.scm.dto.remote.ReferenceObjDto;
import com.aes.erp.scm.dto.remote.VendorRemoteDto;
import com.aes.erp.scm.repositories.OfferItemRepository;
import com.aes.erp.scm.repositories.PriceQuotationRepository;
import com.aes.erp.scm.repositories.TednerDeliveryDetailRepository;
import com.aes.erp.scm.repositories.TenderItemRepository;
import com.aes.erp.scm.repositories.TenderRepository;
import com.aes.erp.scm.services.TenderService;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferDeliveryDetail;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferTermsAndCondition;
import com.aes.erp.vendor.repository.OfferDeliveryDetailRepository;
import com.aes.erp.vendor.repository.OfferRepository;
import com.aes.erp.vendor.repository.OfferTermsAndConditionRepository;
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

    @Autowired
    private TenderService tenderService;

    @Autowired
    private OfferTermsAndConditionRepository offerTermsAndConditionRepository;

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TenderItemRepository tenderItemRepository;

    @Autowired
    private TednerDeliveryDetailRepository tenderDeliveryDetailRepository;

    @Autowired
    private OfferDeliveryDetailRepository offerDeliveryDetailRepository; 

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferItemRepository offerItemRepository;

    @Autowired
    private PoDetailRepository poDetailRepository;

    @Autowired
    private PriceQuotationRepository priceQuotationRepository;
    @Autowired
    private ItemRepository itemRepository;


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
            po.setDeliveryChargeType(poDto.getDeliveryChargeType());
            po.setOrg(organization);
            po.setPoStatus("PENDING");
            po.setCategoryCode(poDto.getCategoryCode());
//            po.setWarehouseId(poDto.getWarehouse().getId());
            
            List<PurchaseOrderDetail> poOrderDetails = new ArrayList<>();
            for(PoDetailReqDto od:poDto.getOrderDetails()){
                Optional<OfferItem> offerItemOp = offer.getOfferItems().stream()
                        .filter(oi->{
                            String oiStr = oi.getProductDescription();
                            if(oi.getExtendedAttributes() != null){
                                oiStr = oiStr.concat(" - "+oi.getExtendedAttributes());
                            }
                            String odStr =od.getItemName();
                            return oiStr.equals(odStr);
                        })
                        .findFirst();
                if(offerItemOp.isPresent()) {
                    // throw new AesException("Sorry! Offer Item not found");

                    PurchaseOrderDetail pod = new PurchaseOrderDetail();
                    pod.setItemName(od.getItemName());
                    pod.setItemQty(od.getItemQty());
//                    pod.setWarehouseId(od.getWarehouse().getId());
                    pod.setDeliveryCharge(od.getDeliveryCharge());
                    pod.setVatAmount(od.getVatAmount());
                    pod.setVatPercent(od.getVatPercent());
                    pod.setTotalPrice(od.getTotalPrice());
                    pod.setSubTotal(od.getSubTotal());
                    pod.setOfferItem(offerItemOp.get());
                    pod.setPurchaseOrder(po);
                    poOrderDetails.add(pod);

                    List<PurchaseOrderDeliveryDetail> purchaseOrderDeliveryDetailsList = new ArrayList<>();
                    for (PoDeliveryDetailsDto pd :od.getPoDeliveryDetailsDtoList()) {
                        PurchaseOrderDeliveryDetail podd = new PurchaseOrderDeliveryDetail();
                        podd.setWarehouseId(pd.getWarehouse().getId());
                        Optional<TenderDeliveryDetail> tenderDeliveryDetailOptional = tenderDeliveryDetailRepository.getTenderDeliveryDetailByWarehouseId(pd.getWarehouse().getId());
                        if(tenderDeliveryDetailOptional.isPresent()) {
                            podd.setWarehouseName(tenderDeliveryDetailOptional.get().getWareHouseName());
                            podd.setWarehouseAddress(tenderDeliveryDetailOptional.get().getWareHouseAddress());
                        }
                        podd.setItemQty(pd.getItemQty());
                        podd.setDeliveryCharge(pd.getDeliveryCharge());
                        podd.setPurchaseOrderDetail(pod);
                        purchaseOrderDeliveryDetailsList.add(podd);
                    }
                    pod.setPurchaseOrderDeliveryDetails(purchaseOrderDeliveryDetailsList);
                }
            };
            po.setOrderDetails(poOrderDetails);
            poRepository.save(po);
        });
        
    }

    @Override
    public Page<?> getPendingPOs(ClaimResponseDto loggedInUser, Optional<Integer> page, Optional<Integer> size) {
        
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(PAGE_SIZE));
        if(loggedInUser.getUserInfoDto().get("vendorId")==null){
            throw new AesException("Sorry! user is not a vendor profile");
        }
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
      
        Optional<PurchaseOrderInfo> detailOp = poRepository.findById(id,PurchaseOrderInfo.class);

        Map<String,Object> result = new HashMap<>();
        List<OfferTermsAndCondition> termsAndConditions = new ArrayList<>();

        if(detailOp.isPresent()){
            var detail = detailOp.get();
            if (detail  instanceof PurchaseOrderInfo){
                Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
                if(!((PurchaseOrderInfo)detail).getVendor().getId().equals(vendorId)){
                    throw new AesException("Sorry! po not for this user");
                }
                String tenderNo = ((PurchaseOrderInfo)detail).getTenderNo();

                Tender tender = tenderService.getTenderByRfqNo(detail.getOrg().getId(), tenderNo);
                if(tender!=null){
                    // tender.getId();
                    // vendorId
                    termsAndConditions = offerTermsAndConditionRepository.findAllByTenderIdAndVendorId(tender.getId(),vendorId);

                }
            }
        }
        result.put("detail", detailOp.get());
        result.put("termsAndConditions",termsAndConditions);
        return (Optional<T>)Optional.ofNullable(result);
        
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

    // @Override
    // @Transactional
    // public void sendPO(Long id) {
    //     Optional<PurchaseOrder> poOp = poRepository.findById(id);
    //     if(poOp.isPresent()){
    //         PurchaseOrder po = poOp.get();
    //         Organization organization = po.getOrg();
    //         po.setIsPoSent(true);
    //         po.setPoStatus("IN PROGRESS");
    //         GoodReceiveNoteCreateDto grn = new GoodReceiveNoteCreateDto();
    //         grn.setRemotePoId(po.getRemotePoId());
    //         grn.setPoId(po.getId());
    //         List<GoodReceiveItemDetailDto> grids = new ArrayList<>();
    //         po.getOrderDetails().stream().forEach(od->{
    //             GoodReceiveItemDetailDto grid = new GoodReceiveItemDetailDto();
    //             grid.setItemAttribute(od.getItemName());
    //             grid.setBrandName(od.getOfferItem().getBrandName());
    //             grid.setReceiveQty(od.getItemQty());
    //             grid.setSubCategoryCode(po.getCategoryCode());
    //             grids.add(grid);
    //         });
    //         grn.setDetails(grids);

    //         String authToken = login(organization);
    //         log.info("authtoken:" +authToken);
    //         sendGrnRequest(organization, authToken, grn);
    //     }
        
    // }
    

    @Override
    @Transactional
    public void sendPO(Long id) {
        Optional<PurchaseOrder> poOp = poRepository.findById(id);
        if(poOp.isPresent()){
            boolean isWarehousePresent = false;
            PurchaseOrder po = poOp.get();
            Organization organization = po.getOrg();
            po.setIsPoSent(true);
            po.setPoStatus("IN PROGRESS");
            Optional<Tender> tenderOp = tenderRepository.findByRfqNoAndTenderCreatorId(po.getTenderNo(), po.getOrg().getId());
            if(tenderOp.isPresent()){
                Tender tender = tenderOp.get();
                ItemCategory itemCategory = tender.getItemCategory();
                ReferenceObjDto rfoDto = new ReferenceObjDto(itemCategory.getScmCategoryId());
                // List<POItemDeliveryInfo> warehouseOfferCatList = tenderDeliveryDetailRepository.getItemWiseDeliveryDetailPO(tender.getId(),po.getVendor().getId());
                
                // List <TenderItem> tenderItemList = tenderItemRepository.findByTenderId(tender.getId());
                List<PurchaseOrderDetail> purchaseOrderDetail = poDetailRepository.findByPurchaseOrderId(po.getId());
                // OfferItem offerItem = offerItemRepository.findById(purchaseOrderDetail.getOfferItem().getId()).get();
                // PriceQuotation priceQuotation = priceQuotationRepository.findById(purchaseOrderDetail.getOfferItem().getPriceQuotation().getId()).get();
                
                VendorRemoteDto vendorRemoteDto = new VendorRemoteDto();
                vendorRemoteDto.setId(po.getVendor().getId());
                vendorRemoteDto.setName(po.getVendor().getName());
                vendorRemoteDto.setVendorEmail(po.getVendor().getEmail());
                vendorRemoteDto.setVendorPhone(po.getVendor().getPhone());
                List<GoodReceivedManualRequestDto> goodReceivedManualRequestDtoList = new ArrayList<>();
                for (PurchaseOrderDetail row:purchaseOrderDetail) {
                    // for (POItemDeliveryInfo row : warehouseOfferCatList) {
                    // Offer offer = offerRepository.findById(purchaseOrderDetail.getOfferItem().getOffer().getId()).get();
                    //one order can have many items
                    for (PurchaseOrderDeliveryDetail podd: row.getPurchaseOrderDeliveryDetails()) {

                    OfferDeliveryDetail delivery_detail = offerDeliveryDetailRepository.findByWarehouseIdAndOfferId(podd.getWarehouseId(), row.getOfferItem().getOffer().getId());

                        for (GoodReceivedManualRequestDto goodReceiveNoteDto : goodReceivedManualRequestDtoList) {
                            if (podd.getWarehouseId().equals(goodReceiveNoteDto.getWarehouseId())) {
                                GrnManualItemDetailDto grnManualItemDetail = new GrnManualItemDetailDto();

                                grnManualItemDetail.setCategory(new ReferenceObjectDto(itemCategory.getParentCategory().getScmCategoryId()));
                                grnManualItemDetail.setSubCategory(new ReferenceObjectDto(itemCategory.getScmCategoryId()));
                                grnManualItemDetail.setEstDeliveryDays(row.getOfferItem().getEstimatedDeliveryDays());
                                Optional<Item> itemOptional = itemRepository.findByItemAttributeNameAndName(row.getOfferItem().getProductDescription(),row.getOfferItem().getBrandName());
                                itemOptional.ifPresent(item -> grnManualItemDetail.setItemCode(item.getCode()));
                                grnManualItemDetail.setOrderQty(podd.getItemQty());
                                grnManualItemDetail.setPricePerUnit(row.getOfferItem().getPriceQuotation().getPricePerUnit());

                                goodReceiveNoteDto.getGrnDetails().add(grnManualItemDetail);
                                goodReceiveNoteDto.setTotalPrice(goodReceiveNoteDto.getTotalPrice().add(podd.getItemQty().multiply(row.getOfferItem().getPriceQuotation().getPricePerUnit())));
                                goodReceiveNoteDto.setTotalVat((goodReceiveNoteDto.getTotalPrice().multiply(goodReceiveNoteDto.getVatPctg())).divide(new BigDecimal(100)));

                                grnManualItemDetail.setDeliveryChargeAmount(podd.getDeliveryCharge());
                                goodReceiveNoteDto.setDeliveryChargeAmount(goodReceiveNoteDto.getDeliveryChargeAmount().add(podd.getDeliveryCharge()));
                                if (row.getOfferItem().getOffer().getVatIncluded() == true) {
                                    goodReceiveNoteDto.setTotalVat((goodReceiveNoteDto.getTotalPrice().multiply(goodReceiveNoteDto.getVatPctg())).divide((new BigDecimal(100).add(goodReceiveNoteDto.getVatPctg())),RoundingMode.HALF_UP));
                                    goodReceiveNoteDto.setInTotal(goodReceiveNoteDto.getTotalPrice().add(goodReceiveNoteDto.getDeliveryChargeAmount()));

                                }else {
                                    goodReceiveNoteDto.setTotalVat((goodReceiveNoteDto.getTotalPrice().multiply(goodReceiveNoteDto.getVatPctg())).divide(new BigDecimal(100),RoundingMode.HALF_UP));
                                    goodReceiveNoteDto.setInTotal((goodReceiveNoteDto.getTotalPrice().add(goodReceiveNoteDto.getTotalVat())).add(goodReceiveNoteDto.getDeliveryChargeAmount()));
                                }

                                isWarehousePresent = true;
                                break;
                            } else {
                                isWarehousePresent = false;
                            }
                        }
                        if (!isWarehousePresent) {
                            GoodReceivedManualRequestDto grn = new GoodReceiveNoteCreateDto();
                            grn.setGrnNo(null);
                            grn.setIndentNo(null);
                            grn.setIndentNo(tender.getRfqNo());

                            grn.setCategory(rfoDto);
                            grn.setPoId(po.getRemotePoId());

                            grn.setDeliveryCharge(po.getDeliveryChargeType());
                            grn.setDeliveryChargeAmount(podd.getDeliveryCharge());
                            // grn.setDays(offerItem.getWarrantyDuration());
                            grn.setDays(row.getOfferItem().getOffer().getCreditPaymentDays());

                            BigDecimal total_price = podd.getItemQty().multiply(row.getOfferItem().getPriceQuotation().getPricePerUnit());
                            grn.setTotalPrice(total_price);
                            BigDecimal total_vat = new BigDecimal(0);

                            if (row.getOfferItem().getOffer().getMushakIncluded() == true) {
                                grn.setMushak("INCLUDED");
                            } else {
                                grn.setMushak("EXCLUDED");
                            }
                            grn.setVatPctg(row.getOfferItem().getOffer().getVatPercent());
                            if (row.getOfferItem().getOffer().getVatIncluded() == true) {
                                grn.setVatOption("INCLUDED");
//                                BigDecimal dividedBy = new BigDecimal(100L);
//                                BigDecimal result1 = dividedBy.add(grn.getVatPctg());
//                                BigDecimal result2 = total_price.multiply(grn.getVatPctg());
//                                total_vat = result2.divide(result1, RoundingMode.HALF_UP);
                                total_vat = (total_price.multiply(grn.getVatPctg())).divide((new BigDecimal(100).add(grn.getVatPctg())),RoundingMode.HALF_UP);
                                grn.setTotalVat(total_vat);
                                grn.setInTotal(total_price.add(grn.getDeliveryChargeAmount()));
                            } else {
                                grn.setVatOption("EXCLUDED");
                                total_vat = (total_price.multiply(grn.getVatPctg())).divide(new BigDecimal(100),RoundingMode.HALF_UP);
                                grn.setTotalVat(total_vat);
                                grn.setInTotal(total_price.add(total_vat).add(grn.getDeliveryChargeAmount()));
                            }
                            if (row.getOfferItem().getOffer().getAitIncluded() == true) {
                                grn.setAitOption("INCLUDED");
                            } else {
                                grn.setAitOption("EXCLUDED");
                            }
                            grn.setWarehouseId(podd.getWarehouseId());
                            grn.setPayment(row.getOfferItem().getOffer().getCreditType());
                            grn.setInvoicePath(po.getInvoicePath());
                            grn.setVendor(vendorRemoteDto);


                            List<GrnManualItemDetailDto> grids = new ArrayList<>();
                            GrnManualItemDetailDto grid = new GrnManualItemDetailDto();
                            grid.setCategory(new ReferenceObjectDto(itemCategory.getParentCategory().getScmCategoryId()));
                            grid.setSubCategory(new ReferenceObjectDto(itemCategory.getScmCategoryId()));
                            grid.setEstDeliveryDays(row.getOfferItem().getEstimatedDeliveryDays());
                            Optional<Item> itemOptional = itemRepository.findByItemAttributeNameAndName(row.getOfferItem().getProductDescription(),row.getOfferItem().getBrandName());
                            itemOptional.ifPresent(item -> grid.setItemCode(item.getCode()));
                            grid.setOrderQty(podd.getItemQty());
                            grid.setDeliveryChargeAmount(podd.getDeliveryCharge());
                            grid.setPricePerUnit(row.getOfferItem().getPriceQuotation().getPricePerUnit());
                            grids.add(grid);
                            grn.setGrnDetails(grids);
                            goodReceivedManualRequestDtoList.add(grn);
                        }
                    }
                }

                for (GoodReceivedManualRequestDto grn: goodReceivedManualRequestDtoList) {
                    String authToken = networkService.getKeycloakAccessToken(organization);
                    log.info("authtoken:" + authToken);
                    sendGrnRequest(organization, authToken, grn);
                }
                
            }
        }
    }

    @Override
    @Transactional
    public void grnReceive(Long id) {
        Optional<PurchaseOrder> poOp = poRepository.findByRemotePoId(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            po.setIsPoSent(true);
            po.setPoStatus("RECEIVED");
            po.setIsGrnReceived(true);
            po.setGrnReceiveDate(Instant.now().toEpochMilli());
        }
    }

    

    @Override
    @Transactional
    public void declineGrn(Long id, NoteDto noteDto) {
        Optional<PurchaseOrder> poOp = poRepository.findByRemotePoId(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            po.setIsPoSent(false);
            po.setPoStatus("DECLINED");
            po.setIsGrnReceived(false);
            po.setGrnReceiveDate(Instant.now().toEpochMilli());
            po.setGrnDeclineNote(noteDto.getNote());
        }
        
    }

    @Override
    @Transactional
    public void receiveQc(Long id, QcResultDto qcResultDto) {
        Optional<PurchaseOrder> poOp = poRepository.findById(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            po.setIsQcPass(true);
            po.setIsPoSent(qcResultDto.getStatus().equals("QC_PASS")? true : false);
            po.setQcDeclineNote(null);
            po.setPoStatus(qcResultDto.getStatus());
            po.setQcResult(qcResultDto.getQcResult());
            po.setQcDetails(qcResultDto.getQcDetails().stream().map(qcDetail->{
                PoQcDetail poQcDetail = new PoQcDetail();
                poQcDetail.setPoId(qcDetail.getPoId());
                poQcDetail.setDate(qcDetail.getDate());
                poQcDetail.setItemAttributeName(qcDetail.getItemAttributeName());
                poQcDetail.setBrandName(qcDetail.getBrandName());
                poQcDetail.setTotalApprovedQty(qcDetail.getTotalApprovedQty());
                poQcDetail.setTotalDeclinedQty(qcDetail.getTotalDeclinedQty());
                poQcDetail.setPurchaseOrder(po);
                return poQcDetail;
            }).collect(Collectors.toList()));
        }
    }

    @Override
    @Transactional
    public void declineQc(Long id, QcResultDto qcResultDto) {
        Optional<PurchaseOrder> poOp = poRepository.findById(id);
        if(poOp.isPresent()){
            PurchaseOrder po = poOp.get();
            po.setIsQcPass(false);
            po.setIsPoSent(false);
            po.setQcDeclineNote(qcResultDto.getNote());
            po.setPoStatus(qcResultDto.getStatus());
            po.setQcResult(qcResultDto.getQcResult());
        }
        
    }

    private String login(Organization organization){
       
        String url = organization.getServiceIpAddress().replace("/api/v1","")
                            .concat("/authenticate");
        String username = organization.getServiceUsername();
        String password = organization.getServicePassword();
        return networkService.getAuthToken(url,username,password);
    }

   
    private void sendGrnRequest(Organization organization, String authToken, GoodReceivedManualRequestDto grn){
        StringBuilder sb = new StringBuilder("/goods-receive-note");
        
        String priceQuotationEndpoint = organization.getScmIpAddress().concat(sb.toString());
//        String priceQuotationEndpoint = "http://172.17.18.79:9095/api/v1/goods-receive-note";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoodReceivedManualRequestDto> pqPayload = new HttpEntity<>(grn, headers);
        System.out.println(priceQuotationEndpoint);
        ResponseEntity<Void> response = networkService.post(priceQuotationEndpoint,pqPayload,Void.class);
        if(!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new AesException("something wrong.");
        }
    }
 
}
