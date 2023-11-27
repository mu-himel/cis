package com.aes.erp.organogram_system;

import com.aes.erp.helper.BaseHelper;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.repository.DepartmentRepository;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganogramHelper extends BaseHelper {

    private final DepartmentRepository departmentRepository;
    private final RoleNodeRepository roleNodeRepository;

    /*public Department getRootOfDepartment(Department department) {
        if (department==null) return null;
        while (department.getParentDepartment() != null) {
            department = department.getParentDepartment();
        }
        return department;
    }*/


    /*public String printCurrentDepartment(Department currDepartment, RoleNode currRoleNode) {
        List<Department> childDepartments = departmentRepository.findAllByParentDepartmentId(currDepartment.getId());
        List<RoleNode> childRoles = roleNodeRepository.findByParentId(currRoleNode.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("[Current Department Cursor]: " + currDepartment.getName() + ":--> \n Sub-Departments:");
        System.out.println("[Current Department Cursor]: " + currDepartment.getName() + ":--> \n Sub-Departments:");
        for(int i = 0; i < childDepartments.size(); i++){
            sb.append("["+ i +"]: " + childDepartments.get(i).getName() + "  |  ");
            System.out.print("["+ i +"]: " + childDepartments.get(i).getName() + "  |  ");
        }
        sb.append("\n");
        System.out.println();
        System.out.println("[[Current Root Cursor]]: " + currDepartment.getName() +"/"+ currRoleNode.getName() + ":--> \n Roles:");//want to print out entire node hierarchy here
        sb.append("[[Current Root Cursor]]: " + currDepartment.getName() +"/"+ currRoleNode.getName() + ":--> \n Roles:");
        for(int i = 0; i < childRoles.size(); i++){
            System.out.print("["+ i +"]: " + childRoles.get(i).getName() + "  |  ");
            sb.append("["+ i +"]: " + childRoles.get(i).getName() + "  |  ");
        }
        sb.append("\n");
        System.out.println();
        return sb.toString();
    }*/

    //recursively go through config starting from rootDepartment and build StringBuilder--> Calls function in DepartmentTraversal
    private void recursiveStringBuilderDepartment(Department rootDepartment, StringBuilder sb, String padding, String pointer, int index) {
//        String boldStart = Objects.equals(ConsoleCurrentState.currentDepartment.getId(), rootDepartment.getId())
//                ? ConstantsConsole.SET_BOLD_TEXT + ConstantsConsole.SET_ITALIC_TEXT : "";
//        String boldEnd = Objects.equals(ConsoleCurrentState.currentDepartment.getId(), rootDepartment.getId())
//                ? ConstantsConsole.SET_PLAIN_TEXT : "";

        sb.append(padding);
        sb.append(pointer);
//        sb.append(
//                "[" + index + "] "
//                        + boldStart
//                        + "[id-" +rootDepartment.getId()+ "] "
//                        + rootDepartment.getName()
//                        + boldEnd
//        );
        sb.append("\n");

        StringBuilder paddingBuilder = new StringBuilder(padding);
        paddingBuilder.append("│  ");
        String paddingForAll = paddingBuilder.toString();

        String pointerForAll = "├──";
        String pointerForLast = "└──";

        List<Department> childDepartments = departmentRepository.findAllByParentDepartmentId(rootDepartment.getId());
        for (int i = 0; i < childDepartments.size(); i++) {
            if (i == (childDepartments.size() - 1)) {// if its the lastOne
                recursiveStringBuilderDepartment(childDepartments.get(i), sb, paddingForAll, pointerForLast, i);
            } else {
                recursiveStringBuilderDepartment(childDepartments.get(i), sb, paddingForAll, pointerForAll, i);
            }
        }
    }

    //recursively go through config starting from rootRoleNode and build StringBuilder--> Calls function in RoleNodeTraversal
//    private void recursiveStringBuilderRoleNode(RoleNode rootRoleNode, StringBuilder sb, String padding, String pointer, int index) {
//        String boldStart = Objects.equals(ConsoleCurrentState.currentRoleNode.getId(), rootRoleNode.getId())
//                ? ConstantsConsole.SET_BOLD_TEXT + ConstantsConsole.SET_ITALIC_TEXT : "";
//        String boldEnd = Objects.equals(ConsoleCurrentState.currentRoleNode.getId(), rootRoleNode.getId())
//                ? ConstantsConsole.SET_PLAIN_TEXT : "";
//        sb.append(padding);
//        sb.append(pointer);
//        sb.append("[" + index + "] "
//                + boldStart
//                + rootRoleNode.getName()
//                + boldEnd
//        );
//        sb.append("\n");
//
//        StringBuilder paddingBuilder = new StringBuilder(padding);
//        paddingBuilder.append("│  ");
//        String paddingForAll = paddingBuilder.toString();
//
//        String pointerForAll = "├──";
//        String pointerForLast = "└──";
//
//        List<RoleNode> childNodes = roleNodeRepository.findByParentId(rootRoleNode.getId());
//        for (int i = 0; i < childNodes.size(); i++) {
//            if (i == (childNodes.size() - 1)) {// if its the lastOne
//                recursiveStringBuilderRoleNode(childNodes.get(i), sb, paddingForAll, pointerForLast, i);
//            } else {
//                recursiveStringBuilderRoleNode(childNodes.get(i), sb, paddingForAll, pointerForAll, i);
//            }
//        }
//    }

//    public Department  printGraph(Department rootDepartment, Department currDepartment, RoleNode currRoleNode) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Current Department: " + currDepartment.getName() + "\n");
//
//        recursiveStringBuilderDepartment(rootDepartment, sb, "", "", 0);
//        sb.append("Current Role Node: " + currRoleNode.getName() + "\n");
//        recursiveStringBuilderRoleNode(currDepartment.getRootRoleNode(), sb, "", "", 0);
//        System.out.println("\n\n"+sb);
//        return rootDepartment;
//    }
}
