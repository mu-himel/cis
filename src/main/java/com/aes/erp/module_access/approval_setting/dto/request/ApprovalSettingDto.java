package com.aes.erp.module_access.approval_setting.dto.request;

import com.aes.erp.common.ReferenceObjectDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalSettingDto {

    private Long id;


    private ReferenceObjectDto department;

    @ManyToOne
    private ReferenceObjectDto designation;

    @ManyToOne
    private ReferenceObjectDto moduleAccess;

    private Integer approvalOrder;

    private Boolean isDefault;

    private List<ApprovalSettingOptionDto> approvalSettingOptions;
}
