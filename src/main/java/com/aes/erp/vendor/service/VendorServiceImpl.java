package com.aes.erp.vendor.service;

import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.dto.response.ItemCategoryDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.aes.erp.inventory.repository.CategoryRepository;
import com.aes.erp.inventory.repository.ItemRepository;
import com.aes.erp.inventory.repository.OrganizationRepository;
import com.aes.erp.inventory.service.CategoryService;
import com.aes.erp.inventory.service.OrganizationService;
import com.aes.erp.network.NetworkService;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserService;
import com.aes.erp.vendor.dto.*;

import com.aes.erp.vendor.entity.*;
import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolderStatus;
import com.aes.erp.vendor.enums.VendorDocType;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.repository.*;
import com.aes.erp.vendor.service.DocumentHolderServices.DocumentHolderService;
import com.aes.erp.vendor.utils.EmailSenderUtil;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class VendorServiceImpl implements VendorService {
    private final EmailSenderUtil emailSenderUtil;


    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VendorProfileService vendorProfileService;
    private final VendorSubCategoryRepository vendorSubCategoryRepository;
    private final GenericModelMapper modelMapper;
    @Autowired
    @Lazy
    private  DocumentHolderService documentHolderService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserService userService;
    @Autowired
    private VendorScoreRepository vendorScoreRepository;
    private final VendorTypeService vendorTypeService;

    @Autowired
    private VendorFileRepository vendorFileRepository;
    private final CategoryService categoryService;
    private final GeneralDetailsRepository generalDetailsRepository;
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AuthorizedPersonRepository autorizedPersonRepository;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Value("${cps.frontend}")
    private String cpsFrontendLink;

    @Value("${accounts.apiEndpoint}")
    private String accountsApiEndpoint;


    public VendorServiceImpl(EmailSenderUtil emailSenderUtil, VendorSubCategoryRepository vendorSubCategoryRepository, GenericModelMapper modelMapper, VendorTypeService vendorTypeService, CategoryService categoryService, GeneralDetailsRepository generalDetailsRepository) {
        this.emailSenderUtil = emailSenderUtil;
        this.vendorSubCategoryRepository = vendorSubCategoryRepository;
        this.modelMapper = modelMapper;
        this.vendorTypeService = vendorTypeService;
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

            if(vendorRepository.existsByPhone(vendorDto.getPhone())){
                throw new AesException("Sorry! Vendor exist with this phone number");
            }
        }
        
        User user = userService.createVendorUserAccount(vendorDto);
        Vendor vendor = vendorDto.getEntity();
        vendor.setStatus(VendorStatus.CREATED);
        // vendor.setCategory(new ItemCategory(vendorDto.getCategory().getId()));
        //Create SubCategory List For Vendor
        if(vendorDto.getSubCategory() != null && !vendorDto.getSubCategory().isEmpty()){
            Set<VendorSubCategory> newSubcategorySet = new HashSet<>();
            for(Long id: vendorDto.getSubCategory()){
                Optional<ItemCategory> existingItemCategory = categoryService.getItemCategory(id);
                if(existingItemCategory.isPresent()){
                    ItemCategory previousReference = existingItemCategory.get();
                    VendorSubCategory vendorSubCategory = new VendorSubCategory();
                    vendorSubCategory.setSubcategory(previousReference);
                    vendorSubCategory.setVendor(vendor);
                    vendorSubCategory = vendorSubCategoryRepository.save(vendorSubCategory);
                    newSubcategorySet.add(vendorSubCategory);
                }
            }
            vendor.setVendorSubCategories(newSubcategorySet);
        }
        vendor.setCategories(vendorDto.getCategories());
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.PENDING_DOCUMENT_VERIFICATION);
        vendor.setVendorType(vendorTypeService.getVendorById(vendorDto.getVendorType().getId()));
        vendor.setUser(user);

        vendor.setStartedAt(new Date());
        vendor = vendorRepository.save(vendor);
        //Notify user Through a mail
        VendorRegistrationMailSender senderBody = new VendorRegistrationMailSender(vendor.getEmail());
        
        // send superadmin a copy of vendor registration credential's mail
        User superAdminUser = userService.findByEmailAddressIgnoreCaseStartingWith("superadmin@");
        if(superAdminUser!=null){
            senderBody.addReceipent(superAdminUser.getEmailAddress());
        }
   
        
        senderBody.setContent("<p>"+senderBody.getContent()+"</p><p>" +  "Email: " + vendorDto.getEmail() +" " + "Password: " + vendorDto.getPassword()+"</p><p>Please visit <a href=\""+cpsFrontendLink+"\">here</a> to login");
        emailSenderUtil.sendMail(senderBody);
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
        if(vendorDto.getCategories() !=null && !vendorDto.getCategories().isEmpty()){
            vendor.setCategories(vendorDto.getCategories());
        }
        if(vendorDto.getPhone()!=null && !vendorDto.getPhone().isEmpty()) {
            vendor.setPhone(vendorDto.getPhone());
        }
        if(vendorDto.getCategory() != null){
            vendor.setCategory(new ItemCategory(vendorDto.getCategory().getId()));
        }
        //Update SubCategory List For Vendor
        if(vendorDto.getSubCategory() != null && !vendorDto.getSubCategory().isEmpty()){
            //First Remove the Detached SubCategory for a vendor id any
            removeSubCategoryListForVendor(vendor.getId(), vendor.getVendorSubCategories(), vendorDto.getSubCategory());
            //Add the new SubCategories
            for(Long subCategoryId: vendorDto.getSubCategory()){
                VendorSubCategory vendorSubCategory = vendorSubCategoryRepository.findByVendorIdAndSubCategoryId(vendor.getId(), subCategoryId);
                if(vendorSubCategory == null){
                    Optional<ItemCategory> existingItemCategory = categoryService.getItemCategory(subCategoryId);
                    if(existingItemCategory.isPresent()){
                        ItemCategory previousReference = existingItemCategory.get();
                        vendorSubCategory = new VendorSubCategory();
                        vendorSubCategory.setSubcategory(previousReference);
                        vendorSubCategory.setVendor(vendor);
                        vendorSubCategoryRepository.save(vendorSubCategory);
                    }
                }
            }
        }
        vendor.setVendorType(vendorTypeService.getVendorById(vendorDto.getVendorType().getId()));
        vendorRepository.save(vendor);
    }
    public void removeSubCategoryListForVendor(Long vendorId, Set<VendorSubCategory> subCategoryList, List<Long> subCategoryIdList){
        if(subCategoryIdList != null){
            List<VendorSubCategory> subCategoriesToBeDeleted = new ArrayList<>();
            for (VendorSubCategory category : subCategoryList) {
                if(!subCategoryIdList.contains(category.getSubcategory().getId())){
                    subCategoriesToBeDeleted.add(category);
                }
            }
            for(VendorSubCategory ic: subCategoriesToBeDeleted){
                removeSubCategoryFromVendor(vendorId, ic.getSubcategory().getId());
                subCategoryList.remove(ic);
            }
        }
    }
    @Override
    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }

    @Override
    public void uploadProfileImage(Long id,String img) {
        Optional<Vendor> vendorOptional = vendorRepository.findByUserId(id);
        if(vendorOptional.isPresent()){
            Vendor vendor = vendorOptional.get();
            vendor.setVendorImage(img);
            vendorRepository.save(vendor);
        }
    }

    @Override
    public void removeSubCategoryFromVendor(Long vendorId, Long subCategoryId){
        VendorSubCategory vendorSubCategory = vendorSubCategoryRepository.findByVendorIdAndSubCategoryId(vendorId, subCategoryId);
        if(vendorSubCategory == null) throw new AesException("No such sub category found for this vendor");
        vendorSubCategoryRepository.deleteById(vendorSubCategory.getId());
    }

    @Override
    public Page<?> getVendors(
            Optional<Integer> page,
            Optional<Integer> size,
            Optional<String> name,
            Optional<String> email,
            Optional<String> phone,
            Optional<String> vendorType,
            Optional<String> vendorStatus
    ) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
            return vendorRepository.findAllVendors(
                    name.orElse(null),
                    email.orElse(null),
                    phone.orElse(null),
                    vendorType.orElse(null),
                    vendorStatus.orElse(null),
                    pageable
            );
        } catch (Exception e) {
            LOGGER.error("Error in getVendors method", e);
            throw e; // rethrow the exception after logging
        }
    }
    @Override
    public Page<?> getPendingVerificationVendors(Optional<Integer> page, Optional<Integer> size,
                                                 Optional<String> name,
                                                 Optional<String> email,
                                                 Optional<String> phone,
                                                 Optional<String> vendorType,
                                                 Optional<String> vendorStatus
                                                 ) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        return vendorRepository.findAllVendorForStatus(VendorDocumentVerificationStatus.PENDING_VERIFICATION,
                name.orElse(null),
                email.orElse(null),
                phone.orElse(null),
                vendorType.orElse(null),
                vendorStatus.orElse(null),
                pageable);
    }

    @Override
    public Page<?> getPendingApprovalVendors(Optional<Integer> page, Optional<Integer> size,
                                             Optional<String> name,
                                             Optional<String> email,
                                             Optional<String> phone,
                                             Optional<String> vendorType,
                                             Optional<String> vendorStatus) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        return vendorRepository.findAllVendorForStatus(VendorDocumentVerificationStatus.PENDING_APPROVAL,
                name.orElse(null),
                email.orElse(null),
                phone.orElse(null),
                vendorType.orElse(null),
                vendorStatus.orElse(null),
                pageable);
    }

    @Override
    public Page<?> getVendors(Optional<Integer> page, Optional<Integer> size,
                                      Optional<String> name,
                                      Optional<String> email,
                                      Optional<String> phone,
                                      Optional<String> vendorType,
                                      Optional<String> vendorStatus,
                                      VendorDocumentVerificationStatus vStatus
                                      ) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        List<VendorDocumentVerificationStatus> verificationStatus = new ArrayList<>();
        verificationStatus.add(vStatus);
        return vendorRepository.findAllVendorForComplete(
                name.orElse(null),
                email.orElse(null),
                phone.orElse(null),
                vendorType.orElse(null),
                vendorStatus.orElse(null),
                verificationStatus,
                pageable);
    }


    @Override
    public VendorDetailsDto getAllDetailsOfVendor(Long vendorId) {
       Optional<Vendor> vendor = vendorRepository.findById(vendorId);
       if(vendor.isEmpty()) throw new AesException("No vendor Found with this vendor id");
       Long documentHolderId = vendor.get().getDocumentHolder().getId();
       DocumentHolder documentHolder = vendor.get().getDocumentHolder();
       VendorDetailsDto dto = new VendorDetailsDto();
       Optional<GeneralDetails> generalDetails = generalDetailsRepository.findByDocumentHolderId(documentHolderId);
       generalDetails.ifPresent(dto::setGeneralDetails);
       if(!documentHolder.getBusinessDetailsRecords().isEmpty())dto.setBusinessDetails(documentHolder.getBusinessDetailsRecords());
       Vendor vendorEntity = vendor.get();
       if(vendorEntity.getVendorType() != null) dto.setVendorType(vendorEntity.getVendorType().toString());
       if(vendorEntity.getName() != null)dto.setVendorName(vendorEntity.getName());
       if(vendorEntity.getCategory() != null)dto.setVendorCategory(vendorEntity.getCategory().getName());
       if(!vendorEntity.getVendorSubCategories().isEmpty()){
           List<String> subCategories = new ArrayList<>();
           for(VendorSubCategory item: vendorEntity.getVendorSubCategories()){
               String subCategoryName = item.getSubcategory().getName();
               subCategories.add(subCategoryName);
           }
           dto.setSubCategories(subCategories);
       }
       if(documentHolder.getBinDocument() != null)dto.setBinNumber(documentHolder.getBinDocument().getBin());
       if(documentHolder.getTinDocument() != null)dto.setTinNumber(documentHolder.getTinDocument().getTin());
       if(documentHolder.getTradeDocument() != null)dto.setTradeNumber(documentHolder.getTradeDocument().getTradeLicenseNumber());
       if(documentHolder.getBankSolvencyDocument() != null)dto.setSolvencyNumber(documentHolder.getBankSolvencyDocument().getAccount());
       if(documentHolder.getNidDocument() != null)dto.setNidNumber(documentHolder.getNidDocument().getNid());
       return dto;
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
    public FileUploadResponse uploadVendorFile(Long id,  Optional<MultipartFile> file) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if (vendorOptional.isEmpty()) {
            throw new AesException("Vendor not found");
        }
        List<VendorFile> vendorFiles = new ArrayList<>();
        Vendor vendor = vendorOptional.get();
        Path shopPhotoPath = Path.of("./uploads/vendor/" + vendor.getId() + "/shop");
        FileUploadResponse fileUploadResponse = null;
        if (file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(shopPhotoPath, file.get());
            if (fileUploadResponse != null) {
                VendorFile vendorFile = new VendorFile(vendor,
                        fileUploadResponse.getFilename(),
                        fileUploadResponse.getPath(),
                        fileUploadResponse.getSize(),
                        fileUploadResponse.getMimeType(),
                        VendorDocType.NONE);
//                vendorFile.setBusinessDetails(new BusinessDetails(businessDetailId));
                vendorFiles.add(vendorFile);
            }
        }
        vendorFileRepository.saveAll(vendorFiles);

        return fileUploadResponse;
    }

    @Override
    public Optional<VendorFile> getShopFile(Long id,  String filename) {
        return vendorFileRepository.findByVendorIdAndFileName(id, filename);
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

    @Override
    @Transactional
    public void rejectVendor(Long id) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty()){
            throw new AesException("Sorry! Vendor is not found");
        }
        Vendor vendor = vendorOptional.get();
        vendor.getUser().getUserCredential().setActive(false);
        vendor.setStatus(VendorStatus.DISABLED);
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.REJECTED);
    }

    public void setPermittedProductsForVendor(VendorProfileDto dto, Set<VendorSubCategory> categoryList){
        for(VendorSubCategory vendorSubCategory: categoryList){
            ItemCategory subCategory = vendorSubCategory.getSubcategory();
            if(subCategory != null){
                dto.getPermittedProducts().add(subCategory.getName() +" "+ subCategory.getCode());
            }
        }
    }
    @Override
    public VendorProfileDto getVendorProfile(Long id){
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if(vendorOptional.isEmpty())throw new AesException("Vendor couldn't be found with this user Id");
        Vendor vendor = vendorOptional.get();
        VendorProfileDto profileDto = new VendorProfileDto();
        setPermittedProductsForVendor(profileDto, vendor.getVendorSubCategories());
        profileDto.setBasicInformation(vendorProfileService.getVendorBasicInformation(vendor));
        profileDto.setIdentification(vendorProfileService.getVendorIdentification(vendor));
        profileDto.setAddress(vendorProfileService.getVendorAddress(vendor));
        profileDto.setVendorFileList(vendor.getFiles());
        profileDto.setVendorType(vendor.getVendorType());
        profileDto.setAitPercentage(vendor.getAitPercentage());
        profileDto.setCategories(vendor.getCategories());
        if(!vendor.getName().isEmpty())profileDto.setName(vendor.getName());
        profileDto.setStartedAt(vendor.getStartedAt());
        if(vendor.getVendorSubCategories() != null){
            List<ItemCategoryDto> itemCategories = new ArrayList<>();
            for(VendorSubCategory vendorSubCategory: vendor.getVendorSubCategories()){
                ItemCategoryDto itemCategoryDto = new ItemCategoryDto();
                itemCategoryDto.setName(vendorSubCategory.getSubcategory().getCode()+" "+vendorSubCategory.getSubcategory().getName());
                itemCategoryDto.setId(vendorSubCategory.getSubcategory().getId());
                itemCategories.add(itemCategoryDto);
            }
            profileDto.setVendorSubCategories(itemCategories);
        }
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
    @Transactional
    public void approveVendor(Long vendorId, VendorScoreDto dto) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(vendorId);
        if(vendorOptional.isEmpty())throw new AesException("Vendor not found");
        Vendor vendor = vendorOptional.get();

        VendorDocumentVerificationStatus e = vendor.getVerificationStatus();

        if (dto.getEmployeeType().equals(EmployeeType.ENLISTER) && vendor.getVerificationStatus().equals(VendorDocumentVerificationStatus.PENDING_VERIFICATION)){
            vendor.setVerificationStatus(VendorDocumentVerificationStatus.PENDING_APPROVAL);
            vendor.setVerificationDate(LocalDate.now());
            vendor.setAitPercentage(dto.getAitPercentage());
            if(vendor.getDocumentHolder() != null){
                DocumentHolder documentHolder = vendor.getDocumentHolder();
                documentHolder = documentHolderService.updateDocumentHolderStatus(documentHolder.getId(), DocumentHolderStatus.APPROVED_BY_ENLISTER);
                vendor.setDocumentHolder(documentHolder);
            }
            vendorRepository.save(vendor);
        }
        if(dto.getEmployeeType()  == EmployeeType.AUDITOR && vendor.getVerificationStatus() == VendorDocumentVerificationStatus.PENDING_APPROVAL){
            vendor.setVerificationStatus(VendorDocumentVerificationStatus.APPROVED);
            vendor.setApprovedDate(LocalDate.now());
            if(vendor.getDocumentHolder() != null){
                DocumentHolder documentHolder = vendor.getDocumentHolder();
                documentHolder = documentHolderService.updateDocumentHolderStatus(documentHolder.getId(), DocumentHolderStatus.APPROVED_BY_AUDITOR);
                vendor.setDocumentHolder(documentHolder);
            }
            vendor.setStatus(VendorStatus.ENABLED);
            organizationService.sentVendorApprovedSignal();
            vendor = vendorRepository.save(vendor);
            List<Organization> organizationList = organizationRepository.findAll();
            for (Organization org:organizationList) {
                createVendorLedger(vendor,org);

            }
        }
    }

    public void createVendorLedger(Vendor vendor, Organization organization){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(networkService.getKeycloakAccessToken(organizationService.getOrganizationById(organization.getId())));
        headers.setContentType(MediaType.APPLICATION_JSON);

        VendorLedgerCreateDto postDto = new VendorLedgerCreateDto();
        postDto.setVendorCpsId(vendor.getId().toString());
        postDto.setVendorName(vendor.getName());
        postDto.setVendorType(vendor.getVendorType().getName());
        postDto.setVendorPhone(vendor.getPhone());
        postDto.setVendorEmail(vendor.getEmail());
        postDto.setVendorNid(vendor.getDocumentHolder().getNidDocument().getNid());
        postDto.setTin(vendor.getDocumentHolder().getTinDocument().getTin());
        postDto.setTradeLicense(vendor.getDocumentHolder().getTradeDocument().getTradeLicenseNumber());
        postDto.setVatCertificate(vendor.getDocumentHolder().getBinDocument().getBin());
        postDto.setBankSolvency(vendor.getDocumentHolder().getBankSolvencyDocument().getAccount());

        StringBuilder sb1 = new StringBuilder();
        for (String id : vendor.getCategories().split(",")) {
            Optional<ItemCategory> catOpt = categoryRepository.findById(Long.parseLong(id.trim()));
            if (catOpt.isPresent()) {
                sb1.append(catOpt.get().getName());
                sb1.append(",");
            }
        }
        if (sb1.length() > 0) {
            postDto.setCategories(sb1.substring(0, sb1.length() - 1));
        }

        postDto.setModeOfTransaction(vendor.getDocumentHolder().getGeneralDetails().getModeOfTransaction());
        postDto.setModeOfTransportation(vendor.getDocumentHolder().getGeneralDetails().getModeOfTransportation());
        postDto.setCreditPeriod(vendor.getDocumentHolder().getGeneralDetails().getCreditPeriodDays());
        postDto.setAvgDeliveryTime(vendor.getDocumentHolder().getGeneralDetails().getDeliveryLeadTime());
        postDto.setDeliverySchedule(vendor.getDocumentHolder().getGeneralDetails().getDeliverySchedule());
        postDto.setUrgentDeliverySupport(vendor.getDocumentHolder().getGeneralDetails().getUrgentDeliverySupport());
        postDto.setProductReplacementType(vendor.getDocumentHolder().getGeneralDetails().getReplacementType());
        postDto.setAnnualBusinessVolume(vendor.getDocumentHolder().getGeneralDetails().getAnnualBusinessVolume());

        List<BusinessDetailsDto> businessDetailsDtoList = new ArrayList<>();
        for (BusinessDetails detail : vendor.getDocumentHolder().getBusinessDetailsRecords()) {
            // Access fields or methods of each BusinessDetail instance
            BusinessDetailsDto businessDetailsDto = new BusinessDetailsDto();
            businessDetailsDto.setOrgName(detail.getOrgName());
            businessDetailsDto.setBusinessType(detail.getBusinessType());
            businessDetailsDto.setAnnualVolume(detail.getAnnualVolume());
            businessDetailsDto.setNumberOfYear(detail.getNumberOfYear());
            businessDetailsDto.setWorkOrderFile(detail.getWorkOrderFile());
            businessDetailsDtoList.add(businessDetailsDto);
        }
        postDto.setBusinessDetails(businessDetailsDtoList);

        HttpEntity<VendorLedgerCreateDto> mPCDtoPayload = new HttpEntity<>(postDto, headers);

        String vendorLedgerCreationApiEndpoint = organization.getAccIpAddress().concat("/vendor-ledgers/create");
        ResponseEntity<Void> response = networkService.post(vendorLedgerCreationApiEndpoint, mPCDtoPayload, Void.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            System.out.println("Ledger Created");
        } else {
            throw new RuntimeException("No Ledger Created");
        }
    }

    @Override
    public Optional<Vendor> getVendorByUserId(Long userId) {
        Optional<Vendor> vendor = vendorRepository.findByUserId(userId);
        return vendor;
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

    @Override
    public Optional<?> getAvailableVendorCountBySubCategory(String subCatCode) {
        Optional<Integer> countOp = vendorRepository.countVendorsBySubCategory(subCatCode);
        Map<String,Object> map = new HashMap<>();
        if(countOp.isPresent()){
            Integer count =countOp.get();
            map.put("count", count);
        }else{
            map.put("count",0);
        }

        return Optional.ofNullable(map);
    }

    @Override
    public List<?> getVendorList(Optional<String> name,Optional<Long> categoryId,Optional<Long> subCategoryId) {
      
        return vendorRepository.findAllVendors(name.orElse(null),categoryId.orElse(null),subCategoryId.orElse(null));
    }

    
}
