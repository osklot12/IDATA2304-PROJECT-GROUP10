package no.ntnu.network.controlprocess;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.net.Socket;

/**
 * The TCP control process is responsible for exchanging control messages with another entity in the network.
 * The class provides a simple interface for sending and receiving control messages.
 * It uses java generics to allow for the receiving of messages processing on specific
 * message contexts.
 *
 * @param <C> a message context used for message deserialization
 */
public class TcpControlProcess<C extends MessageContext> {
    private final TcpTlvSender tlvSender;
    private final TcpTlvReceiver tlvReceiver;
    private final ByteSerializerVisitor serializer;
    private final MessageDeserializer<C> deserializer;

    /**
     * Creates a new TCPControlProcess.
     *
     * @param socket       the socket used for TCP communication
     * @param serializer   the serializer for serializing messages
     * @param deserializer the deserializer for deserializing messages
     */
    public TcpControlProcess(Socket socket, ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) throws IOException {
        if (serializer == null) {
            throw new IllegalArgumentException("Cannot create TCPControlProcess, because serializer is null");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create TCPControlProcess, because deserializer is null");
        }

        this.tlvSender = new TcpTlvSender(socket);
        this.tlvReceiver = new TcpTlvReceiver(socket, deserializer.getTlvFrame());
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Sets the encryption used for sending Tlvs.
     *
     * @param encryption the encryption strategy to use
     */
    public void setEncryption(EncryptionStrategy encryption) {
        tlvSender.setEncryption(encryption);
    }

    /**
     * Sets the decryption used for receiving Tlvs.
     *
     * @param decryption the decryption strategy to use
     */
    public void setDecryption(DecryptionStrategy decryption) {
        tlvReceiver.setDecryption(decryption);
    }

    /**
     * Sends a control message.
     *
     * @param message message to send
     */
    public void sendMessage(ControlMessage message) throws IOException {
        Tlv serializedMessage = serializer.serialize(message);
        tlvSender.sendTlv(serializedMessage);
    }

    /**
     * Returns the next available message received.
     * The method blocks until a message is received, the end of the stream is met or an exception is thrown.
     *
     * @return the next message received, null if end of stream is met
     * @throws IOException thrown if an I/O exception occurs
     */
    public Message<C> getNextMessage() throws IOException {
        Tlv serializedMessage = tlvReceiver.getNextTlv();
        return deserializer.deserializeMessage(serializedMessage);
    }
}
