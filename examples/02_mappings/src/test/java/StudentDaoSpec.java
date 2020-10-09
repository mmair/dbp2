import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDao;
import at.campus02.dbp2.jpa.StudentDaoImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class StudentDaoSpec {

    private EntityManagerFactory factory;
    private EntityManager manager;
    private StudentDao dao;

    private Student prepareStudent(
            String firstname,
            String lastname,
            Gender gender,
            String birthdayString
    ) {
        Student student = new Student();
        student.setFirstName(firstname);
        student.setLastName(lastname);
        student.setGender(gender);
        if (birthdayString != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            student.setBirthday(LocalDate.parse(birthdayString, formatter));
        }
        return student;
    }

    private void create(Student student) {
        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();
    }

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");
        manager = factory.createEntityManager();
        dao = new StudentDaoImpl(factory);
    }

    @Test
    public void ensureThatToUpperCaseResultsInAllUppercaseLetters() {
        // given
        String str1 = "string";

        // when
        String result = str1.toUpperCase();

        // then
        assertThat(result, is("STRING"));
    }

    @Test
    public void createNullAsStudentReturnsFalse() {
        // when
        boolean result = dao.create(null);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void createPersistsStudentInDatabaseAndReturnsTrue() {
        // given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        // when
        boolean result = dao.create(student);

        // then
        assertThat(result, is(true));
        // überprüfen, ob der Student in der Datenbank existiert.
        Student fromDB = manager.find(Student.class, student.getId());
        assertThat(fromDB, is(student));
    }

    @Test
    public void createAlreadyExistingStudentReturnsFalse() {
        // given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "13.05.1978");
        create(student);

        // when
        boolean result = dao.create(student);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void findStudentReturnsEntityFromDatabase() {
        // given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "13.05.1978");
        create(student);

        // when

        // der Student, den wir vom DAO bekommen....
        Student result = dao.find(student.getId());
        // ... und der Student, den wir im Test aus der DB lesen...
        Student fromDB = manager.find(Student.class, student.getId());

        // then

        // ... sollen die gleichen sein
        assertThat(result, is(fromDB));
    }

    @Test
    public void findStudentWithNullAsIdReturnsNull() {
        // expect
        assertThat(dao.find(null), is(nullValue()));
    }

    @Test
    public void findStudentWithNotExistingIdReturnsNull() {
        // expect
        assertThat(dao.find(4711), is(nullValue()));
    }












}
