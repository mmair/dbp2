package at.campus02.dbp2.jpa;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OneToOneSpec {

    private EntityManagerFactory factory;
    private EntityManager manager;

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
        factory = Persistence.createEntityManagerFactory("relationsPersistenceUnit");
        manager = factory.createEntityManager();
    }

    @After
    public void tearDown() {
        if (manager.isOpen()) {
            manager.close();
        }
        if (factory.isOpen()) {
            factory.close();
        }
    }
    // </editor-fold>

    @Test
    public void persistAnimalAndOwnerStoresRelationInDatabase() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");

        Student owner = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        bunny.setOwner(owner);

        // when
        manager.getTransaction().begin();
//        manager.persist(owner);
        manager.persist(bunny);
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));
    }















}
