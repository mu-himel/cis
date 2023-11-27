package com.aes.erp.productrequirment.repository;

public interface ProductRequirementQuery extends ProductRequirementViewQuery {
    String getProductRequirementWithSearch =
            """
                    
                    SELECT c.id                                               AS categoryId,
                           c.name                                             AS categoryName,
                           sc.id                                              AS subCategoryId,
                           sc.name                                            AS subCategoryName,
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
                                  LIMIT 1) as ptys)                           as daysRemain,

                           (SELECT COUNT(distinct ddsq1.item_id)
                            FROM demand_details ddsq1
                            where ddsq1.item_parent_category_id = p.category_id
                              and ddsq1.item_category_id = p.sub_category_id) AS itemsQty

                    FROM product_requirements AS p
                             LEFT JOIN item_categories c ON p.category_id = c.id
                             LEFT JOIN item_categories sc ON p.sub_category_id = sc.id
                             LEFT JOIN demands d ON p.demand_id = d.id
                             LEFT JOIN demand_details dd ON p.demand_detail_id = dd.id

                    WHERE (:categoryId IS NULL OR c.id = :categoryId)
                      AND (:subCategoryId IS NULL OR sc.id = :subCategoryId)
                      AND (:startDate IS NULL OR :endDate IS NULL OR p.product_requirement_date BETWEEN :startDate AND :endDate)
                      AND p.status = 'OPEN'
                    GROUP BY c.id, sc.id
                    ORDER BY c.id, sc.id
                                        """;


    String countProductRequirementWithSearch =
            """
                    select count(*)
                    from ( 
                    
                    """ +
                    getProductRequirementWithSearch
                    + """
                         
                         ) as prs
                    """;
}
