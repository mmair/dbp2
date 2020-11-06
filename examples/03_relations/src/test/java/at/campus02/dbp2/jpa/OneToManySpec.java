package at.campus02.dbp2.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
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


        Animal clownfish = new Animal();
        clownfish.setName("clownfish");
        Animal shark = new Animal();
        shark.setName("shark");

        Species fish = new Species();
        fish.setName("fish");

        clownfish.setSpecies(fish);
        shark.setSpecies(fish);
        fish.getAnimals().add(clownfish);
        fish.getAnimals().add(shark);

        manager.getTransaction().begin();
        manager.persist(mammals);
        manager.getTransaction().commit();

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

    @Test
    public void updateExample() {

        // !!!!
        // Dieser Testfall funktioniert nur korrekt, wenn orphanRemoval nicht auf true gesetzt ist (siehe Test unten)
        // !!!!

        // -------------------------------------------------------------------------------------------------------------
        // given
        // -------------------------------------------------------------------------------------------------------------
        Animal clownfish = new Animal();
        clownfish.setName("Nemo");
        Animal squirrel = new Animal();
        squirrel.setName("Squirrel");
        Species fish = new Species();
        fish.setName("Fish");

        // Referenzen verwalten
        clownfish.setSpecies(fish);
        // Fehler, den wir dann korrigieren wollen
        squirrel.setSpecies(fish);
        fish.getAnimals().add(squirrel);
        fish.getAnimals().add(clownfish);

        // -> Bild dazu: src/main/resources/update_example_values.png

        // Speichern
        manager.getTransaction().begin();
        manager.persist(fish);
        manager.getTransaction().commit();
        manager.clear();

        // -> Bild dazu: src/main/resources/update_example_persist.png

        // -------------------------------------------------------------------------------------------------------------
        // when: Fehler korrigieren / UNVOLLSTÄNDIGE VARIANTE
        // -------------------------------------------------------------------------------------------------------------
        manager.getTransaction().begin();
        fish.getAnimals().remove(squirrel);
        manager.merge(fish);
        manager.getTransaction().commit();

        manager.clear();
        manager.getEntityManagerFactory().getCache().evictAll();

        // -> "Squirrel" sollte noch in der DB vorhanden sein
        Animal squirrelFromDb = manager.find(Animal.class, squirrel.getName());
        assertThat(squirrelFromDb, is(notNullValue()));

        // -> Obwohl wir squirrel aus der Liste der Animals entfernt haben und fish mit der DB angeglichen haben,
        //    verweist das neu aus der DB gelesene squirrel immer noch auf fish als Species!
        assertThat(squirrelFromDb.getSpecies().getId(), is(fish.getId()));

        // -> Und auch wenn wir das fish-Objekt aus dem Speicher mit der DB abgleichen ("refresh"),
        //    ist squirrel wieder in der Liste enthalten
        Species mergedFish = manager.merge(fish);
        manager.refresh(mergedFish);
        assertThat(mergedFish.getAnimals().size(), is(2));

        // -> Bild dazu: src/main/resources/update_example_wrong.png


        // Damit die Relation auch tatsächlich aufgelöst wird (und nicht nur in java im Speicher),
        // muss man auf dem Animal setSpecies(null) aufrufen.

        // -------------------------------------------------------------------------------------------------------------
        // when: neuer Versuch / KORREKTE VARIANTE
        // -------------------------------------------------------------------------------------------------------------
        manager.getTransaction().begin();
        fish.getAnimals().remove(squirrel);
        manager.merge(fish);
        // das ist anders als beim ersten Versuch
        squirrelFromDb.setSpecies(null);
        manager.getTransaction().commit();

        manager.clear();
        // braucht man nicht, aber wir gehen auf Nummer Sicher, damit kein Cache dazwischen sein kann...
        // -> normalerweise brauchen Sie das nicht verwenden
        manager.getEntityManagerFactory().getCache().evictAll();

        // -------------------------------------------------------------------------------------------------------------
        // then:
        // -------------------------------------------------------------------------------------------------------------

        // -> "Squirrel" sollte noch in der DB vorhanden sein
        Animal squirrelAgainFromDb = manager.find(Animal.class, squirrel.getName());
        assertThat(squirrelAgainFromDb, is(notNullValue()));

        // -> Diesmal wurde die Relation auch in der Datenbank aufgelöst, also sollte Squirrel auf keine Species
        //    mehr zeigen....
        assertThat(squirrelAgainFromDb.getSpecies(), is(nullValue()));

        // -> Und auch wenn wir das fish-Objekt aus dem Speicher mit der DB abgleichen ("refresh"),
        //    ist squirrel nicht mehr in der Liste enthalten
        Species mergedAgainFish = manager.merge(fish);
        manager.refresh(mergedAgainFish);
        assertThat(mergedAgainFish.getAnimals().size(), is(1));

        // -> "Nemo" ist aber in der Liste vorhanden
        assertThat(mergedAgainFish.getAnimals().get(0).getName(), is("Nemo"));

        // -> Bild dazu: src/main/resources/update_example_correct.png
    }

    @Test
    public void orphanRemovalTest() {

        // !!!!
        // Dieser Testfall funktioniert nur korrekt, wenn orphanRemoval auf true gesetzt ist
        // !!!!

        // -------------------------------------------------------------------------------------------------------------
        // given
        // -------------------------------------------------------------------------------------------------------------
        Animal clownfish = new Animal();
        clownfish.setName("Nemo");
        Animal squirrel = new Animal();
        squirrel.setName("Squirrel");

        Species fish = new Species();
        fish.setName("Fish");

        // Referenzen.....
        fish.getAnimals().add(clownfish);
        clownfish.setSpecies(fish);

        // Fehler, den wir dann korrigieren wollen
        fish.getAnimals().add(squirrel);
        squirrel.setSpecies(fish);

        // speichern
        manager.getTransaction().begin();
        manager.persist(fish);
        manager.getTransaction().commit();
        manager.clear();

        // -------------------------------------------------------------------------------------------------------------
        // when
        // -------------------------------------------------------------------------------------------------------------
        manager.getTransaction().begin();
        fish.getAnimals().remove(squirrel);
        // bei orphanRemoval braucht man zwar das merge,
        // muss aber dafür die entfernten Relationen nicht mehr händisch löschen.
        // -> dafür werden solche "Orphans" gelöscht!
        manager.merge(fish);
        manager.getTransaction().commit();

        manager.clear();
        manager.getEntityManagerFactory().getCache().evictAll();

        // -------------------------------------------------------------------------------------------------------------
        // then:
        // -------------------------------------------------------------------------------------------------------------

        // -> "Squirrel" sollte wegen orphanRemoval=true nicht mehr in der DB vorhanden sein
        Animal squirrelAgainFromDb = manager.find(Animal.class, squirrel.getName());
        assertThat(squirrelAgainFromDb, is(nullValue()));

        // -> Und auch wenn wir das fish-Objekt aus dem Speicher mit der DB abgleichen ("refresh"),
        //    ist squirrel natürlich nicht mehr in der Liste
        Species mergedAgainFish = manager.merge(fish);
        manager.refresh(mergedAgainFish);
        assertThat(mergedAgainFish.getAnimals().size(), is(1));

        // -> "Nemo" ist aber auch hier in der Liste vorhanden
        assertThat(mergedAgainFish.getAnimals().get(0).getName(), is("Nemo"));
    }

}
