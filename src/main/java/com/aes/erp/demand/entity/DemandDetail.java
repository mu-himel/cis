package com.aes.erp.demand.entity;

import com.aes.erp.demand.enums.DemandItemStatus;
import com.aes.erp.demand.enums.DemandPriority;
import com.aes.erp.demand.enums.DemandStatus;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@Table(name = "demand_details")
public class DemandDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ApiModelProperty(hidden = true)
    private Demand demand;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemCategory itemCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Category is Required")
    private ItemCategory itemParentCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Product is required")
    private Item item;

    @NotNull(message = "Quantity is required")
    private Integer requestQuantity;

    private Integer approvedQuantity;

    private Integer currentStock;

    private String specification;

    @Column(length = 500)
    private String receiveNote;

    @Column(length = 500)
    private String storeNote;

    @Enumerated(EnumType.STRING)
    private DemandPriority priority;

    @Enumerated(EnumType.STRING)
    private DemandStatus status;

    @OneToMany(mappedBy = "demandDetail", cascade = CascadeType.ALL)
    private List<DemandDetailAttribute> attributes;
}
