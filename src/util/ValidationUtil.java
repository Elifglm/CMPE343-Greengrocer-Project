package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validation utility for business rules.
 */
public class ValidationUtil {

    // Valid fruit names (100+ common fruits in English)
    private static final Set<String> VALID_FRUITS = new HashSet<>(Arrays.asList(
            "apple", "red apple", "green apple", "banana", "orange", "mandarin", "tangerine",
            "grape", "strawberry", "cherry", "peach", "apricot", "plum", "fig", "pomegranate",
            "watermelon", "melon", "kiwi", "pineapple", "avocado", "lemon", "lime", "blueberry",
            "raspberry", "blackberry", "pear", "grapefruit", "quince", "mango", "coconut",
            "papaya", "guava", "passion fruit", "dragon fruit", "lychee", "persimmon", "nectarine",
            "cantaloupe", "honeydew", "date", "cranberry", "gooseberry", "mulberry", "elderberry",
            "currant", "clementine", "kumquat", "star fruit", "kiwano", "rambutan", "longan",
            "ackee", "durian", "jackfruit", "mangosteen", "soursop", "tamarind", "breadfruit",
            "plantain", "boysenberry", "cloudberry", "loganberry", "salmonberry", "thimbleberry",
            "dewberry", "chokeberry", "acai berry", "goji berry", "jabuticaba", "miracle fruit",
            "pitaya", "sapodilla", "cherimoya", "sugar apple", "custard apple", "feijoa", "jujube",
            "medlar", "rowan", "sea buckthorn", "service berry", "sloe", "wild strawberry",
            "wood apple", "african cherry", "bilberry", "crowberry", "hackberry", "hawthorn",
            "huckleberry", "juniper berry", "lingonberry", "mayapple", "nannyberry", "pawpaw",
            "salal", "serviceberry", "snowberry", "wayfaring tree", "barberry"));

    // Valid vegetable names (100+ common vegetables in English)
    private static final Set<String> VALID_VEGETABLES = new HashSet<>(Arrays.asList(
            "tomato", "cherry tomato", "cucumber", "green pepper", "red pepper", "bell pepper",
            "yellow pepper", "orange pepper", "eggplant", "zucchini", "potato", "sweet potato",
            "onion", "red onion", "white onion", "spring onion", "garlic", "carrot", "spinach",
            "lettuce", "iceberg lettuce", "romaine lettuce", "parsley", "dill", "arugula", "basil",
            "mint", "thyme", "oregano", "rosemary", "cilantro", "sage", "broccoli", "cauliflower",
            "leek", "cabbage", "red cabbage", "mushroom", "corn", "peas", "green beans", "asparagus",
            "celery", "beetroot", "radish", "turnip", "parsnip", "rutabaga", "kohlrabi", "fennel",
            "artichoke", "brussels sprouts", "kale", "swiss chard", "collard greens", "bok choy",
            "napa cabbage", "endive", "radicchio", "watercress", "mustard greens", "beet greens",
            "dandelion greens", "turnip greens", "sorrel", "chicory", "escarole", "frisee", "mache",
            "okra", "squash", "butternut squash", "acorn squash", "pumpkin", "spaghetti squash",
            "chayote", "jicama", "taro", "yam", "cassava", "daikon", "horseradish", "ginger",
            "turmeric", "jerusalem artichoke", "salsify", "water chestnut", "bamboo shoots",
            "bean sprouts", "alfalfa sprouts", "snow peas", "snap peas", "lima beans", "fava beans",
            "edamame", "shallot", "scallion", "chives"));

    // Stock limits
    public static final double MIN_STOCK = 0.0;
    public static final double MAX_STOCK = 10000.0;

    // Price limits (reasonable for fruits/vegetables)
    public static final double MIN_PRICE = 0.01;
    public static final double MAX_PRICE = 5000.0;

    /**
     * Validate username: letters and numbers allowed, but must contain at least one
     * letter.
     * Username like "elif12" or "12elif" is OK, but "123" or "_____" is NOT OK.
     * 
     * @return true if valid (alphanumeric with at least one letter)
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // Allow letters, numbers, and underscore
        if (!username.matches("[A-Za-z0-9_]+")) {
            return false;
        }

        // MUST contain at least one letter (cannot be just numbers/underscores)
        if (!username.matches(".*[A-Za-z].*")) {
            return false;
        }

        return true;
    }

    /**
     * Validate product name: must be a known fruit or vegetable.
     * 
     * @param name Product name
     * @param type Product type ("fruit" or "vegetable")
     * @return true if valid
     */
    public static boolean isValidProductName(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String normalized = name.trim().toLowerCase();

        if ("fruit".equalsIgnoreCase(type)) {
            return VALID_FRUITS.contains(normalized);
        } else if ("vegetable".equalsIgnoreCase(type)) {
            return VALID_VEGETABLES.contains(normalized);
        }

        return false;
    }

    /**
     * Validate stock quantity: must be within reasonable limits.
     * 
     * @return true if stock is between MIN_STOCK and MAX_STOCK
     */
    public static boolean isValidStock(double stock) {
        return stock >= MIN_STOCK && stock <= MAX_STOCK;
    }

    /**
     * Validate price: must be positive and within reasonable limits.
     * 
     * @return true if price is between MIN_PRICE and MAX_PRICE
     */
    public static boolean isValidPrice(double price) {
        return price >= MIN_PRICE && price <= MAX_PRICE;
    }

    /**
     * Validate Turkish phone number.
     * Accepts formats (with or without spaces/dashes):
     * - 0555 555 5555 or 05555555555 (11 digits starting with 05)
     * - +90 555 555 5555 or +905555555555 (country code)
     * - 555 555 5555 or 5555555555 (10 digits starting with 5)
     * 
     * @return true if valid Turkish mobile format
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        phone = phone.trim();

        // Remove spaces and dashes for validation
        String cleaned = phone.replaceAll("[\\s-]", "");

        // Must be all digits (except optional + at start)
        if (!cleaned.matches("\\+?[0-9]+")) {
            return false;
        }

        // Must have minimum length (reject "123", "000" etc)
        if (cleaned.length() < 10) {
            return false;
        }

        // Pattern 1: 05XXXXXXXXX (exactly 11 digits)
        if (cleaned.matches("05[0-9]{9}")) {
            return true;
        }

        // Pattern 2: +905XXXXXXXXX (exactly 13 chars with +90)
        if (cleaned.matches("\\+905[0-9]{9}")) {
            return true;
        }

        // Pattern 3: 5XXXXXXXXX (exactly 10 digits)
        if (cleaned.matches("5[0-9]{9}")) {
            return true;
        }

        return false;
    }

    /**
     * Validate address: must contain at least some letters, not just numbers.
     * 
     * @return true if address contains at least one letter
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        // Must contain at least one letter (not just numbers/symbols)
        return address.matches(".*[A-Za-z].*");
    }

    /**
     * Get a user-friendly error message for invalid product name.
     */
    public static String getProductNameError(String type) {
        if ("fruit".equalsIgnoreCase(type)) {
            return "Invalid fruit name. Please enter a valid fruit (e.g., Apple, Banana, Orange).";
        } else if ("vegetable".equalsIgnoreCase(type)) {
            return "Invalid vegetable name. Please enter a valid vegetable (e.g., Tomato, Carrot, Potato).";
        }
        return "Invalid product name.";
    }

    /**
     * Get list of valid product names for a type.
     */
    public static String getValidProductExamples(String type) {
        if ("fruit".equalsIgnoreCase(type)) {
            return "Examples: Apple, Banana, Orange, Grape, Strawberry, Kiwi";
        } else if ("vegetable".equalsIgnoreCase(type)) {
            return "Examples: Tomato, Cucumber, Carrot, Potato, Onion, Lettuce";
        }
        return "";
    }
}
