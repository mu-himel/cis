package com.aes.erp.vendor.dto;

import com.aes.erp.common.EntityConvertible;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.enums.VendorStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link Vendor} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDto implements Serializable, EntityConvertible<Vendor> {
    // TODO: 11-Oct-23 validations for dto

    private Long id;
    private String name;

    @Email(message = "Please provide your E-mail address")
    private String email;


    @Length(max=20, message = "Max 20 digit")
    private String phone;


    private VendorType vendorType;

    private String password;

    private ReferenceObjectDto category;

    private ReferenceObjectDto subCategory;

    private List<ReferenceObjectDto> items;

    // nid validation
//    private String nid;
//    private String tin;
//    private String bankAccountNumber;
//    private String businessIdNumber;
//    private String tradeLicense;



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