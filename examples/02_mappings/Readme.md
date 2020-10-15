## Beispiel zu Mappings und Queries mit JPA

Es soll die Geschäftslogik eines sehr einfachen Studentenverwaltungssystems implementiert werden. 

Die Klasse ``Student`` wird dabei als Entity in der Datenbank gespeichert und ist wie folgt definiert:

```java
class Student {
    // automatisch generierte technische ID, PrimaryKey
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    // enum mit 2 Werten: MALE und FEMALE
    private Gender gender; 
}
```

Die Geschäftslogik wird über ein DataAccessObject definiert:

```java
public interface StudentDao {

    boolean create(Student student);
    Student update(Student student);
    void delete(Student student);
    Student find(Integer id);

    List<Student> findAll();
    List<Student> findAllByLastname(String lastname);
    List<Student> findAllBornBefore(int year);
    List<Student> findAllByGender(Gender gender);

    void close();
}
```

Dabei gelten folgende Randbedingungen:

* **create**
	* Nach erfolgreichem Aufruf soll der Student in der Datenbank existieren und ``true`` zurückgegeben werden. 
	* Existiert der angegebene Student bereits (oder wird ``null`` übergeben), soll ``false`` zurückgegeben werden.
* **find**
	* Bei Angabe einer existierenden ID soll der ``Student`` aus der Datenbank gefunden werden.
	* Bei Angabe einer nicht existierenden ID (oder ``null``) soll ``null`` zurückgegeben werden. 
* **update**
	* Nach erfolgreichem Aufruf soll die Entity in der Datenbank die geänderten Werte übernommen haben und das geänderte Objekt zurückgegeben werden. 
	* Bei Angabe eines nicht existierenden Studenten (oder ``null``) soll ``null`` zurückgegeben werden.
* **delete**
	* Nach erfolgreichem Aufruf soll die Entity nicht mehr in der Datenbank existieren.
	* Bei Angabe eines nicht existierenden Studenten (oder ``null``) soll sich nichts ändern, aber auch keine Exception geworfen werden. 
* **findAll**
	* Es werden alle in der Datenbank existierenden Studenten gefunden.
* **findByLastname**
	* Es werden alle Studenten mit dem angegebenen Nachnamen gefunden.
	* Wird ``null``als Parameter übergeben, werden _alle_ Studenten aus der Datenbank zurückgegeben.
	* Die Suche soll "case-insensitive" funktionieren. 
* **findAllByGender**
	* Es werden alle Studenten mit dem angegebenen Geschlecht gefunden.
	* Wird ``null`` als Parameter übergeben, wird eine leere Liste zurückgegeben.
* **findAllBornBefore**
	* Es werden alle Studenten gefunden, deren ``birthday`` vor dem 1. Jänner des angegebenen Jahres liegt. 
* **close**
	* Die ``close``-Methode soll am Ende jedes Tests aufgerufen werden und sicherstellen, dass die verwendeten Ressourcen (``EntityManager``) aufgeräumt werden.

Die Implementierung der Logik erfolgt in einer Klasse ``StudentDaoImpl``.
Die Funktionalität wird mittels Unit-Tests geprüft, die alle angegebenen Randbedingungen überprüft (außer für ``close``, wird nur verwendet, nicht getestet). 

#### Empfohlene Vorgehensweise:

In Anlehnung an _Test Driven Development_ werden Schritt für Schritt zu den angegebenen Randbedingungen Tests spezifiziert und die entsprechende Business-Logik implementiert, bis die zugehörigen Tests erfolgreich laufen. 

