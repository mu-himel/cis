package com.aes.erp.indent.repository;

public interface PrIndentQuery {
    String getReadyIndentWithSearch =
            """
SELECT pri.id              as          id,
       pri.category_id     as          categoryId,
       c.name              as          categoryName,
       pri.sub_category_id as          subCategoryId,
       sc.name             as          subCategoryName,
       COALESCE(SUM(prid.order_qty), 0) orderQty,
       pri.priority        as          priority


FROM pr_indents pri
         LEFT JOIN pr_indent_details prid on pri.id = prid.pr_indent_id
         LEFT JOIN item_categories c on pri.category_id = c.id
         LEFT JOIN item_categories sc on pri.sub_category_id = sc.id
         LEFT JOIN items it on prid.item_id = it.id


WHERE pri.status = 'OPEN'
  AND (:categoryId IS NULL OR c.id = :categoryId)
  AND (:subCategoryId IS NULL OR sc.id = :subCategoryId)
  AND (:priority IS NULL OR pri.priority = :priority)

GROUP BY pri.id
                                                            """;

    String countReadyPrIndentWithSearch =
            """
                    select count(*)
                    from (
                    """ +
                    getReadyIndentWithSearch
                    + """
                        ) as temp
                    """;

    String getPrIndentByIdWithSearch =
            """
SELECT pri.id                                             as id,
       prid.id                                            as prDetailId,
       pri.category_id                                    as categoryId,
       c.name                                             as categoryName,
       pri.sub_category_id                                as subCategoryId,
       sc.name                                            as subCategoryName,
       it.id                                              as itemId,
       it.name                                            as itemName,
       it.name                                            as itemDescription,
       COALESCE(SUM(prid.order_qty), 0)                   as orderQty,
       COALESCE(SUM(prid.pr_qty), 0)                      as prQty,
       pri.priority                                       as priority,
       COALESCE((SELECT SUM(istock.stock_qty)
                 FROM item_stocks istock
                 where istock.item_id = prid.item_id), 0) as currentStock,
       COALESCE((SELECT item.stock_threshold_qty
                 FROM items item
                 where item.id = prid.item_id), 0)        as safetytStock,
       0                                                  as transitQty,
       (CASE
            WHEN pri.priority = 'URGENT' THEN DATEDIFF(DATE_ADD(pri.priority_date, INTERVAL 7 DAY), CURRENT_DATE)
            WHEN pri.priority = 'MEDIUM' THEN DATEDIFF(DATE_ADD(pri.priority_date, INTERVAL 14 DAY), CURRENT_DATE)
            WHEN pri.priority = 'REGULAR' THEN DATEDIFF(DATE_ADD(pri.priority_date, INTERVAL 20 DAY), CURRENT_DATE)
           END)                                           as daysRemain


FROM pr_indents pri
         LEFT JOIN pr_indent_details prid on pri.id = prid.pr_indent_id
         LEFT JOIN item_categories c on pri.category_id = c.id
         LEFT JOIN item_categories sc on pri.sub_category_id = sc.id
         LEFT JOIN items it on prid.item_id = it.id


WHERE pri.status = 'OPEN'
  AND (:id IS NOT NULL AND pri.id = :id)
group by CASE
             WHEN it.id is not null then it.id
             END
                                                                          """;

String getPrIndentByIdsWithSearch =
            """
SELECT pri.id                                             as id,

       pri.category_id                                    as categoryId,
       c.name                                             as categoryName,
       pri.sub_category_id                                as subCategoryId,
       sc.name                                            as subCategoryName,
       it.id                                              as itemId,
       it.name                                            as itemName,
       it.name                                            as itemDescription,
       COALESCE(SUM(prid.order_qty), 0)                   as orderQty,
       COALESCE(SUM(prid.pr_qty), 0)                      as prQty,
       pri.priority                                       as priority,
       COALESCE((SELECT SUM(istock.stock_qty)
                 FROM item_stocks istock
                 where istock.item_id = prid.item_id), 0) as currentStock,
       COALESCE((SELECT item.stock_threshold_qty
                 FROM items item
                 where item.id = prid.item_id), 0)        as safetytStock,
       0                                                  as transitQty,
       (CASE
            WHEN pri.priority = 'URGENT' THEN DATEDIFF(DATE_ADD(pri.priority_date, INTERVAL 7 DAY), CURRENT_DATE)
            WHEN pri.priority = 'MEDIUM' THEN DATEDIFF(DATE_ADD(pri.priority_date, INTERVAL 14 DAY), CURRENT_DATE)
            WHEN pri.priority = 'REGULAR' THEN DATEDIFF(DATE_ADD(pri.priority_date, INTERVAL 20 DAY), CURRENT_DATE)
           END)                                           as daysRemain


FROM pr_indents pri
         LEFT JOIN pr_indent_details prid on pri.id = prid.pr_indent_id
         LEFT JOIN item_categories c on pri.category_id = c.id
         LEFT JOIN item_categories sc on pri.sub_category_id = sc.id
         LEFT JOIN items it on prid.item_id = it.id


WHERE pri.status = 'OPEN'
  AND (pri.id is not null)
  AND (pri.id in :ids)
group by CASE
             WHEN it.id is not null then it.id
             END
                                                                          """;


}
