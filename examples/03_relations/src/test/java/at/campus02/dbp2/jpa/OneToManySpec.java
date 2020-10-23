package at.campus02.dbp2.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class OneToManySpec {

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
    public void persistSpeciesWithCascadeAlsoPersistsAnimals() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");
        Animal cat = new Animal();
        cat.setName("Rudolf");
        Species mammals = new Species();
        mammals.setName("Mammals");

        // Referenzen im Speicher verwalten
        bunny.setSpecies(mammals);
        cat.setSpecies(mammals);
        // für Cascade persist müssen wir die animals auch der Species zuweisen
        mammals.getAnimals().add(bunny);
        mammals.getAnimals().add(cat);

        // when
        manager.getTransaction().begin();
        manager.persist(mammals);
        manager.getTransaction().commit();
        manager.clear();

        // then
        Species mammalsFromDb = manager.find(Species.class, mammals.getId());
        // vorsichtshalber...
        manager.refresh(mammalsFromDb);

        assertThat(mammalsFromDb.getAnimals().size(), is(2));
        assertThat(bunny.getSpecies().getId(), is(mammalsFromDb.getId()));
        assertThat(cat.getSpecies().getId(), is(mammalsFromDb.getId()));
    }




}
