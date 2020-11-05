package at.campus02.dbp2.jpa.cars;

import javax.persistence.EntityManagerFactory;

public class CarSharingDaoFactory {

    private EntityManagerFactory factory;

    public CarSharingDaoFactory(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public CarSharingDao getDao() {
        return new CarSharingDaoImpl(factory);
    }

}
