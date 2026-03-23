package dev.codecounty.orm.dao;

import java.util.List;

import dev.codecounty.orm.entities.Employee;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
public class EmployeeDaoImpl implements EmployeeDao {

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * TRIGGERING THE N+1 PROBLEM This query only selects Employees. When you loop
	 * through them later, Hibernate fires N extra queries for projects.
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Employee> getEmployeesBadWay() {
		List<Employee> employees = sessionFactory.getCurrentSession().
				createQuery("from Employee", Employee.class)
				.getResultList();

//		for (Employee e : employees) {
//			e.getProjects().size();
//		}

	    employees.get(0).getProjects().size(); // triggers Only a batch load

//		employees.forEach(e -> // BEST
//		Hibernate.initialize(e.getProjects()));

		return employees;
	}

	/**
	 * FIXING THE N+1 PROBLEM The 'JOIN FETCH' keyword tells Hibernate to grab
	 * Projects in the SAME query as Employees using a SQL JOIN.
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Employee> getEmployeesGoodWay() {
		return sessionFactory.getCurrentSession()
				.createQuery("select e from Employee e join fetch e.projects", Employee.class)
				.getResultList();// HQL /
																												// JPQL
		// (Object-centric)
		/*
		 * SELECT e.id, e.name, p.id, p.project_name FROM employees e JOIN projects p ON
		 * p.emp_id = e.id;
		 */

	}



	/**
	 * DELETE AN EMPLOYEE
	 * We use @Transactional (without readOnly) because this modifies the database.
	 */

	@Transactional
	@Override
	public void deleteEmployee(int id) {
		// 1. Get the current session
		var session = sessionFactory.getCurrentSession();

		// 2. Fetch the persistent instance from the database
		// It's better to fetch it first to ensure it's in the 'Persistent' state
		Employee employee = session.get(Employee.class, id);

		// 3. Delete if it exists
		if (employee != null) {
			session.remove(employee);
			// In older Hibernate versions, you might use session.delete(employee);
			System.out.println("Employee with ID " + id + " deleted successfully.");
		} else {
			System.out.println("Employee with ID " + id + " not found.");
		}
	}
}