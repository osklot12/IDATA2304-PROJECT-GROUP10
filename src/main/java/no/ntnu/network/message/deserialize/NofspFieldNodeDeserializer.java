package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.deserialize.component.NofspClientMessageDeserializer;
import no.ntnu.network.message.request.AdlUpdateRequest;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.util.Set;

/**
 * A deserializer for deserializing field node messages.
 */
public class NofspFieldNodeDeserializer extends NofspClientMessageDeserializer<FieldNodeContext> {
    /**
     * Creates a new NofspFieldNodeDeserializer.
     */
    public NofspFieldNodeDeserializer() {
        super();

        initializeDeserializationMethods();
    }

    /**
     * Adds the implemented control panel message deserialization methods to the lookup tables.
     */
    private void initializeDeserializationMethods() {
        // requests
        addRequestMessageDeserialization(NofspSerializationConstants.ADL_UPDATE_COMMAND, this::getAdlUpdateRequest);
    }

    /**
     * Deserializes an {@code AdlUpdateRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private AdlUpdateRequest getAdlUpdateRequest(int messageId, TlvReader parameterReader) throws IOException {
        AdlUpdateRequest request = null;

        // deserializes the adl update set
        ByteSerializableSet<ByteSerializableInteger> serializableAdlUpdate
                = getSetOfType(parameterReader.readNextTlv(), ByteSerializableInteger.class);
        Set<Integer> adlUpdates = DataTypeConverter.getSetOfIntegers(serializableAdlUpdate);

        request = new AdlUpdateRequest(messageId, adlUpdates);

        return request;
    }
}
