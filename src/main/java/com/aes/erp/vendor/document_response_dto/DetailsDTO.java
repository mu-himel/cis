package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.dto.BusinessDetailsDto;
import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DetailsDTO {
    private List<BusinessDetailsDto> businessDetails = new ArrayList<>();
    private GeneralDetails generalDetails;
}
