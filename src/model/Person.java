package model;

/**
 * Abstract class representing a person in the system.
 * 
 * INHERITANCE: Extends Entity, extended by Customer, Carrier, Owner
 * POLYMORPHISM: Abstract methods for role-specific behavior
 * ENCAPSULATION: Private fields with public getters/setters
 */
public abstract class Person extends Entity {

    // ENCAPSULATION: Private fields
    private String username;
    private String password;
    private String address;
    private String phone;

    // Constructors
    public Person() {
        super();
    }

    public Person(int id, String username, String address, String phone) {
        super(id);
        this.username = username;
        this.address = address;
        this.phone = phone;
    }

    // ENCAPSULATION: Getters/Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // POLYMORPHISM: Abstract methods - subclasses MUST implement
    public abstract String getRole();

    public abstract String getDashboardView(); // Returns FXML path

    // POLYMORPHISM: Implementation of Entity's abstract method
    @Override
    public String getDisplayName() {
        return username + " (" + getRole() + ")";
    }
}
