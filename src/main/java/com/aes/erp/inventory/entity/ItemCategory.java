package com.aes.erp.inventory.entity;


import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_categories")
public class ItemCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false)
  private Long id;

  private String name;

  @Column(unique = true, name="code")
  private String code;

  @ManyToOne
  private ItemCategory parentCategory;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_type_id")
  private StoreType storeType;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  @ApiModelProperty(hidden = true)
  private List<CategoryBudget> budgets;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<CategoryAttribute> attributes;

  private Boolean active=true;

  private BigDecimal vat;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;


  public ItemCategory(Long id) {
    this.id = id;
  }
}


