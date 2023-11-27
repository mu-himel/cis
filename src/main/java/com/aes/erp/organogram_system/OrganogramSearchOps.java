package com.aes.erp.organogram_system;

import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.repository.DepartmentRepository;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganogramSearchOps {// common problem is that only first case is being met, others are failing

    private final DepartmentRepository departmentRepository;
    private final RoleNodeRepository roleNodeRepository;
    
    public Department handleRootDepartment(List<String> departmentTree, Department tmp){
        if(departmentTree.size() <= 1){
            return tmp;
        }else{
            departmentTree.remove(0);
            return searchDepartment(departmentTree, tmp);
        }
    }

    public Department handleNonRootDepartment(List<String> departmentTree, Department dep){
        if(departmentTree.size() <= 1){
            return dep;
        }else{
            departmentTree.remove(0);
            return searchDepartment(departmentTree, dep);
        }
    }

    public Department searchDepartment(List<String> departmentTree, Department rootDepartment) {
        Department tmp = rootDepartment;

        if(departmentTree.get(0).equals("root")){
            return handleRootDepartment(departmentTree, tmp);
        }
        List<Department> childDepartment = departmentRepository.findAllByParentDepartmentId(tmp.getId());
        for (Department dep : childDepartment/*tmp.getChildDepartments()*/) {
            if (dep.getName().equals(departmentTree.get(0))) {
                return handleNonRootDepartment(departmentTree, dep);
            }
        }
        return null;//return null if department not found
    }

    private RoleNode handRootRoleNode(List<String> roleTree, RoleNode child) {
        if(roleTree.size() <= 1){
            return child;
        }else{
            roleTree.remove(0);
            return searchRole(roleTree, child);
        }
    }

    private RoleNode handleNonRootRoleNode(List<String> roleTree, RoleNode child){
        if(roleTree.size() <= 1){
            return child;
        }else{
            roleTree.remove(0);
            return searchRole(roleTree, child);
        }
    }

    public RoleNode searchRole(List<String> roleTree, RoleNode node) {//now give us what we expect
        RoleNode tmp = node;
        if(roleTree.get(0).equals("root")){
            return handRootRoleNode(roleTree, node);
        }
        List<RoleNode> childNodes = roleNodeRepository.findByParentId(tmp.getId());
        for(RoleNode child : childNodes/*tmp.getChildNodes()*/) {
            if(roleTree.get(0).equals(child.getName())) {
                return handleNonRootRoleNode(roleTree, child);
            }
        }
        return null;
    }

}
