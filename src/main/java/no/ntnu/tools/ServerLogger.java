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

    /**
     * Log a timeout for a request message.
     *
     * @param request the timed out request message
     */
    public static void requestTimeout(RequestMessage request, String clientAddress) {
        Logger.error("[ TIMEOUT " + TimeFormatter.now() + " ] REQUEST ( " + request.getId() + " ): " + request.toString() + " -> " + clientAddress);
    }

    /**
     * Log a dead heartbeat event.
     *
     * @param clientAddress the client address for the dead client
     */
    public static void deadHeartbeat(String clientAddress) {
        Logger.error("[ DISCONNECTED " + TimeFormatter.now() + " ] Client " + clientAddress + " do not respond and has been disconnected.");
    }
}
