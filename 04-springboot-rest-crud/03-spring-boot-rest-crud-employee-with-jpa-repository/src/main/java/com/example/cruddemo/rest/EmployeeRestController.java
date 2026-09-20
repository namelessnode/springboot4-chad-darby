package com.example.cruddemo.rest;

import com.example.cruddemo.entity.Employee;
import com.example.cruddemo.service.EmployeeService;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final JsonMapper jsonMapper;

    public EmployeeRestController(EmployeeService employeeService, JsonMapper jsonMapper) {
        this.employeeService = employeeService;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/employees/{employeeId}")
    public Employee findById(@PathVariable(name = "employeeId") int id) {
        return employeeService.findById(id);
    }

    @PostMapping("/employees")
    public Employee save(@RequestBody Employee employee) {
        employee.setId(null);
        return employeeService.save(employee);
    }

    @PutMapping("/employees")
    public Employee update(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    @PatchMapping("/employees/{employeeId}")
    public Employee patch(@PathVariable(value = "employeeId") int id, @RequestBody Map<String, Object> patchPayload) {
        Employee employee = employeeService.findById(id);

        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        if (patchPayload.containsKey("id"))
            throw new RuntimeException("Id is not allowed");

        Employee updatedEmployee = jsonMapper.updateValue(employee, patchPayload);

        return employeeService.save(updatedEmployee);

    }

    @DeleteMapping("/employees/{employeeId}")
    public String delete(@PathVariable("employeeId") int id) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        employeeService.deleteById(id);
        return "Employee deleted with id " + employee.getId();
    }
}
