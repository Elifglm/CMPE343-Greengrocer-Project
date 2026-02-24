package app;

public class Launcher {
    public static void main(String[] args) {
        // Run schema updates to fix missing columns
        SchemaFixer.main(args);

        // Start app
        Main.main(args);
    }
}
