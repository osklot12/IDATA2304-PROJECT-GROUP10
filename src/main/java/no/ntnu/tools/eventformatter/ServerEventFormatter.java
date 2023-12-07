package no.ntnu.tools.eventformatter;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.tools.TimeFormatter;

/**
 * A class encapsulating logic for formatting certain server events to string.
 */
public class ServerEventFormatter {
    /**
     * Does not allow creating instances of the class.
     */
    private ServerEventFormatter(){}

    /**
     * Generates a formatted string describing the event of a request sent.
     *
     * @param request the request sent
     * @param clientAddress the destination client
     * @return the formatted string
     */
    public static String requestSent(RequestMessage request, String clientAddress) {
        return "[ SENT " + TimeFormatter.now() + " ] REQUEST ( " + request.getSerializableId() + " ): " + request.toString() + " -> " + clientAddress;
    }

    /**
     * Generates a formatted string describing the event of a response sent.
     *
     * @param response the response sent
     * @param clientAddress the destination client
     * @return the formatted string
     */
    public static String responseSent(ResponseMessage response, String clientAddress) {
        return "[ SENT " + TimeFormatter.now() + " ] RESPONSE ( " + response.getSerializableId() + " ): " + response.toString() + " -> " + clientAddress;
    }

    /**
     * Generates a formatted string describing the event of a request received.
     *
     * @param request the request received
     * @param clientAddress the destination client
     * @return the formatted string
     */
    public static String requestReceived(RequestMessage request, String clientAddress) {
        return "[ RECEIVED " + TimeFormatter.now() + " ] REQUEST ( " + request.getSerializableId() + " ): " + request.toString() + " -> " + clientAddress;
    }

    /**
     * Generates a formatted string describing the event of a response received.
     *
     * @param response the response received
     * @param clientAddress the destination client
     * @return the formatted string
     */
    public static String responseReceived(ResponseMessage response, String clientAddress) {
        return "[ RECEIVED " + TimeFormatter.now() + " ] RESPONSE ( " + response.getSerializableId() + " ): " + response.toString() + " -> " + clientAddress;
    }

    /**
     * Generates a formatted string describing the event of a request timeout.
     *
     * @param request the timed out request
     * @param clientAddress the destination client
     * @return the formatted string
     */
    public static String requestTimeout(RequestMessage request, String clientAddress) {
        return "[ TIMEOUT " + TimeFormatter.now() + " ] REQUEST ( " + request.getSerializableId() + " ): " + request.toString() + " -> " + clientAddress;
    }

    /**
     * Generates a formatted string describing the event of a sensor data message received.
     *
     * @param message the received sensor data message
     * @param clientAddress the destination client
     * @return the formatted string
     */
    public static String sensorDataReceived(SensorDataMessage message, String clientAddress) {
        return "[ RECEIVED " + TimeFormatter.now() + " ] SENSOR DATA: " + message.toString() + " -> " + clientAddress;
    }

    /**
     * Generates a formatted string describing the event of a dead client.
     *
     * @param clientAddress the dead client
     * @return the formatted string
     */
    public static String deadClient(String clientAddress) {
        return "[ DISCONNECTED " + TimeFormatter.now() + " ] Client " + clientAddress + " do not respond and has been disconnected.";
    }

    /**
     * Generates a formatted string describing an emergency.
     *
     * @param message the description of the emergency
     * @return the formatted string
     */
    public static String emergency(String message) {
        return "[ EMERGENCY " + TimeFormatter.now() + " ] " + message;
    }
}
