package no.ntnu.tools.logger;

/**
 * A logger that logs to the 'standard' output stream on the system.
 */
public class SystemOutLogger implements SimpleLogger {
    @Override
    public void logInfo(String message) {
        System.out.println(message);
    }

    @Override
    public void logError(String error) {
        System.err.println(error);
    }
}
