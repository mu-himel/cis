package com.aes.erp.demand.dto.request;

import com.aes.erp.common.EntityConvertible;
import com.aes.erp.demand.entity.Demand;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandRequestDto implements EntityConvertible<Demand> {

    private Long id;

    @NotBlank(message = "Demand no is required")
    private String demandNo;

    @NotNull(message = "Demand Initiator Required")
    private DemandInitiator requestedBy;

    @NotNull(message = "Demand Detail is required")
    List<DemandDetailDto> demandDetails;

    @NotNull(message = "Demand Category is required")
    DemandCategoryDto category;

    DemandCategoryDto subCategory;

    Boolean isVerificationRequired;


    @Override
    @ApiModelProperty(hidden = true)
    public Demand getEntity() {
        Demand demand = Demand.builder()
                .id(id)
                .demandNo(demandNo)
                .build();

        return demand;
    }
}
