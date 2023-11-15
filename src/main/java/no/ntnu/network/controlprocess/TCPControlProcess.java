package no.ntnu.network.controlprocess;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * The TCP control process is responsible for exchanging control messages with another entity in the network.
 * The class provides a simple interface for sending and receiving control messages.
 *
 * @param <C> a message context used for message processing
 */
public class TCPControlProcess<C extends MessageContext> {
    private TCPMessageReceiver<C> messageReceiver;
    private TCPMessageSender messageSender;

    /**
     * Creates a new TCPControlProcess.
     */
    public TCPControlProcess(ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        
    }
}
