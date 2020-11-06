package at.campus02.dbp2.jpa.cars;

import java.util.List;

public interface CarSharingDao {

    // CRUD: cars
    /**
     * Erzeugt das übergebene Car in der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es ein Car mit derselben ID in der Datenbank passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird das Car in der Datenbank persistiert und <code>true</code> zurückgegeben.
     *   <li> Nach erfolgreichem Persistieren sind auch alle noch nicht vorhandenen Rides in der DB persistiert.
     * </ul>
     * @param car das abzuspeichernde Car
     * @return <code>true</code> falls das Car durch diese Aktion in der DB erzeugt wurde, ansonsten <code>false</code>.
     */
    boolean create(Car car);

    /**
     * Liest das Car mit der angegebenen ID aus der Datenbank.
     * Bei Übergabe von <code>null</code> wird <code>null</code> zurückgegeben.
     * @param id die ID des gewünschten Cars.
     * @return das gefundene Car oder <code>null</code>, falls das Car mit dieser ID nicht existiert.
     */
    Car read(Integer id);

    /**
     * Überträgt den aktuellen Zustand eines Cars in die Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Hat das übergebene Car keine ID passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Gibt es kein Car mit dieser ID in der Datenbank passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Ansonsten wird das Car in der Datenbank übernommen und zurückgegeben.
     *   <li> Rides, die nicht mehr zu diesem Car gehören, werden in der Datenbank ebenfalls gelöscht.
     *   <li> Rides, die zu diesem Car gehören, aber noch nicht in der Datenbank existieren, werden persistiert.
     * </ul>
     * @param car das zu speichernde Car.
     * @return der aktuelle Zustand dieses Cars oder <code>null</code>, falls das Car nicht übertragen wurde.
     */
    Car update(Car car);

    /**
     * Löscht das angegebene Car aus der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat das übergebene Car keine ID passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es kein Car mit dieser ID in der Datenbank passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird das Car aus der Datenbank gelöscht und <code>true</code> zurückgegeben.
     *   <li> Rides, die zu diesem Car gehörten, werden in der Datenbank ebenfalls gelöscht.
     * </ul>
     * @param car das zu löschende Car.
     * @return <code>true</code>, falls das Car durch diese Aktion gelöscht wurde, ansonsten <code>false</code>.
     */
    boolean delete(Car car);

    // CRUD: customers
    /**
     * Erzeugt den übergebenen Customer in der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es einen Customer mit derselben Email in der Datenbank passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird der Customer in der Datenbank persistiert und <code>true</code> zurückgegeben.
     * </ul>
     * @param customer der zu persistierende Customer
     * @return <code>true</code> falls der Customer durch diese Aktion in der DB erzeugt wurde, ansonsten <code>false</code>.
     */
    boolean create(Customer customer);

    /**
     * Liest den Customer mit der übergebenen Email aus der Datenbank.
     * Bei Übergabe von <code>null</code> wird <code>null</code> zurückgegeben.
     * @param email die Email des gewünschten Customers.
     * @return der gefundene Customer oder <code>null</code>, falls der Customer mit dieser Email nicht existiert.
     */
    Customer read(String email);

    /**
     * Überträgt den aktuellen Zustand eines Customers in die Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit dieser Email in der Datenbank passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Ansonsten wird der Customer in der Datenbank übernommen und zurückgegeben.
     * </ul>
     * @param customer der zu speichernde Customer.
     * @return der aktuelle Zustand dieses Customers oder <code>null</code>, falls der Customer nicht übertragen wurde.
     */
    Customer update(Customer customer);

    /**
     * Löscht den angegebenen Customer aus der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit derselben Email in der Datenbank passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird der Customer aus der Datenbank gelöscht und <code>true</code> zurückgegeben.
     * </ul>
     * @param customer der zu löschende Customer
     * @return <code>true</code>, falls der Customer durch diese Aktion gelöscht wurde, ansonsten <code>false</code>.
     */
    boolean delete(Customer customer);

    // Suche: cars
    /**
     * Findet alle Cars mit angegebenem VehicleType in der angegebenen Location.
     * <p>Bedingungen: <ul>
     *   <li> Wird als <code>type</code> <code>null</code> übergeben, wird eine leere Liste zurückgegeben.
     *   <li> Wird als <code>location</code> <code>null</code> übergeben, wird eine leere Liste zurückgegeben.
     *   <li> Wird sowohl für <code>type</code>, als auch <code>location</code> <code>null</code> übergeben, wird eine leere Liste zurückgegeben.
     * </ul>
     * @param type VehicleType des zu suchenden Cars.
     * @param location Location des zu suchenden Cars.
     * @return Liste der gefundenen Cars.
     */
    List<Car> findCarsBy(VehicleType type, String location);

    // Suche: customers
    /**
     * Findet alle Customer mit angegebenem Vor- bzw. Nachnamen.
     * <p>Bedingungen: <ul>
     *   <li> Wird als <code>lastname</code> <code>null</code> übergeben, werden alle Customer mit dem angegeben Vornamen
     *     zurückgegeben und der Nachname ignoriert.
     *   <li> Wird als <code>firstname</code> <code>null</code> übergeben, werden alle Customer mit dem angegeben Nachnamen
     *     zurückgegeben und der Vorname ignoriert.
     *   <li> Wird sowohl für <code>lastname</code>, als auch <code>firstname</code> <code>null</code> übergeben, werden alle
     *     Customer aus der Datenbank zurückgegeben.
     *   <li> Die Suche soll auch case-insensitive funktionieren, braucht aber nur exakte Matches finden.
     * </ul>
     * @param lastname Nachname des zu suchenden Customer (oder <code>null</code>, falls nicht nach Nachname gesucht werden soll).
     * @param firstname Vorname des zu suchenden Customer (oder <code>null</code>, falls nicht nach Vorname gesucht werden soll).
     * @return Liste der gefundenen Customers.
     */
    List<Customer> findCustomersBy(String lastname, String firstname);

    // Suche: rides
    /**
     * Findet alle noch nicht reservierten Rides von Cars, die an einem bestimmten Ort stationiert sind.
     * Wird als <code>location</code> <code>null</code> übergeben, werden alle freien Rides aller Cars zurückgegeben.
     * @param location der gewünschte Ort(Location) (oder <code>null</code>, falls nicht nach Ort
     *                    eingeschränkt werden soll).
     * @return Liste aller gefundenen verfügbaren Rides.
     */
    List<Ride> findAvailableRides(String location);
    /**
     * Findet alle Rides (von allen Cars), die von einem bestimmten Customer gebucht wurden.
     * @param customer der Customer, dessen reservierte Rides gefunden werden sollen.
     * @return Liste der gebuchten Rides des Customers
     */
    List<Ride> findRidesReservedFor(Customer customer);

    // Reservierung / Cancel
    /**
     * Reserviert einen Ride für einen Customer.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> für den Customer passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit dieser Email in der Datenbank, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Bei Parameter <code>null</code> für den Ride passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Ride keine Id, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ist der Ride bereits von einem Customer gebucht, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird der Ride dem Customer in der Datenbank zugewiesen.
     * </ul>
     * @param ride der zu buchende Ride
     * @param customer der Customer, für den der Ride reserviert werden soll.
     * @return <code>true</code>, falls die Reservierung erfolgreich war, ansonsten <code>false</code>
     */
    boolean reserve(Ride ride, Customer customer);

    /**
     * Storniert eine Reservierung für einen Customer.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> für den Customer passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit dieser Email in der Datenbank, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Bei Parameter <code>null</code> für den Ride, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Ride keine Id, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ist der Ride noch frei, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird die Buchung des Rides in der Datenbank aufgehoben.
     * </ul>
     * @param ride der zu stornierende Ride
     * @param customer der Customer, für den der Ride storniert werden soll.
     * @return <code>true</code>, falls die Stornierung erfolgreich war, ansonsten <code>false</code>
     */
    boolean cancel(Ride ride, Customer customer);

    // Resource management
    /**
     * Sollte alle Ressourcen schließen, die intern erzeugt und verwendet wurden - normalerweise den EntityManager.
     */
    void close();

}