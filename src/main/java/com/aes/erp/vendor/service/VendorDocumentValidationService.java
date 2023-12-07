package com.aes.erp.vendor.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.document_response_dto.*;
import com.aes.erp.vendor.entity.DocmentEntities.*;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import com.aes.erp.vendor.service.DocumentServices.DocumentService;
import com.aes.erp.vendor.utils.GenericModelMapper;
import com.aes.erp.vendor.utils.GenericObjectMapper;
import com.aes.erp.vendor.utils.RestTemplateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VendorDocumentValidationService {
    public final String TIN_URL =  "http://172.17.18.41:5052/tin";
    public final String BIN_URL =  "http://172.17.18.41:5051/bin";
    public final String NID_URL =  "http://172.17.18.41:5054/nid";
    public final String BANK_SOLVENCY_URL =  "http://172.17.18.41:5050/bank";
    public final String TRADE_LICENSE_URL =  "http://172.17.18.41:5053/trade";
    private final RestTemplateService restClient;
    private final GenericObjectMapper genericMapper;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;

    public VendorDocumentValidationService(RestTemplateService restClient, GenericObjectMapper genericMapper, DocumentHolderRepository documentHolderRepository, DocumentService documentService) {
        this.restClient = restClient;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
        this.genericMapper = genericMapper;
    }
    public void multipartFileToBytes(MultipartFile file, Document document){
        try{
            document.setFile(file.getBytes());
        }catch (IOException error){
            throw new AesException("File to Byte conversion failed");
        }
    }

    @Async
    public Object validateDocument(Long documentHolderId, String fileName, MultipartFile file, String orgName){
        //Starting an Asynchronous Task
        //Creating a document Entity first
        Document document = new Document();
        document.setDocumentHolder(documentHolderRepository.getReferenceById(documentHolderId));
        multipartFileToBytes(file, document);
        document.setName(fileName);

        String url = "";
        String result = "";
        if(fileName.equals("TIN")){
            url = TIN_URL;
            document.setDocumentType(DocumentType.TIN);
        }
        else if(fileName.equals("BIN")){
            url = BIN_URL;
            document.setDocumentType(DocumentType.BIN);
        }
        else if(fileName.equals("NID")){
            url = NID_URL;
            document.setDocumentType(DocumentType.NID);
        }
        else if(fileName.equals("BANK")){
            url = BANK_SOLVENCY_URL;
            document.setDocumentType(DocumentType.BANK_SOLVENCY);
        }
        else if(fileName.equals("TRADE")){
            url = TRADE_LICENSE_URL;
            document.setDocumentType(DocumentType.TRADE);
        }
        try{
            result = restClient.postPdfFile(documentHolderId, fileName, file, orgName, url);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
           if(result == null || jsonNode.has("Error")){
               throw new AesException("Wrong document uploaded");
           }
           else{
               document.setResultFromMachineLearning(result);
               documentService.create(document);
               //Finishing The asynchronous task
               return mapToDto(result, fileName);
           }
        }catch(Error | IOException e){
            System.out.println(e.getMessage());
            throw new AesException("Document information extraction process failed. Error -->" + e.getMessage());
        }
    }
    public <T> T mapToDto(String result, String fileName) {
        try {
            Class<T> dtoClass = getDtoClassForFileName(fileName);
            System.out.println(result);
            return genericMapper.convertStringToDto(result, dtoClass);
        } catch (Exception e) {
            System.out.println("Object Mapping failed: " + e.getMessage());
            throw new AesException(e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private <T> Class<T> getDtoClassForFileName(String fileName) {
        return switch (fileName) {
            case "TIN" -> (Class<T>) TinResponseDto.class;
            case "BIN" -> (Class<T>) BinResponseDto.class;
            case "NID" -> (Class<T>) NidResponseDto.class;
            case "TRADE" -> (Class<T>) TradeLicenseDto.class;
            case "BANK" -> (Class<T>) BankSolvencyDto.class;

            default -> null;
        };
    }

}
