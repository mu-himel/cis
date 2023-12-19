package com.aes.erp.vendor.repository;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.enums.VendorDocType;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {


    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.vendorType vt " +
            "WHERE v.id=:id")
    Optional<Vendor> findById(Long id);

    @Query(value = "SELECT v FROM Vendor v LEFT JOIN FETCH v.vendorType vt LEFT JOIN FETCH v.category c LEFT JOIN FETCH v.subCategoryList sc " +
            "WHERE :searchFilter IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :searchFilter, '%')) " +
            "   OR :searchFilter IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :searchFilter, '%')) " +
            "   OR :searchFilter IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :searchFilter, '%')) " +
            "   OR :searchFilter IS NULL OR LOWER(vt.name) LIKE LOWER(CONCAT('%', :searchFilter, '%')) ",
            countQuery = "SELECT COUNT(v) FROM Vendor v LEFT JOIN v.vendorType vt LEFT JOIN v.category c LEFT JOIN v.subCategoryList sc " +
                    "WHERE :searchFilter IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :searchFilter, '%')) " +
                    "   OR :searchFilter IS NULL OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :searchFilter, '%')) " +
                    "   OR :searchFilter IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :searchFilter, '%')) " +
                    "   OR :searchFilter IS NULL OR LOWER(vt.name) LIKE LOWER(CONCAT('%', :searchFilter, '%')) ")
    Page<VendorInfo> findAllVendors(Pageable pageable, @Param("searchFilter") String searchFilter);

    @Query(value = "SELECT v FROM Vendor v " +
            " LEFT JOIN FETCH v.vendorType vt " +
            " LEFT JOIN FETCH v.category c " +
            " LEFT JOIN FETCH v.subCategoryList sc " +
            " WHERE v.status IN (:status)",
            countQuery =  "SELECT count(v) FROM Vendor v " +
                    " LEFT JOIN v.vendorType vt " +
                    " LEFT JOIN v.category c " +
                    " LEFT JOIN v.subCategoryList sc " +
                    " WHERE v.status IN (:status)"
    )
    Page<VendorInfo> findAllVendorForStatus(@Param("status") VendorStatus status, Pageable pageable);


    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.vendorType vt " +
            "WHERE v.id=:id")
    Optional<VendorDetail> findVendorById(@Param("id") Long id);
    Optional<Vendor> findByUserId(Long id);

    interface VendorDetail{
        Long getId();
        String getName();
        ReferenceObjectDto getVendorType();

        String getPhone();

        String getEmail();

        VendorDocumentVerificationStatus getVendorVerifyStatus();

        VendorStatus getStatus();
        UserInfo getUser();

        ReferenceObjectDto getCategory();
        ReferenceObjectDto getSubCategory();

        List<VendorItem> getVendorItems();
        List<VendorFile> getFiles();
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

        VendorDocumentVerificationStatus getVerificationStatus();

        VendorType getVendorType();
        ItemCategory getCategory();
        List<ItemCategory> getSubCategoryList();
    }
}
