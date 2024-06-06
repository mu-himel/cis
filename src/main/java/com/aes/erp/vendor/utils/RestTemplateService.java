package com.aes.erp.vendor.utils;
import com.aes.erp.exception.AesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
public class RestTemplateService {
    private final RestTemplate restTemplate;

    @Autowired
    private MLApiConfig mlApiConfig;

    @Autowired
    public RestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public String postPdfFile(Long documentHolderId, String docType, MultipartFile file, String org_name, String url) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
    
        if(url.contains(mlApiConfig.getTrade()) || url.contains(mlApiConfig.getSolvency())){
            body.add("secret_key", "secret_key");
            body.add("documentHolderId", documentHolderId);
            body.add("org_name", org_name);
        }else{
            body.add("docType", docType);
        }
        


        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println(response.getBody());
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                // Handle non-successful response, if needed
                throw new AesException(response.getStatusCode() + " Error in information extraction process");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new AesException(" Error in information extraction process --> " + e.getMessage());
        }
    }
}
