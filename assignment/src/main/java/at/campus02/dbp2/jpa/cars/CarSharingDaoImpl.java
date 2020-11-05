package at.campus02.dbp2.jpa.cars;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class CarSharingDaoImpl implements CarSharingDao {

    private EntityManager manager;

    public CarSharingDaoImpl(EntityManagerFactory factory) {
        this.manager = factory.createEntityManager();
    }

    @Override
    public boolean create(Car car) {
        if (car == null || read(car.getId()) != null)
            return false;
        manager.getTransaction().begin();
        car.getRides().forEach(ride -> ride.setCar(car));
        manager.persist(car);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Car read(Integer id) {
        if (id == null)
            return null;
        return manager.find(Car.class, id);
    }

    @Override
    public Car update(Car car) {
        if (car == null || car.getId() == null || read(car.getId()) == null)
            return null;
        manager.getTransaction().begin();
        car.getRides().forEach(ride -> ride.setCar(car));
        Car merged = manager.merge(car);
        manager.getTransaction().commit();
        manager.refresh(merged);
        return merged;
    }

    @Override
    public boolean delete(Car car) {
        if (car == null || car.getId() == null || read(car.getId()) == null)
            return false;
        manager.getTransaction().begin();
        manager.remove(manager.merge(car));
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public boolean create(Customer customer) {
        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) != null)
            return false;
        manager.getTransaction().begin();
        manager.persist(customer);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Customer read(String email) {
        if (email == null)
            return null;
        return manager.find(Customer.class, email);
    }

    @Override
    public Customer update(Customer customer) {
        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) == null)
            return null;
        manager.getTransaction().begin();
        Customer merged = manager.merge(customer);
        manager.getTransaction().commit();
        return merged;
    }

    @Override
    public boolean delete(Customer customer) {
        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) == null)
            return false;
        manager.getTransaction().begin();
        findRidesReservedFor(customer).forEach(ride -> ride.setCustomer(null));
        manager.remove(manager.merge(customer));
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public List<Car> findCarsBy(VehicleType type, String location) {
        String queryString = "select c from Car c where 1 = 1";
        if (location != null)
            queryString += " and lower(c.location) = lower(:location)";
        if (type != null)
            queryString += " and c.type = :type";

        TypedQuery<Car> query = manager.createQuery(queryString, Car.class);
        if (location != null)
            query.setParameter("location", location);
        if (type != null)
            query.setParameter("type", type);

        return query.getResultList();
    }

    @Override
    public List<Customer> findCustomersBy(String lastname, String firstname) {
        String queryString = "select c from Customer c where 1 = 1";
        if (lastname != null)
            queryString += " and lower(c.lastname) = lower(:lastname)";
        if (firstname != null)
            queryString += " and lower(c.firstname) = lower(:firstname)";

        TypedQuery<Customer> query = manager.createQuery(queryString, Customer.class);
        if (lastname != null)
            query.setParameter("lastname", lastname);
        if (firstname != null)
            query.setParameter("firstname", firstname);

        return query.getResultList();
    }

    @Override
    public List<Ride> findAvailableRides(String location) {
        return location == null
                ? manager.createQuery("select r from Ride r", Ride.class).getResultList()
                : manager.createNamedQuery("Ride.findAvailableRides", Ride.class).setParameter("location", location).getResultList();
    }

    @Override
    public List<Ride> findRidesReservedFor(Customer customer) {
        return manager.createQuery(
                "select r from Ride r where r.customer = :customer",
                Ride.class)
                .setParameter("customer", customer)
                .getResultList();
    }

    @Override
    public boolean reserve(Ride ride, Customer customer) {
        if (ride == null || ride.getId() == null)
            return false;
        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) == null)
            return false;
        if (ride.getCustomer() != null)
            return false;
        manager.getTransaction().begin();
        ride.setCustomer(customer);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public boolean cancel(Ride ride, Customer customer) {
        if (ride == null || ride.getId() == null)
            return false;
        if (ride.getCustomer() == null)
            return false;
        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) == null)
            return false;
        manager.getTransaction().begin();
        ride.setCustomer(null);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public void close() {
        if (manager.isOpen())
            manager.close();
    }
}
