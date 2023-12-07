package com.aes.erp.vendor.service.DocumentHolderServices;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.document_response_dto.*;
import com.aes.erp.vendor.entity.DocmentEntities.*;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorScore;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.repository.*;
import com.aes.erp.vendor.service.DocumentServices.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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



    @Override
    public DocumentHolderResponseDto create(Long userId, DocumentHolderRequestDto dto) {
        DocumentHolder documentHolder = new DocumentHolder();
        Optional<Vendor> vendorOptional = vendorRepository.findByUserId(userId);
        if(vendorOptional.isEmpty()) throw new AesException("No Vendor Found for this user Id");
        Vendor vendor = vendorOptional.get();
        documentHolder = documentHolderRepository.save(documentHolder);
        enableAllDocumentTypes(documentHolder, dto);
        documentHolder.setName(vendor.getName());
        documentHolder = documentHolderRepository.save(documentHolder);
        vendor.setDocumentHolder(documentHolder);
        vendorRepository.save(vendor);
        DocumentHolderResponseDto responseDto = new DocumentHolderResponseDto();
        responseDto.setId(documentHolder.getId());
        return responseDto;
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
        return misMatchResponseDto;
    }

    @Override
    public void addHolderDetails(DetailsDTO dto, Long userId,  Long documentHolderId) {
        Optional<Vendor> vendorOptional = vendorRepository.findByUserId(userId);
        if(vendorOptional.isEmpty()) throw new AesException("No Vendor Found for this user Id");
        Vendor vendor = vendorOptional.get();
        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
        BusinessDetails businessDetails = modelMapper.map(dto.getBusinessDetails(), BusinessDetails.class);
        GeneralDetails generalDetails = modelMapper.map(dto.getGeneralDetails(), GeneralDetails.class);
        generalDetails = generalDetailsRepository.save(generalDetails);
        businessDetails = businessDetailsRepository.save(businessDetails);
        documentHolder.setBusinessDetails(businessDetails);
        documentHolder.setGeneralDetails(generalDetails);
        documentHolder = documentHolderRepository.save(documentHolder);
        vendor.setVerificationStatus(VendorDocumentVerificationStatus.DOCUMENTS_SUBMITTED);
        vendor.setDocumentHolder(documentHolder);
        //Setting up Score For Vendor
        VendorScore vendorScore = new VendorScore();
        setVendorScore(vendor, vendorScore);
        vendorRepository.save(vendor);
    }
    public Long calculateYearsOfBusiness(DocumentHolder documentHolder){
        Date effectiveDateBin = documentHolder.getBinDocument().getEffectiveDate();
        return (long) (LocalDate.now().getYear() - effectiveDateBin.getYear());
    }
    public void setVendorScore(Vendor vendor, VendorScore vendorScore){
        Long totalBusinessYears = calculateYearsOfBusiness(vendor.getDocumentHolder());
        vendorScore.setYearOfEstablishmentWeight((float) ((5 * totalBusinessYears) / 10));
        vendorScore.setYearOfEstablishmentWeight((float) (totalBusinessYears / 10));
        vendorScore.setLegalDocumentationWeight(5F);
        vendorScore.setLegalDocumentationGrade(1F);
        vendorScore = vendorScoreRepository.save(vendorScore);
        vendor.setVendorScore(vendorScore);
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
