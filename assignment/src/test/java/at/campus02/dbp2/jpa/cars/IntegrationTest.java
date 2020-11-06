package at.campus02.dbp2.jpa.cars;

import org.junit.Test;

import static at.campus02.dbp2.jpa.cars.VehicleType.FAMILY;
import static at.campus02.dbp2.jpa.cars.VehicleType.SMALL;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegrationTest extends BaseSpecification {

    @Test
    public void integrationTest() {

        // given
        // "raw" data
        Customer customer1 = prepareCustomer("last1", "first1", "mail1");
        Customer customer2 = prepareCustomer("last2", "first2", "mail2");
        Customer customer3 = prepareCustomer("last3", "first3", "mail3");
        Customer customer4 = prepareCustomer("last4", "first4", "mail4");
        Customer customer5 = prepareCustomer("last5", "first5", "mail5");

        Car car1 = prepareCar(SMALL, graz);
        Car car2 = prepareCar(VehicleType.FAMILY, graz);
        Car car3 = prepareCar(VehicleType.SUV, wien);

        Ride ride1 = prepareRide("01.12.2019");
        Ride ride2 = prepareRide("02.12.2019");
        Ride ride3 = prepareRide("05.12.2019");
        Ride ride4 = prepareRide("12.12.2019");
        Ride ride5 = prepareRide("15.12.2019");
        Ride ride6 = prepareRide("16.12.2019");
        Ride ride7 = prepareRide("17.12.2019");

        // assign rides to cars
        car1.getRides().add(ride1);
        car1.getRides().add(ride2);
        car2.getRides().add(ride3);
        car2.getRides().add(ride4);
        car2.getRides().add(ride5);
        car3.getRides().add(ride6);
        car3.getRides().add(ride7);

        // when
        carSharingDao.create(customer1);
        carSharingDao.create(customer2);
        carSharingDao.create(customer3);
        carSharingDao.create(customer4);
        carSharingDao.create(customer5);

        carSharingDao.create(car1);
        carSharingDao.create(car2);
        carSharingDao.create(car3);

        carSharingDao.reserve(ride1, customer1);
        carSharingDao.reserve(ride2, customer2);
        carSharingDao.reserve(ride4, customer3);
        carSharingDao.reserve(ride5, customer3);

        // then
        assertThat(carSharingDao.findAvailableRides(graz), hasItems(ride3));
        assertThat(carSharingDao.findAvailableRides(wien), hasItems(ride6, ride7));
        assertThat(carSharingDao.findCarsBy(SMALL, graz), hasItems(car1));
        assertThat(carSharingDao.findCarsBy(VehicleType.FAMILY, graz), hasItems(car2));
        assertThat(carSharingDao.findCarsBy(VehicleType.SUV, wien), hasItems(car3));
        assertThat(carSharingDao.findRidesReservedFor(customer2), hasItems(ride2));
        assertThat(carSharingDao.findRidesReservedFor(customer3), hasItems(ride4, ride5));

        // and when ... data is removed
        carSharingDao.delete(car2);
        carSharingDao.delete(customer4);

        // then
        assertThat(carSharingDao.findAvailableRides(graz).isEmpty(), is(true));
        assertThat(carSharingDao.findAvailableRides(wien), hasItems(ride6, ride7));
        assertThat(carSharingDao.findCarsBy(SMALL, graz), hasItems(car1));
        assertThat(carSharingDao.findCarsBy(FAMILY, graz).size(), is(0));
        assertThat(carSharingDao.findCarsBy(VehicleType.SUV, wien), hasItems(car3));
        assertThat(carSharingDao.findRidesReservedFor(customer2), hasItems(ride2));
        assertThat(carSharingDao.findRidesReservedFor(customer3).isEmpty(), is(true));
        assertThat(carSharingDao.findRidesReservedFor(customer4).isEmpty(), is(true));
        assertThat(carSharingDao.findCustomersBy("last4", "first4").isEmpty(), is(true));

        // also check database, there should be
        // - 2 cars (SMALL in Graz, SUV in Wien)
        // - 4 customers
        // - 4 rides (2 occupied, 2 free)
    }


}
