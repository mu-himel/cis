//package com.aes.erp.indent.dto.request;
//
//import com.aes.erp.common.EntityConvertable;
//import com.aes.erp.indent.entity.PrIndent;
//import com.aes.erp.indent.entity.PrIndentDetail;
//import com.aes.erp.indent.enums.IndentPriority;
//import com.aes.erp.inventory.entity.Item;
//import com.aes.erp.inventory.entity.ItemCategory;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.beans.BeanUtils;
//
//import javax.validation.constraints.NotNull;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class PrIndentRequestDto implements EntityConvertable<PrIndent> {
//
//    private Long id;
//
//    private String indentNo;
//
//    @NotNull(message = "Item Category Missing")
//    private ItemCategory category;
//
//    @NotNull(message = "Item Sub Category Missing")
//    private ItemCategory subCategory;
//
//    @NotNull(message = "PR Indent Priority Missing")
//    private IndentPriority priority;
//    @NotNull(message = "PR Indent Priority Missing")
//    private LocalDateTime priorityDate;
//
//    @NotNull(message = "PR Indent Detail is required")
//    private List<IndentDetailRequestDto> prIndentDetails;
//
//    private String deliveryLocation;
//
//    public PrIndentRequestDto(Long id) {
//        this.id = id;
//    }
//
//    @Override
//    @ApiModelProperty(hidden = true)
//    public PrIndent getEntity() {
//        PrIndent prIndent = new PrIndent(id);
//        List<PrIndentDetail> prIndentDetails = new ArrayList<>();
//        BeanUtils.copyProperties(this, prIndent);
//
//        if (CollectionUtils.isNotEmpty(this.getPrIndentDetails())) {
//
//            this.getPrIndentDetails().forEach(x -> {
//                PrIndentDetail prIndentDetail = new PrIndentDetail();
//                BeanUtils.copyProperties(x, prIndentDetail);
//                prIndentDetail.setItem(new Item(x.getItem().getId()));
//                prIndentDetail.setPrIndent(prIndent);
//                prIndentDetails.add(prIndentDetail);
//            });
//            prIndent.setPrIndentDetails(prIndentDetails);
//        }
//        return prIndent;
//    }
//}
