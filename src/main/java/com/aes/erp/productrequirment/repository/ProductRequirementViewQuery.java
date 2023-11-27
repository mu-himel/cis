package com.aes.erp.productrequirment.repository;

public interface ProductRequirementViewQuery {
    String getProductRequirementViewWithSearch =
            """
                    SELECT c.id                                                                   as categoryId,
                           c.name                                                                 as categoryName,
                           sc.id                                                                  as subCategoryId,
                           sc.name                                                                as subCategoryName,
                           i.id                                                                   as itemId,
                           i.name                                                                 as itemName,
                           sum(dd.request_quantity)                                               as prQty,

                           (SELECT COUNT(distinct ddsq1.item_id)
                            from demand_details ddsq1
                            where ddsq1.id in (select id
                                               from demand_details ddsq2
                                               where ddsq2.item_parent_category_id = p.category_id
                                                 and ddsq2.item_category_id = p.sub_category_id)) as itemsQty,
                           COALESCE((SELECT SUM(istock.stock_qty)
                                     FROM item_stocks istock
                                     where istock.item_id = dd.item_id), 0)                       as currentStock,
                           COALESCE((SELECT item.stock_threshold_qty
                                     FROM items item
                                     where item.id = dd.item_id), 0)                              as safetytStock,
                           0                                                                      as transitQty,
                           (SELECT CASE
                                       WHEN pty = 'URGENT' THEN DATEDIFF(DATE_ADD(MIN(demand_date_f), INTERVAL 7 DAY),
                                                                         CURRENT_DATE)
                                       WHEN pty = 'MEDIUM' THEN DATEDIFF(DATE_ADD(MIN(demand_date_f), INTERVAL 14 DAY),
                                                                         CURRENT_DATE)
                                       WHEN pty = 'REGULAR' THEN DATEDIFF(DATE_ADD(MIN(demand_date_f), INTERVAL 20 DAY),
                                                                          CURRENT_DATE)
                                       END
                            FROM (SELECT distinct CASE
                                                      WHEN ddsq.priority = 'URGENT' THEN 'URGENT'
                                                      WHEN ddsq.priority = 'MEDIUM' THEN 'MEDIUM'
                                                      WHEN ddsq.priority = 'REGULAR' THEN 'REGULAR'
                                                      END         as pty,
                                                  dms.demand_date as demand_date_f
                                  FROM demand_details ddsq
                                           left join demands dms on ddsq.demand_id = dms.id

                                  where ddsq.item_parent_category_id = p.category_id
                                    and ddsq.item_category_id = p.sub_category_id
                                  ORDER BY dms.demand_date,
                                           CASE
                                               WHEN ddsq.priority = 'URGENT' THEN 1
                                               WHEN ddsq.priority = 'MEDIUM' THEN 2
                                               WHEN ddsq.priority = 'REGULAR' THEN 3
                                               END
                                  LIMIT 1) as ptys)                                               as daysRemain,


                           (SELECT distinct CASE
                                                WHEN ddsq.priority = 'URGENT' THEN 'URGENT'
                                                WHEN ddsq.priority = 'MEDIUM' THEN 'MEDIUM'
                                                WHEN ddsq.priority = 'REGULAR' THEN 'REGULAR'
                                                END as demand_date
                            FROM demand_details ddsq

                            where ddsq.item_parent_category_id = p.category_id
                              and ddsq.item_category_id = p.sub_category_id
                            ORDER BY CASE
                                         WHEN ddsq.priority = 'URGENT' THEN 1
                                         WHEN ddsq.priority = 'MEDIUM' THEN 2
                                         WHEN ddsq.priority = 'REGULAR' THEN 3
                                         END
                            LIMIT 1)                                                              as priority

                    FROM product_requirements as p
                             LEFT JOIN item_categories c on p.category_id = c.id
                             LEFT JOIN item_categories sc on p.sub_category_id = sc.id
                             LEFT JOIN demands d on p.demand_id = d.id
                             LEFT JOIN demand_details dd on p.demand_detail_id = dd.id
                             LEFT JOIN items i on dd.item_id = i.id


                    WHERE (:categoryId IS NULL OR c.id = :categoryId)
                      AND (:subCategoryId IS NULL OR sc.id = :subCategoryId)
                      AND p.status = 'OPEN'
                    GROUP BY p.category_id, p.sub_category_id, dd.item_id
                                                                                                    """;


}
