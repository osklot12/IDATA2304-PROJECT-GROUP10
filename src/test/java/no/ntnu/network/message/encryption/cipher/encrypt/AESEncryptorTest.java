package no.ntnu.network.message.encryption.cipher.encrypt;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.cipher.decrypt.AESDecryption;
import no.ntnu.network.message.encryption.keygen.AESKeyGenerator;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * JUnit testing for both the {@code AESEncryptor} and {@code AESDecryptor}.
 * The class verifies that the encryption/decryption works as expected.
 */
public class AESEncryptorTest {
    AESEncryption encryptor;
    AESDecryption decryptor;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() throws NoSuchAlgorithmException {
        AESKeyGenerator keyGen = new AESKeyGenerator();
        keyGen.createKey();

        encryptor = new AESEncryption(keyGen.getKey());
        decryptor = new AESDecryption(keyGen.getKey());
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

    /**
     * Tests that the bytes are actually encrypted, and not just remains the same, which may not be observed by the
     * previous test.
     *
     * @throws EncryptionException thrown if encryption fails
     */
    @Test
    public void testEffect() throws EncryptionException {
        byte[] bytes = "This is the string to encrypt".getBytes(StandardCharsets.UTF_8);

        byte[] encryptedBytes = encryptor.encrypt(bytes);

        assertFalse(Arrays.equals(bytes, encryptedBytes));
    }
}