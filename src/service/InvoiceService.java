package service;

import dao.InvoiceDAO;
import dao.OrderDAO;
import model.Invoice;
import model.OrderDetail;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service for generating PDF invoices.
 * 
 * Since we cannot add external PDF libraries (iText/PDFBox) without user
 * confirmation,
 * we generate a simple text-based invoice and store it as a "pseudo-PDF" (text
 * format).
 * The content is stored as CLOB and can be converted to actual PDF later.
 * 
 * For a full PDF implementation, add iText or Apache PDFBox to the project.
 */
public class InvoiceService {

    private static final double VAT_RATE = 0.18;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Generate invoice for an order.
     */
    public static Invoice generateInvoice(int orderId) {
        OrderDetail order = OrderDAO.getOrderDetail(orderId);
        if (order == null)
            return null;

        // Generate invoice content (text format)
        String invoiceContent = generateInvoiceContent(order);

        // Generate transaction log
        String transactionLog = generateTransactionLog(order);

        // Convert content to bytes (pseudo-PDF)
        byte[] pdfBytes = createPseudoPdf(invoiceContent);

        // Save to database
        return InvoiceDAO.createInvoice(orderId, pdfBytes, invoiceContent, transactionLog);
    }

    /**
     * Generate invoice content as text.
     */
    private static String generateInvoiceContent(OrderDetail order) {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("                         GreenGrocer\n");
        sb.append("                           INVOICE\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        sb.append("Invoice Date: ").append(DATE_FORMAT.format(new Date())).append("\n");
        sb.append("Order ID: #").append(order.getOrderId()).append("\n");
        sb.append("Order Date: ")
                .append(order.getCreatedAt() != null ? DATE_FORMAT.format(order.getCreatedAt()) : "N/A").append("\n\n");

        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("CUSTOMER INFORMATION\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("Customer: ").append(order.getCustomerUsername()).append("\n");
        sb.append("Address: ").append(order.getCustomerAddress() != null ? order.getCustomerAddress() : "Not provided")
                .append("\n");
        sb.append("Phone: ").append(order.getCustomerPhone() != null ? order.getCustomerPhone() : "Not provided")
                .append("\n\n");

        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("DELIVERY INFORMATION\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("Requested Delivery: ")
                .append(order.getRequestedDelivery() != null ? DATE_FORMAT.format(order.getRequestedDelivery()) : "N/A")
                .append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        if (order.getCarrierUsername() != null) {
            sb.append("Carrier: ").append(order.getCarrierUsername()).append("\n");
        }
        if (order.getDeliveredAt() != null) {
            sb.append("Delivered At: ").append(DATE_FORMAT.format(order.getDeliveredAt())).append("\n");
        }
        sb.append("\n");

        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("ORDER ITEMS\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-30s %8s %12s %12s\n", "Product", "Kg", "Unit Price", "Total"));
        sb.append("───────────────────────────────────────────────────────────────\n");

        double subtotal = 0;
        for (OrderDetail.OrderItem item : order.getItems()) {
            double lineTotal = item.getLineTotal();
            subtotal += lineTotal;
            sb.append(String.format("%-30s %8.2f %12.2f %12.2f\n",
                    truncate(item.getProductName(), 30),
                    item.getKg(),
                    item.getPriceAtTime(), // This is getEffectivePrice
                    lineTotal));
        }

        sb.append("───────────────────────────────────────────────────────────────\n");

        double vat = subtotal * VAT_RATE;
        double total = subtotal + vat;

        sb.append(String.format("%52s %12.2f\n", "Subtotal:", subtotal));
        sb.append(String.format("%52s %12.2f\n", "VAT (18%):", vat));
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append(String.format("%52s %12.2f\n", "TOTAL:", total));
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        sb.append("Thank you for shopping at GreenGrocer!\n");
        sb.append("For any questions, please contact us.\n\n");

        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("This invoice was automatically generated.\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    /**
     * Generate transaction log.
     */
    private static String generateTransactionLog(OrderDetail order) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("=== TRANSACTION LOG ===");
        pw.println("Generated: " + DATE_FORMAT.format(new Date()));
        pw.println("Order ID: " + order.getOrderId());
        pw.println("Customer: " + order.getCustomerUsername());
        pw.println();

        pw.println("--- ORDER DETAILS ---");
        pw.println("Status: " + order.getStatus());
        pw.println("Created: " + (order.getCreatedAt() != null ? DATE_FORMAT.format(order.getCreatedAt()) : "N/A"));
        pw.println("Requested Delivery: "
                + (order.getRequestedDelivery() != null ? DATE_FORMAT.format(order.getRequestedDelivery()) : "N/A"));
        pw.println();

        pw.println("--- ITEMS ---");
        for (OrderDetail.OrderItem item : order.getItems()) {
            pw.printf("Product ID: %d, Name: %s, Kg: %.2f, Price: %.2f (effective), Line Total: %.2f%n",
                    item.getProductId(),
                    item.getProductName(),
                    item.getKg(),
                    item.getPriceAtTime(),
                    item.getLineTotal());
        }
        pw.println();

        // Calculate totals
        double subtotal = order.getItems().stream()
                .mapToDouble(OrderDetail.OrderItem::getLineTotal)
                .sum();
        double vat = subtotal * VAT_RATE;

        pw.println("--- TOTALS ---");
        pw.printf("Subtotal: %.2f%n", subtotal);
        pw.printf("VAT (18%%): %.2f%n", vat);
        pw.printf("Total (DB): %.2f%n", order.getTotalVatIncluded());
        pw.println();

        if (order.getCarrierUsername() != null) {
            pw.println("--- DELIVERY ---");
            pw.println("Carrier: " + order.getCarrierUsername());
            pw.println("Delivered: "
                    + (order.getDeliveredAt() != null ? DATE_FORMAT.format(order.getDeliveredAt()) : "Not yet"));
        }

        if (order.getCancelledAt() != null) {
            pw.println("--- CANCELLATION ---");
            pw.println("Cancelled: " + DATE_FORMAT.format(order.getCancelledAt()));
            pw.println("Reason: " + order.getCancelReason());
        }

        pw.println();
        pw.println("=== END TRANSACTION LOG ===");

        return sw.toString();
    }

    /**
     * Create pseudo-PDF (text bytes with PDF header for identification).
     */
    private static byte[] createPseudoPdf(String content) {
        // Add a simple header to identify as pseudo-PDF
        String header = "%PDF-PSEUDO-1.0\n% GreenGrocer Invoice\n% Convert with PDF library for actual PDF\n\n";
        String fullContent = header + content;
        return fullContent.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Truncate string to max length.
     */
    private static String truncate(String s, int maxLen) {
        if (s == null)
            return "";
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    /**
     * Get invoice content as viewable text.
     */
    public static String getInvoiceText(int orderId) {
        Invoice invoice = InvoiceDAO.getInstance().findByOrderId(orderId);
        if (invoice != null && invoice.getInvoiceContent() != null) {
            return invoice.getInvoiceContent();
        }

        // Generate on the fly if not exists
        OrderDetail order = OrderDAO.getOrderDetail(orderId);
        if (order != null) {
            return generateInvoiceContent(order);
        }

        return "Invoice not available.";
    }
}
