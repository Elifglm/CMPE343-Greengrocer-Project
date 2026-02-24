package model;

/**
 * Invoice entity for order invoices.
 * 
 * INHERITANCE: Extends Entity
 * Contains PDF blob and CLOB content for invoice and transaction log.
 */
public class Invoice extends Entity {

    private int orderId;
    private byte[] invoicePdf; // BLOB
    private String invoiceContent; // CLOB
    private String transactionLog; // CLOB
    private java.sql.Timestamp createdAt;

    public Invoice() {
        super();
    }

    public Invoice(int invoiceId, int orderId, byte[] invoicePdf,
            String invoiceContent, String transactionLog) {
        super(invoiceId);
        this.orderId = orderId;
        this.invoicePdf = invoicePdf;
        this.invoiceContent = invoiceContent;
        this.transactionLog = transactionLog;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public byte[] getInvoicePdf() {
        return invoicePdf;
    }

    public void setInvoicePdf(byte[] invoicePdf) {
        this.invoicePdf = invoicePdf;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getTransactionLog() {
        return transactionLog;
    }

    public void setTransactionLog(String transactionLog) {
        this.transactionLog = transactionLog;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getDisplayName() {
        return "Invoice #" + getId() + " for Order #" + orderId;
    }
}
