package no.ntnu.network.message.encryption;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.encryption.cipher.decrypt.AESDecryption;
import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.AESEncryption;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.encryption.keygen.AESKeyGenerator;
import no.ntnu.network.message.encryption.keygen.SymmetricKeyGenerator;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code TlvEncryption} class.
 * The class verifies that encryption/decryption of TLVs are correct and consistent.
 */
public class TlvEncryptionTest {
    EncryptionStrategy encryptor;
    DecryptionStrategy decryptor;
    ByteSerializerVisitor serializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() throws NoSuchAlgorithmException {
        // uses a random algorithm for encryption/decryption
        SymmetricKeyGenerator keyGen = new AESKeyGenerator();
        keyGen.createKey();
        SecretKey key = keyGen.getKey();
        encryptor = new AESEncryption(key);
        decryptor = new AESDecryption(key);

        // creates a serializer in order to access a proper tlv easily
        serializer = new NofspSerializer();
    }

    /**
     * Tests that TLV decryption reverts the effect of TLV encryption.
     */
    @Test
    public void testTlvEncryption() throws IOException, EncryptionException {
        Tlv originalTlv = serializer.serialize(new ByteSerializableString("Hello world!"));

        Tlv encryptedTlv = TlvEncryption.encryptTlv(originalTlv, encryptor);

        Tlv decryptedTlv = TlvEncryption.decryptTlv(encryptedTlv, decryptor);

        assertEquals(originalTlv, decryptedTlv);
    }

    /**
     * Tests that the TLV is actually encrypted, and not just remains the same, which may not be observed by the
     * previous test.
     *
     * @throws EncryptionException thrown if encryption fails
     */
    @Test
    public void testEffect() throws EncryptionException, IOException {
        Tlv originalTlv = serializer.serialize(new ByteSerializableString("Hello world!"));

        Tlv encryptedTlv = TlvEncryption.encryptTlv(originalTlv, encryptor);

        assertNotEquals(originalTlv, encryptedTlv);
    }
}