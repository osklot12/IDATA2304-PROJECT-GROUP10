package no.ntnu.tools;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.time.LocalTime;

/**
 * A class encapsulating logging for the central server.
 */
public class ServerLogger {
    /**
     * Log a sending of a request message.
     *
     * @param request the request message sent
     * @param clientAddress the destination address for the request
     */
    public static void requestSent(RequestMessage request, String clientAddress) {
        Logger.info("[ SENT " + TimeFormatter.now() + " ] REQUEST ( " + request.getId() + " ): " + request.toString() + " -> " + clientAddress);
    }

    /**
     * Log a sending of a response message.
     *
     * @param response the response message sent
     * @param clientAddress the destination address for the request
     */
    public static void responseSent(ResponseMessage response, String clientAddress) {
        Logger.info("[ SENT " + TimeFormatter.now() + " ] RESPONSE ( " + response.getId() + " ): " + response.toString() + " -> " + clientAddress);
    }

    /**
     * Log a receiving of a request message.
     *
     * @param request the received request message
     * @param clientAddress the destination address for the request
     */
    public static void requestReceived(RequestMessage request, String clientAddress) {
        Logger.info("[ RECEIVED " + TimeFormatter.now() + " ] REQUEST ( " + request.getId() + " ): " + request.toString() + " -> " + clientAddress);
    }

    /**
     * Log a receiving of a response message.
     *
     * @param response the received response message
     * @param clientAddress the destination address for the request
     */
    public static void responseReceived(ResponseMessage response, String clientAddress) {
        Logger.info("[ RECEIVED " + TimeFormatter.now() + " ] RESPONSE ( " + response.getId() + " ): " + response.toString() + " -> " + clientAddress);
    }
}
