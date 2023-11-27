package com.aes.erp.indent.repository;

public interface IndentQuery {
    String getReadyIndentWithSearch =
            """
                    SELECT i.id              as            id,
                           i.indent_no       as            indentNo,
                           i.category_id     as            categoryId,
                           c.name            as            categoryName,
                           i.sub_category_id as            subCategoryId,
                           sc.name           as            subCategoryName,
                           COALESCE(SUM(ide.order_qty), 0) orderQty,
                           i.priority        as            priority,
                           i.status          as            status

                    FROM indents i
                             LEFT JOIN indent_details ide on i.id = ide.indent_id
                             LEFT JOIN item_categories c on i.category_id = c.id
                             LEFT JOIN item_categories sc on i.sub_category_id = sc.id
                             LEFT JOIN items it on ide.item_id = it.id

                    WHERE (:categoryId IS NULL OR c.id = :categoryId)
                      AND (:subCategoryId IS NULL OR sc.id = :subCategoryId)
                      AND (:priority IS NULL OR i.priority = :priority)

                    GROUP BY i.id
                                                                                """;

    String getIndentByIdWithSearch =
            """
                    SELECT i.id                                              as id,
                           i.indent_no                                       as indentNo,
                           i.category_id                                     as categoryId,
                           c.name                                            as categoryName,
                           i.sub_category_id                                 as subCategoryId,
                           sc.name                                           as subCategoryName,
                           it.id                                             as itemId,
                           it.name                                           as itemName,
                           it.name                                           as itemDescription,
                           COALESCE(SUM(ide.order_qty), 0)                   as orderQty,
                           COALESCE(SUM(ide.pr_qty), 0)                      as prQty,
                           i.priority                                        as priority,
                           i.delivery_location                               as deliveryLocation,
                           COALESCE((SELECT SUM(istock.stock_qty)
                                     FROM item_stocks istock
                                     where istock.item_id = ide.item_id), 0) as currentStock,
                           COALESCE((SELECT item.stock_threshold_qty
                                     FROM items item
                                     where item.id = ide.item_id), 0)        as safetytStock,
                           0                                                 as transitQty,
                           (CASE
                                WHEN i.priority = 'URGENT' THEN DATEDIFF(DATE_ADD(i.indent_date, INTERVAL 7 DAY), CURRENT_DATE)
                                WHEN i.priority = 'MEDIUM' THEN DATEDIFF(DATE_ADD(i.indent_date, INTERVAL 14 DAY), CURRENT_DATE)
                                WHEN i.priority = 'REGULAR' THEN DATEDIFF(DATE_ADD(i.indent_date, INTERVAL 20 DAY), CURRENT_DATE)
                               END)                                          as daysRemain


                    FROM indents i
                             LEFT JOIN indent_details ide on i.id = ide.indent_id
                             LEFT JOIN item_categories c on i.category_id = c.id
                             LEFT JOIN item_categories sc on i.sub_category_id = sc.id
                             LEFT JOIN items it on ide.item_id = it.id


                    WHERE (:indentId IS NOT NULL AND i.id = :indentId)
                    group by CASE
                                 WHEN it.id is not null then it.id
                                 END
                                                                          """;

    String getIndentByIdsWithSearch =
            """
                    SELECT i.id              as            id,
                           i.indent_no       as            indentNo,
                           i.category_id     as            categoryId,
                           c.name            as            categoryName,
                           i.sub_category_id as            subCategoryId,
                           sc.name           as            subCategoryName,
                           it.id             as            itemId,
                           it.name           as            itemName,
                           COALESCE(SUM(ide.order_qty), 0) orderQty,
                           i.priority        as            priority


                    FROM indents i
                             LEFT JOIN indent_details ide on i.id = ide.indent_id
                             LEFT JOIN item_categories c on i.category_id = c.id
                             LEFT JOIN item_categories sc on i.sub_category_id = sc.id
                             LEFT JOIN items it on ide.item_id = it.id


                    WHERE i.status = 'INIT'
                      AND i.id in (:indentIds)
                    group by i.category_id,i.sub_category_id,
                             CASE
                                 WHEN it.id is not null then it.id
                             END
                                                                          """;


    String countReadyIndentWithSearch =
            """
                    select count(*)
                    from (
                    """ +
                    getReadyIndentWithSearch
                    + """
                        ) as temp
                    """;


}
