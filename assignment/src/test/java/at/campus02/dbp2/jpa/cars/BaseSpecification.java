package at.campus02.dbp2.jpa.cars;

import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseSpecification {

    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;
    CarSharingDao carSharingDao;

    // <editor-fold desc="Common Testdata">
    final String email = "email.address@campus02.at";
    final String lastname = "Lastname";
    final String firstname = "Firstname";
    final String alternativeLastname = "Different-lastname";

    final String graz = "Graz";
    final String wien = "Wien";

    Car car1;
    Car car2;
    Car car3;

    Customer customer1;
    Customer customer2;
    Customer customer3;

    Ride ride1;
    Ride ride2;
    Ride ride3;
    Ride ride4;
    Ride ride5;
    Ride ride6;
    Ride ride7;
    Ride ride8;

    final Set<Ride> availableRides = new HashSet<>();
    final Set<Ride> availableRidesInGraz = new HashSet<>();
    final Set<Ride> availableRidesInWien = new HashSet<>();
    final Set<Ride> ridesReservedByCustomer1 = new HashSet<>();
    final Set<Ride> ridesReservedByCustomer2 = new HashSet<>();

    void setUpTestData() {

        entityManager.getTransaction().begin();

        // Cars...
        car1 = prepareCar(VehicleType.SMALL, graz);
        car2 = prepareCar(VehicleType.FAMILY, graz);
        car3 = prepareCar(VehicleType.SUV, wien);
        entityManager.persist(car1);
        entityManager.persist(car2);
        entityManager.persist(car3);

        // Customers...
        customer1 = prepareCustomer("Huber", "Hansi", "hansi.huber@email.com");
        customer2 = prepareCustomer("Huber", "Hannelore", "lore.huber@email.com");
        customer3 = prepareCustomer("Kluibnschedl", "Konrad", "konrad.k@email.com");
        entityManager.persist(customer1);
        entityManager.persist(customer2);
        entityManager.persist(customer3);

        // Rides...
        ride1 = prepareRide("12.12.2020");
        ride1.setCar(car1);
        car1.getRides().add(ride1);
        ride1.setCustomer(customer1); // occupied by customer 1
        entityManager.persist(ride1);

        ride2 = prepareRide("10.11.2020");
        ride2.setCar(car1);
        car1.getRides().add(ride2);
        entityManager.persist(ride2);

        ride3 = prepareRide("02.01.2021");
        ride3.setCar(car2);
        car2.getRides().add(ride3);
        ride3.setCustomer(customer1); // occupied by customer 1
        entityManager.persist(ride3);

        ride4 = prepareRide("10.01.2021");
        ride4.setCar(car2);
        car2.getRides().add(ride4);
        ride4.setCustomer(customer3); // occupied by customer 3
        entityManager.persist(ride4);

        ride5 = prepareRide("10.11.2020");
        ride5.setCar(car2);
        car2.getRides().add(ride5);
        ride5.setCustomer(customer2); // occupied by customer 2
        entityManager.persist(ride5);

        ride6 = prepareRide("26.12.2020");
        ride6.setCar(car2);
        car2.getRides().add(ride6);
        entityManager.persist(ride6);

        ride7 = prepareRide("27.11.2020");
        ride7.setCar(car3);
        car3.getRides().add(ride7);
        ride7.setCustomer(customer2); // occupied by customer 2
        entityManager.persist(ride7);

        ride8 = prepareRide("30.12.2020");
        ride8.setCar(car3);
        car3.getRides().add(ride8);
        entityManager.persist(ride8);

        entityManager.getTransaction().commit();

        // test sets
        availableRidesInGraz.addAll(Arrays.asList(ride2, ride6));
        availableRidesInWien.addAll(Collections.singletonList(ride8));
        availableRides.addAll(availableRidesInGraz);
        availableRides.addAll(availableRidesInWien);
        ridesReservedByCustomer1.addAll(Arrays.asList(ride1, ride3));
        ridesReservedByCustomer2.addAll(Arrays.asList(ride5, ride7));
    }    // </editor-fold>

    @Before
    public void before() {
        entityManagerFactory = Persistence.createEntityManagerFactory("assignment");
        entityManager = entityManagerFactory.createEntityManager();
        CarSharingDaoFactory daoFactory = new CarSharingDaoFactory(entityManagerFactory);
        carSharingDao = daoFactory.getDao();
    }

    @After
    public void after() {
        if (carSharingDao != null) {
            carSharingDao.close();
        }
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    static Customer prepareCustomer(String lastname, String firstname, String email) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setLastname(lastname);
        customer.setFirstname(firstname);
        return customer;
    }

    static Car prepareCar(VehicleType type, String location) {
        Car car = new Car();
        car.setType(type);
        car.setLocation(location);
        return car;
    }

    static Ride prepareRide(String offerDateString) {
        Ride ride = new Ride();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate offerDate = LocalDate.parse(offerDateString, formatter);
        ride.setOfferDate(offerDate);
        return ride;
    }

    protected Customer createCustomer(Customer customer) {
        entityManager.getTransaction().begin();
        entityManager.persist(customer);
        entityManager.getTransaction().commit();
        return customer;
    }

    protected Car createCar(Car car) {
        entityManager.getTransaction().begin();
        for (Ride ride : car.getRides()) {
            ride.setCar(car);
        }
        entityManager.persist(car);
        entityManager.getTransaction().commit();
        entityManager.refresh(car);
        return car;
    }


    <T> EntityManager getEntityManagerFromInterface(T manager) throws IllegalAccessException {
        EntityManager em = null;
        Field[] fields = manager.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(EntityManager.class)) {
                field.setAccessible(true);
                em = (EntityManager) field.get(manager);
                break;
            }
        }
        return em;
    }

}