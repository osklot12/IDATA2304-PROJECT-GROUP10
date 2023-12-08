package no.ntnu.network.message.encryption.cipher.encrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.decrypt.RSADecryptor;
import no.ntnu.network.message.encryption.keygen.RSAKeyPairGenerator;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * JUnit tests for both the {@code RSAEncryptor} and {@code RSADecryptor}.
 * The class verifies that the encryption/decryption works as expected.
 */
public class RSAEncryptorTest {
    RSAEncryptor encryptor;
    RSADecryptor decryptor;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() throws NoSuchAlgorithmException {
        RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();
        keyGen.createKeys();
        KeyPair keyPair = keyGen.getKeyPair();

        encryptor = new RSAEncryptor(keyPair.getPublic());
        decryptor = new RSADecryptor(keyPair.getPrivate());
    }

    /**
     * Tests that the decryptor reverses the effects of the encryptor.
     *
     * @throws EncryptionException thrown if encryption fails
     */
    @Test
    public void testEncryption() throws EncryptionException {
        byte[] bytes = "This is the string to encrypt".getBytes(StandardCharsets.UTF_8);

        byte[] encryptedBytes = encryptor.encrypt(bytes);

        assertArrayEquals(bytes, decryptor.decrypt(encryptedBytes));
    }
}