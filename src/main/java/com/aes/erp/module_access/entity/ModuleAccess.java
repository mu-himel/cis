package com.aes.erp.module_access.entity;

import com.aes.erp.module_access.enums.ModuleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "modules")
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    private String uri;
    private String route;
    private String icon;

    @ManyToOne(fetch =FetchType.LAZY)
    private ModuleAccess parentModuleAccess;

    @OneToMany(mappedBy = "parentModuleAccess",cascade = CascadeType.ALL)
    private List<ModuleAccess> children=new ArrayList<>();

    private Integer displayOrder;

    @ColumnDefault(value = "true")
    private Boolean showInMenu;

    public ModuleAccess(Long id) {
        this.id = id;
    }
}
