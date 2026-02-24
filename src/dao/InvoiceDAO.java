package dao;

import model.Invoice;
import util.DBUtil;

import java.sql.*;

/**
 * DAO for Invoice operations.
 * Handles PDF blob and CLOB content storage.
 * 
 * INHERITANCE: Extends AbstractDAO<Invoice>
 */
public class InvoiceDAO extends AbstractDAO<Invoice> {

    private static final InvoiceDAO INSTANCE = new InvoiceDAO();

    public static InvoiceDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "Invoice";
    }

    @Override
    protected String getIdColumnName() {
        return "invoice_id";
    }

    @Override
    protected Invoice mapResultSetToEntity(ResultSet rs) throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("invoice_id"));
        invoice.setOrderId(rs.getInt("order_id"));
        invoice.setInvoicePdf(rs.getBytes("invoice_pdf"));
        invoice.setInvoiceContent(rs.getString("invoice_content"));
        invoice.setTransactionLog(rs.getString("transaction_log"));
        invoice.setCreatedAt(rs.getTimestamp("created_at"));
        return invoice;
    }

    /**
     * Save invoice with PDF and CLOB content.
     */
    @Override
    public boolean save(Invoice invoice) {
        String sql = """
                INSERT INTO Invoice(order_id, invoice_pdf, invoice_content, transaction_log)
                VALUES(?, ?, ?, ?)
                """;

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getOrderId());

            if (invoice.getInvoicePdf() == null) {
                ps.setNull(2, Types.BLOB);
            } else {
                ps.setBytes(2, invoice.getInvoicePdf());
            }

            if (invoice.getInvoiceContent() == null) {
                ps.setNull(3, Types.CLOB);
            } else {
                ps.setString(3, invoice.getInvoiceContent());
            }

            if (invoice.getTransactionLog() == null) {
                ps.setNull(4, Types.CLOB);
            } else {
                ps.setString(4, invoice.getTransactionLog());
            }

            int affected = ps.executeUpdate();
            if (affected == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        invoice.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find invoice by order ID.
     */
    public Invoice findByOrderId(int orderId) {
        String sql = "SELECT * FROM Invoice WHERE order_id = ?";

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get invoice PDF bytes.
     */
    public static byte[] getInvoicePdf(int orderId) {
        Invoice invoice = INSTANCE.findByOrderId(orderId);
        return invoice != null ? invoice.getInvoicePdf() : null;
    }

    /**
     * Create and save invoice for an order.
     */
    public static Invoice createInvoice(int orderId, byte[] pdfBytes,
            String content, String transactionLog) {
        Invoice invoice = new Invoice();
        invoice.setOrderId(orderId);
        invoice.setInvoicePdf(pdfBytes);
        invoice.setInvoiceContent(content);
        invoice.setTransactionLog(transactionLog);

        if (INSTANCE.save(invoice)) {
            return invoice;
        }
        return null;
    }
}
