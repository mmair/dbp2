package at.campus02.dbp2.jpa.cars;

import java.time.LocalDate;
import java.util.Objects;

public class Ride {

    public Integer getId() {
        return null;
    }
    public Car getCar() {
        return null;
    }
    public void setCar(Car car) {}
    public LocalDate getOfferDate() {
        return null;
    }
    public void setOfferDate(LocalDate offerDate) {}
    public Customer getCustomer() {
        return null;
    }
    public void setCustomer(Customer customer) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return Objects.equals(getId(), ride.getId()) &&
                Objects.equals(getCar(), ride.getCar()) &&
                Objects.equals(getOfferDate(), ride.getOfferDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCar(), getOfferDate());
    }
}
