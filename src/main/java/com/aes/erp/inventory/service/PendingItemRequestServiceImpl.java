package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.controller.PendingItemReqController.PendingAttributesDto;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.dto.request.MergePendingItemsPostDto;
import com.aes.erp.inventory.dto.request.PendingBrandDto;
import com.aes.erp.inventory.dto.request.PendingItemRequestDto;
import com.aes.erp.inventory.entity.*;
import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.PendingAttributeRepository;
import com.aes.erp.inventory.repository.PendingBrandRepository;
import com.aes.erp.inventory.repository.PendingItemRequestRepository;
import com.aes.erp.network.NetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PendingItemRequestServiceImpl implements PendingItemRequestService {

    private static final Integer PAGE_SIZE = 10;

    private final PendingItemRequestRepository pendingItemRequestRepository;

    private final BrandRepository brandRepository;

    private final PendingBrandRepository pendingBrandRepository;

    private final PendingAttributeRepository pendingAttributeRepository;

    private final CategoryService categoryService;

    private final ItemService itemService;

    private final NetworkService networkService;

//    @Value("${scm.apiEndpoint}")
//    private String scmApiEndpoint;

    @Override
    @Transactional
    public Map<String, Object> createPendingItemRequest(PendingItemRequestDto pRequestDto) {

        PendingItemRequest pir = pRequestDto.getEntity();

        Optional<Brand> brandOp = brandRepository.findByName(pRequestDto.getBrand());
        if (brandOp.isEmpty()) {
            throw new AesException("Sorry! Brand not specified");
        }

        Optional<ItemCategory> catOp = categoryService.existByCode(pRequestDto.getSubCategoryCode());
        if (catOp.isEmpty()) {
            throw new AesException("Sorry! Sub Category not specified");
        }
        ItemCategory subCat = catOp.get();
        pir.setRequestNo(getNextItemCode());
        pir.setSubCategory(subCat);
        pir.setCategory(subCat.getParentCategory());
        pir.setBrand(brandOp.get());
        pir.setAttributes(pRequestDto.getAttributes().stream().map(pia -> {
            pia.setId(null);
            pia.setPendingItemRequest(pir);
            return pia;
        }).collect(Collectors.toList()));
        pir.setOrganization(new Organization(pRequestDto.getOrganizationId()));
        pir.setCode(subCat.getCode().concat("-").concat(itemService.getNextItemCode(subCat.getCode())));
        pendingItemRequestRepository.save(pir);
        Map<String, Object> result = new HashMap<>();
        result.put("code", pir.getCode());
        result.put("pendingItemRequestId", pir.getId());
        return result;
    }

    private String getNextItemCode() {
        Optional<Long> pirOp = pendingItemRequestRepository.findMaxOrderById();
        if (pirOp.isPresent()) {
            Long pir = pirOp.get();
            Long newProductId = pir + 1;
            return String.format("%05d", newProductId);
        }
        return String.format("%05d", 1);
    }

    @Override
    public Optional<?> getDetail(Long id) {
        return pendingItemRequestRepository.findById(id, PendingItemRequest.class);
    }

    @Override
    public Page<?> getPage(Optional<Long> categoryId, Optional<Long> subCategoryId, Optional<Integer> page, Optional<Integer> size) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(PAGE_SIZE));
        return pendingItemRequestRepository.findAllPendingItemRequests(categoryId.orElse(null)
                , subCategoryId.orElse(null), pageable);
    }

    @Override
    public List<?> getPendingBrands(Long subCatId) {
        return pendingBrandRepository.findAllBySubCategoryId(subCatId);
    }

    @Override
    @Transactional
    public void createPendingBrand(PendingBrandDto pendingBrandDto) {
        PendingBrand pendingBrand = pendingBrandDto.getEntity();
        List<PendingBrand> pendingBrands = pendingBrandRepository.findAllByBrandName(pendingBrand.getBrandName());
        if (pendingBrands.size() > 0) {
            throw new AesException("Brand Request Already Pending");
        }
        pendingBrandRepository.save(pendingBrand);
    }


    @Override
    @Transactional
    public void deletePendingBrand(Long id) {
        Optional<PendingBrand> pendingBrandOp = pendingBrandRepository.findById(id);
        if (pendingBrandOp.isPresent()) {
            PendingBrand pendingBrand = pendingBrandOp.get();
            pendingBrandRepository.delete(pendingBrand);
        }
    }


    @Override
    @Transactional
    public void deletePendingBrands(List<Long> id) {
        if (id.isEmpty()) {
            throw new AesException("Sorry! delete not possible list is empty");
        }
        pendingBrandRepository.deleteAllById(id);
    }


    @Override
    @Transactional
    public void deletePendingAttributes(List<Long> ids) {
        if (ids.isEmpty()) {
            throw new AesException("Sorry! delete not possible list is empty");
        }
        pendingAttributeRepository.deleteAllById(ids);

    }

    @Override
    @Transactional
    public void deletePendingItemRequest(Long id) {
        pendingItemRequestRepository.deleteById(id);
    }

    @Override
    public List<?> getPendingAttributes(Long subCatId) {
        return pendingAttributeRepository.findAllBySubCategoryId(subCatId);
    }

    @Override
    @Transactional
    public void createPendingAttribute(PendingAttributesDto pendingAttributesDto) {
        List<PendingAttribute> pendingAttributes = new ArrayList<>();
        pendingAttributesDto.pendingAttributes().stream().forEach(pendingAttributeDto -> {
            PendingAttribute pendingAttribute = pendingAttributeDto.getEntity();
            Optional<PendingAttribute> pendingAttrOp = pendingAttributeRepository
                    .findAllBySubCategoryIdAndAttributeTypeAndAttributeValue(pendingAttribute.getSubCategory().getId(),
                            pendingAttribute.getAttributeType(), pendingAttribute.getAttributeValue());
            if (pendingAttrOp.isEmpty()) {

                Optional<CategoryAttribute> catAttrOp = categoryService.getCategoryAttributeValueBySubCatAndAttributeType(pendingAttribute.getSubCategory().getId(),
                        pendingAttribute.getAttributeType());
                if (catAttrOp.isPresent()) {
                    if (catAttrOp.get().getAttributeValue().toLowerCase().contains(pendingAttribute.getAttributeValue().toLowerCase())) {
                        throw new AesException("Sorry! Attribute already exist");
                    }
                }

                pendingAttributes.add(pendingAttribute);
            } else {
                throw new AesException("Sorry! Attribute already exist as pending");
            }

        });

        pendingAttributeRepository.saveAll(pendingAttributes);
    }

    @Override
    @Transactional
    public void deletePendingAttribute(Long id) {
        Optional<PendingAttribute> pendAttrOptional = pendingAttributeRepository.findById(id);
        if (pendAttrOptional.isPresent()) {

            PendingAttribute pendingAttribute = pendAttrOptional.get();
            pendingAttributeRepository.delete(pendingAttribute);
        }

    }


    @Override
    @Transactional
    public void rejectPendingItem(Long id) {
        PendingItemRequest getPendingItem = pendingItemRequestRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not exist"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(networkService.getKeycloakAccessToken(getPendingItem.getOrganization()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        MergePendingItemsPostDto mpcpDTO = new MergePendingItemsPostDto();
        mpcpDTO.setApproveStatus("REJECTED");
        mpcpDTO.setIsMerged(false);
        mpcpDTO.setWarehouseId(getPendingItem.getWarehouseId());
        mpcpDTO.setWarehouseStoreId(getPendingItem.getWarehouseStoreId());
        HttpEntity<MergePendingItemsPostDto> mPCDtoPayload = new HttpEntity<>(mpcpDTO, headers);

        StringBuilder sb = new StringBuilder("/items");
        sb.append("/approve/");
        sb.append(getPendingItem.getScmItemId());
        String scmEndpoint = getPendingItem.getOrganization().getScmIpAddress();
        String itemTransferEndpoint = scmEndpoint.concat(sb.toString());

        ResponseEntity<Void> response = networkService.put(itemTransferEndpoint, mPCDtoPayload, Void.class);
        if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            pendingItemRequestRepository.deleteById(getPendingItem.getId());
        } else {
            throw new AesException("Something wrong");
        }

    }

    @Override
    @Transactional
    public void updatePendingItem(Long id, ItemRequestDto itemRequestDto) {
        Optional<PendingItemRequest> opPendingItemRequest = pendingItemRequestRepository.findById(id);
        if (opPendingItemRequest.isEmpty()) {
            throw new AesException("Pending item not found");
        }

        PendingItemRequest pendingItemRequest = opPendingItemRequest.get();


        if (itemRequestDto.getItemUnit() != null) {
            pendingItemRequest.setItemUnit(itemRequestDto.getItemUnit());
        }

        if (itemRequestDto.getBrand() != null) {
            Optional<Brand> optionalBrand = brandRepository.findById(itemRequestDto.getBrand().getId());
            optionalBrand.ifPresent(brand -> {
                pendingItemRequest.setBrand(brand);
            });
        }

        if (itemRequestDto.getAttributes() != null && itemRequestDto.getAttributes().size() > 0) {
            pendingItemRequest.setAttributes(itemRequestDto.getAttributes().stream().map(itemAttribute -> {
                PendingItemAttribute pendingItemAttribute = new PendingItemAttribute();
                pendingItemAttribute.setPendingItemRequest(pendingItemRequest);
                pendingItemAttribute.setId(itemAttribute.getId());
                pendingItemAttribute.setAttributeType(itemAttribute.getAttributeType());
                pendingItemAttribute.setAttributeValue(itemAttribute.getAttributeValue());
                pendingItemAttribute.setAttributeUnit(itemAttribute.getAttributeUnit());
                pendingItemAttribute.setPendingItemRequest(pendingItemRequest);
                return pendingItemAttribute;
            }).collect(Collectors.toList()));

        }

        pendingItemRequest.setItemAttributeName(generateItemAttributeName(itemRequestDto.getAttributes()));

        pendingItemRequestRepository.save(pendingItemRequest);

    }

    private String generateItemAttributeName(List<ItemAttribute> attributes) {
        StringBuilder sb = new StringBuilder();

        attributes.stream().forEach(itemAttribute -> {
            String attrType = itemAttribute.getAttributeType().trim();
            String attrValue = itemAttribute.getAttributeValue().trim();
            String attrUnit = itemAttribute.getAttributeUnit().trim();
            if (!attrType.isEmpty() && !attrValue.isEmpty() && !attrUnit.isEmpty()) {
                sb.append(attrType + " " + attrValue + " " + attrUnit);
                sb.append(" - ");
            }
        });

        return (sb.isEmpty()) ? "" : sb.toString().substring(0, sb.length() - 3);
    }

}
