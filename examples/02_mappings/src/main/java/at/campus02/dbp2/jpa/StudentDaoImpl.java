package at.campus02.dbp2.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class StudentDaoImpl implements StudentDao {

    private EntityManager manager;

    public StudentDaoImpl(EntityManagerFactory factory) {
        manager = factory.createEntityManager();
    }

    @Override
    public boolean create(Student student) {
        if (student == null)
            return false;

        if(student.getId() != null) {
            return false;
        }

        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Student update(Student student) {
        if (student == null || find(student.getId()) == null) {
            return null;
        }
        manager.getTransaction().begin();
        Student updated = manager.merge(student);
        manager.getTransaction().commit();
        return updated;
    }

    @Override
    public void delete(Student student) {
        if (student == null || find(student.getId()) == null) {
            return;
        }
        manager.getTransaction().begin();
        Student merged = manager.merge(student);
        manager.remove(merged);
        manager.getTransaction().commit();
    }

    @Override
    public Student find(Integer id) {
        if (id == null)
            return null;
        return manager.find(Student.class, id);
    }

    @Override
    public List<Student> findAll() {
        return null;
    }

    @Override
    public List<Student> findAllByLastname(String lastname) {
        return null;
    }

    @Override
    public List<Student> findAllBornBefore(int year) {
        return null;
    }

    @Override
    public List<Student> findAllByGender(Gender gender) {
        return null;
    }

    @Override
    public void close() {

    }
}
