package dev.codecounty.orm.app;

import java.util.List;

import dev.codecounty.orm.dao.StudentDao;
import dev.codecounty.orm.entities.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class _1_MainIntro {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans_config/orm_config.xml");
		StudentDao studentDao = context.getBean("studentDao", StudentDao.class);

		// --- DELETE (Example) ---
//		studentDao.deleteStudent(101);
		
		// --- CREATE ---
//		Student s1 = new Student(101, "Suman Shekhar", "Bangalore");
//		studentDao.insert(s1);
//		System.out.println("Student saved!");

		// --- READ ---
		Student retrievedStudent = studentDao.getStudent(102);
		System.out.println("Retrieved: " + retrievedStudent);

		 retrievedStudent = studentDao.getStudent(102);
		System.out.println("Retrieved: " + retrievedStudent);
		
//		List<Student> bombayStudents = studentDao.getStudentsByCity("Bombay");
//		System.out.println("Bombay Students: " + bombayStudents);



		// --- UPDATE ---
		retrievedStudent.setCity("Mumbai");
		studentDao.updateStudent(retrievedStudent);
		System.out.println("Updated City to Mumbai");

		// --- READ ALL ---
//		List<Student> all = studentDao.getAllStudents();
//		for (Student s : all) {
//			System.out.println(s);
//		}
		

	}
}