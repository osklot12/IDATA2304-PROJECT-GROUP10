package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.AdlUpdateRequest;
import no.ntnu.network.message.response.ServerFnsmUpdatedResponse;
import no.ntnu.network.message.response.error.ServerFnsmUpdateRejectedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * JUnit testing for the {@code NofspFieldNodeDeserializer} class.
 */
public class NofspFieldNodeDeserializerTest {
    ByteSerializerVisitor serializer;
    MessageDeserializer<FieldNodeContext> deserializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        this.serializer = new NofspSerializer();
        this.deserializer = new NofspFieldNodeDeserializer();
    }

    /**
     * Tests the serialization of the {@code AdlUpdateRequest}.
     */
    @Test
    public void testAdlUpdateRequestSerialization() throws IOException {
        Set<Integer> adlUpdates = new HashSet<>();
        adlUpdates.add(1);
        adlUpdates.add(4);
        AdlUpdateRequest request = new AdlUpdateRequest(adlUpdates);

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code ServerFnsmUpdatedResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testServerFnsmUpdatedResponseSerialization() throws IOException {
        ServerFnsmUpdatedResponse response = new ServerFnsmUpdatedResponse();

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code ServerFnsmUpdateRejectedError}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testServerFnsmUpdateRejectedErrorSerialization() throws IOException {
        ServerFnsmUpdateRejectedError response = new ServerFnsmUpdateRejectedError("TestDescription");

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }
}