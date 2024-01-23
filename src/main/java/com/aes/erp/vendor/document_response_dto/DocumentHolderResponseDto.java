package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolderStatus;
import lombok.Data;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class DocumentHolderResponseDto {
    private Long id;
    private String documentHolderName;
    private String nidNumber;
    private String tinNumber;
    private String binNumber;
    private String tradeLicenseNumber;
    private String bankAccountNumber;
    private String category;
    private String vendorType;
    private List<String> vendorSubCategories = new ArrayList<>();
    private List<BusinessDetails> businessDetailsRecords;
    private GeneralDetails generalDetails;
    private String msg;
    private DocumentHolderStatus documentHolderStatus;
//    private Set<Document> documentsList = new HashSet<>();
}
