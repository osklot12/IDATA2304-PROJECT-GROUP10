package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.encryption.keygen.AESKeyGenerator;
import no.ntnu.network.message.encryption.keygen.AsymmetricKeyPairGenerator;
import no.ntnu.network.message.encryption.keygen.RSAKeyPairGenerator;
import no.ntnu.network.message.encryption.keygen.SymmetricKeyGenerator;
import no.ntnu.network.message.request.AdlUpdateRequest;
import no.ntnu.network.message.request.FieldNodeActivateActuatorRequest;
import no.ntnu.network.message.response.AsymmetricEncryptionResponse;
import no.ntnu.network.message.response.DisconnectionAllowedResponse;
import no.ntnu.network.message.response.ServerFnsmUpdatedResponse;
import no.ntnu.network.message.response.SymmetricEncryptionResponse;
import no.ntnu.network.message.response.error.KeyGenError;
import no.ntnu.network.message.response.error.ServerFnsmUpdateRejectedError;
import no.ntnu.network.message.response.error.UnsecureRequestError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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

        Tlv tlv = serializer.serialize(request);

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

        Tlv tlv = serializer.serialize(response);

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

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code FieldNodeActivateActuatorRequest}.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testFieldNodeActivateActuatorRequest() throws IOException {
        FieldNodeActivateActuatorRequest request = new FieldNodeActivateActuatorRequest(2, 3);

        Tlv tlv = serializer.serialize(request);

        assertEquals(request, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code DisconnectionAllowedResponse}.
     * This test covers the response for all clients, as it is implemented in the base class for client message
     * deserialization.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testDisconnectionAllowedResponseSerialization() throws IOException {
        DisconnectionAllowedResponse<FieldNodeContext> response = new DisconnectionAllowedResponse<>();

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code AsymmetricEncryptionResponse}.
     * This test covers the response for all clients, as it is implemented in the base class for client message
     * deserialization.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testAsymmetricEncryptionResponseSerialization() throws IOException, NoSuchAlgorithmException {
        AsymmetricKeyPairGenerator keyGen = new RSAKeyPairGenerator();
        keyGen.createKeys();
        PublicKey key = keyGen.getKeyPair().getPublic();
        AsymmetricEncryptionResponse<FieldNodeContext> response = new AsymmetricEncryptionResponse<>(key);

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code SymmetricEncryptionResponse}.
     * This test covers the response for all clients, as it is implemented in the base class for client message
     * deserialization.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSymmetricEncryptionResponseSerialization() throws IOException, NoSuchAlgorithmException {
        SymmetricKeyGenerator keyGen = new AESKeyGenerator();
        keyGen.createKey();
        SecretKey secretKey = keyGen.getKey();
        SymmetricEncryptionResponse<FieldNodeContext> response = new SymmetricEncryptionResponse<>(secretKey);

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code UnsecureRequestError}.
     * This test covers the response for all clients, as it is implemented in the base class for client message
     * deserialization.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testUnsecureRequestErrorSerialization() throws IOException {
        UnsecureRequestError<FieldNodeContext> response = new UnsecureRequestError<>("TestError");

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }

    /**
     * Tests the serialization of the {@code KeyGenError}.
     * This test covers the response for all clients, as it is implemented in the base class for client message
     * deserialization.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testKeyGenErrorSerialization() throws IOException {
        KeyGenError<FieldNodeContext> response = new KeyGenError<>("TestError");

        Tlv tlv = serializer.serialize(response);

        assertEquals(response, deserializer.deserializeMessage(tlv));
    }
}