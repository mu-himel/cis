package com.aes.erp.purchase_order.repository;

public interface PurchaseQuery {
    String pendingPos = """
             SELECT
                po.id as id,
                po.po_no as poNo, po.tender_no as tenderNo, po.po_date as poDate,
                o.name as orgName,
                (SELECT CONCAT(ipc.name,'-',ic.name) FROM tenders t
                LEFT JOIN item_categories ic ON ic.id = t.item_category_id
                LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id
                WHERE (t.rfq_no = po.tender_no)
                ) as productType,
                po.delivery_date as deliveryDate,
                COUNT(pod.id) as itemQty,
                po.po_status as poStatus
            FROM purchase_orders po
            LEFT JOIN purchase_order_details pod ON pod.purchase_order_id = po.id
            LEFT JOIN organizations o ON o.id = po.org_id
            LEFT JOIN tenders t1 ON t1.rfq_no=po.tender_no
            LEFT JOIN item_categories ic1 on ic1.id = t1.item_category_id
            LEFT JOIN item_categories ipc1 on ipc1.id = ic1.parent_category_id
            WHERE po.vendor_id = :vendorId
            AND (:poNo IS NULL OR po.po_no LIKE CONCAT('%', :poNo, '%'))
            AND (:tenderNo IS NULL OR  po.tender_no LIKE CONCAT('%', :tenderNo, '%'))
            AND (:categoryId IS NULL OR ic1.parent_category_id = :categoryId)
            AND (:subCategoryId IS NULL OR ic1.id = :subCategoryId)
            AND (:status IS NULL OR po.po_status = UPPER(:status))
            AND (:organizationId IS NULL OR po.org_id = :organizationId)
            AND (:deliveryDate IS NULL OR po.delivery_date = :deliveryDate)
            AND (:fromDate IS NULL OR po.po_date BETWEEN :fromDate AND :toDate)
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
                WHERE (t.rfq_no = po.tender_no)
                ) as productType,
                po.delivery_date as deliveryDate,
                COUNT(pod.id) as itemQty,
                po.po_status as poStatus
            FROM purchase_orders po
            LEFT JOIN purchase_order_details pod ON pod.purchase_order_id = po.id
            LEFT JOIN organizations o ON o.id = po.org_id
            LEFT JOIN tenders t1 ON t1.rfq_no=po.tender_no
            LEFT JOIN item_categories ic1 on ic1.id = t1.item_category_id
            LEFT JOIN item_categories ipc1 on ipc1.id = ic1.parent_category_id
            WHERE po.vendor_id = :vendorId
            AND (:poNo IS NULL OR po.po_no LIKE CONCAT('%', :poNo, '%'))
            AND (:tenderNo IS NULL OR  po.tender_no LIKE CONCAT('%', :tenderNo, '%'))
            AND (:categoryId IS NULL OR ic1.parent_category_id = :categoryId)
            AND (:subCategoryId IS NULL OR ic1.id = :subCategoryId)
            AND (:organizationId IS NULL OR po.org_id = :organizationId)
            AND (:deliveryDate IS NULL OR po.delivery_date = :deliveryDate)
            AND (:fromDate IS NULL OR po.po_date BETWEEN :fromDate AND :toDate)
            AND po.po_status IN ('COMPLETED','REJECTED','DECLINED')
            GROUP BY po.id
            """;

    String countPendingPos=" SELECT COUNT(*) FROM ("+pendingPos+") total";
    String countClosedPos=" SELECT COUNT(*) FROM ("+closedPos+") total";
}
