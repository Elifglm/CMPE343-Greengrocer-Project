package dao;

import model.Entity;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract DAO with common database operations.
 * 
 * INHERITANCE: Implements BaseDAO, extended by concrete DAOs
 * POLYMORPHISM: Template Method Pattern - abstract methods called by concrete
 * implementations
 * ENCAPSULATION: Protected utility methods
 * 
 * @param <T> Entity type (must extend Entity)
 */
public abstract class AbstractDAO<T extends Entity> implements BaseDAO<T> {

    // ENCAPSULATION: Protected helper method
    protected Connection getConnection() throws Exception {
        return DBUtil.getConnection();
    }

    // ========== ABSTRACT METHODS (POLYMORPHISM - Template Method Pattern)
    // ==========

    /**
     * Map ResultSet row to entity object.
     * Each subclass implements this for its specific entity type.
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws Exception;

    /**
     * Get the database table name for this entity.
     */
    protected abstract String getTableName();

    /**
     * Get the primary key column name.
     */
    protected abstract String getIdColumnName();

    // ========== CONCRETE IMPLEMENTATIONS (INHERITANCE) ==========

    /**
     * Find entity by ID using Template Method Pattern.
     * POLYMORPHISM: Calls abstract mapResultSetToEntity() which is implemented by
     * subclasses
     */
    @Override
    public T findById(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + "=?";

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs); // POLYMORPHISM: Calls subclass implementation
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find all entities.
     * POLYMORPHISM: Uses abstract methods implemented by subclasses
     */
    @Override
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToEntity(rs)); // POLYMORPHISM
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Default implementation - subclasses should override if needed.
     */
    @Override
    public boolean save(T entity) {
        throw new UnsupportedOperationException("save() not implemented for " + getClass().getSimpleName());
    }

    /**
     * Default implementation - subclasses should override if needed.
     */
    @Override
    public boolean update(T entity) {
        throw new UnsupportedOperationException("update() not implemented for " + getClass().getSimpleName());
    }

    /**
     * Delete entity by ID.
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + "=?";

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
