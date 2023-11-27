package com.aes.erp.module_access.approval_setting.repository;

public interface ApprovalSettingQuery {

    String getApprovalSettingModuleWise = """
            SELECT e.id as id, e.name as name, e.department_id as departmentId,
            e.role_node_id as designationId, e.user_id as userId,d.level as departmentLevel, 
            d.name as departmentName, rn.name as designationName 
            FROM approval_setting_options aso 
            LEFT JOIN approval_settings as2  on as2.id = aso.approval_setting_id 
            LEFT JOIN modules m on m.id=as2.module_access_id 
            LEFT JOIN employees e ON e.role_node_id = as2.designation_id AND e.department_id = as2.department_id 
            LEFT JOIN department d on d.id=e.department_id 
            LEFT JOIN role_node rn on rn.id=e.role_node_id 
            WHERE as2.is_default = 0 AND m.uri = :uri  
            AND (:categoryId IS NULL OR aso.category_id IN (:categoryId)) 
            AND (:amount IS NULL OR aso.amount <= :amount) 
            GROUP BY as2.designation_id, aso.amount 
            ORDER by as2.approval_order asc
            """;

    interface ApprovalPanel{
        Long getId();
        String getName();
        Long getDepartmentId();
        Long getDesignationId();
        Long getUserId();
        String getDepartmentName();
        String getDesignationName();
        Integer getDepartmentLevel();
    }
}
