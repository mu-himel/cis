package com.aes.erp.scm.repositories;

public interface TenderQuery {
    String tenderProjectionQuery = """
                SELECT 
                t.rfq_no as tenderNo,
                t.id as id, 
                CASE WHEN tp.id IS NOT NULL THEN
                        (SELECT status from tender_participators tp2 WHERE 
                        tp2.id in (select max(id) from tender_participators tp3 
                        WHERE tp3.tender_id=t.id 
                                AND tp3.vendor_id = :vendorId))
                ELSE
                        t.tender_status
                END as tenderStatus, 
                t.tender_type as tenderType, 
                CONCAT(ipc.name, '-',ic.name) as itemCategory, 
                tc.name as tenderCreator, 
                t.creation_date as creationDate, 
                t.deadline as deadline,
                COUNT(ti.id) as tenderItemCount 
        FROM tenders t 
        LEFT JOIN tender_participators tp ON tp.tender_id = t.id AND tp.vendor_id = :vendorId
        LEFT JOIN tender_items ti ON ti.tender_id = t.id 
        LEFT JOIN item_categories ic ON ic.id = t.item_category_id
        LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id
        LEFT JOIN organizations tc ON tc.id = t.organization_id 
        WHERE (:searchFilter IS NULL OR LOWER(ic.name) 
                LIKE %:searchFilter% OR LOWER(tc.name) LIKE %:searchFilter%) 
        AND (:tenderType IS NULL OR t.tender_type = :tenderType) 
        AND (:startDate IS NULL OR t.creation_date >= :startDate) 
        AND (:endDate IS NULL OR t.creation_date <= :endDate) 
        AND ic.id IN :subCategoryIds
        AND t.deadline > :currentDateTime
        GROUP BY t.id, t.tender_status, t.tender_type, ic.id, tc.id, t.creation_date""";


        String tenderProjectionWithFilterQuery = """
                SELECT 
                t.rfq_no as tenderNo,
                t.id as id, 
                CASE WHEN tp.id IS NOT NULL THEN
                        (SELECT status from tender_participators tp2 WHERE 
                        tp2.id in (select max(id) from tender_participators tp3 
                        WHERE tp3.tender_id=t.id 
                                AND tp3.vendor_id = :vendorId))
                ELSE
                        t.tender_status
                END as tenderStatus, 
                t.tender_type as tenderType, 
                CONCAT(ipc.name, '-',ic.name) as itemCategory, 
                tc.name as tenderCreator, 
                t.creation_date as creationDate, 
                t.deadline as deadline,
                COUNT(ti.id) as tenderItemCount 
        FROM tenders t 
        LEFT JOIN tender_participators tp ON tp.tender_id = t.id AND tp.vendor_id = :vendorId
        LEFT JOIN tender_items ti ON ti.tender_id = t.id 
        LEFT JOIN item_categories ic ON ic.id = t.item_category_id
        LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id
        LEFT JOIN organizations tc ON tc.id = t.organization_id 
        WHERE (:searchFilter IS NULL OR LOWER(ic.name) 
                LIKE %:searchFilter% OR LOWER(tc.name) LIKE %:searchFilter%)
        AND (:categoryId IS NULL OR ic.id = :categoryId)
        AND (:organizationId IS NULL OR t.organization_id = :organizationId)
        AND (:tenderType IS NULL OR t.tender_type = :tenderType) 
        AND (:startDate IS NULL OR t.creation_date >= :startDate) 
        AND (:endDate IS NULL OR t.creation_date <= :endDate) 
        AND (COALESCE(:subCategoryIds) IS NULL OR ic.id IN (:subCategoryIds))
        AND (t.deadline > :currentDateTime)
        GROUP BY t.id, t.tender_status, t.tender_type, ic.id, tc.id, t.creation_date""";

        String closedTenderProjectionQuery = """
                SELECT 
                t.rfq_no as tenderNo,
                t.id as id, 
                CASE WHEN tp.id IS NOT NULL THEN
                        (SELECT 
                        CASE WHEN (tp2.status = 'TENDER_SENT' AND t.deadline < :currentDateTime) THEN 'LOST' 
                        ELSE tp2.status
                        END as status
                        FROM tender_participators tp2 WHERE 
                        tp2.id in (select max(id) from tender_participators tp3 where tp3.tender_id=t.id AND tp3.vendor_id = :vendorId))
                ELSE
                        t.tender_status
                END as tenderStatus, 
                t.tender_type as tenderType, 
                CONCAT(ipc.name, '-',ic.name) as itemCategory, 
                tc.name as tenderCreator, 
                t.creation_date as creationDate, 
                t.deadline as deadline,
                COUNT(ti.id) as tenderItemCount 
        FROM tenders t 
        LEFT JOIN tender_participators tp ON tp.tender_id = t.id AND tp.vendor_id = :vendorId
        LEFT JOIN tender_items ti ON ti.tender_id = t.id 
        LEFT JOIN item_categories ic ON ic.id = t.item_category_id
        LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id
        LEFT JOIN organizations tc ON tc.id = t.organization_id 
        WHERE (:searchFilter IS NULL OR LOWER(ic.name) 
                LIKE %:searchFilter% OR LOWER(tc.name) LIKE %:searchFilter%) 
        AND (:categoryId IS NULL OR ic.id = :categoryId)
        AND (:organizationId IS NULL OR t.organization_id = :organizationId)
        AND (:tenderType IS NULL OR t.tender_type = :tenderType) 
        AND (:startDate IS NULL OR t.creation_date >= :startDate) 
        AND (:endDate IS NULL OR t.creation_date <= :endDate)  
        AND (COALESCE(:subCategoryIds) IS NULL OR ic.id IN (:subCategoryIds))
                AND (t.deadline < :currentDateTime)
                GROUP BY t.id, t.tender_status, t.tender_type, ic.id, tc.id, t.creation_date""";

    String tenderProjectionCountQuery = " SELECT count(*) FROM (" + tenderProjectionQuery + " ) ";

    String tenderProjectionCountQueryFilterQuery = " SELECT count(*) FROM (" + tenderProjectionWithFilterQuery + " ) ";


    String closedTenderProjectionCountQuery = " SELECT count(*) FROM (" + closedTenderProjectionQuery + " ) ";

    String getLowestBidByTenderNo = """
            SELECT 
                p.rfq_no as rfqNo,
                p.brand_name as brandName,
                p.product_description as itemAttributeName,
                p.extended_attributes as extendedAttributes,
                p.total as total
            FROM (SELECT rfq_no, oi.brand_name , oi.product_description , oi.extended_attributes, COALESCE(pq.total_price,0) as total FROM tenders t
            LEFT JOIN tender_items ti ON ti.tender_id  = t.id
            LEFT JOIN offers o ON o.tender_id  = t.id
            LEFT JOIN offer_items oi ON oi.offer_id  = o.id
            LEFT JOIN price_quotations pq ON oi.price_quotation_id = pq.id
            where t.rfq_no = :tenderNo
            GROUP BY t.rfq_no,oi.brand_name , oi.product_description , oi.extended_attributes
            ) p
            WHERE  p.total>0
            ORDER BY total ASC 
            """;
}
