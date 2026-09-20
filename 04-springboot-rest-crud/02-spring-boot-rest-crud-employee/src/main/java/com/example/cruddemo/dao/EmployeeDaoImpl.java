package com.example.cruddemo.dao;

import com.example.cruddemo.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDaoImpl implements EmployeeDao {

    private final EntityManager entityManager;

    public EmployeeDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Employee> findAll() {
        TypedQuery<Employee> query = entityManager.createQuery("Select e from Employee e", Employee.class);
        return query.getResultList();
    }

    @Override
    public Employee findById(int id) {
        TypedQuery<Employee> query = entityManager.createQuery("From Employee where id=:employeeId", Employee.class);
        query.setParameter("employeeId", id);
        return query.getSingleResult();
    }

    @Override
    public Employee save(Employee  employee) {
        return entityManager.merge(employee);
    }

    @Override
    public void deleteById(int id) {
        Employee employee = entityManager.find(Employee.class, id);
        entityManager.remove(employee);
    }
}
