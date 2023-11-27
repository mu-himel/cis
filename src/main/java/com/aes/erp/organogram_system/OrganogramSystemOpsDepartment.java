package com.aes.erp.organogram_system;

import com.aes.erp.exception.AesException;
import com.aes.erp.helper.BaseHelper;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.repository.DepartmentRepository;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import com.aes.erp.organogram_system.user_assignment_system.UserAssignmentOps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganogramSystemOpsDepartment {

    private final BaseHelper baseHelper;
    private final DepartmentRepository departmentRepository;
    private final RoleNodeRepository roleNodeRepository;
    private final UserAssignmentOps userAssignmentService;

    public void addChildDepartment(Department department, String name) {
        if (baseHelper.isNameInvalid(name))
            throw new AesException("Department creation failed: " + name);
//            return baseHelper.getFailedReturnObject("Department creation failed: " + name);

//        baseHelper.isNameValid(name);
//        if(!(fsReturnObject == null)){
//            return fsReturnObject;
//            throw new AesException("Department creation failed: " + name);
//        }

        List<Department> childDepartments =  departmentRepository.findAllByParentDepartmentId(department.getId());
        for (Department childDepartment : childDepartments) {
            if (childDepartment.getName().equalsIgnoreCase(name)) {
//                return baseHelper.getFailedReturnObject("Department with name exists: " + name);
                throw new AesException("Department creation failed: " + name);
            }
        }

        departmentRepository.save(department.addChildDepartment(childDepartments, name));
//        return new FSReturnObject().setReturnObject(
//                true,
//                "Department created: " + name,
//                childDepartments.stream().filter(dept -> name.equals(dept.getName())).findFirst()
//                // department.getChildDepartments().stream().filter(dept -> name.equals(dept.getName())).findFirst()
//        );
    }

    public void updateDepartment(Department currDepartment, String arg) {
        if (baseHelper.isNameInvalid(arg)){
            throw new AesException("Invalid Name: " + arg);
//            return baseHelper.getFailedReturnObject("Invalid Name: " + arg);
        }
        List<String> args = baseHelper.getUpdateArgs(arg);
        int index = baseHelper.convertStringToInt(args.get(0));
        baseHelper.isNameValid(args.get(1));
//        if(!(fsReturnObject == null)){
//            return fsReturnObject;
//        }
        List<Department> childDepartments = departmentRepository.findAllByParentDepartmentId(currDepartment.getId());
        if (index > -1 && index < childDepartments.size() && args.size() > 1) {
            Department temp = departmentRepository.findById(childDepartments.get(index).getId()).orElse(null);
            childDepartments.get(index).setName(args.get(1));
            if (Objects.nonNull(temp)) {
                temp.setName(args.get(1));
                departmentRepository.save(temp);
            }
//            return new FSReturnObject().setReturnObject(
//                    true,
//                    "Department updated " + index,
//                    temp
//            );
        } else {
            throw new AesException("Invalid or unavailable index: " + index);
//            return new FSReturnObject().setReturnObject(
//                    false,
//                    "Invalid or unavailable index: " + index,
//                    null
//            );
        }
    }

    public void removeChildDepartment(Department department, String name) {
        if (baseHelper.isNameInvalid(name)){
            throw new AesException("Invalid name: "+ name);
//            return baseHelper.getFailedReturnObject("Invalid name: " + arg);
        }

        int index = baseHelper.convertStringToInt(name);
        List<Department> childDepartments = departmentRepository.findAllByParentDepartmentId(department.getId());
        if (index > -1 && index < childDepartments.size()) {
            // delete assigned user
            Department temp = childDepartments.get(index);
            userAssignmentService.deleteAssignedUserByDepartment(temp);
//            if (!fsReturnObject.isSuccess()) return baseHelper.getFailedReturnObject("Department removal failed: Assigned user can not be deleted");

            childDepartments.remove(index);
            temp.setParentDepartment(null);
            departmentRepository.save(temp);
            departmentRepository.delete(temp);
//            return new FSReturnObject().setReturnObject(
//                    true,
//                    "Department removed: " + index + " - " + department.getName(),
//                    temp
//            );
        } else {
            throw new AesException("Department removal failed: " + index + " - " + department.getName());
//            return baseHelper.getFailedReturnObject("Department removal failed: " + index + " - " + department.getName());
        }
    }

    public Department getChild(Department currDepartment, String childName) {
        List<Department> childDepartments = departmentRepository.findAllByParentDepartmentId(currDepartment.getId());
        for (Department d : childDepartments) {
            if (d.getName().equals(childName)) {
                return d;
            }
        }
        return null;
    }

    public Department traverse(String arg, Department currDepartment) {
        Department rootDepartment = this.getRootOfDepartment(currDepartment);
        if (arg.equals("..")) {//go back one department
            if (!currDepartment.equals(rootDepartment)) {
                return currDepartment.getParentDepartment();
            } else {
                return rootDepartment;
            }
        } else if(arg.equals(".")) {// go back to root department
            return rootDepartment;
        } else {
            Department tmp = getChild(currDepartment, arg);
            if (Objects.nonNull(tmp)) {
                return tmp;
            } else {
                return null;
            }
        }
    }

    public Department getRootOfDepartment(Department department) {
        if (department == null) return null;
        while (department.getParentDepartment() != null) {
            department = department.getParentDepartment();
        }
        return department;
    }

    public String printCurrentDepartment(Department currDepartment, RoleNode currRoleNode) {
        List<Department> childDepartments = departmentRepository.findAllByParentDepartmentId(currDepartment.getId());
        List<RoleNode> childRoles = roleNodeRepository.findByParentId(currRoleNode.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("[Current Department Cursor]: " + currDepartment.getName() + ":--> \n Sub-Departments:");
        System.out.println("[Current Department Cursor]: " + currDepartment.getName() + ":--> \n Sub-Departments:");
        for (int i = 0; i < childDepartments.size(); i++) {
            sb.append("["+ i +"]: " + childDepartments.get(i).getName() + "  |  ");
            System.out.print("["+ i +"]: " + childDepartments.get(i).getName() + "  |  ");
        }
        sb.append("\n");

        System.out.println();
        System.out.println("[[Current Root Cursor]]: " + currDepartment.getName() +"/"+ currRoleNode.getName() + ":--> \n Roles:");//want to print out entire node hierarchy here
        sb.append("[[Current Root Cursor]]: " + currDepartment.getName() +"/"+ currRoleNode.getName() + ":--> \n Roles:");
        for (int i = 0; i < childRoles.size(); i++) {
            System.out.print("["+ i +"]: " + childRoles.get(i).getName() + "  |  ");
            sb.append("["+ i +"]: " + childRoles.get(i).getName() + "  |  ");
        }
        sb.append("\n");
        System.out.println();
        return sb.toString();
    }
}
