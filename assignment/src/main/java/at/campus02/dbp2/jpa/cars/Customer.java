package at.campus02.dbp2.jpa.cars;

import java.util.Objects;

public class Customer {

    public String getEmail() {
        return null;
    }

    public void setEmail(String email) {
    }

    public String getLastname() {
        return null;
    }

    public void setLastname(String lastname) {
    }

    public String getFirstname() {
        return null;
    }

    public void setFirstname(String firstname) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(getEmail(), customer.getEmail()) &&
                Objects.equals(getLastname(), customer.getLastname()) &&
                Objects.equals(getFirstname(), customer.getFirstname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getLastname(), getFirstname());
    }
}
