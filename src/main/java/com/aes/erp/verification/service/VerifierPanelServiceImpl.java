package com.aes.erp.verification.service;

import com.aes.erp.authentication.dto.EmployeeInfoDto;
import com.aes.erp.verification.dto.response.Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class VerifierPanelServiceImpl implements VerifierPanelService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Verifier> getVerifierPanel(EmployeeInfoDto employeeInfo, Integer fromLevel, Integer toLevel) {
        String sql ="select id,name,verified,departmentId, departmentName, departmentLevel," +
                "designationId, designation  FROM (\n";
        if(employeeInfo.getReportingManagerId()!=null) {
            sql += "SELECT e.id, e.name, false as verified, d1.id as departmentId, " +
                    "d1.name as departmentName, d1.`level` as departmentLevel," +
                    "rn.name as designation,rn.id as designationId " +
                    "FROM employees e " +
                    "LEFT JOIN department d1 on d1.id=e.department_id " +
                    "LEFT JOIN role_node rn on rn.id=e.role_node_id " +
                    " WHERE e.id = " + employeeInfo.getReportingManagerId() + "\n" +
                    "UNION \n";
        }
        if(employeeInfo.getReportingManagerId()==null && fromLevel>toLevel){
            fromLevel = fromLevel-1;
        }
        sql += "SELECT e2.id, e2.name , false as verified, " +
                "d2.id as departmentId, d2.name as departmentName, d2.`level` as departmentLevel," +
                "rn1.name as designation,rn1.id as designationId " +
                "FROM employees e2 " +
                "LEFT JOIN department d2 on d2.id=e2.department_id " +
                "LEFT JOIN role_node rn1 on rn1.id=e2.role_node_id " +
                "where e2.user_id  in\n" +
                "(SELECT user_id  FROM user_assignment ua WHERE ua.department_id in (" +
                "SELECT id FROM department d  WHERE d.`level` BETWEEN "+toLevel+" AND "+fromLevel+")\n" +
                "AND parent_user_assignment_id IS NULL)";

                if(employeeInfo.getParentDepartmentId()!=null) {
                    sql +="And d2.id <= " + employeeInfo.getParentDepartmentId();
                }

        sql += ") as verifiers ";
                if(employeeInfo.getReportingManagerId()==null && (fromLevel==toLevel)){
                    sql+=" WHERE departmentId="+ employeeInfo.getParentDepartmentId();
                }
                if(employeeInfo.getReportingManagerId()==null && (toLevel<fromLevel)){
                    sql+="WHERE (departmentLevel = 2 AND departmentId="+employeeInfo.getParentDepartmentId()+") OR departmentLevel<2";
                }
        sql+= " ORDER BY departmentLevel desc";
        return  entityManager.createNativeQuery(sql, "Verifier")
                .getResultList();
    }


}
