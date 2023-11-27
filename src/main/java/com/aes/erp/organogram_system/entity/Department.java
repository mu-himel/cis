package com.aes.erp.organogram_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Department {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private RoleNode rootRoleNode;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Department parentDepartment;

    @OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Department> childDepartments = new ArrayList<>();

    private boolean hasUser = false;

    private Integer level;

//    public Department(String name, OrganizationFile organization) {
//        this.name = name;
//        this.parentDepartment = null;
//        this.rootRoleNode = new RoleNode("RootRoleNode", null, organization); // Create the no name, no parent rootRoleNode
//    }
//
//
//    public Department(String name, Department parentDepartment, OrganizationFile organization) {
//        this.name = name;
//        this.parentDepartment = parentDepartment;
//        this.rootRoleNode = new RoleNode("RootRoleNode", null,this, organization); // Create the no name, no parent rootRoleNode
//    }


    public Department(Long id) {
        this.id = id;
    }

    public Department(String name) {
        this.name = name;
        this.parentDepartment = null;
        this.rootRoleNode = new RoleNode("RootRoleNode", null); // Create the no name, no parent rootRoleNode
    }


    public Department(String name, Department parentDepartment) {
        this.name = name;
        this.parentDepartment = parentDepartment;
        this.rootRoleNode = new RoleNode("RootRoleNode", null,this); // Create the no name, no parent rootRoleNode
    }

//    public String getName() {
//        return name;
//    }

//    public void setName(String name) {
//        this.name = name;
//    }

    //READ RoleNode
//    public RoleNode getRootRoleNode() {
//        return rootRoleNode;
//    }

//    public Department getParentDepartment() {
//        return parentDepartment;
//    }
    //READ Child Departments
//    public List<Department> getChildDepartments() {
//        return childDepartments;
//    }

    //Add child departments
//    public Department addChildDepartment(List<Department> childDepartments, String name, OrganizationFile organization){
//        //input validation must be done somewhere in the request/response route
//        Department temp = new Department(name, this, organization);
//        childDepartments.add(temp);
//        return temp;
//    }
    public Department addChildDepartment(List<Department> childDepartments, String name){
        //input validation must be done somewhere in the request/response route
        Department temp = new Department(name, this);

        temp.level = (this.level !=null)? this.level+1:0;

        childDepartments.add(temp);
        return temp;
    }
    //Update and Delete child Nodes remaining

    //Add child Role Node
    /*public void addChildRoleNode(RoleNode parent, RoleNode child){//given parent, add child role node
        parent.addChild(child);
    }*/

    //Update and Delete child Role Nodes remaining

}