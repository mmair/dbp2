package at.campus02.dbp2.jpa.cars;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public class Car {

    @Id @GeneratedValue
    private Integer id;
    private VehicleType type;
    private String location;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Ride> getRides() {
        return rides;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(getId(), car.getId()) &&
                getType() == car.getType() &&
                Objects.equals(getLocation(), car.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getLocation());
    }
}
