package com.aes.erp.organogram_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class RoleNode {

    @Id
    @Column(name = "id")
    private Long id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @ToString.Exclude
    private RoleNode parent;

    @ToString.Exclude
    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<RoleNode> childNodes = new ArrayList<>();

    @JsonIgnore
    @OneToOne
    @ToString.Exclude
    private Department parentDepartment;

//    @JsonIgnore
//    @ManyToOne
//    @ToString.Exclude
//    private OrganizationFile organizationFile;

    /*@ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "roleNodes")
    @JsonIgnore
    private Set<Folder> folders = new HashSet<>();*/


//    public RoleNode(String name, RoleNode parent, Department parentDepartment, OrganizationFile organizationFile) {
//        this.name = name;
//        this.parent = parent;
//        this.organizationFile = organizationFile;
//        //childNodes = new ArrayList<>();
//        this.parentDepartment = parentDepartment;
//    }
//
//    public RoleNode(String name, RoleNode parent, OrganizationFile organizationFile) {
//        this.name = name;
//        this.parent = parent;
//        this.organizationFile = organizationFile;
//        //childNodes = new ArrayList<>();
//    }
    public RoleNode(String name, RoleNode parent, Department parentDepartment) {
        this.name = name;
        this.parent = parent;
//        this.organizationFile = organizationFile;
        //childNodes = new ArrayList<>();
        this.parentDepartment = parentDepartment;
    }

    public RoleNode(String name, RoleNode parent) {
        this.name = name;
        this.parent = parent;
//        this.organizationFile = organizationFile;
        //childNodes = new ArrayList<>();
    }

    public RoleNode(Long id) {
        this.id = id;
    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    //    public RoleNode getParent() {
//        return parent;
//    }
    //CRUD Children
    //Read child nodes
//    public List<RoleNode> getChildNodes() {
//        return childNodes;
//    }
    //Add child nodes
    // public void addChild(RoleNode child) {
    public void addChild(List<RoleNode> childNodes, RoleNode child) {
        childNodes.add(child);
    }

//    public Department getParentDepartment() {
//        return parentDepartment;
//    }
    //Update and Delete child Nodes remaining
}
