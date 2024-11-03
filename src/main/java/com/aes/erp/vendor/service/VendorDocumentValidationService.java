package com.aes.erp.vendor.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.vendor.document_response_dto.*;
import com.aes.erp.vendor.entity.DocmentEntities.*;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import com.aes.erp.vendor.service.DocumentServices.DocumentService;
import com.aes.erp.vendor.utils.GenericModelMapper;
import com.aes.erp.vendor.utils.GenericObjectMapper;
import com.aes.erp.vendor.utils.MLApiConfig;
import com.aes.erp.vendor.utils.RestTemplateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class VendorDocumentValidationService {
    private final RestTemplateService restClient;
    private final GenericObjectMapper genericMapper;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;
    private final MLApiConfig mlApiConfig;
    private final FileUploadService fileUploadService;

    @Value("${uploadDir}")
    private String uploadDir;

    public VendorDocumentValidationService(RestTemplateService restClient, GenericObjectMapper genericMapper, DocumentHolderRepository documentHolderRepository,
    DocumentService documentService,  MLApiConfig mlApiConfig, FileUploadService fileUploadService) {
        this.restClient = restClient;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
        this.genericMapper = genericMapper;
        this.mlApiConfig = mlApiConfig;
        this.fileUploadService = fileUploadService;
    }
    public void multipartFileToBytes(MultipartFile file, Document document){
        try{
            document.setFile(file.getBytes());
        }catch (IOException error){
            throw new AesException("File to Byte conversion failed");
        }
    }

    
    public Object validateDocument(Long documentHolderId, String docType, MultipartFile file, String orgName){
        //Starting an Asynchronous Task
        //Creating a document Entity first
        Document document = new Document();
        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
        document.setDocumentHolder(documentHolder);
        document.setContentType(file.getContentType());
        Path path = Path.of(uploadDir+"/vendor/doc/"+documentHolderId+"/"+docType);
        fileUploadService.uploadFile(path, file);
        // multipartFileToBytes(file, document);
        document.setName(docType);
        document.setFileName(file.getOriginalFilename());
        try {
            // document.setFile(file.getBytes());
        
            document.setFilePath(path.toString());
            String url = mlApiConfig.getApiEndpoint();;
            String result = "";
            if(docType.toUpperCase().equals("TIN")){
                // url = mlApiConfig.getApiEndpoint();
                document.setDocumentType(DocumentType.TIN);
            }
            else if(docType.toUpperCase().equals("BIN")){
                // url = mlApiConfig.getApiEndpoint();
                document.setDocumentType(DocumentType.BIN);
            }
            else if(docType.toUpperCase().equals("NID")){
//                url = mlApiConfig.getApiEndpoint_dev_cluster();
                document.setDocumentType(DocumentType.NID);
            }
            else if(docType.toUpperCase().contains("SOLVENCY")){
                // url = mlApiConfig.getSolvency();
                document.setDocumentType(DocumentType.BANK_SOLVENCY);
            }
            else if(docType.toUpperCase().contains("TRADE")){
                url = mlApiConfig.getApiEndpoint_dev_cluster();
                document.setDocumentType(DocumentType.TRADE);
            }
            else if(docType.toUpperCase().contains("IRC")){
                // url = mlApiConfig.getTrade();
                document.setDocumentType(DocumentType.IRC);
            }else if(docType.toUpperCase().contains("AOA")){
                document.setDocumentType(DocumentType.AOA);
                document.setResultFromMachineLearning("{\"company_name\":\"null\", \"address\":\"null\", \"year_of_establishment\":\"2000\"}");
                documentService.create(document);
                return new ArticleOfAssociationDto();
            }else if(docType.toUpperCase().contains("MOA")){
                document.setDocumentType(DocumentType.MOA);
                document.setResultFromMachineLearning("{\"company_name\":\"null\"}");
                documentService.create(document);
                return new MemorandumOfAssociationDto();
            }
//            else if(docType.toUpperCase().contains("OWNER_IMAGE")){
//                document.setDocumentType(DocumentType.OWNER_IMAGE);
//                documentService.create(document);
//                return null;
//            }
            
            result = restClient.postPdfFile(documentHolderId, docType.toLowerCase(), file, orgName, url);
            document.setResultFromMachineLearning(result);
            documentService.create(document);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);

            if (result == null || jsonNode.has("Error") || jsonNode.has("state")) {
                throw new AesException("Wrong document uploaded");
            } else {
                //Finishing The asynchronous task
                return mapToDto(result, docType);
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
        return switch (fileName.toUpperCase()) {
            case "TIN" -> (Class<T>) TinResponseDto.class;
            case "BIN" -> (Class<T>) BinResponseDto.class;
            case "NID" -> (Class<T>) NidResponseDto.class;
            case "TRADE" -> (Class<T>) TradeLicenseDto.class;
            case "SOLVENCY" -> (Class<T>) BankSolvencyDto.class;
            case "BANK_SOLVENCY" -> (Class<T>) BankSolvencyDto.class;
            case "MOA" -> (Class<T>) MemorandumOfAssociationDto.class;
            case "IRC" -> (Class<T>) CertificateOfIncorporationDto.class;
            case "AOA" -> (Class<T>) ArticleOfAssociationDto.class;
            default -> null;
        };
    }
    public void removePreviousSameTypeDocument(DocumentHolder documentHolder, DocumentType documentType){
        for(Document document : documentHolder.getDocumentList()){
            if(documentType.equals(document.getDocumentType())){
                documentHolder.removeDocument(document);
            }
        }
    }
}
