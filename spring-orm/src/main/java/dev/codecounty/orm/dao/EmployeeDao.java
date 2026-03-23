package dev.codecounty.orm.dao;

import java.util.List;


import dev.codecounty.orm.entities.Employee;
import org.springframework.transaction.annotation.Transactional;

public interface EmployeeDao {

	public List<Employee> getEmployeesGoodWay();

	public List<Employee> getEmployeesBadWay();

	@Transactional
	void deleteEmployee(int id);
}
