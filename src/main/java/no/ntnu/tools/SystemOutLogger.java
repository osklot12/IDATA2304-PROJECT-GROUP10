package no.ntnu.tools;

import no.ntnu.network.message.serialize.tool.ByteHandler;

/**
 * A logger class for encapsulating all the logging. We can either reduce the number of SonarLint
 * warnings, or implement it properly. This class makes sure we sue the same logging in all
 * places of our code.
 */
public class SystemOutLogger implements SimpleLogger {
    /**
     * Log an information message.
     *
     * @param message The message to log. A newline is appended automatically.
     */
    public static void info(String message) {
        System.out.println(message);
    }

    /**
     * Log an info message without appending a newline to the log.
     *
     * @param message The message to log
     */
    public static void infoNoNewline(String message) {
        System.out.print(message);
    }

    /**
     * Log an error message.
     *
     * @param message The error message to log
     */
    public static void error(String message) {
        System.err.println(message);
    }

    /**
     * Log an array of bytes.
     *
     * @param bytes array of bytes to log
     */
    public static void printBytes(byte[] bytes) {
        System.out.println(ByteHandler.bytesToString(bytes));
    }

    @Override
    public void logInfo(String message) {
        info(message);
    }

    @Override
    public void logError(String error) {
        error(error);
    }
}
