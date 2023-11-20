package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.request.RegisterFieldNodeRequest;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
     * Tests the serialization of the {@code RegisterFieldNodeRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testRegisterFieldNodeRequestSerialization() throws IOException {
        Map<Integer, DeviceClass> fnst = new HashMap<>();
        fnst.put(20, DeviceClass.S3);
        fnst.put(3, DeviceClass.A2);

        Map<Integer, Integer> fnsm = new HashMap<>();
        fnsm.put(2, 34);
        fnsm.put(1, 5);
        RegisterFieldNodeRequest request = new RegisterFieldNodeRequest(fnst, fnsm, "test request");

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);
        Message<ServerContext> reconstructedMessage = deserializer.deserializeMessage(tlv);

        assertEquals(request, reconstructedMessage);
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
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);
        Message<ServerContext> reconstructedMessage = deserializer.deserializeMessage(tlv);

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
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }
}