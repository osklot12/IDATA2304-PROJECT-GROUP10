package no.ntnu.tools;

/**
 * Logs events for network traffic.
 */
public interface SimpleLogger {
    /**
     * Logs info.
     *
     * @param message the message to log
     */
    void logInfo(String message);

    /**
     * Logs error.
     *
     * @param error the error message to log
     */
    void logError(String error);
}
