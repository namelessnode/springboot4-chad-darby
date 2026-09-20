package com.example.cruddemo.service;

import com.example.cruddemo.dao.EmployeeDao;
import com.example.cruddemo.entity.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeDao employeeDao;

    public EmployeeServiceImpl(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Override
    public List<Employee> findAll() {
        return employeeDao.findAll();
    }

    public Employee findById(int id) {
        return employeeDao.findById(id);
    }

    @Transactional
    public Employee save(Employee employee) {
        return employeeDao.save(employee);
    }

    @Transactional
    public void deleteById(int id) {
        employeeDao.deleteById(id);
    }
}
