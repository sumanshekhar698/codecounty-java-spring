package dev.codecounty.orm.dao;

import java.util.List;

import dev.codecounty.orm.entities.Student;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;



public interface StudentDao {
	// 1. CREATE (Insert)
	public int insert(Student student);

	// 2. READ (Single Student)
	public Student getStudent(int studentId);

	public List<Student> getStudentsByCity(String city);

	// 3. READ ALL
	public List<Student> getAllStudents();

	// 4. UPDATE
	public void updateStudent(Student student);

	// 5. DELETE
	public void deleteStudent(int studentId);
	
	// 5. DIRTY CHECK
	public void testDirtyChecking();
}
