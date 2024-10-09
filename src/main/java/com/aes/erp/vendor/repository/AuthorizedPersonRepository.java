package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.AuthorizedPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedPersonRepository extends JpaRepository<AuthorizedPerson,Long> {

}
