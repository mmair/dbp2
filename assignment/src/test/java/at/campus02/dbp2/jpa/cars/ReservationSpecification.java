package at.campus02.dbp2.jpa.cars;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ReservationSpecification extends BaseSpecification {

    // <editor-fold desc="findCars">
    @Test
    public void reserve01_findCarsByTypeOnlyReturnsEmptyList() {
        // given
        setUpTestData();

        // when
        List<Car> cars = carSharingDao.findCarsBy(VehicleType.FAMILY, null);

        // then
        assertThat(cars.size(), is(0));

        // and when
        cars = carSharingDao.findCarsBy(VehicleType.SMALL, null);
        assertThat(cars.size(), is(0));
    }
    @Test
    public void reserve02_findCarsByLocationOnlyReturnsEmptyList() {
        // given
        setUpTestData();

        // when
        List<Car> cars = carSharingDao.findCarsBy(null, graz);

        // then
        assertThat(cars.size(), is(0));
    }
    @Test
    public void reserve03_findCarsByNeitherTypeNorLocationReturnsEmptyList() {
        // given
        setUpTestData();

        // when
        List<Car> cars = carSharingDao.findCarsBy(null, null);

        // then
        assertThat(cars.size(), is(0));
    }

    @Test
    public void reserve04_findCarsByTypeAndLocationFindsMatchingCars() {
        // given
        setUpTestData();

        // when
        List<Car> cars = carSharingDao.findCarsBy(VehicleType.SMALL, graz);

        // then
        assertThat(cars.size(), is(1));
        assertThat(cars.contains(car1), is(true));

        // and when
        cars = carSharingDao.findCarsBy(VehicleType.FAMILY, wien);
        assertThat(cars.size(), is(1));
        assertThat(cars.contains(car3), is(true));
    }

    // </editor-fold>
    // <editor-fold desc="findCustomers">
    @Test
    public void reserve05_findCustomerByLastnameFindsMatchingCustomers() {
        // given
        setUpTestData();

        // when
        List<Customer> customers = carSharingDao.findCustomersBy("Huber", null);

        // then
        assertThat(customers.size(), is(2));
        assertThat(customers.contains(customer1), is(true));
        assertThat(customers.contains(customer2), is(true));
    }
    @Test
    public void reserve06_findCustomerByFirstnameFindsMatchingCustomers() {
        // given
        setUpTestData();

        // when
        List<Customer> customers = carSharingDao.findCustomersBy(null, "Konrad");

        // then
        assertThat(customers.size(), is(1));
        assertThat(customers.contains(customer3), is(true));
    }
    @Test
    public void reserve07_findCustomerByLastnameAndFirstnameFindsMatchingCustomers() {
        // given
        setUpTestData();

        // when
        List<Customer> customers = carSharingDao.findCustomersBy("Huber", "Hansi");

        // then
        assertThat(customers.size(), is(1));
        assertThat(customers.contains(customer1), is(true));
    }
    // </editor-fold>
    // <editor-fold desc="findAvailableRidesIn">
    @Test
    public void reserve08_findAvailableRidesInFindsAvailableRidesOfAllHotelsWithThatLocation() {
        // given
        setUpTestData();

        // when
        List<Ride> rides = carSharingDao.findAvailableRides(graz);

        // then
        assertThat(rides.size(), is(availableRidesInGraz.size()));
        assertThat(rides.containsAll(availableRidesInGraz), is(true));

        // and when
        rides = carSharingDao.findAvailableRides(wien);

        // then
        assertThat(rides.size(), is(availableRidesInWien.size()));
        assertThat(rides.containsAll(availableRidesInWien), is(true));
    }
    @Test
    public void reserve09_findAvailableRidesInUnknownLocationReturnsEmptyList() {
        // given
        setUpTestData();

        // when
        List<Ride> rides = carSharingDao.findAvailableRides("New York");

        // then
        assertThat(rides.isEmpty(), is(true));
    }
    // </editor-fold>

    // <editor-fold desc="findRidesReservedFor">
    @Test
    public void reserve10_findRidesReservedForFindsRidesReservedByCustomer() {
        // given
        setUpTestData();

        // when
        List<Ride> rides = carSharingDao.findRidesReservedFor(customer1);

        // then
        assertThat(rides.size(), is(ridesReservedByCustomer1.size()));
        assertThat(rides.containsAll(ridesReservedByCustomer1), is(true));

        // and when
        rides = carSharingDao.findRidesReservedFor(customer2);

        // then
        assertThat(rides.size(), is(ridesReservedByCustomer2.size()));
        assertThat(rides.containsAll(ridesReservedByCustomer2), is(true));
    }
    @Test
    public void reserve11_findRidesReservedForUnknownCustomerReturnsEmptyList() {
        // given
        setUpTestData();

        // when
        List<Ride> rides = carSharingDao.findRidesReservedFor(null);

        // then
        assertThat(rides.isEmpty(), is(true));

        // when
        rides = carSharingDao.findRidesReservedFor(prepareCustomer("Lastname", "Firstname", "email"));

        // then
        assertThat(rides.isEmpty(), is(true));
    }
    // </editor-fold>

    // <editor-fold desc="reserve">
    @Test
    public void reserve12_reserveAssignsCustomerToRideAndReturnsTrue() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.reserve(ride2, customer3);

        // then
        assertThat(success, is(true));
        assertThat(ride2.getCustomer().getEmail(), is(customer3.getEmail()));
        assertThat(ride2.getCustomer().getLastname(), is(customer3.getLastname()));
        assertThat(ride2.getCustomer().getFirstname(), is(customer3.getFirstname()));
    }
    @Test
    public void reserve13_reserveAlreadyBookedRideReturnsFalse() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.reserve(ride1, customer3);

        // then
        assertThat(success, is(false));
        assertThat(ride1.getCustomer(), is(customer1));
    }
    @Test
    public void reserve14_reserveForUnknownCustomerReturnsFalse() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.reserve(ride2, prepareCustomer("Lastname", "Firstname", "email@email.com"));

        // then
        assertThat(success, is(false));
        assertThat(ride2.getCustomer(), is(nullValue()));

        // and when
        success = carSharingDao.reserve(ride2, null);

        // then
        assertThat(success, is(false));
    }
    @Test
    public void reserve15_reserveNullAsRideReturnsFalse() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.reserve(null, customer3);

        // then
        assertThat(success, is(false));

        // and when
        success = carSharingDao.reserve(null, null);

        // then
        assertThat(success, is(false));
    }
    @Test
    public void reserve16_reserveNotYetPersistedRideForCustomerReturnsFalse() {
        // given
        setUpTestData();
        Ride newRide = prepareRide("17.12.2020");
        newRide.setCar(car3);

        // when
        boolean success = carSharingDao.reserve(newRide, customer3);

        // then
        assertThat(success, is(false));
        assertThat(newRide.getCustomer(), is(nullValue()));
    }
    // </editor-fold>
    // <editor-fold desc="cancel">
    @Test
    public void reserve17_cancelAssignsNullAsCustomerToRideAndReturnsTrue() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.cancel(ride1, customer1);

        // then
        assertThat(success, is(true));
        assertThat(ride1.getCustomer(), is(nullValue()));
    }
    @Test
    public void reserve18_cancelAvailableRideReturnsFalse() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.cancel(ride2, customer3);

        // then
        assertThat(success, is(false));
        assertThat(ride2.getCustomer(), is(nullValue()));
    }
    @Test
    public void reserve19_cancelRideForUnknownCustomerReturnsFalse() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.cancel(ride1, prepareCustomer("Lastname", "Firstname", "email@email.com"));

        // then
        assertThat(success, is(false));
        assertThat(ride1.getCustomer(), is(customer1));

        // and when
        success = carSharingDao.cancel(ride2, null);

        // then
        assertThat(success, is(false));
    }
    @Test
    public void reserve20_cancelNullAsRideReturnsFalse() {
        // given
        setUpTestData();

        // when
        boolean success = carSharingDao.cancel(null, customer3);

        // then
        assertThat(success, is(false));

        // and when
        success = carSharingDao.reserve(null, null);

        // then
        assertThat(success, is(false));
    }
    @Test
    public void reserve21_cancelNotYetPersistedRideForCustomerReturnsFalse() {
        // given
        setUpTestData();
        Ride newRide = prepareRide("17.12.2019");
        newRide.setCar(car3);

        // when
        boolean success = carSharingDao.cancel(newRide, customer3);

        // then
        assertThat(success, is(false));
        assertThat(newRide.getCustomer(), is(nullValue()));
    }
    // </editor-fold>
    
    // <editor-fold desc="tricky">
    @Test
    public void reserve22_tricky_findCustomersByNeitherLastnameNorFirstnameReturnsAllCustomers() {
        // given
        setUpTestData();

        // when
        List<Customer> customers = carSharingDao.findCustomersBy(null, null);

        // then
        assertThat(customers.size(), is(3));
        assertThat(customers.contains(customer1), is(true));
        assertThat(customers.contains(customer2), is(true));
        assertThat(customers.contains(customer3), is(true));
    }
    @Test
    public void reserve23_tricky_findCarsBySearchesCaseInsensitiveForLocation() {
        // given
        setUpTestData();

        // when
        List<Car> cars = carSharingDao.findCarsBy(VehicleType.FAMILY, "graz");

        // then
        assertThat(cars.size(), is(1));
        assertThat(cars.contains(car2), is(true));
    }
    @Test
    public void reserve24_tricky_findCustomerSearchesCaseInsensitive() {
        // given
        setUpTestData();

        // when
        List<Customer> customers = carSharingDao.findCustomersBy("huber", "hansi");

        // then
        assertThat(customers.size(), is(1));
        assertThat(customers.contains(customer1), is(true));
    }
    @Test
    public void reserve25_tricky_findAvailableRidesWithParameterNullReturnsAllAvailableRides() {
        // given
        setUpTestData();

        // when
        List<Ride> rooms = carSharingDao.findAvailableRides(null);

        // then
        assertThat(rooms.isEmpty(), is(false));
    }
    @Test
    public void reserve26_tricky_findAvailableRidesSearchesCaseInsensitive() {
        // given
        setUpTestData();

        // when
        List<Ride> rooms = carSharingDao.findAvailableRides("graz");

        // then
        assertThat(rooms.size(), is(availableRidesInGraz.size()));
        assertThat(rooms.containsAll(availableRidesInGraz), is(true));

        // and when
        rooms = carSharingDao.findAvailableRides("wien");

        // then
        assertThat(rooms.size(), is(availableRidesInWien.size()));
        assertThat(rooms.containsAll(availableRidesInWien), is(true));
    }

    // </editor-fold>
    
    
}
