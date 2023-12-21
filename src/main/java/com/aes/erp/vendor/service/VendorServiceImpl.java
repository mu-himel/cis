package com.aes.erp.vendor.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.SubcategoryBrand;
import com.aes.erp.inventory.service.CategoryService;
import com.aes.erp.inventory.service.ItemService;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserService;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.dto.VendorRegistrationMailSender;
import com.aes.erp.vendor.dto.VendorScoreDto;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorFile;
import com.aes.erp.vendor.entity.VendorScore;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.enums.VendorDocType;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.repository.GeneralDetailsRepository;
import com.aes.erp.vendor.repository.VendorFileRepository;
import com.aes.erp.vendor.repository.VendorRepository;
import com.aes.erp.vendor.repository.VendorScoreRepository;
import com.aes.erp.vendor.utils.EmailSenderUtil;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService {
    private final EmailSenderUtil emailSenderUtil;


    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VendorProfileService vendorProfileService;
    private final GenericModelMapper modelMapper;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserService userService;
    @Autowired
    private VendorScoreRepository vendorScoreRepository;

    @Autowired
    private VendorFileRepository vendorFileRepository;
    private final CategoryService categoryService;
    private final GeneralDetailsRepository generalDetailsRepository;



    public VendorServiceImpl(EmailSenderUtil emailSenderUtil, GenericModelMapper modelMapper, CategoryService categoryService, GeneralDetailsRepository generalDetailsRepository) {
        this.emailSenderUtil = emailSenderUtil;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.generalDetailsRepository = generalDetailsRepository;
    }

    @Override
    public Optional<?> getVendorDetail(Long vendorId) {
        Optional<?> vendorOptional = vendorRepository.findVendorById(vendorId);
        return vendorOptional;
    }

    @Override
    @Transactional
    public void createVendor(VendorDto vendorDto) {
        if(!vendorDto.getPhone().isEmpty()){
            if(vendorDto.getPhone().matches("[a-zA-Z]")){
                throw new AesException("phone number should not contain alphabets");
            }
        }
        User user = userService.createVendorUserAccount(vendorDto);
        Vendor vendor = vendorDto.getEntity();
        vendor.setStatus(VendorStatus.CREATED);
        vendor.setCategory(new ItemCategory(vendorDto.getCategory().getId()));
        //Set SubCategory List For Vendor
        if(vendorDto.getSubCategory() != null && !vendorDto.getSubCategory().isEmpty()){
           List<ItemCategory> subCategoryList = new ArrayList<>();
           for(Long id: vendorDto.getSubCategory()){
               Optional<ItemCategory> existingItemCategory = categoryService.getItemCategory(id);
               if(existingItemCategory.isPresent()){
                   ItemCategory previousReference = existingItemCategory.get();
                   ItemCategory itemCategory = new ItemCategory();
                   setNewItemCategory(previousReference, itemCategory);
                   itemCategory.setVendor(vendor);
                   itemCategory = categoryService.addCategoryFromCategoryEntity(itemCategory);
                   subCategoryList.add(itemCategory);
               }
           }
        }
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.PENDING_DOCUMENT_VERIFICATION);
        vendor.setVendorType(vendorDto.getVendorType());
        vendor.setUser(user);
        vendor.setStartedAt(new Date());
        vendor = vendorRepository.save(vendor);
        //Notify user Through a mail
        VendorRegistrationMailSender senderBody = new VendorRegistrationMailSender(vendor.getEmail());
        senderBody.setContent(senderBody.getContent() +  "Email: " + vendorDto.getEmail() + "\n" + "Password: " + vendorDto.getPassword());
        emailSenderUtil.sendMail(senderBody);
    }
    public void setNewItemCategory(ItemCategory refCat, ItemCategory destination){
        destination.setName(refCat.getName());
        destination.setCopiedFrom(refCat.getId());
        if(refCat.getParentCategory() != null){
            destination.setParentCategory(refCat.getParentCategory());
        }
        if(refCat.getStoreType() != null)destination.setStoreType(refCat.getStoreType());
        if(refCat.getActive() != null)destination.setActive(refCat.getActive());
//        if(!refCat.getBrands().isEmpty())destination.setBrands(refCat.getBrands());
//        if(!refCat.getAttributes().isEmpty()){
//            List<CategoryAttribute> attributeList = new ArrayList<>();
//            for(CategoryAttribute attribute : refCat.getAttributes()){
//                CategoryAttribute newAttribute = new CategoryAttribute();
//                newAttribute = attribute;
//                newAttribute.setCategory(null);
//                attributeList.add(newAttribute);
//            }
//            destination.setAttributes(attributeList);
//        }
//        if(!refCat.getBudgets().isEmpty())destination.setBudgets(refCat.getBudgets());
    }

    @Override
    @Transactional
    public void updateVendor(Long id, VendorDto vendorDto) {

        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Vendor Information not found");
        }

        Vendor vendor = vendorOptional.get();
        if(vendorDto.getName()!=null && !vendorDto.getName().isEmpty()) {
            vendor.setName(vendorDto.getName());
        }
        if(vendorDto.getEmail()!=null && !vendorDto.getEmail().isEmpty()) {
            vendor.setEmail(vendorDto.getEmail());
        }
        if(vendorDto.getPhone()!=null && !vendorDto.getPhone().isEmpty()) {
            vendor.setPhone(vendorDto.getPhone());
        }
        if(vendorDto.getCategory() != null){
            vendor.setCategory(new ItemCategory(vendorDto.getCategory().getId()));
        }
        //Set SubCategory List For Vendor
        if(vendorDto.getSubCategory() != null && !vendorDto.getSubCategory().isEmpty()){
            List<ItemCategory> subCategoryList = new ArrayList<>();
            subCategoryList = updateSubCategoryListForVendor(vendor, id, vendor.getSubCategoryList(), vendorDto.getSubCategory());
            vendor.setSubCategoryList(subCategoryList);
        }
        if(vendorDto.getVendorType() != null){
            VendorType vendorType = new VendorType();
            vendorType.setId(vendorDto.getVendorType().getId());
            vendor.setVendorType(vendorType);
        }
        vendorRepository.save(vendor);
    }
    public List<ItemCategory> updateSubCategoryListForVendor(Vendor vendor, Long vendorId, List<ItemCategory> subCategoryList, List<Long> subCategoryIdList){
        if(subCategoryIdList != null){
            List<ItemCategory> subCategoriesToBeDeleted = new ArrayList<>();
            for (ItemCategory category : subCategoryList) {
                if(!subCategoryIdList.contains(category.getId())){
                    subCategoriesToBeDeleted.add(category);
                }
            }
            for(ItemCategory ic: subCategoriesToBeDeleted){
                categoryService.deleteCategory(ic.getId());
                subCategoryList.remove(ic);
            }
            for(Long id: subCategoryIdList) {
                Optional<ItemCategory> existingCategoryOptional = categoryService.getCategoryForAVendor(vendorId, id);
                if(!existingCategoryOptional.isPresent()){
                    Optional<ItemCategory> refCat = categoryService.findRootReferenceItem(id);
                    ItemCategory refCategory = refCat.get();
                    ItemCategory newItem = new ItemCategory();
                    setNewItemCategory(refCategory, newItem);
                    newItem.setVendor(vendor);
                    newItem = categoryService.addCategoryFromCategoryEntity(newItem);
                    subCategoryList.add(newItem);
                }
            }
        }
        return subCategoryList;
    }
    @Override
    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }

    @Override
    public Page<?> getVendors(Optional<Integer> page, Optional<Integer> size,
                                        Optional<String> name,
                                        Optional<String> email,
                                        Optional<String> phone,
                                        Optional<String> vendorType,
                                        Optional<String> vendorStatus
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        return vendorRepository.findAllVendors(
                name.orElse(null),
                email.orElse(null),
                phone.orElse(null),
                vendorType.orElse(null),
                vendorStatus.orElse(null),
                pageable
        );
    }

    @Override
    public Page<?> getPendingVerificationVendors(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        return vendorRepository.findAllVendorForStatus(VendorDocumentVerificationStatus.PENDING_VERIFICATION,pageable);
    }

    @Override
    public Page<?> getPendingApprovalVendors(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        return vendorRepository.findAllVendorForStatus(VendorDocumentVerificationStatus.PENDING_APPROVAL,pageable);
    }

    @Override
    public Page<?> getApprovedVendors(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        return vendorRepository.findAllVendorForStatus(VendorDocumentVerificationStatus.APPROVED,pageable);
    }

    @Override
    public void uploadVendorFiles(Long id,
                                  String address,
                                  String password,
                                  String confirmPassword,
                                  Optional<MultipartFile> tinCert,
                                  Optional<MultipartFile> vatCert,
                                  Optional<MultipartFile> tradeLicense,
                                  Optional<MultipartFile> quotationFormat
    ) {

        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Vendor not found");
        }

        Path tinCertPath = Path.of("./uploads/vendor/"+id+"/tin-certificate");
        Path vatCertPath = Path.of("./uploads/vendor/"+id+"/vat-certificate");
        Path tradeLicensePath = Path.of("./uploads/vendor/"+id+"/trade-license");
        Path quotationFormatPath = Path.of("./uploads/vendor/"+id+"/quotation-format");

        List<VendorFile> vendorFiles = new ArrayList<>();
        Vendor vendor = vendorOptional.get();
        if(tinCert.isPresent()) {
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(tinCertPath, tinCert.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.TIN_CERTIFICATE);
                vendorFiles.add(vendorFile);
            }
        }

        if(vatCert.isPresent()){
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(vatCertPath, vatCert.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.VAT_CERTIFICATE);
                vendorFiles.add(vendorFile);
            }
        }

        if(tradeLicense.isPresent()){
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(tradeLicensePath, tradeLicense.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.TRADE_LICENSE);
                vendorFiles.add(vendorFile);
            }
        }

        if(quotationFormat.isPresent()){
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(quotationFormatPath, quotationFormat.get());
            if(fileUploadResponse!=null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.QUOTATION_FORMAT);
                vendorFiles.add(vendorFile);
            }
        }


        vendorFileRepository.saveAll(vendorFiles);


    }

    @Override
    @Transactional
    public void updateVendorStatus(Long id, VendorStatus status) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Sorry! Vendor is not found");
        }
        Vendor vendor = vendorOptional.get();
        vendor.getUser().getUserCredential().setActive(
                (status==VendorStatus.DISABLED)? false : true
        );
        vendor.setStatus(status);

    }
    public void setPermittedProductsForVendor(VendorProfileDto dto, List<ItemCategory> categoryList){
        for(ItemCategory subCategory: categoryList){
            Optional<ItemCategory> refCategory = categoryService.getItemCategory(subCategory.getCopiedFrom());
            refCategory.ifPresent(category -> dto.getPermittedProducts().add(subCategory.getName() + category.getCode()));
        }
    }
    @Override
    public VendorProfileDto getVendorProfile(Long id){
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty())throw new AesException("Vendor couldn't be found with this user Id");
        Vendor vendor = vendorOptional.get();
        VendorProfileDto profileDto = new VendorProfileDto();
        setPermittedProductsForVendor(profileDto, vendor.getSubCategoryList());
        profileDto.setBasicInformation(vendorProfileService.getVendorBasicInformation(vendor));
        profileDto.setIdentification(vendorProfileService.getVendorIdentification(vendor));
        profileDto.setAddress(vendorProfileService.getVendorAddress(vendor));
        if(!vendor.getName().isEmpty())profileDto.setName(vendor.getName());
        profileDto.setStartedAt(vendor.getStartedAt());
        if(vendor.getDocumentHolder() != null){
            DocumentHolder documentHolder = vendor.getDocumentHolder();
            if(documentHolder.getBusinessDetailsRecords() != null)profileDto.setBusinessDetails(vendor.getDocumentHolder().getBusinessDetailsRecords());
            Optional<GeneralDetails> vendorGeneralDetails = generalDetailsRepository.findByDocumentHolderId(documentHolder.getId());
            vendorGeneralDetails.ifPresent(profileDto::setGeneralDetails);
        }
        if(vendor.getVendorScore() != null)profileDto.setVendorScore(vendor.getVendorScore());
        return profileDto;
    }

    @Override
    public void approveVendor(Long vendorId) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(vendorId);
        if(!vendorOptional.isPresent())throw new AesException("Vendor not found");
        Vendor vendor = vendorOptional.get();
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.VERIFIED);
        vendor.setStatus(VendorStatus.ENABLED);
        vendorRepository.save(vendor);
    }

    @Override
    public Optional<Vendor> getVendorByUserId(Long userId) {
        return vendorRepository.findByUserId(userId);
    }

    @Override
    public void updateVendorScore(VendorScoreDto dto) {
        Optional<VendorScore> vendorScoreOptional = vendorScoreRepository.findById(dto.getId());
        if(vendorScoreOptional.isEmpty()) throw  new AesException("Vendor Score couldn't be found");
        VendorScore vendorScore;
        vendorScore = modelMapper.map(dto, VendorScore.class);
        vendorScore = vendorScoreRepository.save(vendorScore);
        vendorScoreRepository.save(vendorScore);
    }

    @Override
    public Vendor getById(Long id) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()) throw  new AesException("Vendor Score couldn't be found");
        return vendorOptional.get();
    }
}
