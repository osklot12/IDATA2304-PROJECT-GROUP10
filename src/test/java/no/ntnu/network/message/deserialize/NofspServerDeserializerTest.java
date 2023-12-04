package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.centralserver.CentralHubTestFactory;
import no.ntnu.network.centralserver.centralhub.CentralHub;
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
import no.ntnu.network.message.sensordata.SduSensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.representation.FieldNodeInformation;
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
    CentralHub centralHub;
    NofspServerDeserializer deserializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        this.serializer = new NofspSerializer();
        this.centralHub = CentralHubTestFactory.getPopulatedHub();
        this.deserializer = new NofspServerDeserializer(centralHub);
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
        FieldNodeInformation fieldNodeInformation = new FieldNodeInformation(fnst, fnsm, "test request");
        RegisterFieldNodeRequest request = new RegisterFieldNodeRequest(fieldNodeInformation);

        Tlv tlv = serializer.serialize(request);
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
        ControlMessage request = new RegisterControlPanelRequest(compatibilityList, 60005);

        Tlv tlv = serializer.serialize(request);
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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(request);

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

        Tlv tlv = serializer.serialize(request);

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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(request);

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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(request);

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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code SduSensorDataMessageSerialization}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSduSensorDataMessageSerialization() throws IOException {
        SduSensorDataMessage message = new SduSensorDataMessage(1, 3, 34.9);

        Tlv tlv = serializer.serialize(message);

        assertEquals(message, deserializer.deserializeSensorData(tlv));
    }

    /**
     * Tests the serialization of the {@code UnsubscribeFromFieldNodeRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testUnsubscribeFromFieldNodeRequestSerialization() throws IOException {
        UnsubscribeFromFieldNodeRequest request = new UnsubscribeFromFieldNodeRequest(2);

        Tlv tlv = serializer.serialize(request);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code DisconnectRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testDisconnectRequestSerialization() throws IOException {
        DisconnectRequest request = new DisconnectRequest();

        Tlv tlv = serializer.serialize(request);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }
}