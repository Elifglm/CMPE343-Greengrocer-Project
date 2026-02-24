package model;

/**
 * Abstract base class for all entities in the system.
 * 
 * ENCAPSULATION: protected/private fields with getters/setters
 * INHERITANCE: All model classes extend this
 * POLYMORPHISM: Abstract methods overridden by subclasses
 */
public abstract class Entity {

    // ENCAPSULATION: protected field accessible only to subclasses
    protected int id;

    // Constructors
    public Entity() {
    }

    public Entity(int id) {
        this.id = id;
    }

    // ENCAPSULATION: Getter/Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // POLYMORPHISM: Abstract method - MUST be overridden by subclasses
    public abstract String getDisplayName();

    // POLYMORPHISM: Can be overridden, has default implementation
    public String getEntityType() {
        return this.getClass().getSimpleName();
    }

    // POLYMORPHISM: toString uses polymorphic getDisplayName()
    @Override
    public String toString() {
        return getEntityType() + ": " + getDisplayName();
    }
}
