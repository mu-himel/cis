package com.aes.erp.scm.repositories;

public interface TenderQuery {
    String tenderProjectionQuery = """
                SELECT 
                t.rfqNo as tenderNo,
                t.id as id, 
                t.tenderStatus as tenderStatus, 
                t.tenderType as tenderType, 
                CONCAT(t.itemCategory.parentCategory.name, '-',t.itemCategory.name) as itemCategory, 
                t.tenderCreator.name as tenderCreator, 
                t.creationDate as creationDate, 
                t.deadline as deadline,
                COUNT(ti) as tenderItemCount 
        FROM Tender t LEFT JOIN t.tenderItems ti  
        WHERE (:searchFilter IS NULL OR LOWER(t.itemCategory.name) 
                LIKE %:searchFilter% OR LOWER(t.tenderCreator.name) LIKE %:searchFilter%) 
        AND (:tenderType IS NULL OR t.tenderType = :tenderType) 
        AND (:startDate IS NULL OR t.creationDate >= :startDate) 
        AND (:endDate IS NULL OR t.creationDate <= :endDate) 
        AND t.itemCategory.id IN :subCategoryIds
        AND t.deadline > :currentDateTime
        GROUP BY t.id, t.tenderStatus, t.tenderType, t.itemCategory, t.tenderCreator, t.creationDate""";
                           
                          
    String tenderProjectionCountQuery = "SELECT COUNT(DISTINCT t.id) " +
            "                    FROM Tender t LEFT JOIN t.tenderItems ti " +
            "                    WHERE (:searchFilter IS NULL OR LOWER(t.itemCategory.name) LIKE %:searchFilter% OR LOWER(t.tenderCreator.name) LIKE %:searchFilter%) " +
            "                    AND (:tenderType IS NULL OR t.tenderType = :tenderType) " +
            "                    AND (:startDate IS NULL OR t.creationDate >= :startDate) " +
            "                    AND (:endDate IS NULL OR t.creationDate <= :endDate)"+
            " AND t.itemCategory.id IN :subCategoryIds";

}
