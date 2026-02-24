package service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import dao.OrderDAO;
import model.OrderDetail;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service for generating PDF invoices using iText library.
 */
public class PdfInvoiceService {

    private static final double VAT_RATE = 0.18;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // PDF Colors
    private static final BaseColor HEADER_COLOR = new BaseColor(45, 122, 79); // #2D7A4F
    private static final BaseColor TEXT_COLOR = BaseColor.DARK_GRAY;
    private static final BaseColor LINE_COLOR = new BaseColor(229, 231, 235); // #E5E7EB

    /**
     * Generate PDF invoice for an order and save to file.
     */
    public static void generatePdfInvoice(int orderId, String outputPath) throws Exception {
        OrderDetail order = OrderDAO.getOrderDetail(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));

        document.open();

        // Add content
        addHeader(document);
        addInvoiceInfo(document, order);
        addCustomerInfo(document, order);
        addDeliveryInfo(document, order);
        addOrderItems(document, order);
        addFooter(document);

        document.close();
    }

    private static void addHeader(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, HEADER_COLOR);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_COLOR);

        Paragraph title = new Paragraph("GreenGrocer", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        Paragraph subtitle = new Paragraph("INVOICE", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        addLine(document);
    }

    private static void addInvoiceInfo(Document document, OrderDetail order) throws DocumentException {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_COLOR);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_COLOR);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        addInfoRow(table, "Invoice Date:", DATE_FORMAT.format(new Date()), labelFont, valueFont);
        addInfoRow(table, "Order ID:", "#" + order.getOrderId(), labelFont, valueFont);
        addInfoRow(table, "Order Date:",
                order.getCreatedAt() != null ? DATE_FORMAT.format(order.getCreatedAt()) : "N/A",
                labelFont, valueFont);

        document.add(table);
    }

    private static void addCustomerInfo(Document document, OrderDetail order) throws DocumentException {
        addSectionTitle(document, "CUSTOMER INFORMATION");

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_COLOR);

        document.add(new Paragraph("Customer: " + order.getCustomerUsername(), font));
        document.add(new Paragraph("Address: " +
                (order.getCustomerAddress() != null ? order.getCustomerAddress() : "Not provided"), font));
        document.add(new Paragraph("Phone: " +
                (order.getCustomerPhone() != null ? order.getCustomerPhone() : "Not provided"), font));

        document.add(Chunk.NEWLINE);
    }

    private static void addDeliveryInfo(Document document, OrderDetail order) throws DocumentException {
        addSectionTitle(document, "DELIVERY INFORMATION");

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_COLOR);

        document.add(new Paragraph("Requested Delivery: " +
                (order.getRequestedDelivery() != null ? DATE_FORMAT.format(order.getRequestedDelivery()) : "N/A"),
                font));
        document.add(new Paragraph("Status: " + order.getStatus(), font));

        if (order.getCarrierUsername() != null) {
            document.add(new Paragraph("Carrier: " + order.getCarrierUsername(), font));
        }
        if (order.getDeliveredAt() != null) {
            document.add(new Paragraph("Delivered At: " + DATE_FORMAT.format(order.getDeliveredAt()), font));
        }

        document.add(Chunk.NEWLINE);
    }

    private static void addOrderItems(Document document, OrderDetail order) throws DocumentException {
        addSectionTitle(document, "ORDER ITEMS");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1, 1.5f, 1.5f });
        table.setSpacingBefore(10);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_COLOR);

        // Header
        addTableHeader(table, "Product", headerFont);
        addTableHeader(table, "Kg", headerFont);
        addTableHeader(table, "Unit Price", headerFont);
        addTableHeader(table, "Total", headerFont);

        // Items
        double subtotal = 0;
        for (OrderDetail.OrderItem item : order.getItems()) {
            double lineTotal = item.getLineTotal();
            subtotal += lineTotal;

            addTableCell(table, truncate(item.getProductName(), 30), cellFont);
            addTableCell(table, String.format("%.2f", item.getKg()), cellFont);
            addTableCell(table, String.format("%.2f TL", item.getPriceAtTime()), cellFont);
            addTableCell(table, String.format("%.2f TL", lineTotal), cellFont);
        }

        document.add(table);

        // Totals
        double vat = subtotal * VAT_RATE;
        double total = subtotal + vat;

        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setSpacingBefore(15);

        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_COLOR);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, HEADER_COLOR);

        addTotalRow(totalsTable, "Subtotal:", String.format("%.2f TL", subtotal), totalFont);
        addTotalRow(totalsTable, "VAT (18%):", String.format("%.2f TL", vat), totalFont);
        addTotalRow(totalsTable, "TOTAL:", String.format("%.2f TL", total), boldFont);

        document.add(totalsTable);
    }

    private static void addFooter(Document document) throws DocumentException {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);

        Paragraph footer = new Paragraph("\nThank you for shopping with GreenGrocer!", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }

    // Helper methods
    private static void addLine(Document document) throws DocumentException {
        Paragraph line = new Paragraph("_________________________________________________________________");
        line.setFont(FontFactory.getFont(FontFactory.HELVETICA, 8, LINE_COLOR));
        document.add(line);
    }

    private static void addSectionTitle(Document document, String title) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, HEADER_COLOR);
        Paragraph section = new Paragraph(title, sectionFont);
        section.setSpacingBefore(10);
        section.setSpacingAfter(8);
        document.add(section);
    }

    private static void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        table.addCell(valueCell);
    }

    private static void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(LINE_COLOR);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private static void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(3);
        table.addCell(valueCell);
    }

    private static String truncate(String s, int maxLen) {
        if (s == null)
            return "";
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 3) + "...";
    }
}
