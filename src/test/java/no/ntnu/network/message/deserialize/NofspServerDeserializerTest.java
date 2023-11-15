package no.ntnu.network.message.deserialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.ServerMessage;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit testing for the NofspServerDeserializer class.
 * The class tests both the {@code NofspSerializer} and the {@code NofspServerDeserializer}.
 */
public class NofspServerDeserializerTest {
    ByteSerializerVisitor serializer;
    MessageDeserializer<ServerMessage> deserializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        this.serializer = NofspSerializer.getInstance();
        this.deserializer = new NofspServerDeserializer();
    }

    /**
     * Tests the serialization of the {@code RegisterControlPanelRequest}.
     */
    @Test
    public void testRegisterControlPanelRequestSerialization() throws IOException {
        Set<DeviceClass> compatibilityList = new HashSet<>();
        compatibilityList.add(DeviceClass.A1);
        compatibilityList.add(DeviceClass.S3);
        ControlMessage request = new RegisterControlPanelRequest(compatibilityList);

        byte[] bytes = serializer.serialize(request);
        ServerMessage reconstructedMessage = deserializer.deserializeMessage(bytes);

        assertEquals(request, reconstructedMessage);
    }

    @Test
    public void testTest() throws SerializationException {
        Logger.printBytes(serializer.serialize(new RegistrationConfirmationResponse(4)));
    }
}