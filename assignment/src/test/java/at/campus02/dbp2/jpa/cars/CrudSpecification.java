package at.campus02.dbp2.jpa.cars;

import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class CrudSpecification extends BaseSpecification {

    // <editor-fold desc="Customer">

    // <editor-fold desc="create Customer">
    @Test
    public void crud01_createCustomerPersistsInDatabase() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);

        // when
        boolean success = carSharingDao.create(customer);
        String id = customer.getEmail();

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Customer.class, id), is(customer));
    }

    @Test
    public void crud02_createCustomerWithExistingEmailReturnsFalse() {
        // given
        Customer customer1 = prepareCustomer(lastname, firstname, email);
        Customer customer2 = prepareCustomer(alternativeLastname, firstname, email);
        createCustomer(customer1);
        entityManager.clear();

        // when
        boolean success = carSharingDao.create(customer2);

        // then
        assertThat(success, is(false));
        assertThat(entityManager.find(Customer.class, email).getLastname(), is(lastname));
    }

    @Test
    public void crud03_createNullAsCustomerReturnsFalse() {
        // when
        boolean success = carSharingDao.create((Customer) null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void crud04_createCustomerWithoutEmailReturnsFalse() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, null);

        // when
        boolean success = carSharingDao.create(customer);

        // then
        assertThat(success, is(false));
        assertThat(entityManager
                .createQuery("select c from Customer c where c.firstname=:firstname and c.lastname=:lastname",
                        Customer.class)
                .setParameter("firstname", firstname)
                .setParameter("lastname", lastname)
                .getResultList().isEmpty(), is(true));
    }
    // </editor-fold>

    // <editor-fold desc="delete Customer">
    @Test
    public void crud05_deleteCustomerRemovesFromDatabase() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);
        entityManager.clear();

        // when
        boolean success = carSharingDao.delete(customer);
        String id = customer.getEmail();

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Customer.class, id), is(nullValue()));
    }

    @Test
    public void crud07_deleteNotExistingCustomerReturnsFalse() {
        // given
        Customer customer1 = prepareCustomer(lastname, firstname, email);
        Customer customer2 = prepareCustomer(lastname, firstname, null);

        // when
        boolean success = carSharingDao.delete(customer1);
        String id = customer1.getEmail();

        // then
        assertThat(success, is(false));
        assertThat(entityManager.find(Customer.class, id), is(nullValue()));

        // and when
        success = carSharingDao.delete(customer2);

        // then
        assertThat(success, is(false));
    }

    // </editor-fold>

    // <editor-fold desc="read Customer">
    @Test
    public void crud08_readCustomerFindsItInDatabase() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);

        // when
        Customer found = carSharingDao.read(customer.getEmail());
        String id = customer.getEmail();

        // then
        assertThat(entityManager.find(Customer.class, id), is(found));
        assertThat(found, is(customer));
    }

    @Test
    public void crud09_readCustomerWithNullAsEmailReturnsNull() {
        // when
        Customer found = carSharingDao.read((String) null);

        // then
        assertThat(found, is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold desc="update Customer">
    @Test
    public void crud10_updateCustomerChangesValuesInDatabase() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);

        // when
        customer.setLastname(alternativeLastname);
        Customer updated = carSharingDao.update(customer);
        String id = customer.getEmail();

        // then
        assertThat(entityManager.find(Customer.class, id), is(updated));
        assertThat(updated, is(customer));
    }

    @Test
    public void crud11_updateNotExistingCustomerReturnsNull() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);

        // when
        Customer updated = carSharingDao.update(customer);

        // then
        assertThat(updated, is(nullValue()));
        assertThat(customer.getEmail(), is(email));
        assertThat(customer.getFirstname(), is(firstname));
        assertThat(customer.getLastname(), is(lastname));

        // and when
        customer = null;
        updated = carSharingDao.update(customer);
        assertThat(updated, is(nullValue()));
    }
    // </editor-fold>
    // </editor-fold>

    // <editor-fold desc="Car">

    // <editor-fold desc="create Car">
    @Test
    public void crud12_createCarPersistsCarInDatabase() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);

        // when
        boolean success = carSharingDao.create(car);
        Integer carId = car.getId();

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Car.class, carId), is(notNullValue()));
        assertThat(entityManager.find(Car.class, carId), is(car));
    }

    @Test
    public void crud13_createCarThatAlreadyExistsReturnsFalse() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        createCar(car);

        // when
        boolean success = carSharingDao.create(car);

        // then
        assertThat(success, is(false));
        assertThat(entityManager.find(Car.class, car.getId()), is(car));
    }

    @Test
    public void crud14_createNullAsCarReturnsFalse() {
        // when
        Car car = null;
        boolean success = carSharingDao.create(car);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void crud15_createCarPersistsAlsoRidesInDatabase() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride ride = prepareRide("12.12.2019");
        car.getRides().add(ride);

        // when
        boolean success = carSharingDao.create(car);
        Integer carId = car.getId();

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Car.class, carId).getRides().contains(ride), is(true));
        assertThat(
                entityManager.createQuery("select r from Ride r where r.car.id=:carId", Ride.class)
                        .setParameter("carId", carId).getResultList().size(), is(1));
    }
    // </editor-fold>

    // <editor-fold desc="delete Car">
    @Test
    public void crud16_deleteCarRemovesFromDatabase() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        createCar(car);
        Integer carId = car.getId();
        entityManager.clear();

        // when
        boolean success = carSharingDao.delete(car);

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Car.class, carId), is(nullValue()));
    }

    @Test
    public void crud17_deleteNotExistingCarReturnsFalse() {
        // given
        Car car1 = prepareCar(VehicleType.SMALL, graz);
        Car car2 = prepareCar(null, null);

        // when
        boolean success = carSharingDao.delete(car1);

        // then
        assertThat(success, is(false));

        // and when
        success = carSharingDao.delete(car2);

        // then
        assertThat(success, is(false));
    }
    // </editor-fold>

    // <editor-fold desc="read Car">
    @Test
    public void crud18_readCarFindsItInDatabase() {
        // given
        Car car = prepareCar(VehicleType.SUV, graz);
        createCar(car);
        entityManager.clear();

        // when
        Integer carId = car.getId();
        Car found = carSharingDao.read(carId);

        // then
        assertThat(entityManager.find(Car.class, carId), is(found));
        assertThat(found, is(car));
        assertThat(found.getLocation(), is(car.getLocation()));
        assertThat(found.getId(), is(car.getId()));
    }

    @Test
    public void crud19_readCarWithNullAsIdReturnsNull() {
        // when
        Integer carId = null;
        Car found = carSharingDao.read(carId);

        // then
        assertThat(found, is(nullValue()));
    }

    @Test
    public void crud20_readCarFindsAlsoRidesInDatabase() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride ride1 = prepareRide("12.11.2019");
        Ride ride2 = prepareRide("14.12.2019");
        car.getRides().add(ride1);
        car.getRides().add(ride2);
        createCar(car);

        // when
        Integer carId = car.getId();
        Car found = carSharingDao.read(carId);
        Car fromDb = entityManager.find(Car.class, carId);

        // then
        assertThat(found, is(fromDb));
        assertThat(found, is(car));
        assertThat(found.getRides().containsAll(Arrays.asList(ride1, ride2)), is(true));
        assertThat(fromDb.getRides().containsAll(Arrays.asList(ride1, ride2)), is(true));
    }
    // </editor-fold>

    // <editor-fold desc="update Car">
    @Test
    public void crud21_updateCarChangesValuesInDatabase() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        createCar(car);

        // when
        car.setLocation(wien);
        Car updated = carSharingDao.update(car);
        Car fromDb = entityManager.find(Car.class, car.getId());

        // then
        assertThat(updated, is(fromDb));
        assertThat(updated, is(car));
        assertThat(updated.getLocation(), is(wien));
    }
    @Test
    public void crud22_updateCarStoresAllRidesNotYetPersisted() {
        // given
        Car car = prepareCar(VehicleType.SMALL, "Graz");
        Ride ride1 = prepareRide("12.12.2019");
        Ride ride2 = prepareRide("14.12.2019");
        Ride ride3 = prepareRide("17.12.2019");
        car.getRides().add(ride1);
        car = createCar(car);

        // when
        car.getRides().add(ride2);
        car.getRides().add(ride3);
        Car updated = carSharingDao.update(car);

        // then
        assertThat(updated, is(car));
        // since rides in original car still have no ID we cannot easily check if they are contained
        // -> compare all values instead
        List<Ride> ridesFromDb = updated.getRides();
        assertThat(ridesFromDb.size(), is(3));
        assertThat( // check if all IDs are != null
                updated.getRides().stream().map(Ride::getId).collect(Collectors.toSet()).contains(null),
                is(false)
        );
        assertThat( // check if all rides have car assigned
                updated.getRides().stream().map(Ride::getCar).collect(Collectors.toSet()).contains(car),
                is(true)
        );
        assertThat( // check if all offer dates are present
                updated.getRides().stream().map(Ride::getOfferDate).collect(Collectors.toList())
                        .containsAll(Arrays.asList(ride1.getOfferDate(), ride2.getOfferDate(), ride3.getOfferDate())),
                is(true)
        );
    }
    @Test
    public void crud23_updateCarAlsoUpdatesRideValues() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride ride2 = prepareRide("14.12.2019");
        Ride ride1 = prepareRide("12.12.2019");
        Ride ride3 = prepareRide("17.12.2019");
        car.getRides().add(ride1);
        car.getRides().add(ride2);
        car.getRides().add(ride3);
        createCar(car);
        LocalDate newDate = LocalDate.of(2012, Month.JANUARY, 3);

        // when
        ride2.setOfferDate(newDate);
        Car updated = carSharingDao.update(car);

        // then
        assertThat(updated, is(car));
        assertThat(updated.getRides().contains(ride2), is(true));
        assertThat(entityManager.find(Ride.class, ride2.getId()).getOfferDate(), is(newDate));
    }
    // </editor-fold>
    // </editor-fold>

    // <editor-fold desc="tricky">
    @Test
    public void crud24_tricky_deleteCustomerAlsoRemovesReferencesFromRides() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride occupied = prepareRide("12.12.2019");
        occupied.setCustomer(customer);
        car.getRides().add(occupied);
        occupied.setCar(car);
        createCar(car);
        entityManager.clear();

        // when
        boolean success = carSharingDao.delete(customer);

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Ride.class, occupied.getId()), is(not(nullValue())));
        assertThat(entityManager.find(Ride.class, occupied.getId()).getCustomer(), is(nullValue()));
    }

    @Test
    public void crud25_tricky_deleteCarAlsoDeletesRidesInDatabase() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride ride = prepareRide("12.12.2019");
        car.getRides().add(ride);
        ride.setCar(car);
        createCar(car);
        entityManager.clear();

        // when
        boolean success = carSharingDao.delete(car);

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Ride.class, ride.getId()), is(nullValue()));
    }

    @Test
    public void crud26_tricky_updateNotExistingCarReturnsNull() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);

        // when
        Car updated = carSharingDao.update(car);

        // then
        assertThat(updated, is(nullValue()));

        // and when
        car = null;
        updated = carSharingDao.update(car);
        assertThat(updated, is(nullValue()));
    }

    @Test
    public void crud27_tricky_updateCarWithTheSameRideTwiceEndsUpHavingThatRideOnlyOnceInList() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride ride1 = prepareRide("12.12.2019");
        car.getRides().add(ride1);
        createCar(car);

        // when
        car.getRides().add(ride1);
        Car updated = carSharingDao.update(car);

        // then
        assertThat(updated, is(car));
        assertThat(updated.getRides().size(), is(1));
        assertThat(updated.getRides().contains(ride1), is(true));
        assertThat(ride1.getId(), is(not(nullValue())));
        assertThat(ride1.getCar(), is(car));
    }

    @Test
    public void crud28_tricky_updateCarDeletesAllRidesNoLongerAssigned() {
        // given
        Car car = prepareCar(VehicleType.SMALL, graz);
        Ride ride1 = prepareRide("12.12.2019");
        Ride ride2 = prepareRide("14.12.2019");
        Ride ride3 = prepareRide("17.12.2019");
        car.getRides().add(ride1);
        car.getRides().add(ride2);
        car.getRides().add(ride3);
        carSharingDao.create(car);

        // when
        car.getRides().remove(ride1);
        car.getRides().remove(ride3);
        Car updated = carSharingDao.update(car);

        // then
        assertThat(updated, is(car));
        assertThat(updated.getRides().size(), is(1));
        assertThat(updated.getRides().contains(ride2), is(true));
        assertThat(entityManager.find(Ride.class, ride1.getId()), is(nullValue()));
        assertThat(entityManager.find(Ride.class, ride2.getId()), is(ride2));
        assertThat(entityManager.find(Ride.class, ride3.getId()), is(nullValue()));
    }    
    // </editor-fold>

    // <editor-fold desc="close">
    @Test
    public void resources_closeClosesEntityManagerButDoesNotCloseFactory() throws IllegalAccessException {
        // given
        EntityManager em = getEntityManagerFromInterface(carSharingDao);

        // when
        carSharingDao.close();

        // then
        if (em != null) {
            assertThat(em.isOpen(), is(false));
        }
        assertThat(entityManagerFactory.isOpen(), is(true));
    }
    // </editor-fold>

}
