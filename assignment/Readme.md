## Aufgabenstellung

Implementieren Sie ein Car-Sharing-System, mit dem kurzfristig zur Verfügung stehende 
Autos reserviert werden können. 

Anbieter können dort kurzfristig für ein bestimmtes Datum verfügbare Autos
einstellen, um eine möglichst gute Auslastung zu erreichen. 

Die zur Verfügung stehenden Autos sind jeweils in einer Stadt stationiert und 
können von registrierten Kunden für ein bestimmtes Datum gebucht werden.

Dazu sollen die Entitäten ``Car``, ``Customer`` und ``Ride`` mittels 
JPA in einer Datenbank verwaltet werden. 

``` java
public class Car {
    public Integer getId() {...}
    public VehicleType getType() {...}
    public void setType(VehicleType type) {...}
    public String getLocation() {...}
    public void setLocation(String location) {...}
    public List<Ride> getRides() {...}
}

public class Customer {
    public String getEmail() {...}
    public void setEmail(String email) {...}
    public String getLastname() {...}
    public void setLastname(String lastname) {...}
    public String getFirstname() {...}
    public void setFirstname(String firstname) {...}
}

public class Ride {
    public Integer getId() {...}
    public Car getCar() {...}
    public void setCar(Car car) {...}
    public Date getOfferDate() {...}
    public void setOfferDate(Date offerDate) {...}
    public Customer getCustomer() {...}
    public void setCustomer(Customer customer) {...}
}
```

Dabei ist der ``VehicleType`` wie folgt definiert:

``` java
public enum VehicleType {
    SMALL,
    SUV,
    FAMILY
}
```


Die "Geschäftslogik" der Applikation wird dabei durch das vorgegebene Interface ``CarSharingDao`` beschrieben:

``` java
public interface CarSharingDao {

    // CRUD: cars
    boolean create(Car car);
    Car read(Integer id);
    Car update(Car car);
    boolean delete(Car car);

    // CRUD: customers
    boolean create(Customer customer);
    Customer read(String email);
    Customer update(Customer customer);
    boolean delete(Customer customer);

    // Suche: cars
    List<Car> findCarsBy(VehicleType type, String location);

    // Suche: customers
    List<Customer> findCustomersBy(String lastname, String firstname);

    // Suche: rides
    List<Ride> findAvailableRides(String location);
    List<Ride> findRidesReservedFor(Customer customer);

    // Reservierung / Cancel
    boolean reserve(Ride ride, Customer customer);
    boolean cancel(Ride ride, Customer customer);

    // Resource management
    void close();

}
``` 

Für die Erzeugung der tatsächlichen Implementierungen dieser Interfaces ist die folgende Factory vorgegeben:

``` java
public class RoomBookingDaoFactory {
    public CarSharingDaoFactory(EntityManagerFactory factory) {...}
    public CarSharingDao getDao() {...}
}
```

Die Spezifikation (und Überprüfung) der Funktionalität erfolgt über vorgegebene Test-Klassen.

### Aufgaben

* Verwenden Sie dieses gradle-Projekt für die Implementierung der Aufgabe.
    * Öffnen Sie das Projekt in IntelliJ, indem Sie das ``build.gradle`` File als Projekt öffnen. 
    * Konfigurieren Sie die nötige _Persistence Unit_ für das Pojekt. Der Name der Persistence-Unit lautet dabei **"assignment"**.  
* Die Rümpfe der Entitäten finden Sie im Package ``at.campus02.dp2.jpa.cars``.
    * Stellen Sie die Entity-Klassen fertig und verwenden Sie dabei die nötigen JPA Mappings. 
    * **Achtung:** Die Methoden ``hashCode`` und ``equals`` sind bereits definiert und so implementiert, wie es für die Tests gebraucht wird. Ändern Sie diese Methoden nicht!
    * Definieren Sie auf zumindest einer der Entitäten zumindest eine ``NamedQuery``, welche bei der Implementierung von ``CarSharingDao`` verwendet wird.
* Das Interface ``CarSharingDao`` finden Sie ebenfalls im Package ``at.campus02.dp2.jpa.cars``.
    * Programmieren Sie eine Implementierung des ``CarSharingDao`` Interfaces. 
    * Stellen Sie die ``CarSharingDaoFactory`` so fertig, dass die Factory-Methode eine Instanz Ihrer Implementierung von ``CarSharingDao`` zurückgibt. 
* Die Spezifikation der Funktionalität ist über Testklassen vorgegeben. Sie finden diese Klassen im Source-Folder ``src/main/test`` im Package ``at.campus02.dp2.jpa.cars``.
    * Implementieren Sie ``CarSharingDao`` so, dass möglichst viele Unit-Tests  erfolgreich durchlaufen. 
    * Lassen Sie dann auch den ``IntegrationTest`` laufen und kontrollieren Sie in Ihrer Datenbank, ob die vorhandenen Objekte dem entsprechen, was am Ende von ``IntegrationTest`` als Kommentar beschrieben ist. 
    * Die vorgegebenen Testklassen sollen nicht geändert werden. Wenn Sie zusätzliche Tests schreiben, erstellen Sie dazu eine eigene Testklasse. 
* Kümmern Sie sich bei der Implementierung auch um die verwendeten Ressourcen (es gibt einen eigenen Test dafür). 
