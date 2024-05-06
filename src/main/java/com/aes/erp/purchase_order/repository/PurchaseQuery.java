package com.aes.erp.purchase_order.repository;

public interface PurchaseQuery {
    String pendingPos = """
        SELECT 
            po.id as id,
            po.po_no as poNo, po.tender_no as tenderNo, po.po_date as poDate,
            o.name as orgName,
            (SELECT DISTINCT CONCAT(ipc.name,'-',ic.name) FROM tenders t 
            LEFT JOIN item_categories ic ON ic.id = t.item_category_id
            LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id
            WHERE t.rfq_no = po.tender_no) as productType,
            po.delivery_date as deliveryDate,
            COUNT(pod.id) as itemQty,
            po.po_status as poStatus
        FROM purchase_orders po 
        LEFT JOIN purchase_order_details pod ON pod.purchase_order_id = po.id
        LEFT JOIN organizations o ON o.id = po.org_id
        WHERE po.vendor_id = :vendorId  AND po.po_status IN ('PENDING','IN PROGRESS','DECLINED','QC_PASS','QC_FAILED','RECEIVED')
        GROUP BY po.id      
        """;

    String closedPos = """
            SELECT 
                po.id as id,
                po.po_no as poNo, po.tender_no as tenderNo, po.po_date as poDate,
                o.name as orgName,
                (SELECT CONCAT(ipc.name,'-',ic.name) FROM tenders t 
                LEFT JOIN item_categories ic ON ic.id = t.item_category_id
                LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id
                WHERE t.rfq_no = po.tender_no) as productType,
                po.delivery_date as deliveryDate,
                COUNT(pod.id) as itemQty,
                po.po_status as poStatus
            FROM purchase_orders po 
            LEFT JOIN purchase_order_details pod ON pod.purchase_order_id = po.id
            LEFT JOIN organizations o ON o.id = po.org_id
            WHERE po.vendor_id = :vendorId  AND po.po_status IN ('COMPLETED','REJECTED')
            GROUP BY po.id      
            """;

    String countPendingPos=" SELECT COUNT(*) FROM ("+pendingPos+") total";
    String countClosedPos=" SELECT COUNT(*) FROM ("+closedPos+") total";
}
