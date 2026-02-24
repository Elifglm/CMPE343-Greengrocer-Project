package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Utility to automatically load product images from images_market folder.
 */
public class ImageUtil {

    private static final String IMAGE_FOLDER = "images_market";

    /**
     * Load image for a product by name.
     * Searches for PNG, JPG, or JPEG files in images_market folder.
     * 
     * @param productName The product name (case-insensitive)
     * @return byte array of image data, or null if not found
     */
    public static byte[] loadProductImage(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return null;
        }

        String targetName = productName.trim();
        System.out.println("🔍 Searching for image: '" + targetName + "'");

        File folder = new File(IMAGE_FOLDER);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("❌ Image folder not found: " + IMAGE_FOLDER);
            return null;
        }

        File[] files = folder.listFiles();
        if (files == null)
            return null;

        // Try to find a match case-insensitively
        for (File file : files) {
            if (!file.isFile())
                continue;

            String fileName = file.getName();
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot == -1)
                continue;

            String nameWithoutExt = fileName.substring(0, lastDot);
            String ext = fileName.substring(lastDot).toLowerCase();

            // Check if extension is supported
            if (!ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".png")) {
                continue;
            }

            // Match criteria:
            // 1. Exact case-insensitive match (e.g., "arugula" matches "Arugula.jpg")
            // 2. Normalized match (no spaces, e.g., "red apple" matches "RedApple.jpg")
            String normalizedTarget = targetName.toLowerCase().replaceAll("\\s+", "");
            String normalizedFile = nameWithoutExt.toLowerCase().replaceAll("\\s+", "");

            if (nameWithoutExt.equalsIgnoreCase(targetName) || normalizedFile.equals(normalizedTarget)) {
                System.out.println("  ✅ Match found: " + fileName);
                return readImageFile(file);
            }
        }

        System.out.println("⚠ No image found for product: " + productName);
        return null;
    }

    /**
     * Read image file into byte array.
     */
    private static byte[] readImageFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] imageData = new byte[(int) file.length()];
            fis.read(imageData);
            System.out.println("✅ Loaded image: " + file.getName());
            return imageData;
        } catch (IOException e) {
            System.err.println("Error reading image file: " + file.getName());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if image exists for a product.
     */
    public static boolean imageExists(String productName) {
        return loadProductImage(productName) != null;
    }
}
