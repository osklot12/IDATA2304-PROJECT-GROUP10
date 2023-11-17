package no.ntnu.tools;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.time.LocalTime;

/**
 * A class encapsulating logging for a client.
 */
public class ClientLogger {
    /**
     * Log a sending of a request message.
     *
     * @param request the request message sent
     */
    public static void requestSent(RequestMessage request) {
        Logger.info("[ SENT " + TimeFormatter.now() + " ] REQUEST ( " + request.getId() + " ): " + request.toString());
    }

    /**
     * Log a sending of a response message.
     *
     * @param response the response message sent
     */
    public static void responseSent(ResponseMessage response) {
        Logger.info("[ SENT " + TimeFormatter.now() + " ] RESPONSE ( " + response.getId() + " ): " + response.toString());
    }

    /**
     * Log the receiving of a request message.
     *
     * @param request the received request message
     */
    public static void requestReceived(RequestMessage request) {
        Logger.info("[ RECEIVED " + TimeFormatter.now() + " ] REQUEST ( " + request.getId() + " ): " + request.toString());
    }

    /**
     * Log a receiving of a response message.
     *
     * @param response the received response message
     */
    public static void responseReceived(ResponseMessage response) {
        Logger.info("[ RECEIVED " + TimeFormatter.now() + " ] RESPONSE ( " + response.getId() + " ): " + response.toString());
    }

    /**
     * Log a timeout for a request message.
     *
     * @param request the timed out request message
     */
    public static void requestTimeout(RequestMessage request) {
        Logger.error("[ TIMEOUT " + TimeFormatter.now() + " ] REQUEST ( " + request.getId() + " ): " + request.toString());
    }
}
