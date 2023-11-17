package no.ntnu.network.connectionservice.requestmanager;

/**
 * A pending request, storing the time of its creation as well as its time-to-live.
 *
 * @param request the pending request
 * @param timestamp a timestamp indicating when the request was sent
 * @param ttl the time-to-live value in milliseconds
 */
public record PendingRequest(no.ntnu.network.message.request.RequestMessage request, long timestamp, long ttl) {
}
