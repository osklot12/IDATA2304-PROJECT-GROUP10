package no.ntnu.network.message.deserialize.component;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.ByteHandler;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvFrame;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A deserializer for deserializing TLVs into {@code Message} objects.
 * The class provides a base for recognizing how a TLV should be deserialized, and lets further implementations of
 * the class define their own methods for specific message deserialization. The class can seem a bit complex and
 * abstract, but actually simplifies the whole process of message deserialization. Every entity in the network
 * can use this class as a base for their deserialization of messages, reducing code duplication providing a common
 * logic for message deserialization.
 * <p/>
 * To introduce new message deserialization methods, a subclass needs to implement the method using the following
 * functional interface:
 * <p>
 * {@code Message<C> deserialize(int messageId, TlvReader parameterReader) throws IOException}.
 * <p>
 * Once the method has been implemented, the method needs to be put into a deserializerMap together with a
 * key. This is done as follows:
 * <ul>
 *     <li>
 *         Request message: If the resulting message is a {@code RequestMessage}, the method needs to be added
 *         using the {@code addRequestMessageDeserialization(String command, ControlMessageDeserializerMethod<C> method)} method.
 *     </li>
 *     <li>
 *         Response message: If the resulting message is a {@code ResponseMessage}, the method needs to be added
 *         using the {@code addResponseMessageDeserialization(int statusCode, ControlMessageDeserializerMethod<C> method)} method.
 *     </li>
 * </ul>
 * Once this has been done, the implemented message deserialization method will be executed by calling the
 * {@code Message<C> deserializeMessage(Tlv tlv) throws IOException} method on the appropriate TLV.
 *
 * @param <C> the message context to use for deserialized messages
 */
public abstract class NofspMessageDeserializer<C extends MessageContext> extends NofspDeserializer implements MessageDeserializer<C> {
    /**
     * A functional interface for deserializing messages.
     *
     * @param <C> the message context to deserialize for
     */
    @FunctionalInterface
    protected interface MessageDeserializerMethod<C extends MessageContext> {
        Message<C> deserialize(TlvReader messageFieldReader) throws IOException;
    }

    /**
     * A functional interface for deserializing control messages.
     *
     * @param <C> the message context used for processing
     */
    @FunctionalInterface
    protected interface ControlMessageDeserializerMethod<C extends MessageContext> {
        Message<C> deserialize(int messageId, TlvReader parameterReader) throws IOException;
    }

    /**
     * A functional interface for deserializing sensor data messages.
     *
     * @param <C> the message context used for processing
     */
    @FunctionalInterface
    protected interface SensorDataDeserializationMethod<C extends MessageContext> {
        Message<C> deserialize(int clientNodeAddress, int sensorAddress, Tlv dataTlv) throws IOException;
    }

    // lookup table for message frame types
    protected final Map<String, MessageDeserializerMethod<C>> messageDeserializerMap;

    // lookup table for request messages commands
    protected final Map<String, ControlMessageDeserializerMethod<C>> requestDeserializerMap;

    // lookup table for response message status codes
    protected final Map<Integer, ControlMessageDeserializerMethod<C>> responseDeserializerMap;

    /**
     * Creates a new NofspMessageDeserializer.
     */
    protected NofspMessageDeserializer() {
        this.messageDeserializerMap = new HashMap<>();
        this.requestDeserializerMap = new HashMap<>();
        this.responseDeserializerMap = new HashMap<>();

        initializeMessageDeserializerMap();
    }

    @Override
    public Message<C> deserializeMessage(Tlv tlv) throws IOException {
        if (tlv == null) {
            throw new IllegalArgumentException("Cannot deserialize TLV, because TLV is null.");
        }

        Message<C> result = null;

        // gets the appropriate deserialization method by identifying the message type
        String byteKey = ByteHandler.bytesToString(tlv.typeField());
        MessageDeserializerMethod<C> deserializerMethod = messageDeserializerMap.get(byteKey);
        if (deserializerMethod != null) {
            // extracts the value field of the message frame TLV, as it holds the specific message field TLVs
            TlvReader messageFieldReader = new TlvReader(tlv.valueField(), tlv.getFrame());
            result = deserializerMethod.deserialize(messageFieldReader);
        }

        return result;
    }

    /**
     * Initializes all the entries for the message deserializer map, which is used to map TLV type fields to
     * deserialization methods.
     */
    private void initializeMessageDeserializerMap() {
        addMessageDeserialization(NofspSerializationConstants.REQUEST_BYTES, this::getRequestMessage);
        addMessageDeserialization(NofspSerializationConstants.RESPONSE_BYTES, this::getResponseMessage);
    }

    /**
     * Adds a message deserialization method for a given message type.
     *
     * @param typeField the type-field for the tlv
     * @param method    the associated deserialization method
     */
    private void addMessageDeserialization(byte[] typeField, MessageDeserializerMethod<C> method) {
        messageDeserializerMap.put(ByteHandler.bytesToString(typeField), method);
    }

    /**
     * Adds a request message deserialization method for a given request command.
     *
     * @param command the request command
     * @param method  the associated deserialization method
     */
    protected void addRequestMessageDeserialization(String command, ControlMessageDeserializerMethod<C> method) {
        requestDeserializerMap.put(command, method);
    }

    /**
     * Adds a response message deserialization method for a given response command.
     *
     * @param statusCode the response status code
     * @param method     the associated deserialization method
     */
    protected void addResponseMessageDeserialization(int statusCode, ControlMessageDeserializerMethod<C> method) {
        responseDeserializerMap.put(statusCode, method);
    }

    /**
     * Deserializes a request message.
     *
     * @param messageFieldReader the TlvReader holding the message fields
     * @return the deserialized request message, null on error
     * @throws IOException thrown if an I/O exception occurs
     */
    protected Message<C> getRequestMessage(TlvReader messageFieldReader) throws IOException {
        Message<C> result = null;

        // the first TLV holds the message ID
        int messageId = getMessageId(messageFieldReader.readNextTlv());

        // the second TLV holds the request message command
        String command = getRequestMessageCommand(messageFieldReader.readNextTlv());

        // the third TLV is a container TLV holding the request parameters
        TlvReader parameterReader = getParameterReader(messageFieldReader.readNextTlv());

        // gets the appropriate request deserialization method identified by the request command
        ControlMessageDeserializerMethod<C> deserializationMethod = requestDeserializerMap.get(command);

        if (deserializationMethod != null) {
            result = deserializationMethod.deserialize(messageId, parameterReader);
        } else {
            throw new IOException("Cannot deserialize message, because no deserialization method was found" +
                    " for request command: " + command);
        }

        return result;
    }

    /**
     * Deserializes a response message.
     *
     * @param messageFieldReader the TlvReader holding the message fields
     * @return the deserialized response message, null on error
     * @throws IOException thrown if an I/O exception occurs
     */
    protected Message<C> getResponseMessage(TlvReader messageFieldReader) throws IOException {
        Message<C> result = null;

        // the first TLV holds the message ID
        int messageId = getMessageId(messageFieldReader.readNextTlv());

        // the second TLV holds the response message status code
        int statusCode = getResponseStatusCode(messageFieldReader.readNextTlv());

        // the third TLV is a container TLV holding the request parameters
        TlvReader parameterReader = getParameterReader(messageFieldReader.readNextTlv());

        // gets the appropriate request deserialization method identified by the response status code
        ControlMessageDeserializerMethod<C> deserializationMethod = responseDeserializerMap.get(statusCode);

        if (deserializationMethod != null) {
            result = deserializationMethod.deserialize(messageId, parameterReader);
        } else {
            throw new IOException("Cannot deserialize message, because no deserialization method was found for" +
                    " response status code: " + statusCode);
        }

        return result;
    }

    private int getResponseStatusCode(Tlv statusCodeTlv) {
        return getInteger(statusCodeTlv).getInteger();
    }

    private static TlvReader getParameterReader(Tlv parameterContainerTlv) {
        TlvReader parameterReader = null;

        if (parameterContainerTlv != null) {
            parameterReader = new TlvReader(parameterContainerTlv.valueField(), parameterContainerTlv.getFrame());
        }

        return parameterReader;
    }

    private String getRequestMessageCommand(Tlv requestMessageCommandTlv) throws IOException {
        return getString(requestMessageCommandTlv).getString();
    }

    private int getMessageId(Tlv messageIdTlv) throws IOException {
        return getInteger(messageIdTlv).getInteger();
    }

    @Override
    public TlvFrame getTlvFrame() {
        return NofspSerializationConstants.TLV_FRAME;
    }
}
