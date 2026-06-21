package com.sapms.pattern.singleton;

/**
 * DESIGN PATTERN: Singleton
 * <p>
 * Ensures only ONE instance of ApplicationConfig exists throughout
 * the application lifecycle. Uses double-checked locking for thread safety.
 * <p>
 * Usage: ApplicationConfig.getInstance().getProperty("key")
 */
public class ApplicationConfig {

    private static volatile ApplicationConfig instance;

    private final java.util.Properties properties = new java.util.Properties();

    // Private constructor — prevents direct instantiation
    private ApplicationConfig() {
        // Load default academic configuration
        properties.setProperty("grade.pass.threshold", "60.0");
        properties.setProperty("grade.atrisk.threshold", "50.0");
        properties.setProperty("attendance.min.rate", "75.0");
        properties.setProperty("grade.weight.assignment", "0.20");
        properties.setProperty("grade.weight.midterm", "0.30");
        properties.setProperty("grade.weight.final", "0.50");
        properties.setProperty("app.name", "SAPMS");
        properties.setProperty("app.version", "1.0.0");
    }

    // Double-checked locking — thread-safe lazy initialization
    public static ApplicationConfig getInstance() {
        if (instance == null) {
            synchronized (ApplicationConfig.class) {
                if (instance == null) {
                    instance = new ApplicationConfig();
                }
            }
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key, "0.0"));
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    // Prevent cloning (Singleton safety)
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone Singleton");
    }
}
