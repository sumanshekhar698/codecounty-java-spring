package dev.codecounty.orm.app;

import java.util.List;

import dev.codecounty.orm.dao.EmployeeDao;
import dev.codecounty.orm.entities.Employee;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class _2_MainNPlus1Issue {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans_config/n_plus_1_config.xml");
        EmployeeDao dao = context.getBean(EmployeeDao.class);

//       CsvLoaderService csvLoaderService = context.getBean(CsvLoaderService.class);
//       csvLoaderService.loadDataFromCsv();

        // This will print 1 query for employees + N queries for their projects
        System.out.println("--- Starting N+1 Problem ---");
//		List<Employee> emps = dao.getEmployeesBadWay();
//		System.out.println(emps);
//		emps.forEach(employee -> System.out.println(employee.getName() + " works on: " + employee.getProjects().size()));
//		for (Employee e : emps) {
//			System.out.println(e.getName() + " works on: " + e.getProjects().size());
//		}

        // This will print exactly 1 query total
        System.out.println("--- Using Join Fetch Fix ---");
        List<Employee> optimizedEmps = dao.getEmployeesGoodWay();
        for (Employee e : optimizedEmps) {
            System.out.println(e.getName() + " works on: " + e.getProjects().size() + " projects");
        }
    }
}