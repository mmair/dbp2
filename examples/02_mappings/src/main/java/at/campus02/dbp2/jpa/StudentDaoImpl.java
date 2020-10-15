package at.campus02.dbp2.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
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
        TypedQuery<Student> query = manager.createQuery(
                "select s from Student s",
                Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllByLastname(String lastname) {
        if (lastname == null)
            return findAll();

        String queryString = "select s from Student s where upper(s.lastName) = upper(:lastname)";
        TypedQuery<Student> query = manager.createQuery(queryString, Student.class);
        query.setParameter("lastname", lastname);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllBornBefore(int year) {
        LocalDate firstDayOfYear = LocalDate.of(year, Month.JANUARY, 1);
        String queryString = "select s from Student s where s.birthday < :firstDayOfYear";
        return manager.createQuery(queryString, Student.class)
                .setParameter("firstDayOfYear", firstDayOfYear)
                .getResultList();
    }

    @Override
    public List<Student> findAllByGender(Gender gender) {
        if (gender == null)
            return Collections.emptyList();

        TypedQuery<Student> query = manager.createNamedQuery("Student.findAllByGender", Student.class);
        query.setParameter("gender", gender);
        return query.getResultList();
    }

    @Override
    public void close() {
        if (manager != null && manager.isOpen())
            manager.close();
    }
}
