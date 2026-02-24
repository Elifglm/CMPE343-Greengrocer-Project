package model;

/**
 * Carrier user type.
 * 
 * INHERITANCE: Extends Person
 * POLYMORPHISM: Overrides abstract methods getRole() and getDashboardView()
 */
public class Carrier extends Person {

    // Constructors
    public Carrier() {
        super();
    }

    public Carrier(int id, String username, String address, String phone) {
        super(id, username, address, phone);
    }

    public Carrier(String username, String password, String address, String phone) {
        super();
        setUsername(username);
        setPassword(password);
        setAddress(address);
        setPhone(phone);
    }

    // POLYMORPHISM: Override abstract methods from Person
    @Override
    public String getRole() {
        return "carrier";
    }

    @Override
    public String getDashboardView() {
        return "/view/carrier.fxml";
    }
}
