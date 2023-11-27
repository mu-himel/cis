package com.aes.erp.employee.service;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.employee.repository.EmployeeRepository;
import com.aes.erp.exception.AesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void createEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public String getNextEmployeeId(){
        // get last employee id convert to long , increment,
        // pad with 0 then return
        Long  nextEmployeeId = employeeRepository.findFirstByOrderByEmployeeIdDesc();
        try {
            nextEmployeeId =(nextEmployeeId!=null)? (nextEmployeeId  + 1) : 1;

            return String.format("%06d",nextEmployeeId);

        }catch (Exception ex){
            throw new AesException(ex.getMessage());
        }

    }

    @Override
    public Optional<Employee> getEmployeeByUserId(Long id) {
        return employeeRepository.findByUserId(id);
    }
}
