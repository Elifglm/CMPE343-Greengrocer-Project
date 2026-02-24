package model;

/**
 * Customer user type.
 * 
 * INHERITANCE: Extends Person
 * POLYMORPHISM: Overrides abstract methods getRole() and getDashboardView()
 */
public class Customer extends Person {

    // Constructors
    public Customer() {
        super();
    }

    public Customer(int id, String username, String address, String phone) {
        super(id, username, address, phone);
    }

    public Customer(String username, String password, String address, String phone) {
        super();
        setUsername(username);
        setPassword(password);
        setAddress(address);
        setPhone(phone);
    }

    // POLYMORPHISM: Override abstract methods from Person
    @Override
    public String getRole() {
        return "customer";
    }

    @Override
    public String getDashboardView() {
        return "/view/customer.fxml";
    }
}
