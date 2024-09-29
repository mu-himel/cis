package com.aes.erp.vendor.repository;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorSubCategory;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.enums.VendorDocType;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {


    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.vendorType vt " +
            "WHERE v.id=:id")
    Optional<Vendor> findById(Long id);

    @Query(value = "SELECT DISTINCT v FROM Vendor v LEFT JOIN FETCH v.vendorType vt LEFT JOIN FETCH v.category c " +
            "WHERE v.verificationStatus NOT IN ('APPROVED','VERIFIED','REJECTED')" +
            "   AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "   AND (:phone IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
            "   AND (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "   AND (:vendorType IS NULL OR vt.name = :vendorType) " +
            "   AND (:vendorStatus IS NULL OR v.verificationStatus = :vendorStatus)",
            countQuery = "SELECT COUNT(DISTINCT v) FROM Vendor v LEFT JOIN v.vendorType vt LEFT JOIN v.category c " +
                    "WHERE v.verificationStatus NOT IN ('APPROVED','VERIFIED','REJECTED') " +
                    "   AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "   AND (:phone IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
                    "   AND (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
                    "   AND (:vendorType IS NULL OR vt.name = :vendorType) " +
                    "   AND (:vendorStatus IS NULL OR v.verificationStatus = :vendorStatus)")
    Page<VendorInfo> findAllVendors(@Param("name") String name,
                                    @Param("email") String email,
                                    @Param("phone") String phone,
                                    @Param("vendorType") String vendorType,
                                    @Param("vendorStatus") String vendorStatus,
                                    Pageable pageable);


    @Query(value="""
            SELECT DISTINCT
                v.id as id,
                v.name as name,
                v.email as email, 
                v.phone as phone, 
                v.verification_status as verificationStatus,
                vt.name as vendorTypeName,
                (vs.total_score*100/1000) as score,
                b.address as addressLine1,
                v.ait_percentage as aitPercentage
            FROM vendor v
            LEFT JOIN vendor_types vt ON v.vendor_type_id = vt.id
            LEFT JOIN vendor_score vs ON v.vendor_score_id = vs.id
            LEFT JOIN document_holders dh ON v.document_holder_id = dh.id
            LEFT JOIN bin b ON dh.bin_document_id = b.id
            LEFT JOIN vendor_sub_category vsc ON v.id = vsc.vendor_id
            WHERE v.verification_status IN ('PENDING_DOCUMENT_VERIFICATION','PENDING_VERIFICATION','VERIFIED','APPROVED') 
                AND (:name IS NULL OR LOWER(v.name) LIKE CONCAT('%',LOWER(:name),'%')) 
                AND (:subCategoryId IS NULL OR vsc.subcategory_id=:subCategoryId)
                AND (:categoryId IS NULL OR (FIND_IN_SET(:categoryId, v.categories) > 0))
            """, nativeQuery = true)
    List<VendorListInfo> findAllVendors(@Param("name") String name,@Param("categoryId") Long categoryId,@Param("subCategoryId") Long subCategoryId);

    /**
     * InnerVendorRepository
     */
    public interface VendorListInfo {
        Long getId();
        String getName();
        String getEmail();
        String getPhone();
        Integer getScore();
        String getVerificationStatus();
        String getVendorTypeName();
        String getAddressLine1();
        Integer getAitPercentage();
    }
    @Query(value = "SELECT v FROM Vendor v " +
            " LEFT JOIN FETCH v.vendorType vt " +
            " LEFT JOIN FETCH v.category c " +
            " WHERE v.verificationStatus IN (:status)" +
            "   AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "   AND (:phone IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
            "   AND (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "   AND (:vendorType IS NULL OR vt.name = :vendorType) " +
            "   AND (:vendorStatus IS NULL OR v.verificationStatus = :vendorStatus)",
            countQuery =  "SELECT count(v) FROM Vendor v " +
                    " LEFT JOIN v.vendorType vt " +
                    " LEFT JOIN v.category c " +
                    " WHERE v.verificationStatus IN (:status)" +
                    "   AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "   AND (:phone IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
                    "   AND (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
                    "   AND (:vendorType IS NULL OR vt.name = :vendorType) " +
                    "   AND (:vendorStatus IS NULL OR v.verificationStatus = :vendorStatus)"
    )
    Page<VendorInfo> findAllVendorForStatus(@Param("status") VendorDocumentVerificationStatus status,
                                            @Param("name") String name,
                                            @Param("email") String email,
                                            @Param("phone") String phone,
                                            @Param("vendorType") String vendorType,
                                            @Param("vendorStatus") String vendorStatus,
                    Pageable pageable);


    @Query(value = "SELECT v FROM Vendor v " +
            " LEFT JOIN FETCH v.vendorType vt " +
            " LEFT JOIN FETCH v.category c " +
            " WHERE v.verificationStatus IN (:verificationStatus)" +
            "   AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "   AND (:phone IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
            "   AND (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "   AND (:vendorType IS NULL OR vt.name = :vendorType) " +
            "   AND (:vendorStatus IS NULL OR v.verificationStatus IN (:vendorStatus))",
            countQuery =  "SELECT count(v) FROM Vendor v " +
                    " LEFT JOIN v.vendorType vt " +
                    " LEFT JOIN v.category c " +
                    " WHERE v.verificationStatus IN (:verificationStatus)" +
                    "   AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "   AND (:phone IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
                    "   AND (:email IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
                    "   AND (:vendorType IS NULL OR vt.name = :vendorType) " +
                    "   AND (:vendorStatus IS NULL OR v.verificationStatus IN (:vendorStatus))"
    )
    Page<VendorInfo> findAllVendorForComplete(
                                            @Param("name") String name,
                                            @Param("email") String email,
                                            @Param("phone") String phone,
                                            @Param("vendorType") String vendorType,
                                            @Param("vendorStatus") String vendorStatus,
                                            @Param("verificationStatus") List<VendorDocumentVerificationStatus> verificationStatus,
                                            Pageable pageable);


    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.vendorType vt " +
            "WHERE v.id=:id")
    Optional<VendorDetail> findVendorById(@Param("id") Long id);
    Optional<Vendor> findByUserId(Long id);
    Optional<Vendor> findByDocumentHolderId(Long id);

    @Query(value = """
            SELECT count(v.name) as total  FROM vendor v
            			LEFT JOIN vendor_sub_category vsc ON vsc.vendor_id = v.id
            			LEFT JOIN item_categories ic ON ic.id= vsc.subcategory_id
            			WHERE ic.active =1 AND ic.code=:subCatCode
            """,nativeQuery = true)
    Optional<Integer> countVendorsBySubCategory(@Param("subCatCode") String subCatCode);

    interface VendorDetail{
        Long getId();
        String getName();
        ReferenceObjectDto getVendorType();
        String getCategories();
        String getPhone();

        String getEmail();

        VendorDocumentVerificationStatus getVerificationStatus();

        VendorStatus getStatus();
        UserInfo getUser();

        ReferenceObjectDto getCategory();
//        ReferenceObjectDto getSubCategory();

        List<VendorSubCategoryInfo> getVendorSubCategories();

        List<VendorItem> getVendorItems();
        List<VendorFile> getFiles();
    }

    interface VendorSubCategoryInfo{
        Long getId();
        CategoryInfo getSubcategory();
    }

    interface CategoryInfo{
        Long getId();
        String getName();
        String getCode();

        CategoryInfo getParentCategory();
    }

    interface VendorFile{
        Long getId();
        String getFileName();
        String getSize();
        String getPath();
        String getMimeType();
        VendorDocType getDocumentType();
    }
    interface ReferenceObjectDto{
        Long getId();
        String getName();
    }

    interface VendorItem{
        ReferenceObjectDto getItem();
    }

    interface UserInfo{
        Long getId();
        String getFirstName();
        String getLastName();
        UserCredentialInfo getUserCredential();
    }

    interface UserCredentialInfo{
        Long getId();
    }



    interface VendorInfo{
        Long getId();
        String getName();

        String getEmail();

        String getPhone();

        VendorStatus getStatus();

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date getStartedAt();

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date getCompletedAt();

        VendorDocumentVerificationStatus getVerificationStatus();

        VendorType getVendorType();
        CategoryInfo getCategory();
//        List<VendorSubCategory> getVendorSubCategories();
    }
    
    Boolean existsByPhone(String phone);


}
