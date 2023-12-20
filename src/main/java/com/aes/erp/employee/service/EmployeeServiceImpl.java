package com.aes.erp.employee.service;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.employee.repository.EmployeeRepository;
import com.aes.erp.exception.AesException;
import com.aes.erp.user_management.dto.EmployeeUserDto;
import com.aes.erp.user_management.entity.User;
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

    @Override
    public void updateEmployee(Long id, EmployeeUserDto employee) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if(employeeOptional.isPresent()){
            Employee _employee = employeeOptional.get();
            if(employee.getName()!=null){
                _employee.setName(employee.getName());
            }
            if(employee.getPhone()!=null){
                _employee.setPhone(employee.getPhone());
            }
            if(employee.getEmail()!=null){
                User user = _employee.getUser();
                user.setEmailAddress(employee.getEmail());
                _employee.setUser(user);
            }
            if(employee.getEmployeeType() !=null ){
                _employee.setEmployeeType(employee.getEmployeeType());
            }
//            if(employee.getWarehouse()!=null && employee.getWarehouse().getId()!=null){
//                _employee.setWarehouse(new Warehouse(employee.getWarehouse().getId()));
//            }
            employeeRepository.save(_employee);
        }
    }
}
