package com.aes.erp.scm.services;

import com.aes.erp.authentication.OrganizationPrincipal;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.response.SubCategory;
import com.aes.erp.inventory.service.CategoryServiceImpl;
import com.aes.erp.inventory.service.OrganizationService;
import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.scm.dto.TenderCreateDto;
import com.aes.erp.scm.dto.TenderItemCreateDto;
import com.aes.erp.scm.dto.TenderResponseDto;
import com.aes.erp.scm.Entities.*;
import com.aes.erp.scm.Query.TenderQuerySpecification;
import com.aes.erp.scm.repositories.DeliveryDetailsRepository;
import com.aes.erp.scm.repositories.TenderItemRepository;
import com.aes.erp.scm.repositories.TenderParticipatorRepository;
import com.aes.erp.scm.repositories.TenderRepository;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.service.VendorService;
import com.aes.erp.vendor.utils.GenericModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class TenderServiceImpl implements TenderService{
    private final GenericModelMapper genericModelMapper;
    private final TenderRepository tenderRepository;
    private final TenderItemRepository tenderItemRepository;
    private final OrganizationService organizationService;
    private final CategoryServiceImpl categoryService;
    private final DeliveryDetailsRepository deliveryDetailsRepository;

    @Autowired
    private TenderParticipatorRepository tpRepository;

    @Autowired
    private VendorService vendorService;


    public TenderServiceImpl(GenericModelMapper genericModelMapper, TenderRepository tenderRepository, TenderItemRepository tenderItemRepository, OrganizationService organizationService, CategoryServiceImpl categoryService, DeliveryDetailsRepository deliveryDetailsRepository) {
        this.genericModelMapper = genericModelMapper;
        this.tenderRepository = tenderRepository;
        this.tenderItemRepository = tenderItemRepository;
        this.organizationService = organizationService;
        this.categoryService = categoryService;
        this.deliveryDetailsRepository = deliveryDetailsRepository;
    }

    @Transactional
    @Override
    public void createTender(TenderCreateDto dto) {
        
        Tender tender = new Tender(); //genericModelMapper.map(dto, Tender.class);
        
        tender.setTenderStatus(TenderStatus.PENDING);
        tender.setTenderType(TenderType.PENDING);
        tender.setDeadline(dto.getDeadline());
        tender.setRfqNo(dto.getRfqNo());
        tender.setCode(dto.getCode());
        
        
        List<TenderItem> tenderItems = new ArrayList<>();
        for(TenderItemCreateDto itemDto: dto.getTenderItems()){
            List<TenderDeliveryDetail> newDeliveryDetails = genericModelMapper.mapDtoListToEntityList(itemDto.getDeliveryDetails(), TenderDeliveryDetail.class);
            TenderItem item = genericModelMapper.map(itemDto, TenderItem.class);
            item.setDeliveryDetails(newDeliveryDetails);
            item.setBrandName(itemDto.getBrandName());
            item = tenderItemRepository.save(item);
            tenderItems.add(item);
            List<TenderDeliveryDetail> savedDeliveryDetails = new ArrayList<>();
            for(TenderDeliveryDetail details : item.getDeliveryDetails()){
                details.setTenderItem(item);
                details = deliveryDetailsRepository.save(details);
                savedDeliveryDetails.add(details);
            }
            item.setDeliveryDetails(savedDeliveryDetails);
            tenderItemRepository.save(item);
        }
        tender.setTenderItems(tenderItems);
        OrganizationPrincipal organizationPrincipal = (OrganizationPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(organizationService.isOrganizationExistAndEnabled(organizationPrincipal.getOrgId())){
            tender.setTenderCreator(organizationService.getOrganizationById(organizationPrincipal.getOrgId()));
        }
        else throw new AesException("Organization doesn't exist or doesn't have permission to create the tender");
        if(categoryService.existByCode(dto.getItemCategoryCode()).isPresent()){
            tender.setItemCategory(categoryService.existByCode(dto.getItemCategoryCode()).get());
        }
        else throw new AesException("No Item Category couldn't be found with given Id");
        tender = tenderRepository.save(tender);
        for(TenderItem item : tender.getTenderItems()){
            item.setTender(tender);
        }
        tender.setCreationDate(Instant.now().toEpochMilli());
        tenderRepository.save(tender);
    }

    @Override
    public Page<?> getAllTenders(ClaimResponseDto loggedInUser,Optional<String> searchFilter, Optional<Integer> page, Optional<Integer> size, Optional<TenderType> tenderType, Optional<Long> startDate, Optional<Long> endDate) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        Specification<Tender> specification = TenderQuerySpecification.getTenderSpecification(searchFilter, tenderType, startDate, endDate);
        return tenderRepository.findAll(specification, pageable);
    }

    @Override
    public TenderResponseDto getTenderResponseById(Long id) {
        Optional<Tender> tender = tenderRepository.findById(id);
        if(tender.isEmpty()) throw new AesException("Tender couldn't be found");
        TenderResponseDto dto = genericModelMapper.map(tender.get(), TenderResponseDto.class);
        return dto;
    }
    @Override
    public Tender getTenderById(Long id) {
        Optional<Tender> tender = tenderRepository.findById(id);
        if(tender.isEmpty()) throw new AesException("Tender couldn't be found");
        return tender.get();
    }

    

    @Override
    public Tender getTenderByRfqNo(String tenderNo) {
        Optional<Tender> tender = tenderRepository.findByRfqNo(tenderNo);
        if(tender.isEmpty()) throw new AesException("Sorry! Tender not found");
        return tender.get();
    }

    @Override
    public Page<?> getAllTenderProjection(ClaimResponseDto loggedInUser,Optional<String> searchFilter, Optional<Integer> page, Optional<Integer> size, Optional<TenderType> tenderType, Optional<Long> startDate, Optional<Long> endDate) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);

        List<SubCategory> subCategories = new ArrayList<>();
        if(loggedInUser.getUserInfoDto().get("vendorId")!=null){
            subCategories = categoryService.getCategoriesForVendor(Long.valueOf((Integer)loggedInUser.getUserInfoDto().get("vendorId")));
        }

        List<Long> subCatIds = subCategories.stream().map(sc->sc.getId()).collect(Collectors.toList());
        if(loggedInUser.getUserInfoDto()==null && loggedInUser.getUserInfoDto().get("vendorId") == null){
            return Page.empty();
        }
        Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        Vendor vendor = vendorService.getById(vendorId);

        if(!vendor.getVerificationStatus().equals(VendorDocumentVerificationStatus.APPROVED)){
            return Page.empty();
        }
        return tenderRepository.findAllTenderProjection(
                vendorId,
                searchFilter.orElse(""),
                subCatIds, tenderType, startDate,
                endDate, Instant.now().toEpochMilli(),pageable
        );
    }

    

    @Override
    public Page<?> getClosedTenderProjection(ClaimResponseDto loggedInUser, Optional<String> searchFilter,
            Optional<Integer> page, Optional<Integer> size, Optional<TenderType> tenderType, Optional<Long> startDate,
            Optional<Long> endDate) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);

        List<SubCategory> subCategories = new ArrayList<>();
        if(loggedInUser.getUserInfoDto().get("vendorId")!=null){
            subCategories = categoryService.getCategoriesForVendor(Long.valueOf((Integer)loggedInUser.getUserInfoDto().get("vendorId")));
        }

        List<Long> subCatIds = subCategories.stream().map(sc->sc.getId()).collect(Collectors.toList());
        if(loggedInUser.getUserInfoDto()==null && loggedInUser.getUserInfoDto().get("vendorId") == null){
            return Page.empty();
        }
        Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        return tenderRepository.findAllClosedTenderProjection(
                vendorId,
                searchFilter.orElse(""),
                subCatIds, tenderType, startDate,
                endDate, Instant.now().toEpochMilli(),pageable);
    }

    @Override
    public List<?> getNegotiationHistories(ClaimResponseDto loggedInUser,Long id) {
        Optional<Tender> tenderOp = tenderRepository.findById(id);
        if(tenderOp.isEmpty()){
            throw new AesException("Sorry! tender is not found");
        }
        String vendorIdStr = loggedInUser.getUserInfoDto().get("vendorId").toString();
        Long vendorId = Long.parseLong(vendorIdStr);
        return tenderRepository.getNegotiationHistoriesByTender(id,vendorId);
    }

    @Override
    @Transactional
    public void rejectTender(ClaimResponseDto loggedInUser, Long id, NoteDto noteDto) {
        
        Optional<Tender> tenderOp = tenderRepository.findById(id);
        if(tenderOp.isEmpty()){
            throw new AesException("Sorry! Tender not found");
        }

        Tender tender = tenderOp.get();
        Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        Optional<TenderParticipator> tpOp = tpRepository.findByTenderIdAndVendorId(tender.getId(), vendorId);
        
        if(tpOp.isPresent()){
            TenderParticipator tenderParticipator = tpOp.get();
            tenderParticipator.setStatus(TenderStatus.REJECTED);
            tenderParticipator.setTender(tenderOp.get());
            tenderParticipator.setVendor(new Vendor(vendorId));
        }
        
    }

    

    
    
}
