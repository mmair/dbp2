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

import static org.hamcrest.CoreMatchers.*;
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
        // falls refresh nicht verwendet wird (oder ALLE caches geleert werden),
        // müssen Referenzen im Speicher verwaltet werden.
        owner.setPet(bunny);

        // when (funktioniert auch ohne Verwendung von cascade: alles selbst persistieren)
        manager.getTransaction().begin();
        manager.persist(owner);
        manager.persist(bunny);
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));

        Student ownerFromDb = manager.find(Student.class, owner.getId());
        assertThat(ownerFromDb.getPet(), is(notNullValue()));
    }

    @Test
    public void persistStudentWithCascadePersistsAlsoAnimalInDatabase() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");

        Student owner = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        bunny.setOwner(owner);
        // falls refresh nicht verwendet wird (oder ALLE caches geleert werden),
        // müssen Referenzen im Speicher verwaltet werden.
        // -> für das cascade persist brauchen wir das in dem Fall sowieso
        owner.setPet(bunny);

        // when
        manager.getTransaction().begin();
        manager.persist(owner);
        // bei Verwendung von cascade (persist) braucht man das Animal nicht selbst persistieren.
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));

        Student ownerFromDb = manager.find(Student.class, owner.getId());
        assertThat(ownerFromDb.getPet(), is(notNullValue()));
        assertThat(ownerFromDb.getPet().getName(), is(bunny.getName()));
    }


    @Test
    public void refreshClosesReferencesNotHandledInMemory() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");

        Student owner = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        bunny.setOwner(owner);
        // absichtlich kein owner.setPet, stattdessen verwenden wir dann refresh

        // when
        manager.getTransaction().begin();
        // nachdem owner.setPet nicht verwendet wird, müssen wir bunny selbst persistieren.
        manager.persist(bunny);
        manager.persist(owner);
        manager.getTransaction().commit();

        // löscht den L1 Cache (= PersistenceUnit), detached alle managed entities
        // möglicherweise wird das Objekt aber noch weiter "unten" trotzdem gecached...
        manager.clear();

        // um wirklich ALLE Caches zu leeren:
        // -> dann kann man sich in diesem Fall auch das refresh sparen
        // -> für uns besser: refresh, nicht evictALl
//        manager.getEntityManagerFactory().getCache().evictAll();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));

        Student ownerFromDb = manager.find(Student.class, owner.getId());

        // ohne refresh (oder Cache komplett leeren) wird die Relation nicht geschlossen
        assertThat(ownerFromDb.getPet(), is(nullValue()));

        // when: mit refresh wird die Relation aber geschlossen
        manager.refresh(ownerFromDb);

        assertThat(ownerFromDb.getPet(), is(notNullValue()));
        assertThat(ownerFromDb.getPet().getName(), is(bunny.getName()));
    }












}
