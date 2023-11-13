package no.ntnu.network.controlprocess;

import no.ntnu.network.message.request.RequestMessage;

/**
 * A sent request, waiting a response.
 *
 * @param request the request message
 * @param timestamp the time at which the message was sent, in milliseconds
 */
public record PendingRequest(RequestMessage request, long timestamp) {
}
