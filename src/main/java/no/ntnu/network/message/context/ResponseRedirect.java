package no.ntnu.network.message.context;

import no.ntnu.network.ControlCommAgent;

/**
 * A redirection for a response message, useful when dealing with chains of requests.
 *
 * @param responseId the message to assign the response for the destination to recognize it
 * @param destination the destination for the response
 */
public record ResponseRedirect(int responseId, ControlCommAgent destination) {
}
