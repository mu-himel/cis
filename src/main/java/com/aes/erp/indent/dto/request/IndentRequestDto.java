//package com.aes.erp.indent.dto.request;
//
//import com.aes.erp.common.EntityConvertable;
//import com.aes.erp.indent.entity.Indent;
//import com.aes.erp.indent.entity.IndentDetail;
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
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class IndentRequestDto implements EntityConvertable<Indent> {
//    @NotNull
//    private List<Long> ids;
//
//    private String deliveryLocation;
//
//
//    @Override
//    @ApiModelProperty(hidden = true)
//    public Indent getEntity() {
//        Indent indent = new Indent(null);
//        return indent;
//    }
//}
