package com.aes.erp.scm.services;

import com.aes.erp.authentication.OrganizationPrincipal;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.service.CategoryServiceImpl;
import com.aes.erp.inventory.service.OrganizationService;
import com.aes.erp.scm.DtoCollection.TenderCreateDto;
import com.aes.erp.scm.DtoCollection.TenderResponseDto;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderItem;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import com.aes.erp.scm.repositories.TenderRepository;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TenderServiceImpl implements TenderService{
    private final GenericModelMapper genericModelMapper;
    private final TenderRepository tenderRepository;
    private final OrganizationService organizationService;
    private final CategoryServiceImpl categoryService;


    public TenderServiceImpl(GenericModelMapper genericModelMapper, TenderRepository tenderRepository, OrganizationService organizationService, CategoryServiceImpl categoryService) {
        this.genericModelMapper = genericModelMapper;
        this.tenderRepository = tenderRepository;
        this.organizationService = organizationService;
        this.categoryService = categoryService;
    }

    @Override
    public void createTender(TenderCreateDto dto) {
        Tender tender = genericModelMapper.map(dto, Tender.class);
        tender.setTenderStatus(TenderStatus.PENDING);
        tender.setTenderType(TenderType.PENDING);
        OrganizationPrincipal organizationPrincipal = (OrganizationPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(organizationService.isOrganizationExistAndEnabled(organizationPrincipal.getOrgId())){
            tender.setTenderCreator(organizationService.getOrganizationById(organizationPrincipal.getOrgId()));
        }
        else throw new AesException("Organization doesn't exist or doesn't have permission to create the tender");
        if(categoryService.getItemCategory(dto.getItemCategoryId()).isPresent()){
            tender.setItemCategory(categoryService.getItemCategory(dto.getItemCategoryId()).get());
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
    public Page<?> getAllTenders(Optional<String> searchFilter, Optional<Integer> page, Optional<Integer> size, Optional<TenderType> tenderType, Optional<Long> startDate, Optional<Long> endDate) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return tenderRepository.getAllTenders(pageable, searchFilter, tenderType, startDate, endDate);
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
}
