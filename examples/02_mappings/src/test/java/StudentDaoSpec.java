import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDao;
import at.campus02.dbp2.jpa.StudentDaoImpl;
import org.hamcrest.CoreMatchers;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class StudentDaoSpec {

    private EntityManagerFactory factory;
    private EntityManager manager;
    private StudentDao dao;

    // <editor-fold desc="Hilfsfunktionen">
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

    @After
    public void tearDown() {
        dao.close();
        if (manager.isOpen()) {
            manager.close();
        }
        if (factory.isOpen()) {
            factory.close();
        }
    }
    // </editor-fold>

    @Test
    public void ensureThatToUpperCaseResultsInAllUppercaseLetters() {
        // given
        String str1 = "string";

        // when
        String result = str1.toUpperCase();

        // then
        assertThat(result, is("STRING"));
    }

    // <editor-fold desc="CREATE">

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
        // ??berpr??fen, ob der Student in der Datenbank existiert.
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
    // </editor-fold>

    // <editor-fold desc="FIND">

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

    // </editor-fold>

    // <editor-fold desc="UPDATE">

    @Test
    public void updateStudentChangesValuesInDatabase() {
        // given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "13.05.1978");
        create(student);

        // vorsichtshalber Cache ausleeren, weil wir denselben EntityManager f??rs persist und dann find verwenden...
        manager.clear();

        // when
        student.setLastName("Married-Now");
        // ge??nderter Student vom DAO
        Student result = dao.update(student);
        // (hoffentlich) ge??nderter Student aus der Datenbank
        Student fromDB = manager.find(Student.class, student.getId());

        // then
        assertThat(result.getLastName(), is("Married-Now"));
        assertThat(fromDB.getLastName(), is("Married-Now"));
        assertThat(result, is(fromDB));

    }

    @Test
    public void updateNullAsStudentReturnsNull() {
        // expect
        assertThat(dao.update(null), is(nullValue()));
    }

    @Test
    public void updateNotExistingStudentReturnsNull() {
        // given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "13.05.1978");

        // when
        Student result = dao.update(student);

        // then
        assertThat(result, is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold desc="DELETE">

    @Test
    public void deleteStudentRemovesEntityFromDatabase() {
        // given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "13.05.1978");
        create(student);
        manager.clear();

        // when
        int id = student.getId();
        dao.delete(student);

        // then
        assertThat(dao.find(id), is(nullValue()));
        assertThat(manager.find(Student.class, id), is(nullValue()));
    }

    @Test
    public void deleteNullOrNotExistingStudentDoesNotThrowException() {
        // expect no exception
        dao.delete(null);
        dao.delete(prepareStudent("firstname", "lastname", null, null));
    }

    // </editor-fold>

    // <editor-fold desc="QUERIES">

    @Test
    public void findAllReturnsAllEntitiesFromDatabase() {
        // given
        Student student1 = prepareStudent("firstname", "lastname", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstname2", "lastname2", Gender.MALE, "13.05.1988");
        Student student3 = prepareStudent("firstname2", "lastname2", Gender.FEMALE, "13.05.1998");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();

        // when
        List<Student> result = dao.findAll();

        // then
        assertThat(result.size(), is(3));
        assertThat(result, hasItems(student1, student2, student3));
    }

    @Test
    public void findByLastnameReturnsMatchingStudents() {
        // given
        String lastnameToFind = "Lastname";
        Student student1 = prepareStudent("Firstname", lastnameToFind, Gender.FEMALE, "15.05.1982");
        create(student1);
        Student student2 = prepareStudent("Firstname2", "NotMatching", Gender.FEMALE, "15.05.1988");
        create(student2);
        Student student3 = prepareStudent("Firstname3", lastnameToFind, Gender.MALE, "15.05.1981");
        create(student3);

        // when
        List<Student> result = dao.findAllByLastname(lastnameToFind);

        // then
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(student1, student3));
        assertThat(result.contains(student2), is(false));
    }

    @Test
    public void findByLastnameWithNullParameterReturnsAllEntitites() {
        // given
        Student student1 = prepareStudent("Firstname", "Lastname", Gender.FEMALE, "15.05.1982");
        create(student1);
        Student student2 = prepareStudent("Firstname2", "NotMatching", Gender.FEMALE, "15.05.1988");
        create(student2);
        Student student3 = prepareStudent("Firstname3", "Lastname", Gender.MALE, "15.05.1981");
        create(student3);

        // when
        List<Student> result = dao.findAllByLastname(null);

        // then
        assertThat(result.size(), is(3));
        assertThat(result, hasItems(student1, student2, student3));
    }

    @Test
    public void findByLastnameReturnsMatchingStudentsCaseInsensitive() {
        // given
        Student student1 = prepareStudent("Firstname", "Lastname", Gender.FEMALE, "15.05.1982");
        create(student1);
        Student student2 = prepareStudent("Firstname2", "NotMatching", Gender.FEMALE, "15.05.1988");
        create(student2);
        Student student3 = prepareStudent("Firstname3", "lastname", Gender.MALE, "15.05.1981");
        create(student3);

        // when
        List<Student> result = dao.findAllByLastname("Lastname");

        // then
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(student1, student3));
        assertThat(result.contains(student2), is(false));
    }


    @Test
    public void findAllByGenderReturnsMatchingEntities() {
        // given
        Student student1 = prepareStudent("Firstname", "Lastname", Gender.FEMALE, "15.05.1982");
        create(student1);
        Student student2 = prepareStudent("Firstname2", "Lastname3", Gender.FEMALE, "15.05.1988");
        create(student2);
        Student student3 = prepareStudent("Firstname3", "Lastname", Gender.MALE, "15.05.1981");
        create(student3);

        // when
        List<Student> result = dao.findAllByGender(Gender.FEMALE);

        // then
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(student1, student2));
    }

    @Test
    public void findAllByGenderWithNullParameterReturnsEmptyList() {
        // given
        Student student1 = prepareStudent("Firstname", "Lastname", Gender.FEMALE, "15.05.1982");
        create(student1);
        Student student2 = prepareStudent("Firstname2", "Lastname3", Gender.FEMALE, "15.05.1988");
        create(student2);
        Student student3 = prepareStudent("Firstname3", "Lastname", Gender.MALE, "15.05.1981");
        create(student3);

        // when
        List<Student> result = dao.findAllByGender(null);

        // then
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void findAllBornBeforeReturnsMatchingEntities() {
        // given
        Student student1 = prepareStudent("Firstname", "Lastname", Gender.FEMALE, "15.05.1982");
        create(student1);
        Student student2 = prepareStudent("Firstname2", "Lastname3", Gender.FEMALE, "01.01.1988");
        create(student2);
        Student student3 = prepareStudent("Firstname3", "Lastname", Gender.MALE, "31.12.1981");
        create(student3);
        Student student4 = prepareStudent("Firstname4", "Lastname4", Gender.MALE, null);
        create(student4);


        // then
        assertThat(dao.findAllBornBefore(1990).size(), is(3));
        assertThat(dao.findAllBornBefore(1990), hasItems(student1, student2, student3));
        assertThat(dao.findAllBornBefore(1983).size(), is(2));
        assertThat(dao.findAllBornBefore(1983), hasItems(student1, student3));
        assertThat(dao.findAllBornBefore(1988).size(), is(2));
        assertThat(dao.findAllBornBefore(1988), hasItems(student1, student3));
        assertThat(dao.findAllBornBefore(1970).isEmpty(), is(true));

    }

    // </editor-fold>













}
