package com.aes.erp.employee.repository;

import com.aes.erp.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findByUserId(Long id);

    @Query(value = "select lpad((max(id)+1),6,0) from employees e ",nativeQuery = true)
    String getMaxId();


    @Query(value = "select\n" +
            "        employee_id\n" +
            "    from\n" +
            "        employees\n" +
            "        employee0_ \n" +
            "    order by\n" +
            "        cast(employee_id as UNSIGNED) desc limit 1",nativeQuery = true)
    Long findFirstByOrderByEmployeeIdDesc();
}
