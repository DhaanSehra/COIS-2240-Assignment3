import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class VehicleRentalTest {
    
    @Test
    void testLicensePlate() {
        // Test valid plates
        Vehicle car1 = new Car("Toyota", "Corolla", 2020, 5);
        assertDoesNotThrow(() -> car1.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> car1.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> car1.setLicensePlate("ZZZ999"));
        
        // Test invalid plates
        Vehicle car2 = new Car("Honda", "Civic", 2021, 5);
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate("ZZZ99"));
    }
    
    @Test
    void testRentAndReturnVehicle() {
        Vehicle car = new Car("Toyota", "Corolla", 2020, 5);
        car.setLicensePlate("AAA111");
        Customer customer = new Customer(1, "John Doe");
        
        RentalSystem system = RentalSystem.getInstance();
        
        // Initially available
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
        
        // Rent vehicle
        assertTrue(system.rentVehicle(car, customer, java.time.LocalDate.now(), 100.0));
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());
        
        // Try to rent again (should fail)
        assertFalse(system.rentVehicle(car, customer, java.time.LocalDate.now(), 100.0));
        
        // Return vehicle
        assertTrue(system.returnVehicle(car, customer, java.time.LocalDate.now(), 0.0));
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
        
        // Try to return again (should fail)
        assertFalse(system.returnVehicle(car, customer, java.time.LocalDate.now(), 0.0));
    }
    
    @Test
    void testSingletonRentalSystem() throws Exception {
        // Check that constructor is private
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        int modifiers = constructor.getModifiers();
        assertEquals(Modifier.PRIVATE, modifiers);
        
        // Get instance
        RentalSystem instance1 = RentalSystem.getInstance();
        assertNotNull(instance1);
        
        // Check it's the same instance
        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance1, instance2);
    }
}