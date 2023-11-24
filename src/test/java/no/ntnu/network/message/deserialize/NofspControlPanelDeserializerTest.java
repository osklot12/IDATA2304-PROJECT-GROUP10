package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.request.ServerFnsmNotificationRequest;
import no.ntnu.network.message.response.FieldNodePoolResponse;
import no.ntnu.network.message.response.SubscribedToFieldNodeResponse;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        serializer = new NofspSerializer();
        deserializer = new NofspControlPanelDeserializer();
    }

    /**
     * Tests that serialization of the heart beat request goes as expected.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testHeartbeatRequestSerialization() throws IOException {
        HeartbeatRequest<ControlPanelContext> request = new HeartbeatRequest<>();

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of {@code FieldNodePoolResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testFieldNodePoolResponseSerialization() throws IOException {
        Map<Integer, String> fieldNodePool = new HashMap<>();
        fieldNodePool.put(2, "GreenhouseNode");
        fieldNodePool.put(3, "OutsideNode");

        FieldNodePoolResponse response = new FieldNodePoolResponse(fieldNodePool);

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of {@code SubscribedToFieldNodeResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSubscribedToFieldNodeResponseSerialization() throws IOException {
        Map<Integer, DeviceClass> fnst = new HashMap<>();
        fnst.put(1, DeviceClass.A2);
        fnst.put(2, DeviceClass.S3);

        Map<Integer, Integer> fnsm = new HashMap<>();
        fnsm.put(1, 0);
        fnsm.put(2, 3);

        String name = "Test field node";

        SubscribedToFieldNodeResponse response = new SubscribedToFieldNodeResponse(fnst, fnsm, name);

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of {@code ServerFnsmNotificationRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testServerFnsmNotificationRequestSerialization() throws IOException {
        ServerFnsmNotificationRequest request = new ServerFnsmNotificationRequest(1, 1, 2);

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }
}