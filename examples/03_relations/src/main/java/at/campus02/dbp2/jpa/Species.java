package at.campus02.dbp2.jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Species {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    // Achtung: updateExample wird nur grün, wenn orphanRemoval = false ist.
    // Achtung: orphanRemovelTest wird nur grün, wenn orphanRemoval = true ist.
    @OneToMany(mappedBy = "species", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Animal> animals = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Animal> getAnimals() {
        return animals;
    }
}
