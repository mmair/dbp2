package at.campus02.dbp2.jpa.cars;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@NamedQuery(name = "Ride.findAvailableRides", query = "select r from Ride r where lower(r.car.location) = lower(:location) and r.customer is null")
@Entity
public class Ride {

    @Id @GeneratedValue
    private Integer id;
    private LocalDate offerDate;

    @ManyToOne
    private Car car;
    @OneToOne
    private Customer customer;

    public Integer getId() {
        return id;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

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
