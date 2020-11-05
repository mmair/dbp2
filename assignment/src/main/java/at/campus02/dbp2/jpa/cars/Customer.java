package at.campus02.dbp2.jpa.cars;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Customer {

    @Id
    private String email;
    private String lastname;
    private String firstname;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
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
