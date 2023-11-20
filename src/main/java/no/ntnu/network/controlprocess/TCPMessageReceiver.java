package no.ntnu.network.controlprocess;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.serialize.tool.InputStreamByteSource;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.net.Socket;

/**
 * Receives TCP (control) messages from another node in the network.
 */
public class TCPMessageReceiver<C extends MessageContext> {
    private final TlvReader socketReader;
    private final MessageDeserializer<C> deserializer;

    /**
     * Creates a new TCPMessageReceiver.
     *
     * @param socket the socket to receive messages from
     * @param deserializer the deserializer used to deserialize messages
     */
    public TCPMessageReceiver(Socket socket, MessageDeserializer<C> deserializer) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Cannot create TCPMessageReceiver, because socket is null");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create TCPMessageReceiver, because deserializer is null");
        }

        this.deserializer = deserializer;
        this.socketReader = new TlvReader(new InputStreamByteSource(socket.getInputStream()), deserializer.getTlvFrame());
    }

    /**
     * Returns the next message received.
     * The method blocks until the next message is received, the end of the stream has been met or an exception occurs.
     *
     * @return the next received message, null if end of stream
     * @throws IOException thrown if an I/O exception occurs
     */
    public Message<C> getNextMessage() throws IOException {
        Message<C> nextMessage = null;

        Tlv nextTlv = socketReader.readNextTlv();
        if (nextTlv != null) {
            nextMessage = deserializer.deserializeMessage(nextTlv);
        }

        return nextMessage;
    }
}
