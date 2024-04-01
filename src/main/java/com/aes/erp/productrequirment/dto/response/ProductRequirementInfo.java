package com.aes.erp.productrequirment.dto.response;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.productrequirment.entity.ProductRequirement;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequirementInfo{

    private Long id;

    private InventoryRefInfo category;

    private InventoryRefInfo subCategory;

    private DemandInfo demand;


    private DemandDetailInfo demandDetail;


}

@Data
@NoArgsConstructor
@AllArgsConstructor
class InventoryRefInfo {
    private Long id;
    private String name;
    private String code;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class DemandInfo {
    private Long id;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class DemandDetailInfo {
    private Long id;
    private InventoryRefInfo item;
}