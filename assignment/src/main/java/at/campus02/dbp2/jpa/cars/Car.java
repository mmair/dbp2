package at.campus02.dbp2.jpa.cars;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Car {

    public Integer getId() {
        return null;
    }

    public VehicleType getType() {
        return null;
    }

    public void setType(VehicleType type) {
    }

    public String getLocation() {
        return null;
    }

    public void setLocation(String location) {
    }

    public List<Ride> getRides() {
        return Collections.emptyList();
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
