package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.network.message.serialize.serializerstrategy.SerializerStrategy;

/**
 * A Message used for requesting a service.
 */
public abstract class RequestMessage implements Message {
    protected final String command;

    /**
     * Creates a new RequestMessage.
     *
     * @param command the command for the request
     */
    protected RequestMessage(String command) {
        this.command = command;
    }

    /**
     * Returns the command for the request message.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }
}
