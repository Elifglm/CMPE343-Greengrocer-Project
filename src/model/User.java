package model;

/**
 * User class for backward compatibility.
 * 
 * INHERITANCE: Extends Person (which extends Entity)
 * ENCAPSULATION: Uses inherited fields from Person
 * POLYMORPHISM: Inherits polymorphic behavior from Person
 * 
 * NOTE: This class is kept for backward compatibility.
 * New code should use Customer, Carrier, or Owner directly.
 */
public class User extends Person {

    // Role field for backward compatibility
    private String role;

    // Constructors
    public User() {
        super();
    }

    // Backward compatible constructor
    public User(int id, String username, String role, String address, String phone) {
        super(id, username, address, phone);
        this.role = role;
    }

    // ENCAPSULATION: Getter for role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // POLYMORPHISM: Override from Person
    @Override
    public String getDashboardView() {
        if (role == null)
            return "/view/customer.fxml";

        return switch (role.toLowerCase()) {
            case "carrier" -> "/view/carrier.fxml";
            case "owner" -> "/view/owner.fxml";
            default -> "/view/customer.fxml";
        };
    }
}