import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDao;
import at.campus02.dbp2.jpa.StudentDaoImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class StudentDaoSpec {


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
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");
        StudentDao dao = new StudentDaoImpl(factory);

        // when
        boolean result = dao.create(null);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void createPersistsStudentInDatabaseAndReturnsTrue() {
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");

        Student student = new Student();
        student.setLastName("lastname");
        student.setFirstName("firstname");
        student.setGender(Gender.FEMALE);
        StudentDao dao = new StudentDaoImpl(factory);

        // when
        boolean result = dao.create(student);

        // then
        assertThat(result, is(true));
        // überprüfen, ob der Student in der Datenbank existiert.
        EntityManager manager = factory.createEntityManager();
        Student fromDB = manager.find(Student.class, student.getId());
        assertThat(fromDB, is(student));
    }

    @Test
    public void createAlreadyExistingStudentReturnsFalse() {
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");

        Student student = new Student();
        student.setLastName("lastname");
        student.setFirstName("firstname");
        student.setGender(Gender.FEMALE);

        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();

        StudentDao dao = new StudentDaoImpl(factory);

        // when
        boolean result = dao.create(student);

        // then
        assertThat(result, is(false));

    }
















}
