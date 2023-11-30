package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import lombok.Data;

@Data
public class DetailsDTO {
    private BusinessDetails businessDetails;
    private GeneralDetails generalDetails;
}
