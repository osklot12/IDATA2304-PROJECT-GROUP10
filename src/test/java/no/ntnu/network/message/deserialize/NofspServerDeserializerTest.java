package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.response.ActuatorStateSetServerResponse;
import no.ntnu.network.message.response.AdlUpdatedResponse;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.response.VirtualActuatorUpdatedResponse;
import no.ntnu.network.message.response.error.AdlUpdateRejectedError;
import no.ntnu.network.message.response.error.DeviceInteractionFailedError;
import no.ntnu.network.message.response.error.NoSuchVirtualDeviceError;
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
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);
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
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);
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
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code FieldNodePoolPullRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testFieldNodePoolPullRequestSerialization() throws IOException {
        FieldNodePoolPullRequest request = new FieldNodePoolPullRequest();

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code SubscribeToFieldNodeRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSubscribeToFieldNodeRequestSerialization() throws IOException {
        SubscribeToFieldNodeRequest request = new SubscribeToFieldNodeRequest(4);

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code AdlUpdatedResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testAdlUpdatedResponseSerialization() throws IOException {
        AdlUpdatedResponse response = new AdlUpdatedResponse();

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code AdlUpdateRejectedError}.
     *
     * @throws IOException thrown if an I/O exception is thrown
     */
    @Test
    public void testAdlUpdateRejectedErrorSerialization() throws IOException {
        AdlUpdateRejectedError response = new AdlUpdateRejectedError("Test description");

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code ActuatorNotificationRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testActuatorNotificationRequestSerialization() throws IOException {
        ActuatorNotificationRequest request = new ActuatorNotificationRequest(4, 0);

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code VirtualActuatorUpdatedResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testVirtualActuatorUpdatedResponse() throws IOException {
        VirtualActuatorUpdatedResponse response = new VirtualActuatorUpdatedResponse();

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code NoSuchVirtualDeviceError}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testNoSuchVirtualDeviceErrorSerialization() throws IOException {
        NoSuchVirtualDeviceError response = new NoSuchVirtualDeviceError("TestDescription");

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code ServerActivateActuatorRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testServerActivateActuatorRequestSerialization() throws IOException {
        ServerActivateActuatorRequest request = new ServerActivateActuatorRequest(1, 2, 1);

        byte[] bytes = serializer.serialize(request);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code ActuatorStateSetServerResponse}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testActuatorStateSetServerResponseSerialization() throws IOException {
        ActuatorStateSetServerResponse response = new ActuatorStateSetServerResponse();

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code DeviceInteractionFailedError}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testDeviceInteractionFailedErrorSerialization() throws IOException {
        DeviceInteractionFailedError response = new DeviceInteractionFailedError("TestDescription");

        byte[] bytes = serializer.serialize(response);
        Tlv tlv = TlvReader.constructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }
}