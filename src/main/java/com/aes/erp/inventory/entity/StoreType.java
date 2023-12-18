package com.aes.erp.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "store_types")
@NoArgsConstructor
@AllArgsConstructor
public class StoreType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private String name;
    private Boolean active = true;
//    @JsonIgnore
//    @OneToMany(mappedBy = "storeType", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<ItemCategory> categories;
}
