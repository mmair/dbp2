package at.campus02.dbp2.jpa.cars;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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


}
