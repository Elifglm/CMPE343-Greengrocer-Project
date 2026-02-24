package dao;

import java.util.List;

/**
 * Generic DAO interface defining CRUD operations.
 * 
 * POLYMORPHISM: All DAO classes implement this interface
 * This allows different DAOs to be used interchangeably through this interface
 * 
 * @param <T> Entity type
 */
public interface BaseDAO<T> {

    /**
     * Find entity by ID
     * 
     * @param id Entity ID
     * @return Entity or null if not found
     */
    T findById(int id);

    /**
     * Find all entities
     * 
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Save a new entity
     * 
     * @param entity Entity to save
     * @return true if successful
     */
    boolean save(T entity);

    /**
     * Update existing entity
     * 
     * @param entity Entity to update
     * @return true if successful
     */
    boolean update(T entity);

    /**
     * Delete entity by ID
     * 
     * @param id Entity ID
     * @return true if successful
     */
    boolean delete(int id);
}
