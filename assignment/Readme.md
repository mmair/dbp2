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
public class CarSharingDaoFactory {
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

### Abgabe

Zippen Sie das fertiggestellte Projekt (den  gesamten "assignment" Folder) und geben Sie dem zip-File den Namen "IHRNAME.zip" (beispielsweise "mair.zip"). 
Das zip-File über Moodle abgegeben (im Ordner "Abgabe Assignment").

### Tipps zur Umsetzung

* Folgende Reihenfolge sollte Ihnen die Umsetzung erleichtern:

    1. Projekt öffnen (``build.gradle``) und die ``TestSuite`` laufen lassen. Zunächst werden alle Tests rot sein, aber Sie wissen, dass das Projekt kompiliert und die Libraries geladen werden. Verwenden Sie auch das JPA-Facet, wenn IntelliJ Sie darauf hinweist, das erleichtert das Schreiben der Queries. 
    2. ``persistence.xml`` fertigstellen. Am besten ``drop-and-create-tables`` verwenden, damit die Datenbank bei jedem Test neu befüllt wird. 
    3. Entities fertigstellen (``Car``, ``Ride``, ``Customer``).  
    4. Implementieren Sie das ``CarSharingDao`` Interface in einer eigenen Klasse im selben Package, erstmal nur die Rümpfe der Methoden (default-Implementierung).
    5. Stellen Sie die ``CarSharingDaoFactory`` so fertig, dass die Factory-Methode eine Instanz Ihrer Implementierung von ``RoomBookingDao`` zurückgibt.
    6. Lassen Sie wieder die ``TestSuite`` laufen. Einige der Tests sollten vorerst schon erfolgreich durchlaufen, wenn alle Schritte bisher korrekt umgesetzt wurden. Falls nicht, kontrollieren Sie, ob ``persistence.xml`` passen kann. 
    7. Beginnen Sie die Tests mit ``CrudSpecification``.
        * Am einfachsten arbeiten Sie die Tests der Reihe nach ab (wie im File vorhanden). 
        * Das Verhalten jeder Methode inklusive Randbedingungen ist als javadoc-Kommentar direkt beim ``CarSharingDao`` beschrieben. Für leichtere Lesbarkeit 
          können Sie sich über den gradle Task ``openJavaDocInBrowser`` eine html-Version erstellen und im Browser öffnen lassen.
        * Erweitern Sie Ihre Implementierung von ``CarSharingDao`` Schritt für Schritt, bis möglichst alle Unit-Tests erfolgreich durchlaufen. 
        * Lassen Sie zwischendurch immer wieder mal alle Tests aus ``Crud-Specification`` laufen, um sicherzustellen, dass Änderungen keine bereits implementierte Funktionalität kaputt machen.
        * Die etwas schwieriger zu erfüllenden Tests sind im Namen mit ``tricky`` markiert, diese können sie auch vorerst weglassen und am Ende umsetzen, wenn die Basisfunktionalität vorhanden ist. 
    8. Fahren Sie mit ``ReservationSpecification`` fort, bis auch dort möglichst alle Unit-Tests erfolgreich sind. 
    9. Sollten Sie die ``tricky`` Tests noch nicht umgesetzt haben, versuchen Sie, auch diese noch grün werden zu lassen. 
    11. Spätestens, wenn Sie mit dem Ergebnis zufrieden sind, lassen Sie auch den ``IntegrationTest`` laufen und kontrollieren Sie den Zustand Ihrer Datenbank, ob er dem entspricht, was als Kommentar am Ende von ``IntegrationTest`` beschrieben ist.  

* Ein paar zusätzliche Bemerkungen noch:
    * Sie können in JPQL auch ``like``, ``lower`` usw. verwenden. 
    * Die Relationen in der Datenbank werden von JPA verwaltet, im Speicher müssen Sie sich darum kümmern. Mit zB. einem ``refresh`` (``EntityManager``) lesen Sie aber die Werte aus der Datenbank neu ein. 
    * ``CascadeType`` für Relationen überlegen. 
    * Falls bei einer Such-Methode bei unterschiedlichen Parameter-Kombinationen Resultate erwartet werden, die mit einer 
      einzelnen Query nur schwer umzusetzen sind, können Sie natürlich auch für jeden Fall eine eigene Query absetzen.  
