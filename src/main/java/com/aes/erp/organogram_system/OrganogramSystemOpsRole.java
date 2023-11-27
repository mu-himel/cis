package com.aes.erp.organogram_system;

import com.aes.erp.exception.AesException;
import com.aes.erp.helper.BaseHelper;
import com.aes.erp.organogram_system.dto.FSReturnObject;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import com.aes.erp.organogram_system.user_assignment_system.UserAssignmentOps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganogramSystemOpsRole {

    private final BaseHelper baseHelper;
    private final RoleNodeRepository roleNodeRepository;
    private final UserAssignmentOps userAssignmentOps;

    public void addChildNode(RoleNode currRoleNode, String name, Department parentDepartment) {
        if (name == null || name.isEmpty() || name.equals(".") || name.equals("..")) {
//            return new FSReturnObject().setReturnObject(false, "Role Creation failed: " + name, null);
            throw new AesException( "Role Creation failed: " + name);
        } else {
            RoleNode tmp;
            if (Objects.nonNull(parentDepartment)) {
                tmp = new RoleNode(name, currRoleNode, parentDepartment);
            } else tmp = new RoleNode(name, currRoleNode);

            List<RoleNode> childNodes = roleNodeRepository.findByParentId(currRoleNode.getId());
            currRoleNode.addChild(childNodes, tmp);
            roleNodeRepository.save(tmp);
//            return new FSReturnObject().setReturnObject(true, "Role Created: " + name, tmp);
        }
    }

    //Remove Department by ID**
    public void removeChildRole(RoleNode roleNode, String arg) {
        int index = baseHelper.convertStringToInt(arg);
        List<RoleNode> childRoleNodes = roleNodeRepository.findByParentId(roleNode.getId());
        if (index > -1 && index < childRoleNodes.size()) {
            RoleNode temp = childRoleNodes.get(index);
            if (temp != null) {
                // delete assigned user
                userAssignmentOps.deleteAssignedUserByRoleNode(temp);

//                if (!fsReturnObject.isSuccess()) return baseHelper.getFailedReturnObject("Role Node removal failed: Assigned user can not be deleted");

                temp.setParent(null);
                childRoleNodes.remove(index);
                roleNodeRepository.save(temp);
                roleNodeRepository.delete(temp);
            }
//            return new FSReturnObject().setReturnObject(true, "Role removed at index: " + index, temp);
        } else {
            throw new AesException("Role removal failed at index: " + index);
//            return baseHelper.getFailedReturnObject();
        }
    }

    //Edit Department name by ID**
    public void updateRole(RoleNode currRoleNode, String arg) {
        if (baseHelper.isNameInvalid(arg)){
            throw new AesException("Invalid Name");
//            return baseHelper.getFailedReturnObject("Invalid Name");
        }
        List<String> args = baseHelper.getUpdateArgs(arg);
        int index = baseHelper.convertStringToInt(args.get(0));
        baseHelper.isNameValid(args.get(1));
//        if(!(fsReturnObject == null)){
//            return fsReturnObject;
//        }
        List<RoleNode> childRoleNodes = roleNodeRepository.findByParentId(currRoleNode.getId());
        if (index > -1 && index < childRoleNodes.size() && args.size() > 1) {
            RoleNode roleNode = roleNodeRepository.findById(childRoleNodes.get(index).getId()).orElse(null);
            childRoleNodes.get(index).setName(args.get(1));
            if (Objects.nonNull(roleNode)) {
                roleNode.setName(args.get(1));
                roleNodeRepository.save(roleNode);
            }
//            return new FSReturnObject().setReturnObject(true, "Role updated: " + args.get(1), roleNode);
        } else {
            throw new AesException("Role update failed, INVALID INDEX: " + args.get(1));
//            return baseHelper.getFailedReturnObject("Role update failed, INVALID INDEX: " + args.get(1));
        }
    }

    public FSReturnObject readRoleNode(long roleId) {
        RoleNode roleNode = roleNodeRepository.findById(roleId).orElse(null);
        if(roleNode == null) return new FSReturnObject().setReturnObject(false,"roleNode doesn't exist", null);
        return new FSReturnObject().setReturnObject(true, "roleNode fetch ok", roleNode);
    }


    public RoleNode getChild(RoleNode currNode, String arg) {
        List<RoleNode> childRoleNodes = roleNodeRepository.findByParentId(currNode.getId());
        for (RoleNode rn : childRoleNodes) {
            if (rn.getName().equals(arg)) return rn;
        }
        return null;
    }

    public RoleNode traverse(String arg, RoleNode currRoleNode) {
        // .. go back one department
        if (arg.equals("..")) {
            return currRoleNode.getParent() == null
                    ? currRoleNode
                    : currRoleNode.getParent();
        }
        // deliver the child node of name arg
        else return getChild(currRoleNode, arg);
    }
}
