package dev.ui;

/** The short "about" block shown at the bottom of the help overlay. */
public final class AboutText {

    /** Product name, as shown in the header. */
    public static final String NAME = "acre";

    /** What the letters stand for. */
    public static final String TITLE = "Apple Container Resource Explorer";

    /** One line on what the app does. */
    public static final String SUMMARY =
        "A terminal UI for Apple's container CLI on macOS.";

    /** Credit line for the runtime and the toolkit. */
    public static final String BUILT_WITH = "Built with Java 25 and TamboUI.";

    private AboutText() {
    }

    /**
     * Returns the version, taken from the jar manifest.
     *
     * @return the implementation version, or {@code "dev"} when running from classes
     */
    public static String version() {
        String version = AboutText.class.getPackage().getImplementationVersion();
        return version == null ? "dev" : version;
    }
}
