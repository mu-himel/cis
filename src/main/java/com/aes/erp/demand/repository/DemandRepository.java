package com.aes.erp.demand.repository;

import com.aes.erp.demand.entity.Demand;
import com.aes.erp.demand.enums.DemandItemStatus;
import com.aes.erp.demand.enums.DemandPriority;
import com.aes.erp.demand.enums.DemandStatus;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.enums.ItemUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandRepository extends JpaRepository<Demand,Long> {

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c WHERE r.id=:id",
    countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
            " WHERE r.id=:id")
    Page<DemandListInfo> findAllByRequestedById(@Param("id") Long id, Pageable pageable);

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c WHERE d.status NOT IN ('PENDING_APPROVAL','PENDING_VERIFICATION','COMPLETED','CANCELED','DECLINED')",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    "WHERE d.status NOT IN ('PENDING_APPROVAL','PENDING_VERIFICATION','COMPLETED','CANCELED','DECLINED')")
    Page<DemandListInfo> findAllDemands(Pageable pageable);

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c " +
            "WHERE dd.itemCategory.id IN :categories AND d.status NOT IN ('PENDING_APPROVAL','PENDING_VERIFICATION','COMPLETED','CANCELED','DECLINED')",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    "WHERE d.status NOT IN ('PENDING_APPROVAL','PENDING_VERIFICATION','COMPLETED','CANCELED','DECLINED')")
    Page<DemandListInfo> findAllDemandsByCategory(List<Long> categories, Pageable pageable);

    @Query(value = "select i.id as id, s.current_stock_qty as currentStockQty,dd.id as demandDetailId," +
            "\nCOALESCE(" +
            "(select (sum(stock_qty)*-1) as consume from item_stocks is2 where is2 .stock_type = 'STOCK_OUT' and is2 .item_id = :id and is2.stock_date LIKE :yearMonth||'%'\n" +
            "),0) as totalConsumeInCurrentMonth,\n" +
            "COALESCE((select (sum(stock_qty)*-1) as consume from item_stocks is2 where is2 .stock_type = 'STOCK_OUT' and is2 .item_id = :id and is2.stock_date LIKE :yearMonth||'%'\n" +
            "),0)/30 as AvgTotalConsumeInCurrentMonth,\n" +
            "COALESCE((" +
            "select sum(stock_qty) as stockIn from item_stocks is2 where is2 .stock_type = 'STOCK_IN' and is2 .item_id = :id and is2.stock_date LIKE :yearMonth||'%'\n" +
            "),0) as totalStockInCurrentMonth,\n" +
            "(select count(*) as pr_qty  from product_requirements pr \n" +
            "where pr.demand_detail_id = dd.id) as prQty," +
            "d.id as demandId, d.demand_date as demandDate, d.demand_no as demandNo, d.status as demandStatus, \n" +
            "dd.approved_quantity as approvedQuantity, dd.request_quantity as requestQuantity," +
            "dd.status as demandDetailStatus , dd.specification  as specification, dd.priority as demandPriority, " +
            "dd.item_category_id as itemCategoryId,dd.item_parent_category_id as itemParentCategoryId, i.name as name, i.code as code, i.item_unit as itemUnit,  " +
            "i.stock_threshold_qty as stockThresholdQty,e.id as empId, e.employee_id as employeeId, e.name as employeeName," +
            "re.name as reportingManager, department.name as department, rn.name as designation," +
            "ic.name as category, ic.code as categoryCode,ic2.name as parentCategory, ic2.code as parentCategoryCode\n" +
            "FROM demand_details dd \n" +
            "join demands d on d.id=dd.demand_id \n" +
            "join items i on i.id = dd.item_id \n" +
            "join item_categories ic on ic.id = dd.item_category_id\n" +
            "join item_categories ic2 on ic2.id = dd.item_parent_category_id\n" +
            "left join employees e on e.id = d.requested_by_id\n" +
            "join department on department.id = e.department_id\n" +
            "join role_node rn on rn.id = e.role_node_id\n" +
            "left join employees re on e.reporting_manager_id = re.id\n" +
            "join (\n" +
            " \tselect is2.item_id, sum(is2.stock_qty) as current_stock_qty\n" +
            " \tfrom item_stocks is2 group by is2.item_id ) as s  on s.item_id = i.id \n" +
            "where demand_id = :id",nativeQuery = true)
    List<DemandDetailItem> findByDemandId(@Param("id") Long id, @Param("yearMonth") String yearMonth);

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c" +
            " WHERE d.status IN ('COMPLETED','CANCELED','DECLINED')",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    "WHERE d.status IN ('COMPLETED','CANCELED','DECLINED')")
    Page<DemandListInfo> findAllCloseDemands(Pageable pageable);

    @Query("select max(d.id) from Demand d")
    Optional<Long> findMaxOrderById();

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c " +
            " JOIN FETCH dd.itemParentCategory pc " +
            "WHERE (c.id IN (:categories) OR pc.id IN (:categoryIds)) " +
            "AND d.nextVerifierId =:nextVerifierId AND d.status IN (:pendingVerification)",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    " JOIN d.demandDetails dd " +
                    " JOIN dd.item i " +
                    " JOIN dd.itemCategory c " +
                    " JOIN dd.itemParentCategory pc " +
                    "WHERE (c.id IN (:categories) OR pc.id IN (:categoryIds)) " +
                    "AND d.nextVerifierId =:nextVerifierId AND d.status IN (:pendingVerification)")
    Page<DemandListInfo> findAllDemandsByCategoryAndDemandStatusAndNextVerifierId(
            @Param("categories") List<Long> categoryIds,
            @Param("nextVerifierId") Long nextVerifierId,
            @Param("pendingVerification") DemandStatus pendingVerification,
            Pageable pageable);

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c " +
            " JOIN FETCH dd.itemParentCategory pc " +
            "WHERE (c.id IN (:categoryIds) OR pc.id IN (:categoryIds)) " +
            "AND d.nextApproverId =:nextApproverId AND d.status IN (:demandStatus)",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    " JOIN d.demandDetails dd " +
                    " JOIN dd.item i " +
                    " JOIN dd.itemCategory c " +
                    " JOIN dd.itemParentCategory pc " +
                    "WHERE (c.id IN (:categoryIds) OR pc.id IN (:categoryIds)) " +
                    "AND d.nextApproverId =:nextApproverId AND d.status IN (:demandStatus)")
    Page<DemandListInfo> findAllDemandsByCategoryAndDemandStatusAndNextApproverId(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("nextApproverId") Long nextApproverId,
            @Param("demandStatus") DemandStatus demandStatus,
            Pageable pageable);

    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c " +
            " JOIN FETCH dd.itemParentCategory pc " +
            "WHERE d.nextVerifierId =:nextVerifierId AND d.status IN (:pendingVerification)",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    " JOIN d.demandDetails dd " +
                    " JOIN dd.item i " +
                    " JOIN dd.itemCategory c " +
                    " JOIN dd.itemParentCategory pc " +
                    "WHERE d.nextVerifierId =:nextVerifierId AND d.status IN (:pendingVerification)")
    Page<DemandListInfo> findAllDemandsByDemandStatusAndNextVerifierId(
            @Param("pendingVerification") DemandStatus pendingVerification,
            @Param("nextVerifierId") Long id,
            Pageable pageable);


    @Query(value = "SELECT d FROM Demand d JOIN FETCH d.requestedBy r" +
            " JOIN FETCH d.demandDetails dd" +
            " JOIN FETCH dd.item i " +
            " JOIN FETCH dd.itemCategory c " +
            " JOIN FETCH dd.itemParentCategory pc " +
            "WHERE d.nextApproverId =:nextApproverId AND d.status IN (:demandStatus)",
            countQuery = "SELECT count(d) FROM Demand d JOIN d.requestedBy r " +
                    " JOIN d.demandDetails dd " +
                    " JOIN dd.item i " +
                    " JOIN dd.itemCategory c " +
                    " JOIN dd.itemParentCategory pc " +
                    "WHERE d.nextApproverId =:nextApproverId AND d.status IN (:demandStatus)")
    Page<DemandListInfo> findAllDemandsByDemandStatusAndNextApproverId(
            @Param("demandStatus") DemandStatus demandStatus,
            @Param("nextApproverId") Long id,
            Pageable pageable);

    interface DemandDetailItem{
        Long getId();
        Long getDemandId();
        Long getDemandDetailId();
        Long getItemCategoryId();
        Long getItemParentCategoryId();
        String getCategory();
        String getParentCategory();
        String getCategoryCode();
        String getParentCategoryCode();
        String getName();
        String getCode();
        String getSpecification();
        Integer getRequestQuantity();
        Integer getApprovedQuantity();
        Integer getCurrentStockQty();
        Integer getStockThresholdQty();
        LocalDateTime getDemandDate();
        String getDemandNo();
        DemandStatus getDemandStatus();
        DemandPriority getDemandPriority();
        DemandStatus getDemandDetailStatus();
        ItemUnit getItemUnit();
        Integer getTotalStockInCurrentMonth();
        Integer getTotalConsumeInCurrentMonth();
        BigDecimal getAvgTotalConsumeInCurrentMonth();
        Long getEmpId();
        String getEmployeeId();
        String getEmployeeName();
        String getReportingManager();
        String getDepartment();
        String getDesignation();
        Long getPrQty();
    }

    Integer countByStatus(DemandStatus pending);

    interface DemandListInfo{

        Long getId();
        String getDemandNo();
        LocalDateTime getDemandDate();
        DemandStatus getStatus();


        List<MyDemandDetail> getDemandDetails();

        RequestedBy getRequestedBy();
    }

    interface ItemCategoryInfo{
        Long getId();
        String getName();
    }

    interface RequestedBy{
        Long getId();
        String getEmployeeId();
        String getName();
        OrganogramInfo getRoleNode();
        OrganogramInfo getDepartment();
        ReportingManager getReportingManager();

    }

    interface ReportingManager{
        Long getId();
        String getName();
        String getEmployeeId();
    }

    interface OrganogramInfo {
        Long getId();
        String getName();
    }

    interface DemandItemInfo{
        Long getId();
        String getName();
        String getCode();

    }

    interface MyDemandDetail{
        Long getId();
        DemandItemInfo getItemCategory();
        DemandItemInfo getItemParentCategory();
        DemandItemInfo getItem();

        Integer getRequestQuantity();
        Integer getApprovedQuantity();

    }
}
