package com.example.cruddemo;

import com.example.cruddemo.dao.StudentDAO;
import com.example.cruddemo.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class CruddemoApplication implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(CruddemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);
	}

	private final StudentDAO studentDAO;

	public CruddemoApplication(StudentDAO studentDAO) {
		this.studentDAO = studentDAO;
	}

	@Override
	public void run(String... args) throws Exception {

//		Create
		createAndSaveStudent(studentDAO);
//		createAndSaveMultipleStudents(studentDAO);
//		storeAndRetrieveStudentById(studentDAO);


//		Retrieve
		// Activate one retrieval method at a time to inspect its output.
//		retrieveAllStudentsUsingHql(studentDAO);
//		 retrieveAllStudentsByLastNameAscendingUsingHql(studentDAO);
//		 retrieveAllStudentsByLastNameDescendingUsingHql(studentDAO);
//		 retrieveAllStudentsUsingJpql(studentDAO);
//		 retrieveAllStudentsByLastNameAscendingUsingJpql(studentDAO);
//		 retrieveAllStudentsByLastNameDescendingUsingJpql(studentDAO);
//		 retrieveByLastNameWithoutSettingParameterUsingHql(studentDAO);
//		 retrieveByLastNameWithoutSettingParameterUsingJpql(studentDAO);
//		 retrieveByLastNameWithSettingParameterUsingHql(studentDAO);
//		 retrieveByLastNameWithSettingParameterUsingJpql(studentDAO);

//		Update
//		updateStudent(studentDAO);

//		Delete
//		deleteStudent(studentDAO);

//		deleteAllStudents(studentDAO);
	}

	private void createAndSaveStudent(StudentDAO studentDAO) {

		Student student = new Student("firstName A", "lastName A", "firstNameALastNameA@gmail.com");

		studentDAO.save(student);
	}

	private void createAndSaveMultipleStudents(StudentDAO studentDAO) {
		Student student1 = new Student("firstName B", "lastName B", "firstNameBLastNameB@gmail.com");
		Student student2 = new Student("firstName C", "lastName C", "firstNameCLastNameC@gmail.com");
		Student student3 = new Student("firstName D", "lastName D", "firstNameDLastNameD@gmail.com");

		studentDAO.save(student1);
		studentDAO.save(student2);
		studentDAO.save(student3);
	}

	private void storeAndRetrieveStudentById(StudentDAO studentDAO) {
		Student student = new Student("firstNameE", "lastNameE", "firstNameELastNameE@gmail.com");
		logger.info("Student before save = {}", student);

		studentDAO.save(student);
		logger.info("Student after save = {}", student);

		Student retrievedStudent = studentDAO.findById(student.getId());
		logger.info("Found in database = {}", retrievedStudent);
	}

	private void retrieveAllStudentsUsingHql(StudentDAO studentDAO) {
		logger.info("Calling StudentDAO.findAllHql()");
		List<Student> students = studentDAO.findAllHql();
		logStudents("findAllHql", students);
	}

	private void retrieveAllStudentsByLastNameAscendingUsingHql(StudentDAO studentDAO) {
		logger.info("Calling StudentDAO.findAllByLastNameAscHql()");
		List<Student> students = studentDAO.findAllByLastNameAscHql();
		logStudents("findAllByLastNameAscHql", students);
	}

	private void retrieveAllStudentsByLastNameDescendingUsingHql(StudentDAO studentDAO) {
		logger.info("Calling StudentDAO.findAllByLastNameDescHql()");
		List<Student> students = studentDAO.findAllByLastNameDescHql();
		logStudents("findAllByLastNameDescHql", students);
	}

	private void retrieveAllStudentsUsingJpql(StudentDAO studentDAO) {
		logger.info("Calling StudentDAO.findAllJpql()");
		List<Student> students = studentDAO.findAllJpql();
		logStudents("findAllJpql", students);
	}

	private void retrieveAllStudentsByLastNameAscendingUsingJpql(StudentDAO studentDAO) {
		logger.info("Calling StudentDAO.findAllByLastNameAscJpql()");
		List<Student> students = studentDAO.findAllByLastNameAscJpql();
		logStudents("findAllByLastNameAscJpql", students);
	}

	private void retrieveAllStudentsByLastNameDescendingUsingJpql(StudentDAO studentDAO) {
		logger.info("Calling StudentDAO.findAllByLastNameDescJpql()");
		List<Student> students = studentDAO.findAllByLastNameDescJpql();
		logStudents("findAllByLastNameDescJpql", students);
	}

	private void retrieveByLastNameWithoutSettingParameterUsingHql(StudentDAO studentDAO) {
		String lastName = "lastName B";
		logger.info("Calling StudentDAO.findByLastNameWithoutSettingParamHql() with lastName={}", lastName);
		List<Student> students = studentDAO.findByLastNameWithoutSettingParamHql(lastName);
		logStudents("findByLastNameWithoutSettingParamHql", students);
	}

	private void retrieveByLastNameWithoutSettingParameterUsingJpql(StudentDAO studentDAO) {
		String lastName = "lastName B";
		logger.info("Calling StudentDAO.findByLastNameWithoutSettingParamJpql() with lastName={}", lastName);
		List<Student> students = studentDAO.findByLastNameWithoutSettingParamJpql(lastName);
		logStudents("findByLastNameWithoutSettingParamJpql", students);
	}

	private void retrieveByLastNameWithSettingParameterUsingHql(StudentDAO studentDAO) {
		String lastName = "lastName B";
		logger.info("Calling StudentDAO.findByLastNameWithSettingParamHql() with lastName={}", lastName);
		List<Student> students = studentDAO.findByLastNameWithSettingParamHql(lastName);
		logStudents("findByLastNameWithSettingParamHql", students);
	}

	private void retrieveByLastNameWithSettingParameterUsingJpql(StudentDAO studentDAO) {
		String lastName = "lastName B";
		logger.info("Calling StudentDAO.findByLastNameWithSettingParamJpql() with lastName={}", lastName);
		List<Student> students = studentDAO.findByLastNameWithSettingParamJpql(lastName);
		logStudents("findByLastNameWithSettingParamJpql", students);
	}

	private void logStudents(String operation, List<Student> students) {
		logger.info("{} returned {} student(s)", operation, students.size());

		if (students.isEmpty()) {
			logger.info("{} returned no matching students", operation);
			return;
		}

		for (Student student : students) {
			logger.info("{} -> {}", operation, student);
		}
	}

    private void updateStudent(StudentDAO studentDAO) {
        int studentId = 5;
        logger.info("Finding student with id={}", studentId);
        Student student = studentDAO.findById(studentId);
        logger.info("Student before update = {}", student);

        student.setFirstName("firstName E");
        student.setLastName("lastName E");
        logger.info("Student after changing fields, before database update = {}", student);

        studentDAO.update(student);
        logger.info("Student update completed = {}", student);
    }

	private void deleteStudent(StudentDAO studentDAO) {
		int studentId = 5;
		logger.info("Requesting deletion of student with id={}", studentId);

		studentDAO.deleteById(studentId);
		logger.info("Delete operation completed for student with id={}", studentId);
	}

	private void deleteAllStudents(StudentDAO studentDAO) {
		logger.info("Requesting deletion of all students");

		int deletedStudentCount = studentDAO.deleteAll();
		logger.info("Deleted {} student(s)", deletedStudentCount);
	}

//	select * from student_tracker.student;
//	ALTER TABLE student AUTO_INCREMENT = 1000; - set the default auto increment value to 1000 instead of 1 in db, once increased cannot set it again to a lower value
//	Truncate table student; - delete existing records in the table and reset the auto increment counter
}
