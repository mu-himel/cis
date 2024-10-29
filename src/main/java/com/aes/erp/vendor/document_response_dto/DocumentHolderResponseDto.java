package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.vendor.entity.DocmentEntities.*;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolderStatus;
import lombok.Data;

import javax.persistence.Lob;
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
    private String certificateOfIncorporation;
    private String articleOfAssociation;
    private String memorandumOfAssociation;
    private String category;
    private String vendorType;
    private List<String> vendorSubCategories = new ArrayList<>();
    private List<BusinessDetails> businessDetailsRecords;
    private GeneralDetails generalDetails;
    private AuthorizedPerson authorizedPerson;
    private String msg;
    private DocumentHolderStatus documentHolderStatus;
//    @Lob
    private String vendorImage;
//    private Set<Document> documentsList = new HashSet<>();
}
