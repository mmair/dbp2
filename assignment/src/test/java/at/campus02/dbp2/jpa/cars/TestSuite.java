package at.campus02.dbp2.jpa.cars;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses( {
        CrudSpecification.class,
        ReservationSpecification.class,
        IntegrationTest.class
} )
public class TestSuite {
}
