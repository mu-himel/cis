package com.aes.erp.vendor.service.DocumentHolderServices;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.document_response_dto.*;
import com.aes.erp.vendor.dto.BusinessDetailsDto;
import com.aes.erp.vendor.dto.ExtractedInformationDto;
import com.aes.erp.vendor.entity.DocmentEntities.*;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolderStatus;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorScore;
import com.aes.erp.vendor.entity.VendorSubCategory;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.repository.*;
import com.aes.erp.vendor.service.DocumentServices.*;
import com.aes.erp.vendor.service.VendorDocumentValidationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class DocumentHolderServiceImpl implements DocumentHolderService{
    private final DocumentHolderRepository documentHolderRepository;
    private final VendorDocumentValidationService vendorDocumentValidationService;
    private final TINService tinService;
    private final BinService binService;
    private final VendorRepository vendorRepository;
    private final TradeLicenseService tradeLicenseService;
    private final NIDService nidService;
    private final BankSolvencyService bankSolvencyService;
    private final VendorScoreRepository vendorScoreRepository;

    private final BusinessDetailsRepository businessDetailsRepository;
    private final GeneralDetailsRepository generalDetailsRepository;
    private final DocumentRepository documentRepository;
    ModelMapper modelMapper = new ModelMapper();

    public DocumentHolderServiceImpl(DocumentHolderRepository documentHolderRepository, VendorDocumentValidationService vendorDocumentValidationService, TINService tinService, BinService binService, VendorRepository vendorRepository, TradeLicenseService tradeLicenseService, NIDService nidService, BankSolvencyService bankSolvencyService, VendorScoreRepository vendorScoreRepository, BusinessDetailsRepository businessDetailsRepository, GeneralDetailsRepository generalDetailsRepository, DocumentRepository documentRepository) {
        this.documentHolderRepository = documentHolderRepository;
        this.vendorDocumentValidationService = vendorDocumentValidationService;
        this.tinService = tinService;
        this.binService = binService;
        this.vendorRepository = vendorRepository;
        this.tradeLicenseService = tradeLicenseService;
        this.nidService = nidService;
        this.bankSolvencyService = bankSolvencyService;
        this.vendorScoreRepository = vendorScoreRepository;
        this.businessDetailsRepository = businessDetailsRepository;
        this.generalDetailsRepository = generalDetailsRepository;
        this.documentRepository = documentRepository;
    }


    public void addVendorScore(Vendor vendor){
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.PENDING_VERIFICATION);
        VendorScore vendorScore = new VendorScore();
        setVendorScore(vendor, vendorScore);
    }

    @Override
    public DocumentHolderResponseDto create(Long userId, DocumentHolderRequestDto dto) {
        Optional<Vendor> vendorOptional = vendorRepository.findByUserId(userId);
        DocumentHolderResponseDto responseDto = new DocumentHolderResponseDto();
        if(vendorOptional.isEmpty()) throw new AesException("No Vendor Found for this user Id");
        if(vendorOptional.get().getDocumentHolder() == null) {
            DocumentHolder documentHolder = new DocumentHolder();
            Vendor vendor = vendorOptional.get();
            documentHolder = documentHolderRepository.save(documentHolder);
            enableAllDocumentTypes(documentHolder, dto);
            documentHolder.setName(vendor.getName());
            documentHolder = documentHolderRepository.save(documentHolder);
            vendor.setDocumentHolder(documentHolder);
            vendorRepository.save(vendor);
            responseDto.setId(documentHolder.getId());
            responseDto.setMsg("Document Holder Created");
            responseDto.setDocumentHolderStatus(documentHolder.getDocumentHolderStatus());
            return responseDto;
        }
        else{
            DocumentHolder documentHolder = vendorOptional.get().getDocumentHolder();
            responseDto.setMsg("Document Holder Already Exists");
            responseDto.setId(documentHolder.getId());
            responseDto.setDocumentHolderStatus(documentHolder.getDocumentHolderStatus());
//            if (documentHolder.getDocumentList() != null && !documentHolder.getDocumentList().isEmpty()) {
//                responseDto.setDocumentsList(new HashSet<>(documentHolder.getDocumentList()));
//            }
            return responseDto;
        }
    }

    private void enableAllDocumentTypes(DocumentHolder documentHolder, DocumentHolderRequestDto dto) {
        if(dto.getTin()){
            TINDocument tinDocument = new TINDocument();
            tinDocument.setEnabledByDocumentHolder(true);
            tinDocument.setDocumentHolder(documentHolder);
            tinDocument = tinService.create(tinDocument);
            documentHolder.setTinDocument(tinDocument);
        }
        if(dto.getBin()){
            BINDocument binDocument = new BINDocument();
            binDocument.setEnabledByDocumentHolder(true);
            binDocument.setDocumentHolder(documentHolder);
            binDocument = binService.create(binDocument);
            documentHolder.setBinDocument(binDocument);
        }
        if(dto.getNid()){
            NIDDocument nidDocument = new NIDDocument();
            nidDocument.setEnabledByDocumentHolder(true);
            nidDocument.setDocumentHolder(documentHolder);
            nidDocument = nidService.create(nidDocument);
            documentHolder.setNidDocument(nidDocument);
        }
        if(dto.getTradeLicense()){
            TradeDocument tradeDocument = new TradeDocument();
            tradeDocument.setDocumentHolder(documentHolder);
            tradeDocument.setEnabledByDocumentHolder(true);
            tradeDocument = tradeLicenseService.create(tradeDocument);
            documentHolder.setTradeDocument(tradeDocument);
        }
        if(dto.getBankSolvency()){
            BankSolvencyDocument bankSolvencyDocument = new BankSolvencyDocument();
            bankSolvencyDocument.setDocumentHolder(documentHolder);
            bankSolvencyDocument.setEnabledByDocumentHolder(true);
            bankSolvencyDocument = bankSolvencyService.create(bankSolvencyDocument);
            documentHolder.setBankSolvencyDocument(bankSolvencyDocument);
        }

        ///TODO Resume
    }

    @Override
    public DocumentHolderResponseDto getDocumentHolderById(Long id) {
        Optional<DocumentHolder> documentHolderOptional = documentHolderRepository.findById(id);
        if(documentHolderOptional.isEmpty()) throw new AesException("No Document Holder found with this id");
        DocumentHolder documentHolder = documentHolderOptional.get();
        Optional<Vendor> vendorOptional = vendorRepository.findByDocumentHolderId(id);
        Vendor vendor = new Vendor();
        if(vendorOptional.isPresent()){
            vendor = vendorOptional.get();
        }
        return mapEntityToDTO(documentHolder, vendor);
    }

    @Override
    public MisMatchResponseDto findMisMatch(MisMatchDto misMatchDto, Long documentHolderId) {;
        MisMatchResponseDto misMatchResponseDto = new MisMatchResponseDto();
        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
        if(!misMatchDto.isFullName()){
            Map<String, String> map = new HashMap<>();
            if(!documentHolder.getNidDocument().getEName().isEmpty())map.put("nid", documentHolder.getNidDocument().getEName());
            if(!documentHolder.getTinDocument().getName().isEmpty())map.put("tin", documentHolder.getTinDocument().getName());
            misMatchResponseDto.setFullName(map);
        }
        if(!misMatchDto.isNid()){
            Map<String, String> map = new HashMap<>();
            if(!documentHolder.getTradeDocument().getNid().isEmpty())map.put("trade", documentHolder.getTradeDocument().getNid());
            if(!documentHolder.getNidDocument().getNid().isEmpty())map.put("nid", documentHolder.getNidDocument().getNid());
            misMatchResponseDto.setNid(map);
        }
        if(misMatchDto.isAddress()) {
            Map<String, String> map = new HashMap<>();
            if (!documentHolder.getTinDocument().getPermanentAddress().isEmpty())
                map.put("tin", documentHolder.getTinDocument().getPermanentAddress());
            if (!documentHolder.getBinDocument().getAddress().isEmpty())
                map.put("bin", documentHolder.getBinDocument().getAddress());
            misMatchResponseDto.setAddress(map);
        }
        Optional<Vendor> optionalVendor = vendorRepository.findByDocumentHolderId(documentHolderId);
        optionalVendor.ifPresent(this::addVendorScore);
        return misMatchResponseDto;
    }


    @Override
    public void addHolderDetails(DetailsDTO dto, Long userId,  Long documentHolderId) {
        Optional<Vendor> vendorOptional = vendorRepository.findByUserId(userId);
        if(vendorOptional.isEmpty()) throw new AesException("No Vendor Found for this user Id");
        Vendor vendor = vendorOptional.get();
        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
        List<BusinessDetails> businessDetailsList = new ArrayList<>();
        for(BusinessDetailsDto details: dto.getBusinessDetails()){
            BusinessDetails newBusinessDetails = new BusinessDetails();
            newBusinessDetails.setBusinessType(details.getBusinessType());
            newBusinessDetails.setAnnualVolume(details.getAnnualVolume());
            newBusinessDetails.setNumberOfYear(details.getNumberOfYear());
            newBusinessDetails.setWorkOrderFile(details.getWorkOrderFile());
            newBusinessDetails.setOrgName(details.getOrgName());
            newBusinessDetails.setDocumentHolder(documentHolder);
            newBusinessDetails = businessDetailsRepository.save(newBusinessDetails);
            businessDetailsList.add(newBusinessDetails);
        }
        GeneralDetails generalDetails = modelMapper.map(dto.getGeneralDetails(), GeneralDetails.class);
        generalDetails.setDocumentHolder(documentHolder);
        generalDetails = generalDetailsRepository.save(generalDetails);
        documentHolder.setGeneralDetails(generalDetails);
        documentHolder = documentHolderRepository.save(documentHolder);
        vendor.setDocumentHolder(documentHolder);
        vendorRepository.save(vendor);
    }

    @Override
    public void updateHolderDetails(DetailsDTO dto, Long documentHolderId) {
        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
        List<BusinessDetails> businessDetailsList = new ArrayList<>();
        for(BusinessDetailsDto details: dto.getBusinessDetails()){
            BusinessDetails previousBusinessDetails = businessDetailsRepository.getReferenceById(details.getId());
            previousBusinessDetails.setBusinessType(details.getBusinessType());
            previousBusinessDetails.setAnnualVolume(details.getAnnualVolume());
            previousBusinessDetails.setNumberOfYear(details.getNumberOfYear());
            previousBusinessDetails.setWorkOrderFile(details.getWorkOrderFile());
            previousBusinessDetails.setOrgName(details.getOrgName());
            previousBusinessDetails.setDocumentHolder(documentHolder);
            previousBusinessDetails = businessDetailsRepository.save(previousBusinessDetails);
            businessDetailsList.add(previousBusinessDetails);
        }
        documentHolder.getBusinessDetailsRecords().clear();
        documentHolder.setBusinessDetailsRecords(businessDetailsList);
        GeneralDetails generalDetails = modelMapper.map(dto.getGeneralDetails(), GeneralDetails.class);
        generalDetails.setDocumentHolder(documentHolder);
        generalDetails = generalDetailsRepository.save(generalDetails);
        documentHolder.setGeneralDetails(generalDetails);
        documentHolderRepository.save(documentHolder);
    }

    @Override
    public ExtractedInformationDto getHolderExtractedDetailsForConfirmation(Long id) {
        ExtractedInformationDto dto = new ExtractedInformationDto();
        List<DocumentType> allDocTypes = Arrays.asList(DocumentType.BIN, DocumentType.TIN, DocumentType.NID, DocumentType.BANK_SOLVENCY, DocumentType.TRADE);
        for(DocumentType type: allDocTypes){
            Document document = documentRepository.getDocumentByDocumentHolderId(id, type.ordinal());
            if(document != null){
                if(type == DocumentType.BANK_SOLVENCY)dto.setSolvency(vendorDocumentValidationService.mapToDto(document.getResultFromMachineLearning(), document.getName()));
                else if(type == DocumentType.NID)dto.setNid(vendorDocumentValidationService.mapToDto(document.getResultFromMachineLearning(), document.getName()));
                else if(type == DocumentType.BIN)dto.setBin(vendorDocumentValidationService.mapToDto(document.getResultFromMachineLearning(), document.getName()));
                else if(type == DocumentType.TIN)dto.setTin(vendorDocumentValidationService.mapToDto(document.getResultFromMachineLearning(), document.getName()));
                else if(type == DocumentType.TRADE)dto.setTrade(vendorDocumentValidationService.mapToDto(document.getResultFromMachineLearning(), document.getName()));
            }
        }
        return dto;
    }

    @Override
    public DocumentHolder updateDocumentHolderStatus(Long id, DocumentHolderStatus status) {
        Optional<DocumentHolder> documentHolderOptional = documentHolderRepository.findById(id);
        if(documentHolderOptional.isEmpty()){
            throw new AesException("No document holder found with this id. Status couldn't be updated");
        }
        DocumentHolder documentHolder = documentHolderOptional.get();
        documentHolder.setDocumentHolderStatus(status);
        return documentHolderRepository.save(documentHolder);
    }

    public int calculateYearsOfBusiness(DocumentHolder documentHolder){
        Timestamp issueDateBin = documentHolder.getBinDocument().getIssueDate();
        if(issueDateBin == null)issueDateBin = Timestamp.from(Instant.now());
        return LocalDate.now().getYear() - issueDateBin.toLocalDateTime().getYear();
    }
    public void setVendorScore(Vendor vendor, VendorScore vendorScore){
        float totalBusinessYears = calculateYearsOfBusiness(vendor.getDocumentHolder());
        vendorScore.setYearOfEstablishmentWeight((float) ((5 * totalBusinessYears) / 10));
        vendorScore.setYearOfEstablishmentGrade((float) (totalBusinessYears / 10));
        vendorScore.setLegalDocumentationWeight(5F);
        vendorScore.setLegalDocumentationGrade(1F);
        vendorScore.setClientListAndCustomerReferenceWeight(10F);
        vendorScore.setOrganizationWeight(5F);
        vendorScore.setTypeOfBusinessWeight(10F);
        vendorScore.setNoOfEmployeeWeight(5F);
        vendorScore.setRelevantExperienceWeight(15F);
        vendorScore.setCapacityWeight(15F);
        vendorScore.setPhysicalVerificationWeight(30F);
        vendorScore = vendorScoreRepository.save(vendorScore);
        vendor.setVendorScore(vendorScore);
        vendorRepository.save(vendor);
    }

    private DocumentHolderResponseDto mapEntityToDTO(DocumentHolder documentHolder, Vendor vendor) {
        DocumentHolderResponseDto responseDto = new DocumentHolderResponseDto();
        responseDto.setDocumentHolderStatus(documentHolder.getDocumentHolderStatus());
        if(documentHolder.getName() != null){
            responseDto.setDocumentHolderName(documentHolder.getName());
        }
        if(documentHolder.getId() != null)responseDto.setId(documentHolder.getId());
        if(documentHolder.getBinDocument()!= null ){
            responseDto.setBinNumber(documentHolder.getBinDocument().getBin());
        }
        if(documentHolder.getBankSolvencyDocument()!= null){
            responseDto.setBankAccountNumber(documentHolder.getBankSolvencyDocument().getAccount());
        }
        if(documentHolder.getTinDocument() != null){
            responseDto.setTinNumber(documentHolder.getTinDocument().getTin());
        }
        if(documentHolder.getNidDocument()!= null){
            responseDto.setNidNumber(documentHolder.getNidDocument().getNid());
        }
        if(documentHolder.getTradeDocument()!= null ){
            responseDto.setTradeLicenseNumber(documentHolder.getTradeDocument().getTradeLicenseNumber());
        }
        if(documentHolder.getBusinessDetailsRecords() != null && !documentHolder.getBusinessDetailsRecords().isEmpty()){
            responseDto.setBusinessDetailsRecords(documentHolder.getBusinessDetailsRecords());
        }
        Optional<GeneralDetails> generalDetails = generalDetailsRepository.findByDocumentHolderId(documentHolder.getId());
        generalDetails.ifPresent(responseDto::setGeneralDetails);
        if(vendor != null){
            if(vendor.getCategory() != null){
                responseDto.setCategory(vendor.getCategory().getName());
            }
            if(vendor.getVendorType() != null){
                responseDto.setVendorType(vendor.getVendorType().getName());
            }
            if(vendor.getVendorSubCategories() != null){
                for(VendorSubCategory subCategory: vendor.getVendorSubCategories()){
                    responseDto.getVendorSubCategories().add(subCategory.getSubcategory().getName());
                }
            }
        }
        return responseDto;
    }
}
