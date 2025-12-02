import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class RentalSystem {
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    
    private RentalSystem() {
    	loadData1();
    }
    private void loadData1() {
		// TODO Auto-generated method stub
		
	}
	public static RentalSystem getInstance() {
    	if (instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }
	
	private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }
	private void loadVehicles() {
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String plate = parts[0];
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[4]);
                    String type = parts[5];
                    
                    Vehicle vehicle = null;
                    if (type.equals("Car")) {
                        vehicle = new Car(make, model, year, 4); 
                    } else if (type.equals("Minibus")) {
                        vehicle = new Minibus(make, model, year, false);  
                    } else if (type.equals("PickupTruck")) {
                        vehicle = new PickupTruck(make, model, year, 1000.0, false); 
                    }
                    
                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate);
                        vehicle.setStatus(status);
                        vehicles.add(vehicle);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No existing vehicle data found.");
        }
    }
	private void loadCustomers() {
        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    customers.add(new Customer(id, name));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing customer data found.");
        }
    }
	private void loadRentalRecords() {
        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String plate = parts[0];
                    int customerId = Integer.parseInt(parts[1]);
                    LocalDate date = LocalDate.parse(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    String type = parts[4];
                    
                    Vehicle vehicle = findVehicleByPlate(plate);
                    Customer customer = findCustomerById(customerId);
                    
                    if (vehicle != null && customer != null) {
                        RentalRecord record = new RentalRecord(vehicle, customer, date, amount, type);
                        rentalHistory.addRecord(record);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No existing rental record data found.");
        }
    }

	
	
	
	private void  saveVehical(Vehicle vehicle) {
		try (PrintWriter out = new PrintWriter(new FileWriter("vehicles.txt",true))){
	        out.println(vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + 
            vehicle.getModel() + "," + vehicle.getYear() + "," + 
            vehicle.getStatus() + "," + getVehicleType(vehicle));
} catch (IOException e) {
 System.out.println("Error saving vehicle: " + e.getMessage());
}
	}
	
	
	
	 private void saveCustomer(Customer customer) {
	       
		 try (PrintWriter out = new PrintWriter(new FileWriter("customers.txt", true))) {
	            out.println(customer.getCustomerId() + "," + customer.getCustomerName());
	        } catch (IOException e) {
	            System.out.println("Error saving customer: " + e.getMessage());
	        }
	    }
	 
	 
	 private void saveRecord(RentalRecord record) {
	        try (PrintWriter out = new PrintWriter(new FileWriter("rental_records.txt", true))) {
	            
	        	out.println(record.getVehicle().getLicensePlate() + "," + 
	                       record.getCustomer().getCustomerId() + "," + 
	                       record.getRecordDate() + "," + 
	                       record.getTotalAmount() + "," + 
	                       record.getRecordType());
	        } catch (IOException e) {
	            System.out.println("Error saving record: " + e.getMessage());
	        }
	    }
	 private String getVehicleType(Vehicle vehicle) {
	        if (vehicle instanceof Car) return "Car";
	        if (vehicle instanceof Minibus) return "Minibus";
	        if (vehicle instanceof PickupTruck) return "PickupTruck";
	        return "Unknown";
	    }


	 public boolean addVehicle(Vehicle vehicle, String plate) {
		 vehicle.setLicensePlate(plate);
	        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
	            System.out.println("Vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
	            return false;
	        }
	        vehicles.add(vehicle);
	        saveVehical(vehicle);
	        return true;
	    }

	 public boolean addCustomer(Customer customer) {
	        if (findCustomerById(customer.getCustomerId()) != null) {
	            System.out.println("Customer with ID " + customer.getCustomerId() + " already exists.");
	            return false;
	        }
	        customers.add(customer);
	        saveCustomer(customer);
	        return true;
	    }

	 public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
	        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
	            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
	            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
	            rentalHistory.addRecord(record);
	            saveRecord(record);
	            System.out.println("Vehicle rented to " + customer.getCustomerName());
	            return true;
	        } else {
	            System.out.println("Vehicle is not available for renting.");
	            return false;
	        }
	    }

	 public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
	        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
	            vehicle.setStatus(Vehicle.VehicleStatus.Available);
	            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
	            rentalHistory.addRecord(record);
	            saveRecord(record);
	            System.out.println("Vehicle returned by " + customer.getCustomerName());
	            return true;
	        } else {
	            System.out.println("Vehicle is not rented.");
	            return false;
	        }
	    }
	

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
}