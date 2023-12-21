package com.aes.erp.vendor.service.DocumentHolderServices;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.document_response_dto.*;
import com.aes.erp.vendor.dto.BusinessDetailsDto;
import com.aes.erp.vendor.entity.DocmentEntities.*;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorScore;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.repository.*;
import com.aes.erp.vendor.service.DocumentServices.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class DocumentHolderServiceImpl implements DocumentHolderService{
    private final DocumentHolderRepository documentHolderRepository;
    private final TINService tinService;
    private final BinService binService;
    private final VendorRepository vendorRepository;
    private final TradeLicenseService tradeLicenseService;
    private final NIDService nidService;
    private final BankSolvencyService bankSolvencyService;
    private final VendorScoreRepository vendorScoreRepository;

    private final BusinessDetailsRepository businessDetailsRepository;
    private final GeneralDetailsRepository generalDetailsRepository;
    ModelMapper modelMapper = new ModelMapper();

    public DocumentHolderServiceImpl(DocumentHolderRepository documentHolderRepository, TINService tinService, BinService binService, VendorRepository vendorRepository, TradeLicenseService tradeLicenseService, NIDService nidService, BankSolvencyService bankSolvencyService, VendorScoreRepository vendorScoreRepository, BusinessDetailsRepository businessDetailsRepository, GeneralDetailsRepository generalDetailsRepository) {
        this.documentHolderRepository = documentHolderRepository;
        this.tinService = tinService;
        this.binService = binService;
        this.vendorRepository = vendorRepository;
        this.tradeLicenseService = tradeLicenseService;
        this.nidService = nidService;
        this.bankSolvencyService = bankSolvencyService;
        this.vendorScoreRepository = vendorScoreRepository;
        this.businessDetailsRepository = businessDetailsRepository;
        this.generalDetailsRepository = generalDetailsRepository;
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
            return responseDto;
        }
        else{
            responseDto.setMsg("Document Holder Already Exists");
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
        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(id);
        return mapEntityToDTO(documentHolder);
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
        documentHolder.setBusinessDetailsRecords(businessDetailsList);
        GeneralDetails generalDetails = modelMapper.map(dto.getGeneralDetails(), GeneralDetails.class);
        generalDetails.setDocumentHolder(documentHolder);
        generalDetailsRepository.save(generalDetails);
//        generalDetails = generalDetailsRepository.save(generalDetails);
//        documentHolder.setGeneralDetails(generalDetails);
//        documentHolder = documentHolderRepository.save(documentHolder);
//        vendor.setDocumentHolder(documentHolder);
//        vendorRepository.save(vendor);
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

    private DocumentHolderResponseDto mapEntityToDTO(DocumentHolder documentHolder) {
        DocumentHolderResponseDto responseDto = new DocumentHolderResponseDto();
        if(documentHolder.getName() != null && !documentHolder.getName().isEmpty()){
            responseDto.setDocumentHolderName(documentHolder.getName());
        }
        if(documentHolder.getId() != null)responseDto.setId(documentHolder.getId());
        if(documentHolder.getBinDocument()!= null && !documentHolder.getBinDocument().getBin().isEmpty()){
            responseDto.setBinNumber(documentHolder.getBinDocument().getBin());
        }
        if(documentHolder.getBankSolvencyDocument()!= null && !documentHolder.getBankSolvencyDocument().getAccount().isEmpty()){
            responseDto.setBankAccountNumber(documentHolder.getBankSolvencyDocument().getAccount());
        }
        if(documentHolder.getTinDocument() != null && !documentHolder.getTinDocument().getTin().isEmpty()){
            responseDto.setTinNumber(documentHolder.getTinDocument().getTin());
        }
        if(documentHolder.getNidDocument()!= null && !documentHolder.getNidDocument().getNid().isEmpty()){
            responseDto.setNidNumber(documentHolder.getNidDocument().getNid());
        }
        if(documentHolder.getTradeDocument()!= null && !documentHolder.getTradeDocument().getTradeLicenseNumber().isEmpty()){
            responseDto.setTradeLicenseNumber(documentHolder.getTradeDocument().getTradeLicenseNumber());
        }
        return responseDto;
    }
}
