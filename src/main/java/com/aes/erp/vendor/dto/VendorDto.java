package com.aes.erp.vendor.dto;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.enums.VendorStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link Vendor} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDto implements Serializable, EntityConvertable<Vendor> {
    // TODO: 11-Oct-23 validations for dto

    private Long id;
    private String name;

    @Email(message = "Please provide your E-mail address")
    private String email;


    @Length(max=20, message = "Max 20 digit")
    private String phone;


    @NotBlank(message = "Vendor Type is required")
    private VendorType vendorType;
    // @NotBlank(message = "Vendor Type is required")
    
    private Long vendorTypeId;

    private String password;
    private ReferenceObjectDto category;

    private List<Long> subCategory;

    private List<ReferenceObjectDto> items;



    @Override
    @ApiModelProperty(hidden = true)
    public Vendor getEntity() {
        return new Vendor(
                id,
                name ,
                email ,
                phone
        );
    }
}