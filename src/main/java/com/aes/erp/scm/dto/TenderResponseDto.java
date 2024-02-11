package com.aes.erp.scm.dto;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.scm.Entities.TenderItem;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TenderResponseDto {
    private Long id;
    private TenderType tenderType;
    private TenderStatus tenderStatus;
    private Organization creatorOrganization;
    private ItemCategory itemCategory;
    private Long itemQuantity;
    private Date creationDate;
    private List<TenderItem> items;
}
