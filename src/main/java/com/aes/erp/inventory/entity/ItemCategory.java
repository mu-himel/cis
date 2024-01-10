package com.aes.erp.inventory.entity;


import com.aes.erp.vendor.entity.Vendor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "item_categories")
public class ItemCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false)
  private Long id;

  private String name;

  @Column(unique = true, name="code")
  private String code;

  @OneToMany(mappedBy = "subcategory", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<SubCategoryBrand> subcategoryBrands = new HashSet<>();
  @ManyToOne
  private ItemCategory parentCategory;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "store_type_id")
  private StoreType storeType;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  @ApiModelProperty(hidden = true)
  private List<CategoryBudget> budgets;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<CategoryAttribute> attributes;

  private Boolean active=true;

  private BigDecimal vat;

  @Column(updatable = false)
  private Long createdAt;

  private Long updatedAt;
  @JsonIgnore
  @ManyToOne
  private Vendor vendor;
  public ItemCategory(Long id) {
    this.id = id;
  }
}


