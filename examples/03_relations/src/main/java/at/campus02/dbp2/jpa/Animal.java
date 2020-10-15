package at.campus02.dbp2.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Animal {

    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
