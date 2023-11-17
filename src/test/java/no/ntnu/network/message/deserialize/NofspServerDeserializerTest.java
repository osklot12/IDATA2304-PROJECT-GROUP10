package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
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
    MessageDeserializer<ServerContext> deserializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        this.serializer = new NofspSerializer();
        this.deserializer = new NofspServerDeserializer();
    }

    /**
     * Tests the serialization of the {@code RegisterControlPanelRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testRegisterControlPanelRequestSerialization() throws IOException {
        Set<DeviceClass> compatibilityList = new HashSet<>();
        compatibilityList.add(DeviceClass.A1);
        compatibilityList.add(DeviceClass.S3);
        ControlMessage request = new RegisterControlPanelRequest(compatibilityList);

        byte[] bytes = serializer.serialize(request);
        Message<ServerContext> reconstructedMessage = deserializer.deserializeMessage(bytes);

        assertEquals(request, reconstructedMessage);
    }

    /**
     * Tests the serialization of the {@code HeartbeatResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testHeartbeatResponseSerialization() throws IOException {
        HeartbeatResponse response = new HeartbeatResponse();

        byte[] bytes = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(bytes));
    }
}