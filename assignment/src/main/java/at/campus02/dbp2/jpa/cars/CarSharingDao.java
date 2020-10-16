package at.campus02.dbp2.jpa.cars;

import java.util.List;

public interface CarSharingDao {

    // CRUD: cars
    boolean create(Car car);
    Car read(Integer id);
    Car update(Car car);
    boolean delete(Car car);

    // CRUD: customers
    boolean create(Customer customer);
    Customer read(String email);
    Customer update(Customer customer);
    boolean delete(Customer customer);

    // Suche: cars
    List<Car> findCarsBy(VehicleType type, String location);

    // Suche: customers
    List<Customer> findCustomersBy(String lastname, String firstname);

    // Suche: rides
    List<Ride> findAvailableRides(String location);
    List<Ride> findRidesReservedFor(Customer customer);

    // Reservierung / Cancel
    boolean reserve(Ride ride, Customer customer);
    boolean cancel(Ride ride, Customer customer);

    // Resource management
    void close();

}