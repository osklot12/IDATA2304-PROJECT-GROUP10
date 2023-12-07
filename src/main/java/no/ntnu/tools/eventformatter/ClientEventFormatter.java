package no.ntnu.tools.eventformatter;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.TimeFormatter;

/**
 * A class encapsulating logic for formatting certain client events to string.
 */
public class ClientEventFormatter {
    /**
     * Does not allow creating instances of the class.
     */
    private ClientEventFormatter() {}

    /**
     * Generates a formatted string describing the event of a request sent.
     *
     * @param request the request sent
     * @return the formatted string
     */
    public static String requestSent(RequestMessage request) {
        return "[ SENT " + TimeFormatter.now() + " ] REQUEST ( " + request.getSerializableId() + " ): " + request.toString();
    }

    /**
     * Generates a formatted string describing the event of a response sent.
     *
     * @param response the request sent
     * @return the formatted string
     */
    public static String responseSent(ResponseMessage response) {
        return "[ SENT " + TimeFormatter.now() + " ] RESPONSE ( " + response.getSerializableId() + " ): " + response.toString();
    }

    /**
     * Generates a formatted string describing the event of a request received.
     *
     * @param request the request received
     * @return the formatted string
     */
    public static String requestReceived(RequestMessage request) {
        return "[ RECEIVED " + TimeFormatter.now() + " ] REQUEST ( " + request.getSerializableId() + " ): " + request.toString();
    }

    /**
     * Generates a formatted string describing the event of a response received.
     *
     * @param response the request received
     * @return the formatted string
     */
    public static String responseReceived(ResponseMessage response) {
        return "[ RECEIVED " + TimeFormatter.now() + " ] RESPONSE ( " + response.getSerializableId() + " ): " + response.toString();
    }

    /**
     * Generates a formatted string describing the event of a request timeout.
     *
     * @param request the timed out request
     * @return the formatted string
     */
    public static String requestTimeout(RequestMessage request) {
        return "[ TIMEOUT " + TimeFormatter.now() + " ] REQUEST ( " + request.getSerializableId() + " ): " + request.toString();
    }
}
