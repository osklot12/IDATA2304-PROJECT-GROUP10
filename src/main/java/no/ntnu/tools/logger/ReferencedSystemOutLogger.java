package no.ntnu.tools.logger;

/**
 * A logger that logs to the 'standard' output stream of the system.
 * All logged lines will contain a prefix reference, which is useful to keep track of the source of the information
 * in a system with many different sources.
 */
public class ReferencedSystemOutLogger extends SystemOutLogger {
    private final String reference;

    /**
     * Creates a new ReferencedSystemOutLogger.
     *
     * @param reference a string reference
     */
    public ReferencedSystemOutLogger(String reference) {
        super();
        if (reference == null) {
            throw new IllegalArgumentException("Cannot create ReferencedSystemOutLogger, because reference is null.");
        }

        this.reference = reference;
    }

    @Override
    public void logInfo(String message) {
        super.logInfo("[ " + reference + " ] " + message);
    }

    @Override
    public void logError(String error) {
        super.logError("[ " + reference + " ] " + error);
    }
}
