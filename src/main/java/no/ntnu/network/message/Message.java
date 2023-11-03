package no.ntnu.network.message;

import no.ntnu.exception.MessageProcessException;
import no.ntnu.network.message.serialize.ByteSerializable;

/**
 * A message sent from one entity in the network to another.
 */
public interface Message extends ByteSerializable {
    /**
     * Processes the message.
     *
     * @throws MessageProcessException thrown if the message cannot be processed
     */
    void process() throws MessageProcessException;
}
