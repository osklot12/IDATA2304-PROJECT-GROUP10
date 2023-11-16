package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the NofspControlPanelDeserializer class.
 */
public class NofspControlPanelDeserializerTest {
    ByteSerializerVisitor serializer;
    MessageDeserializer<ControlPanelContext> deserializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        serializer = NofspSerializer.getInstance();
        deserializer = new NofspControlPanelDeserializer();
    }

    /**
     * Tests that serialization of the heart beat request goes as expected.
     */
    @Test
    public void testHeartbeatRequestSerialization() throws IOException {
        HeartbeatRequest<ControlPanelContext> request = new HeartbeatRequest<>();

        byte[] bytes = serializer.serialize(request);

        assertEquals(request, deserializer.deserializeMessage(bytes));
    }
}