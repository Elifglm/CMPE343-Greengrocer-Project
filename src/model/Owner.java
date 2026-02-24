package model;

/**
 * Owner user type.
 * 
 * INHERITANCE: Extends Person
 * POLYMORPHISM: Overrides abstract methods getRole() and getDashboardView()
 */
public class Owner extends Person {

    // Constructors
    public Owner() {
        super();
    }

    public Owner(int id, String username, String address, String phone) {
        super(id, username, address, phone);
    }

    public Owner(String username, String password) {
        super();
        setUsername(username);
        setPassword(password);
    }

    // POLYMORPHISM: Override abstract methods from Person
    @Override
    public String getRole() {
        return "owner";
    }

    @Override
    public String getDashboardView() {
        return "/view/owner.fxml";
    }
}
