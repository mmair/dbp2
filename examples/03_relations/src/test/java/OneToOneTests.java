import at.campus02.dbp2.relations.Animal;
import at.campus02.dbp2.relations.Student;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class OneToOneTests {

    @Test
    public void justATest() {
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistenceUnitName");
        EntityManager manager = factory.createEntityManager();
        Student student = new Student("Hansi");
        Animal animal = new Animal("Flipper");

        // when
        manager.getTransaction().begin();
        manager.persist(student);
        manager.persist(animal);
        manager.getTransaction().commit();
    }
}
