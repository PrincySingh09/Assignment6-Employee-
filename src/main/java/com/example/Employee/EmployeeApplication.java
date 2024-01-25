package com.example.Employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}

class Employee {
    public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	private int empId;
    private String empName;
    private String designation;
    private int deptId;

    public Employee(int empId, String empName, String designation, int deptId) {
        this.empId = empId;
        this.empName = empName;
        this.designation = designation;
        this.deptId = deptId;
    }

}

class Department {
    public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	private int deptId;
    private String deptName;


    public Department(int deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

  
}

@RestController
@RequestMapping("/api")
class EmployeeController {

    private static List<Employee> employeeList = new ArrayList<>();

    static {

        employeeList.add(new Employee(1, "Princy Singh", "Software Engineer", 101));
        employeeList.add(new Employee(2, "Khushi Amb", "HR Manager", 102));
     
    }

    @GetMapping("/employee/{empId}")
    public Employee getEmployeeDetails(@PathVariable int empId) {
        return employeeList.stream()
                .filter(employee -> employee.getEmpId() == empId)
                .findFirst()
                .orElse(null); 
    }

    @GetMapping("/employeeExists/{empId}")
    public boolean doesEmployeeExist(@PathVariable int empId) {
        return employeeList.stream()
                .anyMatch(employee -> employee.getEmpId() == empId);
    }
    
    @PostMapping("/addEmployee")
    public ResponseEntity<?> addEmployee(@RequestBody Employee newEmployee) {
    	try {
        if (employeeList.stream().anyMatch(e -> e.getEmpId() == newEmployee.getEmpId())) {
            throw new EmployeeAlreadyExistsException("Employee with ID " + newEmployee.getEmpId() + " already exists.");
            
        } else {
            employeeList.add(newEmployee);
            return new ResponseEntity<>(newEmployee, HttpStatus.CREATED);
        }
    	}
    	catch (EmployeeAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/employeesByDesignation/{designation}")
    public List<Employee> getEmployeesByDesignation(@PathVariable String designation) {
        return employeeList.stream()
                .filter(employee -> employee.getDesignation().equalsIgnoreCase(designation))
                .collect(Collectors.toList());
    }

    @GetMapping("/employeesByDepartment/{deptId}")
    public List<Employee> getEmployeesByDepartment(@PathVariable int deptId) {
        return employeeList.stream()
                .filter(employee -> employee.getDeptId() == deptId)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/deleteEmployee/{empId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int empId) {
        try {
            Employee employeeToDelete = employeeList.stream()
                    .filter(employee -> employee.getEmpId() == empId)
                    .findFirst()
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + empId + " not found."));

            employeeList.remove(employeeToDelete);
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

 

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmployeeAlreadyExistsException extends RuntimeException {
        public EmployeeAlreadyExistsException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EmployeeNotFoundException extends RuntimeException {
        public EmployeeNotFoundException(String message) {
            super(message);
        }
    }
}